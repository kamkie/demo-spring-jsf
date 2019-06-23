#!/usr/bin/env bash

HOST_FOR_SELENIUM="$(/sbin/ip route|awk '/default/ { print $3 }')"
export HOST_FOR_SELENIUM
echo "$HOST_FOR_SELENIUM"
