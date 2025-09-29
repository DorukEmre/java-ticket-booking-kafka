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


restart_inventory_service:
	docker restart inventory-service

clean_inventory_service:
	docker exec -it inventory-service mvn clean

restart_booking_service:
	docker restart booking-service

clean_booking_service:
	docker exec -it booking-service mvn clean


clean_microservices:
	docker exec -it inventory-service mvn clean
	docker exec -it booking-service mvn clean


.PHONY: all down stop prune prune_system routine reset mysql react \
	create_volumes_dirs clean_microservices \
	restart_inventory_service clean_inventory_service \
	restart_booking_service clean_booking_service
