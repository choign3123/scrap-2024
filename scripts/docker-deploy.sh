#배포 그룹에 맞는 환경변수 설정
if [ "$DEPLOYMENT_GROUP_NAME" == "scrap-github-dev-group" ]; then
    echo "develop 환경입니다."
    export DB_URL=$(aws ssm get-parameters --names /Dev/Mysql/db_url --with-decryption --query Parameters[0].Value --output text)
    export DB_USERNAME=$(aws ssm get-parameters --names /Dev/Mysql/db_username --with-decryption --query Parameters[0].Value --output text)
    export DB_PASSWORD=$(aws ssm get-parameters --names /Dev/Mysql/db_password --with-decryption --query Parameters[0].Value --output text)
    export JWT_SECRET=$(aws ssm get-parameters --names /Dev/Jwt/jwt_secret_key --with-decryption --query Parameters[0].Value --output text)
    export EXPIRE_HOUR_OF_ACCESS=$(aws ssm get-parameters --names /Dev/Jwt/expire_hour_of_access --with-decryption --query Parameters[0].Value --output text)
    export EXPIRE_DAY_OF_REFRESH=$(aws ssm get-parameters --names /Dev/Jwt/expire_day_of_refresh --with-decryption --query Parameters[0].Value --output text)
    export TEST_TOKEN=$(aws ssm get-parameters --names /Dev/Jwt/test_token --with-decryption --query Parameters[0].Value --output text)
    export REDIS_HOST=$(aws ssm get-parameters --names /Dev/Redis/redis_host --with-decryption --query Parameters[0].Value --output text)
    export REDIS_PORT=$(aws ssm get-parameters --names /Dev/Redis/redis_port --with-decryption --query Parameters[0].Value --output text)
    export BASE_PORT=$(aws ssm get-parameters --names /Dev/port --with-decryption --query Parameters[0].Value --output text)
    export DOCKER_IMAGE_TAG=dev
    export SPRING_PROFILE=dev
    export DOCKER_CONTAINER_NAME=scrap2024dev
else
    echo "$DEPLOYMENT_GROUP_NAME: 알 수 없는 환경입니다. 실행을 종료합니다"
    exit 1
fi

sudo docker pull choign3123/scrap2024:$DOCKER_IMAGE_TAG

sudo docker run --name $DOCKER_CONTAINER_NAME \
-e REDIS_HOST=$REDIS_HOST \
-e REDIS_PORT=$REDIS_PORT \
-e DB_URL=$DB_URL \
-e DB_USERNAME=$DB_USERNAME \
-e DB_PASSWORD=$DB_PASSWORD \
-e JWT_SECRET=$JWT_SECRET \
-e EXPIRE_HOUR_OF_ACCESS=$EXPIRE_HOUR_OF_ACCESS \
-e EXPIRE_DAY_OF_REFRESH=$EXPIRE_DAY_OF_REFRESH \
-e TEST_TOKEN=$TEST_TOKEN \
-e BASE_PORT=$BASE_PORT \
-d -it -p $BASE_PORT:$BASE_PORT \
choign3123/scrap2024:$DOCKER_IMAGE_TAG \
--spring.profiles.active=$SPRING_PROFILE