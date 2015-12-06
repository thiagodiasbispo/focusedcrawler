#!/bin/sh
cd $1;

script/stopSystem.sh
script/runCleanDirs.sh $1
script/runCleanDirs.sh $1
script/runConfigHomePath.sh $1
script/runInsertLinks.sh $1
script/executar_link_storage.sh $1
script/executar_target_storage.sh $1
script/runCrawler.sh $1

