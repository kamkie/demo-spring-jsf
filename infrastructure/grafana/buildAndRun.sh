#!/usr/bin/env bash

docker build -t grafana .
docker stop grafana
docker rm grafana
docker run -d --name=grafana -p 3000:3000 -v /c/env/grafana:/var/lib/grafana grafana

sleep 5
docker logs grafana
docker ps
