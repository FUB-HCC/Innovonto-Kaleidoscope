FROM openjdk:14-alpine

COPY target/uberjar/kaleidoscope.jar /kaleidoscope/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/kaleidoscope/app.jar"]


