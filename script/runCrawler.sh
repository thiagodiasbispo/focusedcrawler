#!/bin/sh
java -Xmx2000M -cp $1/news_crawler.jar focusedCrawler.crawler.CrawlerManager $1/conf/crawler/crawler.cfg > $1/log/crawler.log 2>&1 &
