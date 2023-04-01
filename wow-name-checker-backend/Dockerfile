FROM eclipse-temurin:17-jdk-focal as base
WORKDIR app/
RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y dos2unix
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN dos2unix mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src

FROM base as development
CMD ["sh", "run.sh"]

FROM base as build
RUN ./mvnw package

FROM eclipse-temurin:17-jdk-focal as production
EXPOSE 8080
COPY --from=build /app/target/*.jar /app.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
