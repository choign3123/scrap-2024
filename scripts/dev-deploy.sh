sudo docker pull choign3123/scrap2024:dev

echo "test print env:"
echo $DB_URL

sudo docker run --name scrap2024dev \
-e REDIS_HOST=$REDIS_HOST \
-e REDIS_PORT=$REDIS_PORT \
-e DB_URL=$DB_URL \
-e DB_USERNAME=$DB_USERNAME \
-e DB_PASSWORD=$DB_PASSWORD \
-e JWT_SECRET=$JWT_SECRET \
-e EXPIRE_HOUR_OF_ACCESS=$EXPIRE_HOUR_OF_ACCESS \
-e EXPIRE_DAY_OF_REFRESH=$EXPIRE_DAY_OF_REFRESH \
-e BASE_PORT=$BASE_PORT \
-d -it -p $BASE_PORT:$BASE_PORT \
choign3123/scrap2024:dev \
--spring.profiles.active=dev