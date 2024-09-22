#배포 그룹에 맞는 환경변수 설정
if [ "$DEPLOYMENT_GROUP_NAME" == "scrap-github-dev-group" ]; then
    echo "develop 환경입니다."
    export DOCKER_CONTAINER_NAME=scrap2024dev
elif [ "$DEPLOYMENT_GROUP_NAME" == "scrap-github-release-group" ]; then
    echo "release 환경입니다."
    export DOCKER_CONTAINER_NAME=scrap2024release
else
    echo "$DEPLOYMENT_GROUP_NAME: 알 수 없는 환경입니다. 실행을 종료합니다"
    exit 1
fi

# stop docker container
if [ "$(docker ps -q -f name=$DOCKER_CONTAINER_NAME)" ]; then
  echo "컨테이너를 종료합니다: $DOCKER_CONTAINER_NAME"
  sudo docker stop $DOCKER_CONTAINER_NAME
else
  echo "$DOCKER_CONTAINER_NAME: 실행중인 컨테이너가 아닙니다."
fi

# remove docker container
if [ "$(docker ps -a -q -f name=$DOCKER_CONTAINER_NAME)" ]; then
    echo "컨테이너가 존재합니다. 삭제 중..."
    sudo docker rm -f $DOCKER_CONTAINER_NAME
    echo "컨테이너가 삭제되었습니다."
else
    echo "컨테이너가 존재하지 않습니다."
fi