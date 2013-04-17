package de.wehner.mediamagpie.core.util.properties;

import static org.fest.assertions.Assertions.*;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;

import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;
import de.wehner.mediamagpie.core.util.properties.PropertiesUtil;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;

public class PropertiesUtilTest {

    private CipherServiceImpl _cipherService;

    @Before
    public void setUp() throws GeneralSecurityException {
        _cipherService = new CipherServiceImpl("key");
    }

    @Test
    public void testGetProperties() throws Exception {
        List<String> properties = PropertiesUtil.getPropertyKeys(TestConfiguration.class);
        assertThat(properties).hasSize(5);
        assertThat(properties).contains("conf.test.a");
        assertThat(properties).contains("conf.test.caMel");
        assertThat(properties).contains("conf.test.boolean");
        assertThat(properties).contains("conf.test.encrypted");
    }

    @Test
    public void testReadFromProperties() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("conf.test.a", "1");
        properties.setProperty("conf.test.caMel", "2");
        properties.setProperty("conf.test.boolean", "true");
        TestConfiguration testConfiguration = PropertiesUtil.readFromProperties(_cipherService, TestConfiguration.class, properties);
        assertThat(testConfiguration.getA()).isEqualTo("1");
        assertThat(testConfiguration.getCaMel()).isEqualTo("2");
        assertThat(testConfiguration.isBoolean()).isTrue();
    }

    @Test
    public void testTransformToProperties() throws Exception {
        TestConfiguration testConfiguration = new TestConfiguration();
        testConfiguration.setA("1");
        testConfiguration.setCaMel("2");
        testConfiguration.setBoolean(true);

        Properties properties = PropertiesUtil.transformToProperties(_cipherService, testConfiguration);
        assertThat(properties).hasSize(4);
        assertThat(properties.getProperty("conf.test.a")).isEqualTo("1");
        assertThat(properties.getProperty("conf.test.caMel")).isEqualTo("2");
        assertThat(properties.getProperty("conf.test.boolean")).isEqualTo("true");
    }

    @Test
    public void testTransformToProperties_StringArray() throws Exception {
        TestConfiguration conf = new TestConfiguration();
        conf.setArray(new String[] { "/tmp/pathA", "/home/pathB" });

        Properties properties = PropertiesUtil.transformToProperties(_cipherService, conf);
        TestConfiguration readConf = PropertiesUtil.readFromProperties(_cipherService, TestConfiguration.class, properties);
        assertThat(conf.getArray()).isEqualTo(readConf.getArray());

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
        properties.setProperty("conf.test.array", "1,2");
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
        assertThat(testConfiguration.getEncrypted()).isEqualTo("password");
    }

    @Test
    public void testTransformToProperties_encryption() throws Exception {
        TestConfiguration testConfiguration = new TestConfiguration();
        testConfiguration.setEncrypted("password");
        Properties properties = PropertiesUtil.transformToProperties(_cipherService, testConfiguration);
        assertThat(_cipherService.decryptFromBase64(properties.getProperty("conf.test.encrypted"))).isEqualTo("password");
    }

    @Test
    public void testGetAnnotatedClass() throws Exception {
        assertThat(PropertiesUtil.getAnnotatedClass(TestConfiguration.class)).isEqualTo(TestConfiguration.class);
        assertThat(PropertiesUtil.getAnnotatedClass(SubclassTestConfiguration.class)).isEqualTo(TestConfiguration.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAnnotatedClass_InvalidClass() throws Exception {
        PropertiesUtil.getAnnotatedClass(String.class);
    }

    @Test
    public void testGetPrefix_FromSuperClass() throws Exception {
        String prefix = PropertiesUtil.getPrefix(SubclassTestConfiguration.class);

        assertThat(prefix).isEqualTo("conf.test");
    }

    @Test
    public void testGetProperties_FromSuperClass() throws Exception {
        List<String> properties = PropertiesUtil.getPropertyKeys(SubclassTestConfiguration.class);
        assertThat(properties).hasSize(5);
        assertThat(properties).contains("conf.test.a");
        assertThat(properties).contains("conf.test.caMel");
        assertThat(properties).contains("conf.test.boolean");
        assertThat(properties).contains("conf.test.encrypted");
        assertThat(properties.contains("conf.test.myTransientField")).isFalse();
    }

    @Test
    public void testReadFromProperties_ASubclassedObjectThatHasNoAnnotation() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("conf.test.a", "1");
        properties.setProperty("conf.test.caMel", "2");
        properties.setProperty("conf.test.boolean", "true");
        SubclassTestConfiguration testConfiguration = PropertiesUtil.readFromProperties(_cipherService, SubclassTestConfiguration.class, properties);
        assertThat(testConfiguration).isInstanceOf(SubclassTestConfiguration.class);
        assertThat(testConfiguration.getA()).isEqualTo("1");
        assertThat(testConfiguration.getCaMel()).isEqualTo("2");
        assertThat(testConfiguration.getMyTransientField()).isEqualTo(0);
        assertThat(testConfiguration.isBoolean()).isTrue();
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
        assertThat(properties).hasSize(4);
        assertThat(properties.getProperty("conf.test.a")).isEqualTo("1");
        assertThat(properties.getProperty("conf.test.caMel")).isEqualTo("2");
        assertThat(properties.getProperty("conf.test.boolean")).isEqualTo("true");
    }

    @PropertiesBacked(prefix = "conf.test")
    static class TestConfiguration {

        private String _a;

        private String _caMel;

        private boolean _boolean;

        @Encrypted
        private String _encrypted;

        @PropertyDef(editorClass = StringArrayPropertyEditor.class)
        private String[] array;

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

        public void setArray(String[] array) {
            this.array = array;
        }

        public String[] getArray() {
            return array;
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
