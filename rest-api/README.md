```sh
  # dev run
  mvn -Dserver.port=1111 spring-boot:run
  
  # product run
  mvn package 
  java -Dserver.port=1111 -jar target/rest-api-0.0.1-SNAPSHOT.jar
  
  # test api
  curl -XGET localhost:1111/rest-v1/ping
  curl -XPOST -H "Contplication/json" -d '{"fileId":"fileId-1", "fileType":"base64", "baseCode": "data..."}' localhost:1111/rest-v1/preAuthorization
```
