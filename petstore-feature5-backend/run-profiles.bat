@echo off
echo ===============================================
echo PETSTORE BACKEND - SCRIPTS DE EJECUCION
echo ===============================================
echo.
echo Elige el modo de ejecucion:
echo.
echo 1. DESARROLLO (dev) - GraphQL publico, GraphiQL habilitado
echo 2. PRODUCCION (prod) - GraphQL con JWT, GraphiQL deshabilitado
echo 3. DEFAULT - Modo desarrollo por defecto
echo.
set /p choice="Ingresa tu opcion (1-3): "

if "%choice%"=="1" goto dev
if "%choice%"=="2" goto prod
if "%choice%"=="3" goto default
goto invalid

:dev
echo.
echo  Iniciando en modo DESARROLLO...
echo  GraphiQL: http://localhost:8080/graphiql
echo  GraphQL publico: http://localhost:8080/graphql
echo.
mvn spring-boot:run -Dspring.profiles.active=dev
goto end

:prod
echo.
echo  Iniciando en modo PRODUCCION...
echo  GraphiQL: DESHABILITADO
echo  GraphQL: Requiere JWT token
echo.
mvn spring-boot:run -Dspring.profiles.active=prod
goto end

:default
echo.
echo  Iniciando en modo DEFAULT (desarrollo)...
echo  GraphiQL: http://localhost:8080/graphiql
echo  GraphQL publico: http://localhost:8080/graphql
echo.
mvn spring-boot:run
goto end

:invalid
echo.
echo  Opcion invalida. Usa 1, 2 o 3.
pause
goto end

:end
pause
