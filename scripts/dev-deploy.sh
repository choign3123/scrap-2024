# 환경변수 설정

sudo docker pull choign3123/scrap2024:dev

if [ "$(docker ps -q -f name=scrap2024dev)" ]; then
  echo "Stopping container: scrap2024dev"
  sudo docker stop scrap2024dev
else
  echo "Container scrap2024dev is not running."
fi

sudo docker rm scrap2024dev

sudo docker run --name scrap2024dev \
choign3123/scrap2024:dev