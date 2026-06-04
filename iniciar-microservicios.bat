@echo off

start cmd /k "cd usuario-service && mvnw spring-boot:run"

start cmd /k "cd sala-service && mvnw spring-boot:run"

start cmd /k "cd peliculas-service && mvnw spring-boot:run"

start cmd /k "cd cinefunciones-service && mvnw spring-boot:run"

start cmd /k "cd reservas-service && mvnw spring-boot:run"

start cmd /k "cd api-getaway && mvnw spring-boot:run"