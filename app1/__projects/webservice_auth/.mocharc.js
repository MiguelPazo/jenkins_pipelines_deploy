'use strict';

process.env.NODE_ENV = 'local';
process.env.APP_ENV = 'local';
process.env.NODE_PORT = '3030';
process.env.BASE_URL = 'http://localhost:3030';
process.env.CORS_ALLOW_ORIGIN = '.*(app.com)$';
process.env.APP_ACCESS_TOKEN = 'app1_access_token';
process.env.SWAGGER_API_URL = 'http://localhost:3031';
process.env.SWAGGER_ACCESS_TOKEN = 'app1_swagger_access_token';
process.env.DYNAMODB_REGION = 'app1_dynamodb_region';
process.env.DYNAMODB_ACCESS_KEY = 'app1_dynamodb_access_key';
process.env.DYNAMODB_SECRET_KEY = 'app1_dynamodb_secret_key';
process.env.MONGODB_CONNECTOR = 'atlas';
process.env.MONGODB_HOST_READER = 'app1_mongodb_host_reader';
process.env.MONGODB_HOST_WRITER = 'app1_mongodb_host_writer';
process.env.MONGODB_USERNAME = 'app1_mongodb_username';
process.env.MONGODB_PASSWORD = 'app1_mongodb_password';
process.env.MONGODB_DATABASE = 'app1_mongodb_database';
process.env.MYSQL_HOST_READER = 'app1_mysql_host_reader';
process.env.MYSQL_HOST_WRITER = 'app1_mysql_host_writer';
process.env.MYSQL_USERNAME = 'app1_mysql_username';
process.env.MYSQL_PASSWORD = 'app1_mysql_password';
process.env.MYSQL_DATABASE = 'app1_mysql_database';
process.env.REDIS_HOST_READER = 'app1_redis_host_reader';
process.env.REDIS_HOST_WRITER = 'app1_redis_host_writer';
process.env.REDIS_PASSWORD = 'app1_redis_password';
process.env.SQS_REGION = 'app1_sqs_region';
process.env.SQS_ACCESS_KEY = 'app1_sqs_access_key';
process.env.SQS_SECRET_KEY = 'app1_sqs_secret_key';

module.exports = {
    timeout: false,
    require: [
        "ts-node/register",
        "tests/_genericBefore.ts"
    ],
    exit: true
};
