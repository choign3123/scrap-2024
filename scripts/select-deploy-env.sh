# GitHub Actions에서 전달한 ENVIRONMENT 값 확인
if [ "$DEPLOYMENT_GROUP_NAME" == "scrap-github-dev-group" ]; then
    echo "develop 환경입니다. dev 스크립트를 실행합니다."
    sh scripts/dev-deploy.sh
else
    echo "$DEPLOYMENT_GROUP_NAME: 알 수 없는 환경입니다. 실행을 종료합니다"
    exit 1
fi
