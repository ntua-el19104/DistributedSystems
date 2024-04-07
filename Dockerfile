FROM eclipse-temurin:17-jdk-alpine
COPY src/main/java/gr/ntua/input /input
COPY  target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]