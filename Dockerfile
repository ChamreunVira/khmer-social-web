FROM eclipse-temurin:21-alpine AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build -x test


FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]