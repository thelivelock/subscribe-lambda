package subscribe;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.util.StringUtils;

import static subscribe.Constants.AWS_REGION;
import static subscribe.Constants.DYNAMODB_ENDPOINT_OVERRIDE;

public class DynamoDbClientBuilder {

    public static AmazonDynamoDB createClient() {
        AmazonDynamoDBAsyncClientBuilder builder = AmazonDynamoDBAsyncClientBuilder.standard();
        String endpointOverride = System.getenv(DYNAMODB_ENDPOINT_OVERRIDE);
        if (StringUtils.hasValue(endpointOverride)) {
            builder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(
                            endpointOverride,
                            System.getenv(AWS_REGION))
            );
        }

        return builder.build();
    }
}
