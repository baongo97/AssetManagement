package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {
	public static JsonNode convertObjectToJsonNode(Object response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String stringResponse = objectMapper.writeValueAsString(response);
        JsonNode jsonNode = objectMapper.readTree(stringResponse);
        return jsonNode;
	}
}
