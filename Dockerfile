# openjdk 이미지를 베이스로 한다.
FROM openjdk:17

COPY ./*.jar ./app.jar

# 사용자가 이 이미지를 기본으로해 컨테이너를 실행시켰을 때 실행할 명령어
ENTRYPOINT ["java","-jar","./app.jar", "--spring.profiles.active=dev"]