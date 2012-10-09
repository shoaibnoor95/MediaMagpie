package de.wehner.mediamagpie.conductor.metadata;

import static org.fest.assertions.Assertions.*;
import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class CameraMetaDataTest {

    private ObjectMapper _mapper;

    @Rule
    public final TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    @Before
    public void setUp() {
        _mapper = new ObjectMapper();
    }

    @Test
    public void testSimpleRoundRobin() throws JsonGenerationException, JsonMappingException, IOException {
        CameraMetaData cameraMetaData = new CameraMetaData();
        cameraMetaData.getMetaData().put("keyä", "value_öäüß");
        File testFile = new File(_testEnvironment.getWorkingDir(), "simpleConvert1.json");
        
        _mapper.writeValue(testFile, cameraMetaData);
        
        CameraMetaData cameraMetaData2 = _mapper.readValue(testFile, CameraMetaData.class);
        
        assertThat(cameraMetaData.toString()).isEqualTo(cameraMetaData2.toString());
    }
}
