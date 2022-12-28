#!/usr/bin/env bash

docker build -t prometheus .
docker stop prometheus
docker rm prometheus
docker run -d --name=prometheus -p 9090:9090 -v //data/prometheus://prometheus prometheus

sleep 5
docker logs prometheus
docker ps
