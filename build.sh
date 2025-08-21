cd ./API-Gateway
mvn clean package -DskipTests
docker build -t kimleangk/api-gateway:latest .
docker push kimleangk/api-gateway:latest
cd ../Product-Service
mvn clean package -DskipTests
docker build -t kimleangk/product-service:latest .
docker push kimleangk/product-service:latest
cd ../Order-Service
mvn clean package -DskipTests
docker build -t kimleangk/order-service:latest .
docker push kimleangk/order-service:latest
cd ../Inventory-Service
mvn clean package -DskipTests
docker build -t kimleangk/inventory-service:latest .
docker push kimleangk/inventory-service:latest