FROM ubuntu:18.04
RUN apt-get update
RUN apt-get install -y git && \
    apt-get install -y vim  && \
    apt-get install -y curl && \
    apt-get install -y xz-utils && \
    apt-get install -y byacc  && \
    apt-get install -y g++ && \
    apt-get install -y python2.7 && \
    apt-get install -y pkg-config && \
    apt-get install -y cmake && \
    apt-get install -y maven && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y m4 && \
    apt-get install -y pkg-config && \
    rm -rf /var/lib/apt/lists/*
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/
WORKDIR /opt
RUN git clone https://github.com/mirceanagy/Kvs-Recorder.git
WORKDIR /opt/kvs-recorder
# Start the application
COPY run.sh /opt/kvs-recorder/
RUN chmod a+x /opt/kvs-recorder/run.sh