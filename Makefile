SHELL	= /bin/sh

NAME	= ticket-booking

# Parse .env file and fail if not found
-include .env

ifeq ($(wildcard .env),)
$(error .env file is required)
endif


# Trigger deployment by CI/CD pipeline (github actions)

update_repo:
	git pull origin main

deploy_frontend: update_repo build_frontend_prod
	docker compose -f docker-compose.prod.yml restart caddy

deploy_backend: update_repo
	docker compose -f docker-compose.prod.yml build
	docker compose -f docker-compose.prod.yml up -d
	docker image prune -f


# Produce common-library artifact and install into local repo
common_jar:
	cd backend/ticketing-common-library && mvn clean install -DskipTests

# Produce backend jar artifacts
build_jars:
	cd backend && mvn clean package -DskipTests


# Clean / build frontend

clean_frontend:
	rm -rf frontend/dist
	mkdir -p frontend/dist

build_frontend_local: clean_frontend
	cd frontend && npm ci && \
	VITE_API_BASE_URL="$(VITE_API_BASE_URL_LOCAL)" npm run build

build_frontend_prod: clean_frontend
	cd frontend && npm ci && \
	VITE_API_BASE_URL="$(VITE_API_BASE_URL_PROD)" npm run build

# Build environments

dev: common_jar
	docker compose -f docker-compose.dev.yml up --build

prod: build_frontend_prod build_jars
	docker compose -f docker-compose.prod.yml up --build

prod_detached: build_frontend_prod build_jars
	docker compose -f docker-compose.prod.yml up -d --build

local_prod: build_frontend_local build_jars
	docker compose -f docker-compose.prod.yml -f docker-compose.localprod.yml up --build

# Maintenance tasks

clean: clean_frontend
	cd backend && sudo mvn clean

down_dev:
	docker compose -f docker-compose.dev.yml down
stop_dev:
	docker compose -f docker-compose.dev.yml stop

down_prod:
	docker compose -f docker-compose.prod.yml down
stop_prod:
	docker compose -f docker-compose.prod.yml stop

down_prod_local:
	docker compose -f docker-compose.prod.yml -f docker-compose.localprod.yml down
stop_prod_local:
	docker compose -f docker-compose.prod.yml -f docker-compose.localprod.yml stop


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


.PHONY: _no_default dev prod prod_detached local_prod \
	down_dev stop_dev down_prod stop_prod down_prod_local stop_prod_local \
	common_jar build_jars \
	clean_frontend build_frontend_local build_frontend_prod \
	clean reset \
	mysql react \
	cart-service cart-service-redis-cli \
	update_repo deploy_frontend deploy_backend

.DEFAULT_GOAL := _no_default

_no_default:
	@echo "No default target. Please run 'make <target>'." >&2
	@exit 1