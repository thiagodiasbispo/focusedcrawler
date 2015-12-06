#!/bin/sh
java -Xmx2000M -cp $1/news_crawler.jar  focusedCrawler.target.TargetStorage $1/conf/target_storage/target_storage.cfg > $1/log/target_storage.log 2>&1 &

