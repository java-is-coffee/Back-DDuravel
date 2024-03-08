# 빌드 시점에 사용할 환경 변수 ARG 정의
ARG JWT_SECRET_KEY

# gradle:7.3.1-jdk17 이미지를 기반으로 함
FROM openjdk:17

# 작업 디렉토리 설정
WORKDIR /home/gradle/project

# Spring 소스 코드를 이미지에 복사
COPY . .

# 런타임에 사용할 환경 변수 ENV 설정
ENV JWT_SECRET_KEY=$JWT_SECRET_KEY
RUN echo "JWT_SECRET_KEY=${JWT_SECRET_KEY}"

# gradle.properties 파일이 위치할 디렉토리를 생성
RUN mkdir -p /root/.gradle
# gradle 빌드 시 proxy 설정을 gradle.properties에 추가
RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

# gradlew를 이용한 프로젝트 필드
RUN chmod +x ./gradlew && ./gradlew clean build

# DATABASE_URL을 환경 변수로 삽입
ENV DATABASE_URL=jdbc:mysql://mysql/krampoline

# 빌드 결과 jar 파일을 실행
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/home/gradle/project/build/libs/kakao-1.0.jar"]
