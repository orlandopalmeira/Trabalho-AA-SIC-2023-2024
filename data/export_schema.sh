#!/bin/bash

# Export schema from MySQL database
docker exec -i mysql mysqldump -h localhost -u root -pmysql --no-data votingapp > schema.sql
