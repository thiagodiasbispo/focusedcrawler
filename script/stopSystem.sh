#!/bin/sh

kill -9  `ps awux | grep CrawlerManager | grep $1 | grep -v perp | grep -v "grep" | awk '{print $2}' | xargs`
kill -9  `ps awux | grep Storage | grep -v perp | grep $1 | grep -v "grep" | awk '{print $2}' | xargs`

sleep 1