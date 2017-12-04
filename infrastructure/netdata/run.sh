#!/usr/bin/env bash

docker build -t netdata .
docker stop netdata
docker rm netdata
docker run -d \
    --name netdata \
    --privileged \
    --cap-add SYS_PTRACE \
    -v /proc:/host/proc:ro \
    -v /sys:/host/sys:ro \
    -p 19999:19999 \
    firehol/netdata

sleep 5
docker logs netdata
docker ps
