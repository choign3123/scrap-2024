echo "test print parameter: $(aws ssm get-parameters --names /Dev/Redis/redis_port --with-decryption --query Parameters[0].Value)"

export DB_URL=$(aws ssm get-parameters --names /Dev/Mysql/db_url --with-decryption --query Parameters[0].Value)
#export DB_USERNAME=$(aws ssm get-parameters --names /Dev/Mysql/db_username --with-decryption --query Parameters[0].Value)
#export DB_PASSWORD =$(aws ssm get-parameters --names /Dev/Mysql/db_password --with-decryption --query Parameters[0].Value)
#export JWT_SECRET=$(aws ssm get-parameters --names /Dev/Jwt/jwt_secret_key --with-decryption --query Parameters[0].Value)
#export EXPIRE_HOUR_OF_ACCESS=$(aws ssm get-parameters --names /Dev/Jwt/expire_hour_of_access --with-decryption --query Parameters[0].Value)
#export EXPIRE_DAY_OF_REFRESH=$(aws ssm get-parameters --names /Dev/Jwt/expire_day_of_refresh --with-decryption --query Parameters[0].Value)
#export REDIS_HOST=$(aws ssm get-parameters --names /Dev/Redis/redis_host --with-decryption --query Parameters[0].Value)
#export REDIS_PORT=$(aws ssm get-parameters --names /Dev/Redis/redis_port --with-decryption --query Parameters[0].Value)