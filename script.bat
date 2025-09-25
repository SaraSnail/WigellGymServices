@echo off
echo Stopping wigell-gym-service-container
docker stop wigell-gym-service-container
echo Deleting container wigell-gym-service-container
docker rm wigell-gym-service-container
echo Deleting image wigell-gym-service-image
docker rmi wigell-gym-service-image
echo Running mvn package
call mvn package -DskipTests
echo Creating image wigell-gym-service-image
docker build -t wigell-gym-service-image .
echo Creating and running container wigell-gym-service-container
docker run -d -p 6565:6565 --name wigell-gym-service-container --network wigell-network wigell-gym-service-image
echo Done!