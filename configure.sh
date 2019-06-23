#!/usr/bin/env bash

export HOST_FOR_SELENIUM="$(/sbin/ip route|awk '/default/ { print $3 }')"
