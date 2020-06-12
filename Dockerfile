FROM openjdk:14-alpine

COPY target/uberjar/kaleidoscope.jar /kaleidoscope/app.jar
# TODO config? java -Dconf=foo.edn -jar target\uberjar\kaleidoscope.jar

EXPOSE 3000

CMD ["java", "-jar", "/kaleidoscope/app.jar"]


