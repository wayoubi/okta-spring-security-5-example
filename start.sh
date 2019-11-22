git pull

mv src/main/resources/application.yml src/main/resources/application.yml.dev

mv src/main/resources/application.yml.live src/main/resources/application.yml 

./mvnw compile spring-boot:run