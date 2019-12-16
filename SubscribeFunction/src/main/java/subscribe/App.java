package subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import subscribe.dto.UserInfoDto;

import static subscribe.Constants.USERS_TABLE_NAME;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<UserInfoDto, GatewayResponse> {

    public GatewayResponse handleRequest(final UserInfoDto userSubscription, final Context context) {
        validateUserSubscription(userSubscription);

        // set up connection to Dynamo DB
        DynamoDB dynamoDB = DynamoDbClientBuilder.createClient();
        String usersTableName = System.getenv(USERS_TABLE_NAME);
        Table usersTable = dynamoDB.getTable(usersTableName);

        // store user subscription item
        Item userSubscriptionItem = createUserSubscriptionItem(userSubscription);
        usersTable.putItem(userSubscriptionItem);

        // send response
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

    private void validateUserSubscription(UserInfoDto userSubscription) {
        boolean isInvalid = StringUtils.isNullOrEmpty(userSubscription.getEmail())
                              || StringUtils.isNullOrEmpty(userSubscription.getLocale().getLanguage())
                              || StringUtils.isNullOrEmpty(userSubscription.getTimeZone());
        if (isInvalid) {
            throw new RuntimeException("Invalid payload, don't go down to AWS to waste money and free tier quota");
        }
    };

    private Item createUserSubscriptionItem(UserInfoDto userSubscription) {
        Item userSubscriptionItem = new Item();
        String userId = UUID.randomUUID().toString();
        userSubscriptionItem.withString("id", userId);
        userSubscriptionItem.withString("email", userSubscription.getEmail());
        String userLocale = userSubscription.getLocale().getLanguage();
        userSubscriptionItem.withString("locale", userLocale);
        userSubscriptionItem.withString("timeZone", userSubscription.getTimeZone());

        return userSubscriptionItem;
    };
};
