Running DynamoDB Locally
-

- Run DynamoDB docker container 
```
docker run -p 8000:8000 amazon/dynamodb-local --name dynamodb
```
- Create users table (run this command from this folder, file path is relative)
```
aws dynamodb create-table --cli-input-json file://create-users-table.json --endpoint-url http://localhost:8000
```
- Add environment variables in the IDE configuration
```
1) DYNAMODB_ENDPOINT_OVERRIDE = http://host.docker.internal:8000
2) USERS_TABLE_NAME = scoring-service-users
```

After these steps it should be working locally.