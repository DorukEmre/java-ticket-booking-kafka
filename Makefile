SHELL	= /bin/sh

NAME	= ticket-booking


all: create_volumes_dirs
	docker compose up --build

create_volumes_dirs: # creates volume directories if needed
	mkdir -p ./frontend/dist

down:
	docker compose down -v
stop:
	docker compose stop

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


mysql:
	docker exec -it mysql sh \
		-c "mysql -uroot -p\"\$$MYSQL_ROOT_PASSWORD\" -D\$$MYSQL_DATABASE"

react:
	docker exec -it react sh 

cart_service:
	docker exec -it cart-service sh
cart_service_redis_cli:
	docker exec -it cart-service redis-cli -h redis -p 6379


restart_catalog_service:
	docker restart catalog-service

restart_cart_service:
	docker restart cart-service

restart_order_service:
	docker restart order-service

restart_gatewayapi:
	docker restart gatewayapi


.PHONY: all down stop prune prune_system routine reset mysql react \
	create_volumes_dirs \
	restart_catalog_service restart_cart_service \
	restart_order_service restart_gatewayapi \
	cart-service cart-service-redis-cli

.DEFAULT_GOAL := all
