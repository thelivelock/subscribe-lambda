package subscribe;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import subscribe.dto.UserInfoDto;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<UserInfoDto, GatewayResponse> {

    private DynamoDbWrapper dynamoDbWrapper;

    public App() {
        this.dynamoDbWrapper = new DynamoDbWrapper();
    }

    public GatewayResponse handleRequest(final UserInfoDto userSubscription, final Context context) {
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
}
