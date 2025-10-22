#!/bin/bash
set -e  # Exit immediately if any command exits with a non-zero status

# Run MySQL commands as root
# Create databases and users for microservices
mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE_CATALOG\`;
CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE_ORDER\`;

CREATE USER IF NOT EXISTS '$MYSQL_USER'@'%' IDENTIFIED BY '$MYSQL_PASSWORD';
CREATE USER IF NOT EXISTS '$MYSQL_USER'@'%' IDENTIFIED BY '$MYSQL_PASSWORD';

GRANT ALL PRIVILEGES ON \`$MYSQL_DATABASE_CATALOG\`.* TO '$MYSQL_USER'@'%';
GRANT ALL PRIVILEGES ON \`$MYSQL_DATABASE_ORDER\`.* TO '$MYSQL_USER'@'%';

FLUSH PRIVILEGES;
EOSQL
echo "Databases and users for microservices created."