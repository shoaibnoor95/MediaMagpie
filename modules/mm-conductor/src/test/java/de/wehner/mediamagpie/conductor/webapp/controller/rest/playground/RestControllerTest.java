package de.wehner.mediamagpie.conductor.webapp.controller.rest.playground;

import static org.junit.Assert.*;

import static org.junit.internal.matchers.StringContains.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.test.util.TestEnvironment2;


public class RestControllerTest {

    private ObjectMapper mapper;

    @Rule
    public final TestEnvironment2 _itTestEnvironment = new TestEnvironment2(new File("target/testfolder"));

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testJson() throws JsonParseException, JsonMappingException, IOException {
        // generate json output with simple data binding
        Map<String, String> nameStruct = new HashMap<String, String>();
        nameStruct.put("first", "Joe");
        nameStruct.put("last", "Sixpack");
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("name", nameStruct);
        userData.put("gender", "MALE");
        userData.put("verified", Boolean.FALSE);
        userData.put("userImage", "Rm9vYmFyIQ==");
        mapper.writeValue(new File(_itTestEnvironment.getTargetTestDir(), "user.json"), userData);

        // load json into a tree model and modify an object within a node (last name here)
        ObjectMapper m = new ObjectMapper();
        // can either use mapper.readTree(JsonParser), or bind to JsonNode
        JsonNode rootNode = m.readValue(new File(_itTestEnvironment.getTargetTestDir(), "user.json"), JsonNode.class);
        // ensure that "last name" isn't "Xmler"; if is, change to "Jsoner"
        JsonNode nameNode = rootNode.path("name");
        String lastName = nameNode.path("last").getTextValue();
        if ("sixpack".equalsIgnoreCase(lastName)) {
            ((ObjectNode) nameNode).put("last", "Jsoner");
        }
        m.writeValue(new File(_itTestEnvironment.getTargetTestDir(), "user-modified.json"), rootNode);

        // verify changes in result
        assertThat(FileUtils.readFileToString(new File(_itTestEnvironment.getTargetTestDir(), "user-modified.json")), containsString("\"name\":{\"last\":\"Jsoner\",\"first\":\"Joe\"}"));
    }
}