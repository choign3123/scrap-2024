# stop docker container
if [ "$(docker ps -q -f name=scrap2024dev)" ]; then
  echo "Stopping container: scrap2024dev"
  sudo docker stop scrap2024dev
else
  echo "Container scrap2024dev is not running."
fi

# remove docker container
if [ "$(docker ps -a -q -f name=scrap2024dev)" ]; then
    echo "컨테이너가 존재합니다. 삭제 중..."
    sudo docker rm -f scrap2024dev
    echo "컨테이너가 삭제되었습니다."
else
    echo "컨테이너가 존재하지 않습니다."
fi