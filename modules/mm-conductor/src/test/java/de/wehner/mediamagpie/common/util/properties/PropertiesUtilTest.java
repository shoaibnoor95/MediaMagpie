package de.wehner.mediamagpie.common.util.properties;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import static org.junit.internal.matchers.IsCollectionContaining.*;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.common.util.CipherService;
import de.wehner.mediamagpie.common.util.properties.Encrypted;
import de.wehner.mediamagpie.common.util.properties.PropertiesBacked;
import de.wehner.mediamagpie.common.util.properties.PropertiesUtil;


public class PropertiesUtilTest {

    private CipherService _cipherService;

    @Before
    public void setUp() throws GeneralSecurityException {
        _cipherService = new CipherService("key");
    }

    @Test
    public void testGetProperties() throws Exception {
        List<String> properties = PropertiesUtil.getPropertyKeys(TestConfiguration.class);
        assertEquals(4, properties.size());
        assertThat(properties, hasItem("conf.test.a"));
        assertThat(properties, hasItem("conf.test.caMel"));
        assertThat(properties, hasItem("conf.test.boolean"));
        assertThat(properties, hasItem("conf.test.encrypted"));
    }

    @Test
    public void testReadFromProperties() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("conf.test.a", "1");
        properties.setProperty("conf.test.caMel", "2");
        properties.setProperty("conf.test.boolean", "true");
        TestConfiguration testConfiguration = PropertiesUtil.readFromProperties(_cipherService, TestConfiguration.class, properties);
        assertEquals("1", testConfiguration.getA());
        assertEquals("2", testConfiguration.getCaMel());
        assertThat(testConfiguration.isBoolean(), is(true));
    }

    @Test
    public void testTransformToProperties() throws Exception {
        TestConfiguration testConfiguration = new TestConfiguration();
        testConfiguration.setA("1");
        testConfiguration.setCaMel("2");
        testConfiguration.setBoolean(true);

        Properties properties = PropertiesUtil.transformToProperties(_cipherService, testConfiguration);
        assertEquals(3, properties.size());
        assertEquals("1", properties.getProperty("conf.test.a"));
        assertEquals("2", properties.getProperty("conf.test.caMel"));
        assertEquals("true", properties.getProperty("conf.test.boolean"));
    }

    @Test
    public void testTransformToProperties_StringArray() throws Exception {
        // HadoopConfiguration conf = new HadoopConfiguration();
        // conf.setMode(GridMode.LOCAL);
        // conf.setNodes(new String[] { "n1", "n2" });
        //
        // Properties properties = PropertiesUtil.transformToProperties(_cipherService, conf);
        // HadoopConfiguration readConf = PropertiesUtil.readFromProperties(_cipherService,
        // HadoopConfiguration.class, properties);
        // assertArrayEquals(conf.getNodes(), readConf.getNodes());
        UserConfiguration conf = new UserConfiguration();
        // conf.setMode(GridMode.LOCAL);
        conf.setRootMediaPathes(new String[] { "/tmp/pathA", "/home/pathB" });

        Properties properties = PropertiesUtil.transformToProperties(_cipherService, conf);
        UserConfiguration readConf = PropertiesUtil.readFromProperties(_cipherService, UserConfiguration.class, properties);
        assertThat(conf.getRootMediaPathes()).isEqualTo(readConf.getRootMediaPathes());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadFromProperties_unannotatedClass() throws Exception {
        PropertiesUtil.readFromProperties(_cipherService, String.class, new Properties());
    }

    @Test(expected = RuntimeException.class)
    public void testIncompleteness() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("conf.test.a", "1");
        properties.setProperty("conf.test.boolean", "true");
        PropertiesUtil.checkPropertyCompleteness(TestConfiguration.class, properties, true);
    }

    @Test
    public void testCompleteness() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("conf.test.a", "1");
        properties.setProperty("conf.test.caMel", "2");
        properties.setProperty("conf.test.boolean", "true");
        properties.setProperty("conf.test.encrypted", "xxx");
        PropertiesUtil.checkPropertyCompleteness(TestConfiguration.class, properties, true);
    }

    @Test
    public void testReadFromProperties_encryption() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("conf.test.a", "1");
        properties.setProperty("conf.test.caMel", "2");
        properties.setProperty("conf.test.boolean", "true");
        properties.setProperty("conf.test.encrypted", _cipherService.encryptToBase64("password"));
        TestConfiguration testConfiguration = PropertiesUtil.readFromProperties(_cipherService, TestConfiguration.class, properties);
        assertEquals("password", testConfiguration.getEncrypted());
    }

    @Test
    public void testTransformToProperties_encryption() throws Exception {
        TestConfiguration testConfiguration = new TestConfiguration();
        testConfiguration.setEncrypted("password");
        Properties properties = PropertiesUtil.transformToProperties(_cipherService, testConfiguration);
        assertEquals("password", _cipherService.decryptFromBase64(properties.getProperty("conf.test.encrypted")));
    }

    @Test
    public void testGetAnnotatedClass() throws Exception {
        assertEquals(TestConfiguration.class, PropertiesUtil.getAnnotatedClass(TestConfiguration.class));
        assertEquals(TestConfiguration.class, PropertiesUtil.getAnnotatedClass(SubclassTestConfiguration.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAnnotatedClass_InvalidClass() throws Exception {
        PropertiesUtil.getAnnotatedClass(String.class);
    }

    @Test
    public void testGetPrefix_FromSuperClass() throws Exception {
        String prefix = PropertiesUtil.getPrefix(SubclassTestConfiguration.class);

        assertEquals("conf.test", prefix);
    }

    @Test
    public void testGetProperties_FromSuperClass() throws Exception {
        List<String> properties = PropertiesUtil.getPropertyKeys(SubclassTestConfiguration.class);
        assertEquals(4, properties.size());
        assertThat(properties, hasItem("conf.test.a"));
        assertThat(properties, hasItem("conf.test.caMel"));
        assertThat(properties, hasItem("conf.test.boolean"));
        assertThat(properties, hasItem("conf.test.encrypted"));
        assertFalse(properties.contains("conf.test.myTransientField"));
    }

    @Test
    public void testReadFromProperties_ASubclassedObjectThatHasNoAnnotation() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("conf.test.a", "1");
        properties.setProperty("conf.test.caMel", "2");
        properties.setProperty("conf.test.boolean", "true");
        SubclassTestConfiguration testConfiguration = PropertiesUtil.readFromProperties(_cipherService, SubclassTestConfiguration.class, properties);
        assertThat(testConfiguration, instanceOf(SubclassTestConfiguration.class));
        assertEquals("1", testConfiguration.getA());
        assertEquals("2", testConfiguration.getCaMel());
        assertEquals(0, testConfiguration.getMyTransientField());
        assertThat(testConfiguration.isBoolean(), is(true));
    }

    @Test
    public void testTransformToProperties_OfASubclass() throws Exception {
        SubclassTestConfiguration testConfiguration = new SubclassTestConfiguration();
        testConfiguration.setA("1");
        testConfiguration.setCaMel("2");
        testConfiguration.setBoolean(true);
        testConfiguration.setMyTransientField(3);

        // Verify, that only the tree field in the annotated base class will be provided as result.
        // The field SubclassTestConfiguration._myTransientField must be ignored.
        Properties properties = PropertiesUtil.transformToProperties(_cipherService, testConfiguration);
        assertEquals(3, properties.size());
        assertEquals("1", properties.getProperty("conf.test.a"));
        assertEquals("2", properties.getProperty("conf.test.caMel"));
        assertEquals("true", properties.getProperty("conf.test.boolean"));
    }

    @PropertiesBacked(prefix = "conf.test")
    static class TestConfiguration {

        private String _a;
        private String _caMel;
        private boolean _boolean;
        @Encrypted
        private String _encrypted;

        public String getA() {
            return _a;
        }

        public void setA(String a) {
            _a = a;
        }

        public String getCaMel() {
            return _caMel;
        }

        public void setCaMel(String caMel) {
            _caMel = caMel;
        }

        public boolean isBoolean() {
            return _boolean;
        }

        public void setBoolean(boolean b) {
            _boolean = b;
        }

        public void setEncrypted(String encrypted) {
            _encrypted = encrypted;
        }

        public String getEncrypted() {
            return _encrypted;
        }
    }

    static class SubclassTestConfiguration extends TestConfiguration {
        private int _myTransientField;

        public int getMyTransientField() {
            return _myTransientField;
        }

        public void setMyTransientField(int myTransientField) {
            _myTransientField = myTransientField;
        }
    }
}
