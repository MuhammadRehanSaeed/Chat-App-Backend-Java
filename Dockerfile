FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/spring-boot-app.jar spring-boot-app.jar
EXPOSE 8080
CMD ["java", "-jar", "spring-boot-app.jar"]
