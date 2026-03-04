#!/bin/bash

set -e

echo "Waiting for Kafka broker at $KAFKA_BROKER..."

MAX_RETRIES=30
RETRY_COUNT=0

while ! kafka-broker-api-versions --bootstrap-server "$KAFKA_BROKER" > /dev/null 2>&1; do
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -ge $MAX_RETRIES ]; then
        echo "Timeout waiting for Kafka broker"
        exit 1
    fi
    sleep 2
done

TOPICS=(
    "inventory-reservation-failed"
    "inventory-reservation-invalid"
    "inventory-reservation-succeeded"
    "order-cancelled"
    "order-failed"
    "order-invalid"
    "order-requested"
    "order-succeeded"
    "release-inventory"
    "reserve-inventory"
)

for name in "${TOPICS[@]}"; do
    kafka-topics --create \
        --bootstrap-server "$KAFKA_BROKER" \
        --replication-factor 1 \
        --partitions 1 \
        --topic "$name" \
        --if-not-exists
done

echo "Topics created successfully"