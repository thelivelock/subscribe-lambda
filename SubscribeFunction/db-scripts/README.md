Create table on local DynamoDB through aws-cli:
aws dynamodb create-table --cli-input-json file://create-users-table.json --endpoint-url http://localhost:8000