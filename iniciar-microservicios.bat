@echo off
cd usuario-service
call .\mvnw clean package -DskipTests
cd ..

cd sala-service
call .\mvnw clean package -DskipTests
cd ..

cd peliculas-service
call .\mvnw clean package -DskipTests
cd ..

cd cinefunciones-service
call .\mvnw clean package -DskipTests
cd ..

cd reservas-service
call .\mvnw clean package -DskipTests
cd ..

cd pagos-service
call .\mvnw clean package -DskipTests
cd ..

cd api-getaway
call .\mvnw clean package -DskipTests
cd ..

echo TODOS COMPILADOS
pause