package subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import subscribe.dto.UserInfoDto;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<UserInfoDto, Object> {

    public Object handleRequest(final UserInfoDto userSubscription, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // Set up connection to Dynamo DB
        AmazonDynamoDBAsync dynamoDBClient = AmazonDynamoDBAsyncClientBuilder.standard()
                                                                              .withRegion(Regions.EU_CENTRAL_1)
                                                                              .build();
        DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
        Table usersTable = dynamoDB.getTable(Constants.USERS_TABLE.getValue());

        validateUserSubscription(userSubscription);
        Item userSubscriptionItem = createUserSubscriptionItem(userSubscription);

        // store user subscription item
        usersTable.putItem(userSubscriptionItem);

        String output = String.format("{ \"email\": \"%s\", \"location\": \"%s\" }",
                                          userSubscription.getEmail(), userSubscription.getLocale().getLanguage());
        return new GatewayResponse(output, headers, 200);
    }

    private void validateUserSubscription(UserInfoDto userSubscription) {
        boolean isInvalid = StringUtils.isNullOrEmpty(userSubscription.getEmail())
                              || StringUtils.isNullOrEmpty(userSubscription.getLocale().getLanguage());

        if (isInvalid) {
            throw new RuntimeException("Invalid payload, don't go down to AWS to waist money and free tier quota");
        }
    };

    private Item createUserSubscriptionItem(UserInfoDto userSubscription) {
        Item userSubscriptionItem = new Item();
        String userId = UUID.randomUUID().toString();
        userSubscriptionItem.withString("id", userId);
        userSubscriptionItem.withString("email", userSubscription.getEmail());
        String userLocale = userSubscription.getLocale().getLanguage();
        userSubscriptionItem.withString("locale", userLocale);

        return userSubscriptionItem;
    };
};
