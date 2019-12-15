package subscribe;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.util.StringUtils;

public class DynamoDBClientBuilder {

    private static final String DYNAMODB_ENDPOINT_OVERRIDE = "DYNAMODB_ENDPOINT_OVERRIDE";
    private static final String AWS_REGION = "AWS_REGION";

    public static AmazonDynamoDB createClient() {
        AmazonDynamoDBAsyncClientBuilder builder = AmazonDynamoDBAsyncClientBuilder.standard();
        String endpointOverride = System.getenv(DYNAMODB_ENDPOINT_OVERRIDE);
        if (StringUtils.hasValue(endpointOverride)) {
            builder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(
                            System.getenv(DYNAMODB_ENDPOINT_OVERRIDE),
                            System.getenv(AWS_REGION))
            );
        }

        return builder.build();
    }
}
