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
routine:
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

inventoryService_restart:
	docker restart inventoryService


.PHONY: all down stop prune routine reset mysql react inventoryService_restart

