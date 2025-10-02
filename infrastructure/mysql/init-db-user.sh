#!/bin/bash
set -e  # Exit immediately if any command exits with a non-zero status

# Run MySQL commands as root
# Create databases and users for inventory and booking services
mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE_INVENTORY\`;
CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE_ORDER\`;

CREATE USER IF NOT EXISTS '$MYSQL_USER_INVENTORY'@'%' IDENTIFIED BY '$MYSQL_PASSWORD_INVENTORY';
CREATE USER IF NOT EXISTS '$MYSQL_USER_ORDER'@'%' IDENTIFIED BY '$MYSQL_PASSWORD_ORDER';

GRANT ALL PRIVILEGES ON \`$MYSQL_DATABASE_INVENTORY\`.* TO '$MYSQL_USER_INVENTORY'@'%';
GRANT ALL PRIVILEGES ON \`$MYSQL_DATABASE_ORDER\`.* TO '$MYSQL_USER_ORDER'@'%';

FLUSH PRIVILEGES;
EOSQL
echo "Databases and users for inventory and booking services created."