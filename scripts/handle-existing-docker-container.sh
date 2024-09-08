# stop docker container
if [ "$(docker ps -q -f name=scrap2024dev)" ]; then
  echo "Stopping container: scrap2024dev"
  sudo docker stop scrap2024dev
else
  echo "Container scrap2024dev is not running."
fi

# remove docker container
sudo docker rm scrap2024dev