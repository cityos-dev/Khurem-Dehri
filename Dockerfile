FROM amazoncorretto:17-alpine
MAINTAINER khurem dehri
COPY build/libs/woven-videoserver-1.0.0.jar woven-videoserver-1.0.0.jar
ENTRYPOINT ["java","-jar","/woven-videoserver-1.0.0.jar"]