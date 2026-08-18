FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S tptc && adduser -S tptc -G tptc
COPY --from=build /app/target/*.jar app.jar
USER tptc
EXPOSE 8088
ENTRYPOINT ["java","-jar","/app/app.jar"]
