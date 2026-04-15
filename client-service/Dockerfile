FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY client-service/pom.xml client-service/pom.xml
COPY room-service/pom.xml room-service/pom.xml
COPY reservation-service/pom.xml reservation-service/pom.xml

RUN mvn -B -ntp -pl client-service -am dependency:go-offline

COPY client-service/src client-service/src

RUN mvn -B -ntp -pl client-service -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/client-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
