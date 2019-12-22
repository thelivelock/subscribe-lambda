Running DynamoDB Locally
-

- Run DynamoDB docker container 
```
docker run -p 8000:8000 --name dynamodb amazon/dynamodb-local
```

- Configure aws region
```
aws2 configure
```

- Create users table (run this command from this folder, file path is relative)
```
aws2 dynamodb create-table --cli-input-json file://create-users-table.json --endpoint-url http://localhost:8000
```

- Add environment variables in the IDE configuration for local testing
```
DYNAMODB_ENDPOINT_OVERRIDE = http://host.docker.internal:8000
```

After these steps it should be working locally.

- If you want to scan local table and see if values are persisted correctly
```
aws2 dynamodb scan --table-name scoring-service-users --endpoint-url http://localhost:8000
```

Docker 
-

- Listing all containers

```
docker ps -a
```

- Re-running existing dynamodb docker

```
docker start -ai dynamodb
```