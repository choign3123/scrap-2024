sudo docker pull choign3123/scrap2024:dev

export DB_URL=$(aws ssm get-parameters --names /Dev/Mysql/db_url --with-decryption --query Parameters[0].Value)
export DB_USERNAME=$(aws ssm get-parameters --names /Dev/Mysql/db_username --with-decryption --query Parameters[0].Value)
export DB_PASSWORD=$(aws ssm get-parameters --names /Dev/Mysql/db_password --with-decryption --query Parameters[0].Value)
export JWT_SECRET=$(aws ssm get-parameters --names /Dev/Jwt/jwt_secret_key --with-decryption --query Parameters[0].Value)
export EXPIRE_HOUR_OF_ACCESS=$(aws ssm get-parameters --names /Dev/Jwt/expire_hour_of_access --with-decryption --query Parameters[0].Value)
export EXPIRE_DAY_OF_REFRESH=$(aws ssm get-parameters --names /Dev/Jwt/expire_day_of_refresh --with-decryption --query Parameters[0].Value)
export REDIS_HOST=$(aws ssm get-parameters --names /Dev/Redis/redis_host --with-decryption --query Parameters[0].Value)
export REDIS_PORT=$(aws ssm get-parameters --names /Dev/Redis/redis_port --with-decryption --query Parameters[0].Value)

echo "test env print at next line:"
echo $DEV_DB_USERNAME
printenv

exit 1;

#sudo docker run --name scrap2024dev \
#-e DEV_REDIS_HOST=$DEV_REDIS_HOST \
#-e DEV_REDIS_PORT=$DEV_REDIS_PORT \
#-e DEV_DB_URL=$DEV_DB_URL \
#-e DEV_DB_USERNAME=$DEV_DB_USERNAME \
#-e DEV_DB_PASSWORD=$DEV_DB_PASSWORD \
#-e JWT_SECRET=$JWT_SECRET \
#-e EXPIRE_HOUR_OF_ACCESS=$EXPIRE_HOUR_OF_ACCESS \
#-e EXPIRE_DAY_OF_REFRESH=$EXPIRE_DAY_OF_REFRESH \
#choign3123/scrap2024:dev