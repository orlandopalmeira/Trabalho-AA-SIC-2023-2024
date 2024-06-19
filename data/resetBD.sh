#!/bin/bash

# Variables
DB_CONTAINER_NAME="mysql"
DB_CONTAINER_NAME="mysqldockercompose"
DB_USER="mysql"
DB_PASSWORD="mysql"
DB_NAME="votingapp"

# docker exec -it mysql mysql -pmysql

docker exec -it $DB_CONTAINER_NAME mysql -p$DB_PASSWORD -e "DROP DATABASE IF EXISTS $DB_NAME;" && \
echo "Database $DB_NAME has been deleted (if it existed)."

docker exec -it $DB_CONTAINER_NAME mysql -p$DB_PASSWORD -e "CREATE DATABASE $DB_NAME;" && \
echo "Database $DB_NAME has been created."
