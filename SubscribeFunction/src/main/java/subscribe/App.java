package subscribe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import subscribe.dto.UserInfoDto;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<UserInfoDto, Object> {

    public Object handleRequest(final UserInfoDto input, final Context context) {
        AmazonDynamoDB client = DynamoDBClientBuilder.createClient();
        ListTablesResult listTablesResult = client.listTables();
        try {
            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);
            return new GatewayResponse(output, new HashMap<>(), 200);
        } catch (IOException e) {
            return new GatewayResponse("{}", new HashMap<>(), 500);
        }
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
