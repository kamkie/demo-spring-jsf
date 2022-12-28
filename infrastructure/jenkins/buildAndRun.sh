#!/usr/bin/env bash

docker build -t jenkins .
docker stop jenkins
docker rm jenkins
docker run -d --name=jenkins -p 8180:8080 0p 5000:5000 jenkins

sleep 5
docker logs jenkins
docker ps
