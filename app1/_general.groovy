// app1_general.groovy

def loadCredentialsTests(filePath) {
    def prefix = 'dev';

    withCredentials([
            string(credentialsId: "$prefix-app1_dynamodb_region", variable: "app1_dynamodb_region"),
            string(credentialsId: "$prefix-app1_dynamodb_access_key", variable: "app1_dynamodb_access_key"),
            string(credentialsId: "$prefix-app1_dynamodb_secret_key", variable: "app1_dynamodb_secret_key")
    ]) {
        sh """
        sed -i -- 's/app1_dynamodb_region/${APP1_DYNAMODB_REGION.replace('/', '\\/').replace('$', '\\$').replace('&', '\\&')}/g' ${filePath}
        sed -i -- 's/app1_dynamodb_access_key/${APP1_DYNAMODB_ACCESS_KEY.replace('/', '\\/').replace('$', '\\$').replace('&', '\\&')}/g' ${filePath}
        sed -i -- 's/app1_dynamodb_secret_key/${APP1_DYNAMODB_SECRET_KEY.replace('/', '\\/').replace('$', '\\$').replace('&', '\\&')}/g' ${filePath}
    """
    }
}

def loadCredentials(prefix) {
    withCredentials([
            string(credentialsId: 'app1_pulumi_access_token', variable: 'pulumi_access_token'),

            string(credentialsId: "$prefix-app1_aws_access_key", variable: "app1_aws_access_key"),
            string(credentialsId: "$prefix-app1_aws_secret_key", variable: "app1_aws_secret_key"),
            string(credentialsId: "$prefix-app1_dynamodb_region", variable: "app1_dynamodb_region"),
            string(credentialsId: "$prefix-app1_dynamodb_access_key", variable: "app1_dynamodb_access_key"),
            string(credentialsId: "$prefix-app1_dynamodb_secret_key", variable: "app1_dynamodb_secret_key")
    ]) {
        env.PULUMI_ACCESS_TOKEN = pulumi_access_token
        env.AWS_ACCESS_KEY_ID = app1_aws_access_key
        env.AWS_SECRET_ACCESS_KEY = app1_aws_secret_key

        env.APP1_DYNAMODB_REGION = app1_dynamodb_region
        env.APP1_DYNAMODB_ACCESS_KEY = app1_dynamodb_access_key
        env.APP1_DYNAMODB_SECRET_KEY = app1_dynamodb_secret_key
    }
}

def replaceParams(filePath) {
    sh """
        sed -i -- 's/app1_dynamodb_region/${APP1_DYNAMODB_REGION.replace('/', '\\/').replace('$', '\\$').replace('&', '\\&')}/g' ${filePath}
        sed -i -- 's/app1_dynamodb_access_key/${APP1_DYNAMODB_ACCESS_KEY.replace('/', '\\/').replace('$', '\\$').replace('&', '\\&')}/g' ${filePath}
        sed -i -- 's/app1_dynamodb_secret_key/${APP1_DYNAMODB_SECRET_KEY.replace('/', '\\/').replace('$', '\\$').replace('&', '\\&')}/g' ${filePath}
        
        sed -i -- 's/null_to_clean//g' ${filePath}
    """
}

return this
