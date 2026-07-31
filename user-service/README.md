Start docker for pg:

docker run \
--name postgres-db \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=password \
-e POSTGRES_DB=mydb \
-v postgres-data:/var/lib/postgresql/data \
-p 5432:5432 \
-d postgres:17

Start kafka:
docker run -p 9092:9092 apache/kafka:4.0.0

Start keycloak:

docker run -p 127.0.0.1:8181:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.6.4 start-dev

Todo:

- create new activity api
- create analysis api
- add validations
- get user recommendations api
- get activity recommendations api