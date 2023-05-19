FROM bellsoft/liberica-openjdk-alpine:17.0.7-7 as base
WORKDIR app/
RUN apk update
RUN apk add dos2unix
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN dos2unix mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src

FROM base as development
CMD ["sh", "run.sh"]

FROM base as build
RUN ./mvnw package
# Copy jacoco report to github workspace file
COPY target/site/jacoco/jacoco.csv /github/workspace

FROM bellsoft/liberica-openjdk-alpine:17.0.7-7 as production
EXPOSE 8080
COPY --from=build /app/target/*.jar /app.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
