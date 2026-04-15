FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY client-service/pom.xml client-service/pom.xml
COPY room-service/pom.xml room-service/pom.xml
COPY reservation-service/pom.xml reservation-service/pom.xml

RUN mvn -B -ntp -pl room-service -am dependency:go-offline

COPY room-service/src room-service/src

RUN mvn -B -ntp -pl room-service -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/room-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
