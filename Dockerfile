# Este Dockerfile no es necesario ya la imagen se crea con Jib
FROM openjdk:8-jre-alpine

COPY target/hello-world-0.0.1-SNAPSHOT.jar /home/app/hello-world-0.0.1-SNAPSHOT.jar

WORKDIR /home/app/

EXPOSE 8080

CMD [ "java", "-jar", "hello-world-0.0.1-SNAPSHOT.jar" ]
