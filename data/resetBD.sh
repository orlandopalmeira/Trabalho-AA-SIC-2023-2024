#!/bin/bash

# Variables
DB_CONTAINER_NAME="mysql"
DB_USER="mysql"
DB_PASSWORD="mysql"
DB_NAME="mysql"

# Delete the database
docker exec -i $DB_CONTAINER_NAME mysql -p$DB_PASSWORD -e "DROP DATABASE IF EXISTS $DB_NAME;"
echo "Database $DB_NAME has been deleted (if it existed)."

docker exec -i $DB_CONTAINER_NAME mysql -p$DB_PASSWORD -e "CREATE DATABASE $DB_NAME;"
echo "Database $DB_NAME has been created."
