package subscribe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import subscribe.dto.UserInfoDto;
import subscribe.response_messages.EmailAlreadyExists;
import subscribe.response_messages.InvalidUserSubscription;
import subscribe.response_messages.ParsingUserSubscriptionFailed;
import subscribe.response_messages.ResponseMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, Object> {

    private DynamoDbWrapper dynamoDbWrapper;

    public App() {
        this.dynamoDbWrapper = new DynamoDbWrapper();
    }

    public Object handleRequest(final Object lambdaInput, final Context context) {
        UserInfoDto userSubscription = this.getUserSubscriptionDto(lambdaInput);
        if (userSubscription == null) {
            ParsingUserSubscriptionFailed parsingUserSubscriptionFailedResponse = new ParsingUserSubscriptionFailed();
            System.out.println(parsingUserSubscriptionFailedResponse);
            return this.createResponseBadRequest(parsingUserSubscriptionFailedResponse);
        }

        boolean isValidInput = validateInput(userSubscription);
        if (!isValidInput) {
            InvalidUserSubscription invalidUserSubscriptionResponse = new InvalidUserSubscription();
            System.out.println(invalidUserSubscriptionResponse);
            return this.createResponseBadRequest(invalidUserSubscriptionResponse);
        }

        if (dynamoDbWrapper.userSubscriptionExists(userSubscription)) {
            EmailAlreadyExists emailAlreadyExistsResponse = new EmailAlreadyExists();
            System.out.println(emailAlreadyExistsResponse);
            return this.createResponseBadRequest(emailAlreadyExistsResponse);
        }

        dynamoDbWrapper.createUserSubscription(userSubscription);
        return createResponseSuccessfullySubscribed(userSubscription);
    }

    private boolean validateInput(UserInfoDto userSubscription) {
        return userSubscription != null && isValidEmail(userSubscription.getEmail());
    }

    private static boolean isValidEmail(String email) {
        String regexForValidEmail = "[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*" +
                "@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        return email != null && email.length() <= 100 && email.matches(regexForValidEmail);
    }

    private GatewayResponse createResponseSuccessfullySubscribed(UserInfoDto userSubscription) {
        try {
            ObjectMapper jsonConverter = new ObjectMapper();
            String jsonUserSubscription = jsonConverter.writeValueAsString(userSubscription);
            return createJsonResponse(jsonUserSubscription, HttpStatus.SC_CREATED);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not create response object from user subscription.");
        }
    }

    private UserInfoDto getUserSubscriptionDto(Object lambdaInput) {
        UserInfoDto userSubscription = null;
        if (!(lambdaInput instanceof LinkedHashMap)) {
            return null;
        }

        LinkedHashMap<String, String> lambdaMapInput = (LinkedHashMap<String, String>) lambdaInput;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (lambdaMapInput.get("body") == null) {
                return null;
            }

            String body = lambdaMapInput.get("body");
            body = java.net.URLDecoder.decode(body, StandardCharsets.UTF_8.name());
            userSubscription = objectMapper.readValue(body, UserInfoDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userSubscription;
    }

    private GatewayResponse createResponseInternalError() {
        return createJsonResponse("{}", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }


    private GatewayResponse createResponseBadRequest(ResponseMessage message) {
        try {
            ObjectMapper jsonConverter = new ObjectMapper();
            String jsonUserSubscription = jsonConverter.writeValueAsString(message);
            return createJsonResponse(jsonUserSubscription, HttpStatus.SC_BAD_REQUEST);
        } catch (JsonProcessingException e) {
            System.out.println("Could not create response object from user subscription.");
            return createResponseInternalError();
        }
    }

    private GatewayResponse createJsonResponse(String jsonBody, int statusCode) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        return new GatewayResponse(jsonBody, headers, statusCode);
    }
}
