def loadCredentials(prefix) {
    withCredentials([
            string(credentialsId: "$prefix-app1_aws_access_key", variable: "app1_aws_access_key"),
            string(credentialsId: "$prefix-app1_aws_secret_key", variable: "app1_aws_secret_key"),
            string(credentialsId: "$prefix-app1_pulumi_access_token", variable: "app1_pulumi_access_token"),
//            string(credentialsId: "$prefix-app1_frontend_auth_url", variable: "app1_frontend_auth_url"),
//            string(credentialsId: "$prefix-app1_webservice_auth_host", variable: "app1_webservice_auth_host"),
//            string(credentialsId: "$prefix-app1_recaptcha_siteid", variable: "app1_recaptcha_siteid"),
//            string(credentialsId: "$prefix-app1_analytics_id", variable: "app1_analytics_id"),
//            string(credentialsId: "$prefix-app1_access_token", variable: "app1_access_token"),
//            string(credentialsId: "$prefix-app1_swagger_access_token", variable: "app1_swagger_access_token"),
//            string(credentialsId: "$prefix-app1_encryption_key_general", variable: "app1_encryption_key_general"),
//            string(credentialsId: "$prefix-app1_auth_secret", variable: "app1_auth_secret"),
//            string(credentialsId: "$prefix-app1_dynamodb_region", variable: "app1_dynamodb_region"),
//            string(credentialsId: "$prefix-app1_dynamodb_access_key", variable: "app1_dynamodb_access_key"),
//            string(credentialsId: "$prefix-app1_dynamodb_secret_key", variable: "app1_dynamodb_secret_key"),
//            string(credentialsId: "$prefix-app1_mongodb_host_reader", variable: "app1_mongodb_host_reader"),
//            string(credentialsId: "$prefix-app1_mongodb_host_writer", variable: "app1_mongodb_host_writer"),
//            string(credentialsId: "$prefix-app1_mongodb_username", variable: "app1_mongodb_username"),
//            string(credentialsId: "$prefix-app1_mongodb_password", variable: "app1_mongodb_password"),
//            string(credentialsId: "$prefix-app1_mongodb_database", variable: "app1_mongodb_database"),
//            string(credentialsId: "$prefix-app1_mysql_host_reader", variable: "app1_mysql_host_reader"),
//            string(credentialsId: "$prefix-app1_mysql_host_writer", variable: "app1_mysql_host_writer"),
//            string(credentialsId: "$prefix-app1_mysql_username", variable: "app1_mysql_username"),
//            string(credentialsId: "$prefix-app1_mysql_password", variable: "app1_mysql_password"),
//            string(credentialsId: "$prefix-app1_mysql_database", variable: "app1_mysql_database"),
//            string(credentialsId: "$prefix-app1_redis_host_reader", variable: "app1_redis_host_reader"),
//            string(credentialsId: "$prefix-app1_redis_host_writer", variable: "app1_redis_host_writer"),
//            string(credentialsId: "$prefix-app1_redis_password", variable: "app1_redis_password"),
//            string(credentialsId: "$prefix-app1_sqs_region", variable: "app1_sqs_region"),
//            string(credentialsId: "$prefix-app1_sqs_access_key", variable: "app1_sqs_access_key"),
//            string(credentialsId: "$prefix-app1_sqs_secret_key", variable: "app1_sqs_secret_key"),
            string(credentialsId: "$prefix-app1_server1_host", variable: "app1_server1_host")
    ]) {
        env.AWS_ACCESS_KEY_ID = app1_aws_access_key
        env.AWS_SECRET_ACCESS_KEY = app1_aws_secret_key
        env.PULUMI_ACCESS_TOKEN = app1_pulumi_access_token

//        env.APP1_FRONTEND_AUTH_URL = app1_frontend_auth_url
//        env.APP1_WEBSERVICE_AUTH_HOST = app1_webservice_auth_host
//        env.APP1_RECAPTCHA_SITEID = app1_recaptcha_siteid
//        env.APP1_ANALYTICS_ID = app1_analytics_id
//        env.APP1_ACCESS_TOKEN = app1_access_token
//        env.APP1_SWAGGER_ACCESS_TOKEN = app1_swagger_access_token
//        env.APP1_ENCRYPTION_KEY_GENERAL = app1_encryption_key_general
//        env.APP1_AUTH_SECRET = app1_auth_secret
//        env.APP1_DYNAMODB_REGION = app1_dynamodb_region
//        env.APP1_DYNAMODB_ACCESS_KEY = app1_dynamodb_access_key
//        env.APP1_DYNAMODB_SECRET_KEY = app1_dynamodb_secret_key
//        env.APP1_MONGODB_HOST_READER = app1_mongodb_host_reader
//        env.APP1_MONGODB_HOST_WRITER = app1_mongodb_host_writer
//        env.APP1_MONGODB_USERNAME = app1_mongodb_username
//        env.APP1_MONGODB_PASSWORD = app1_mongodb_password
//        env.APP1_MONGODB_DATABASE = app1_mongodb_database
//        env.APP1_MYSQL_HOST_READER = app1_mysql_host_reader
//        env.APP1_MYSQL_HOST_WRITER = app1_mysql_host_writer
//        env.APP1_MYSQL_USERNAME = app1_mysql_username
//        env.APP1_MYSQL_PASSWORD = app1_mysql_password
//        env.APP1_MYSQL_DATABASE = app1_mysql_database
//        env.APP1_REDIS_HOST_READER = app1_redis_host_reader
//        env.APP1_REDIS_HOST_WRITER = app1_redis_host_writer
//        env.APP1_REDIS_PASSWORD = app1_redis_password
//        env.APP1_SQS_REGION = app1_sqs_region
//        env.APP1_SQS_ACCESS_KEY = app1_sqs_access_key
//        env.APP1_SQS_SECRET_KEY = app1_sqs_secret_key
        env.APP1_SERVER1_HOST = app1_server1_host
    }
}

def replaceParams(filePath) {
    sh "sed -i -- 's/null_to_clean//g' ${filePath}"
//    sh """
//        sed -i -- 's/app1_frontend_auth_url/${APP1_FRONTEND_AUTH_URL.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_webservice_auth_host/${APP1_WEBSERVICE_AUTH_HOST.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_recaptcha_siteid/${APP1_RECAPTCHA_SITEID.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_analytics_id/${APP1_ANALYTICS_ID.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_access_token/${APP1_ACCESS_TOKEN.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_swagger_access_token/${APP1_SWAGGER_ACCESS_TOKEN.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_encryption_key_general/${APP1_ENCRYPTION_KEY_GENERAL.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_auth_secret/${APP1_AUTH_SECRET.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_dynamodb_region/${APP1_DYNAMODB_REGION.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_dynamodb_access_key/${APP1_DYNAMODB_ACCESS_KEY.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_dynamodb_secret_key/${APP1_DYNAMODB_SECRET_KEY.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mongodb_host_reader/${APP1_MONGODB_HOST_READER.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mongodb_host_writer/${APP1_MONGODB_HOST_WRITER.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mongodb_username/${APP1_MONGODB_USERNAME.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mongodb_password/${APP1_MONGODB_PASSWORD.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mongodb_database/${APP1_MONGODB_DATABASE.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mysql_host_reader/${APP1_MYSQL_HOST_READER.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mysql_host_writer/${APP1_MYSQL_HOST_WRITER.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mysql_username/${APP1_MYSQL_USERNAME.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mysql_password/${APP1_MYSQL_PASSWORD.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_mysql_database/${APP1_MYSQL_DATABASE.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_redis_host_reader/${APP1_REDIS_HOST_READER.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_redis_host_writer/${APP1_REDIS_HOST_WRITER.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_redis_password/${APP1_REDIS_PASSWORD.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_sqs_region/${APP1_SQS_REGION.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_sqs_access_key/${APP1_SQS_ACCESS_KEY.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_sqs_secret_key/${APP1_SQS_SECRET_KEY.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//        sed -i -- 's/app1_server1_host/${APP1_SERVER1_HOST.replace('/', '\\/').replace('\$', '\\\$').replace('&', '\\&')}/g' ${filePath}
//
//        sed -i -- 's/null_to_clean//g' ${filePath}
//    """
}

return this
