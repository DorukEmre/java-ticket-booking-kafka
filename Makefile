SHELL	= /bin/sh

NAME	= ticket-booking


common_jar:
	cd backend/ticketing-common-library && mvn clean install -DskipTests

backend_install: ## install all backend modules to local repo
	cd backend && mvn -U clean install -DskipTests

build_jar:
	cd backend && mvn clean package -DskipTests

dev: common_jar # or backend_install
	docker compose -f docker-compose.dev.yml up --build

prod: create_volumes_dirs build_jar
	docker compose -f docker-compose.prod.yml up --build

create_volumes_dirs: # creates volume directories if needed
	mkdir -p ./frontend/dist


clean:
	cd backend && sudo mvn clean

down_dev:
	docker compose -f docker-compose.dev.yml down -v
stop_dev:
	docker compose -f docker-compose.dev.yml stop

down_prod:
	docker compose -f docker-compose.dev.yml down -v
stop_prod:
	docker compose -f docker-compose.prod.yml stop

prune:
	docker image prune
prune_system:
	docker system prune -a
reset:
	docker stop $$(docker ps -qa); \
	docker rm $$(docker ps -qa); \
	docker rmi -f $$(docker images -qa); \
	docker volume rm $$(docker volume ls -q); \
	docker network rm $$(docker network ls -q) 2>/dev/null


# mysql:
# 	docker exec -it mysql sh \
# 		-c "mysql -uroot -p\"\$$MYSQL_ROOT_PASSWORD\" -D\$$MYSQL_DATABASE"

react:
	docker exec -it react sh 

cart_service:
	docker exec -it cart-service sh

cart_service_redis_cli:
	docker exec -it cart-service redis-cli -h redis -p 6379


.PHONY: dev prod down_dev stop_dev down_prod stop_prod \
	prune prune_system reset \
	create_volumes_dirs common_jar build_jar backend_install \
	react \
	cart-service cart-service-redis-cli

.DEFAULT_GOAL := dev
