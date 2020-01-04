package subscribe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import subscribe.dto.UserInfoDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<InputStream, GatewayResponse> {

    private DynamoDbWrapper dynamoDbWrapper;

    public App() {
        this.dynamoDbWrapper = new DynamoDbWrapper();
    }

    public GatewayResponse handleRequest(final InputStream lambdaInput, final Context context) {
        UserInfoDto userSubscription = this.getUserSubscriptionDto(lambdaInput);
        validateInput(userSubscription);
        if (dynamoDbWrapper.userSubscriptionExists(userSubscription)) {
            throw new RuntimeException("Email already exists.");
        }

        dynamoDbWrapper.createUserSubscription(userSubscription);
        return createResponseSuccessfullySubscribed(userSubscription);
    }

    private void validateInput(UserInfoDto userSubscription) {
        boolean isValid = userSubscription != null && isValidEmail(userSubscription.getEmail());
        if (!isValid) {
            throw new RuntimeException("Invalid payload, don't go down to AWS to waste money and free tier quota");
        }
    }

    private static boolean isValidEmail(String email) {
        String regexForValidEmail = "[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*" +
                "@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        return email != null && email.length() <= 100 && email.matches(regexForValidEmail);
    }

    private GatewayResponse createResponseSuccessfullySubscribed(UserInfoDto userSubscription) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        ObjectMapper jsonConverter = new ObjectMapper();
        try {
            String jsonUserSubscription = jsonConverter.writeValueAsString(userSubscription);
            return new GatewayResponse(jsonUserSubscription, headers, HttpStatus.SC_CREATED);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create response object from user subscription.");
        }
    }

    private UserInfoDto getUserSubscriptionDto(InputStream lambdaInput) {
        UserInfoDto userSubscription = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(lambdaInput);
            String body = jsonNode.get("body").asText();
            body = java.net.URLDecoder.decode(body, StandardCharsets.UTF_8.name());
            userSubscription = objectMapper.readValue(body, UserInfoDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userSubscription;
    }

}
