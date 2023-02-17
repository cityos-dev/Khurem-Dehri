FROM amazoncorretto:17-alpine
MAINTAINER khurem dehri
COPY build/libs/woven-videoserver-1.0.0.jar woven-videoserver-1.0.0.jar
ENTRYPOINT ["java","-jar","/woven-videoserver-1.0.0.jar"]
ENV JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"
EXPOSE 8080