@echo off
setlocal enabledelayedexpansion

set SERVICES=usuario-service sala-service peliculas-service cinefunciones-service reservas-service pagos-service producto-service promociones-service comentarios-service favoritos-service api-getaway

for %%S in (%SERVICES%) do (
    echo.
    echo ===== Compilando %%S =====
    cd %%S
    call .\mvnw clean package -DskipTests
    if errorlevel 1 (
        echo.
        echo ##### ERROR compilando %%S - ABORTANDO #####
        cd ..
        pause
        exit /b 1
    )
    cd ..
)

echo.
echo ===== TODOS COMPILADOS OK =====
echo.
echo ===== Reconstruyendo contenedores =====
docker compose up -d --build

if errorlevel 1 (
    echo.
    echo ##### ERROR en docker compose up --build #####
    pause
    exit /b 1
)

echo.
echo ===== LISTO - servicios reconstruidos y arriba =====
pause