#!/bin/bash
set -e  # Exit immediately if any command exits with a non-zero status

# Run MySQL commands as root
# Create databases and users for catalog and booking services
mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE_CATALOG\`;
CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE_ORDER\`;

CREATE USER IF NOT EXISTS '$MYSQL_USER_CATALOG'@'%' IDENTIFIED BY '$MYSQL_PASSWORD_CATALOG';
CREATE USER IF NOT EXISTS '$MYSQL_USER_ORDER'@'%' IDENTIFIED BY '$MYSQL_PASSWORD_ORDER';

GRANT ALL PRIVILEGES ON \`$MYSQL_DATABASE_CATALOG\`.* TO '$MYSQL_USER_CATALOG'@'%';
GRANT ALL PRIVILEGES ON \`$MYSQL_DATABASE_ORDER\`.* TO '$MYSQL_USER_ORDER'@'%';

FLUSH PRIVILEGES;
EOSQL
echo "Databases and users for catalog and order services created."