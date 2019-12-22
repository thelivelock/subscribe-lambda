package subscribe;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import subscribe.dto.UserInfoDto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static subscribe.Constants.USERS_TABLE_NAME;

public class DynamoDbWrapper {

    private static final AmazonDynamoDB DYNAMO_DB = DynamoDbClientBuilder.createClient();

    public Map<String, AttributeValue> createUserSubscription(UserInfoDto userSubscription) {
        Map<String, AttributeValue> userSubscriptionItem = createUserSubscriptionItem(userSubscription);
        DYNAMO_DB.putItem(getUsersTableName(), userSubscriptionItem);
        return userSubscriptionItem;
    }

    public boolean userSubscriptionExists(UserInfoDto userSubscription) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":email", new AttributeValue().withS(userSubscription.getEmail()));
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(getUsersTableName())
                .withFilterExpression("email  = :email")
                .withExpressionAttributeValues(expressionAttributeValues);
        ScanResult scanResult = DYNAMO_DB.scan(scanRequest);
        return scanResult.getCount() > 0;
    }

    private Map<String, AttributeValue> createUserSubscriptionItem(UserInfoDto userSubscription) {
        Map<String, AttributeValue> userSubscriptionItem = new HashMap<>();
        String userId = UUID.randomUUID().toString();
        userSubscriptionItem.put("id", new AttributeValue().withS(userId));
        userSubscriptionItem.put("email", new AttributeValue().withS(userSubscription.getEmail()));
        if (userSubscription.getLocale() != null) {
            userSubscriptionItem.put("locale", new AttributeValue().withS(userSubscription.getLocale().getLanguage()));
        }

        if (userSubscription.getTimeZone() != null) {
            userSubscriptionItem.put("timeZone", new AttributeValue().withS(userSubscription.getTimeZone()));
        }

        return userSubscriptionItem;
    }

    private static String getUsersTableName() {
        return System.getenv(USERS_TABLE_NAME);
    }
}
