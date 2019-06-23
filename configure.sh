#!/usr/bin/env bash


ip addr show docker0
#HOST_FOR_SELENIUM="$(/sbin/ip route|awk '/default/ { print $3 }')"
HOST_FOR_SELENIUM="172.17.0.1"
export HOST_FOR_SELENIUM
echo "$HOST_FOR_SELENIUM"
