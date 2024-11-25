FROM openjdk:17-jdk-slim
WORKDIR tedtalks-app
COPY target/tedtalks-app-*.jar tedtalks-app.jar
ENTRYPOINT ["java", "-jar", "tedtalks-app.jar"]