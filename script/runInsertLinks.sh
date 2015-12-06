#!/bin/sh
java -cp $1/news_crawler.jar focusedCrawler.link.frontier.AddSeeds $1/conf/link_storage/link_storage.cfg
