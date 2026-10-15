FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY App.java .

RUN javac --add-modules jdk.httpserver App.java

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/App.class .

EXPOSE 8082

CMD ["java", "--add-modules", "jdk.httpserver", "App"]