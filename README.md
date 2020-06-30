# Kvs-Recorder

## Deploy to AWS
- Create the KVS steam in US-WEST-2
- Created an EC2 instance with docker
- Running the KVS-Recorder in Docker
  - wget https://raw.githubusercontent.com/mirceanagy/Kvs-Recorder/master/docker/Dockerfile
  - sudo docker build -t kvsrecorder .
  - sudo docker run -p 80:8080 -it kvsrecorder bash
  - chmod a+x docker/run.sh
  - ./docker/run.sh <ACCESS_KEY> <SECRET_KEY> <STREAM>
- Expose the 2 application APIs in API GW (ALL http://<ec2_ip>/ and POST http://<ec2_ip>/upload)
## Run locally
- Install ffmpeg locally
- Open the KVS-Recorder pom.xml
- Run the main application with VM options: -Daws.accessKeyId="..." -Daws.secretKey="..." -DKVS_STREAM="..." -DFFMPEG_BIN="C:/dev/ffmpeg-20200628-4cfcfb3-win64-static/bin"
