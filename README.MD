
## AWS Lambda API-Gateway app with DynamoDB.
### Work with own ProductRequest, @DynamoDB annotations, AmazonDynamoDB and DynamoDBMapper.


1) Create Lambda AWS. 'Role' must contain AWSLambdaBasicExecutionRole and AmazonDynamoDBFullAccess.
2) Create new API Gateway.
3) Action/Create Resource (name = 'product', path = '/products')
4) Action/Create Resource (name = 'product', path = '/products/{id}')
5) Action/Create Method -> **GET** and choose your Lambda (don't check: 'Use Lambda Proxy integration').
6) Integration Request -> Mapping Templates (= When there are no templates defined (recommended)) -> Add mapping template -> application/json
7) Generate template **EMPTY** and type:
```
#set($inputRoot = $input.path('$'))
{
"httpMethod": "$context.httpMethod",
"id": $input.params('id')
}
```
8) Action/Create Method -> **POST** and choose your Lambda (don't check: 'Use Lambda Proxy integration').
9) Integration Request -> Mapping Templates (= When there are no templates defined (recommended)) -> Add mapping template -> application/json
```
#set($inputRoot = $input.path('$'))
{
"httpMethod": "$context.httpMethod",
"product": $input.json('$')
}
```
10) Action/Deploy API (new Stages -> name = 'dev', for example)

### Create Lambda-function from AWS CLI
```
aws lambda create-function \
    --function-name my-function-product \
    --runtime java11 \
    --zip-file fileb://"d:\global mentoring program\AWS_1\lambda-aws-module\target\lambda-aws-module-1.0-SNAPSHOT.jar" \
    --handler org.aleks4ay.App::handleRequest \
    --role arn:aws:iam::635067988515:role/lambda-dynamo-db
```