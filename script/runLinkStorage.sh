#!/bin/sh
java -Xmx2000M -cp $1/news_crawler.jar focusedCrawler.link.LinkStorage $1/conf/link_storage/link_storage.cfg > $1/log/link_storage.log 2>&1 &


