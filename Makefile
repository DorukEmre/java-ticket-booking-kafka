SHELL	= /bin/sh

NAME	= ticket-booking


# Produce common-library artifact and install into local repo
common_jar:
	cd backend/ticketing-common-library && mvn clean install -DskipTests

# Produce backend jar artifacts
build_jars:
	cd backend && mvn clean package -DskipTests

build_frontend:
	rm -rf frontend/dist
	mkdir -p frontend/dist
	cd frontend && npm install && \
	VITE_API_BASE_URL=https://localhost:443 npm run build


dev: common_jar
	docker compose -f docker-compose.dev.yml up --build

prod: build_frontend build_jars
	docker compose -f docker-compose.prod.yml up --build

prod_detached: build_frontend build_jars
	docker compose -f docker-compose.prod.yml up -d --build


clean:
	cd backend && sudo mvn clean

down_dev:
	docker compose -f docker-compose.dev.yml down
stop_dev:
	docker compose -f docker-compose.dev.yml stop

down_prod:
	docker compose -f docker-compose.prod.yml down
stop_prod:
	docker compose -f docker-compose.prod.yml stop


reset:
	docker stop $$(docker ps -qa); \
	docker rm $$(docker ps -qa); \
	docker rmi -f $$(docker images -qa); \
	docker volume rm $$(docker volume ls -q); \
	docker network rm $$(docker network ls -q) 2>/dev/null


mysql:
	docker exec -it mysql sh \
		-c "mysql -uroot -p\"\$$MYSQL_ROOT_PASSWORD\" -D\$$MYSQL_DATABASE"

react:
	docker exec -it react sh 

cart_service:
	docker exec -it cart-service sh

cart_service_redis_cli:
	docker exec -it cart-service redis-cli -h redis -p 6379


.PHONY: dev prod prod_detached \
	down_dev stop_dev down_prod stop_prod \
	common_jar build_jars build_frontend \
	clean reset \
	mysql react \
	cart-service cart-service-redis-cli

.DEFAULT_GOAL := dev
