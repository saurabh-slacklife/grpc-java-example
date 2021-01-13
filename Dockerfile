FROM nimmis/java-centos:oracle-8-jdk
MAINTAINER Saurabh Saxena, saurabh.slacklife@gmail.com

COPY target/grpc-trek-example-*-fatjar.jar /opt/saurabh-slacklife/grpc-trek-example.jar

EXPOSE 1313

# CMD should use "shell" form (without []) otherwise environment variables won't be expanded
CMD java -jar /opt/saurabh-slacklife/grpc-trek-example.jar
