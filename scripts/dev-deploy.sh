sudo docker pull choign3123/scrap2024:dev

export DB_URL=$(aws ssm get-parameters --names /Dev/Mysql/db_url --with-decryption --query Parameters[0].Value --output text)
export DB_USERNAME=$(aws ssm get-parameters --names /Dev/Mysql/db_username --with-decryption --query Parameters[0].Value --output text)
export DB_PASSWORD=$(aws ssm get-parameters --names /Dev/Mysql/db_password --with-decryption --query Parameters[0].Value --output text)
export JWT_SECRET=$(aws ssm get-parameters --names /Dev/Jwt/jwt_secret_key --with-decryption --query Parameters[0].Value --output text)
export EXPIRE_HOUR_OF_ACCESS=$(aws ssm get-parameters --names /Dev/Jwt/expire_hour_of_access --with-decryption --query Parameters[0].Value --output text)
export EXPIRE_DAY_OF_REFRESH=$(aws ssm get-parameters --names /Dev/Jwt/expire_day_of_refresh --with-decryption --query Parameters[0].Value --output text)
export REDIS_HOST=$(aws ssm get-parameters --names /Dev/Redis/redis_host --with-decryption --query Parameters[0].Value --output text)
export REDIS_PORT=$(aws ssm get-parameters --names /Dev/Redis/redis_port --with-decryption --query Parameters[0].Value --output text)

sudo docker run --name scrap2024dev \
-e REDIS_HOST=$REDIS_HOST \
-e REDIS_PORT=$REDIS_PORT \
-e DB_URL=$DB_URL \
-e DB_USERNAME=$DB_USERNAME \
-e DB_PASSWORD=$DB_PASSWORD \
-e JWT_SECRET=$JWT_SECRET \
-e EXPIRE_HOUR_OF_ACCESS=$EXPIRE_HOUR_OF_ACCESS \
-e EXPIRE_DAY_OF_REFRESH=$EXPIRE_DAY_OF_REFRESH \
choign3123/scrap2024:dev