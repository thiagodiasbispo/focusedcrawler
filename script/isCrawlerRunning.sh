#!/bin/sh
ps awwux | grep focusedCrawler | grep $1 | grep -v perp | grep -v grep | awk '{print $2}' | xargs 