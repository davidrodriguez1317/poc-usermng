# POC for User Management interacting with Keycloak and RabbitMQ


### Creating the image with spring-boot plugin (root directory with pom.xml)
mvn spring-boot:build-image

### Launching the environment with a specific name (docker directory)
docker compose -p usermng-environment up