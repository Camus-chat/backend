# 빌드 스테이지
FROM gradle:8-jdk17 AS build
WORKDIR /app

# 소스 코드와 빌드 스크립트 복사
COPY build.gradle ./
COPY settings.gradle ./
COPY src ./src

# Gradle Wrapper 사용 없이 의존성을 다운로드하고, 애플리케이션을 빌드
RUN gradle build -x test --no-daemon

# 실행 스테이지
FROM openjdk:17
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 현재 디렉토리로 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션에 필요한 포트를 노출
EXPOSE 8080

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
