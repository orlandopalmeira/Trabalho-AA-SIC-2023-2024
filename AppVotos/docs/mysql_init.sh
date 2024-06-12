#!/bin/bash

# Variáveis
DB_NAME="votingapp"
DB_USER="root"
DB_PASS="mysql"


docker run -d \
   --name mysql \
   -e MYSQL_ROOT_PASSWORD=$DB_PASS \
   -p 3306:3306 \
   -v mysql_data:/var/lib/mysql \
   mysql

sleep 1
docker exec -it mysql mysql -p $DB_PASS -e "CREATE DATABASE $DB_NAME;"
#docker exec -it mysql mysql -u root -pmysql -e "CREATE DATABASE votingapp;"


