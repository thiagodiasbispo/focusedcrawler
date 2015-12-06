#!/bin/sh

cd $1
java -cp news_crawler.jar focusedCrawler.query.BingSearchWithoutRelevance conf/stoplist.txt $2