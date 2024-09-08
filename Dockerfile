# openjdk 이미지를 베이스로 한다.
FROM openjdk:17

EXPOSE 8090/tcp

COPY ./build/libs/*.jar ./app.jar

# 사용자가 이 이미지를 기본으로해 컨테이너를 실행시켰을 때 실행할 명령어
# ./app.jar는 github actions에서 만들어줌
ENTRYPOINT ["java","-jar","./app.jar"]