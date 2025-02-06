# 빌드 스테이지
FROM amazoncorretto:17
WORKDIR /app

# COPY jar file
COPY build/libs/app.jar app.jar

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]