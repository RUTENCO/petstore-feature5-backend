@echo off
echo ===============================================
echo PETSTORE BACKEND - PERFILES CON JAR
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
echo 🔓 Construyendo JAR y ejecutando en modo DESARROLLO...
echo ✅ GraphiQL: http://localhost:8080/graphiql
echo ✅ GraphQL publico: http://localhost:8080/graphql
echo.
mvn clean package -DskipTests
if errorlevel 1 goto build_failed
java "-Dspring.profiles.active=dev" -jar target/petstore-feature5-backend-0.0.1-SNAPSHOT.jar
goto end

:prod
echo.
echo 🔒 Construyendo JAR y ejecutando en modo PRODUCCION...
echo ❌ GraphiQL: DESHABILITADO
echo 🔐 GraphQL: Requiere JWT token
echo.
mvn clean package -DskipTests
if errorlevel 1 goto build_failed
java "-Dspring.profiles.active=prod" -jar target/petstore-feature5-backend-0.0.1-SNAPSHOT.jar
goto end

:default
echo.
echo 🔓 Construyendo JAR y ejecutando en modo DEFAULT...
echo ✅ GraphiQL: http://localhost:8080/graphiql
echo ✅ GraphQL publico: http://localhost:8080/graphql
echo.
mvn clean package -DskipTests
if errorlevel 1 goto build_failed
java -jar target/petstore-feature5-backend-0.0.1-SNAPSHOT.jar
goto end

:build_failed
echo.
echo ❌ Error al construir el JAR. Revisa los logs de Maven.
pause
goto end

:invalid
echo.
echo ❌ Opcion invalida. Usa 1, 2 o 3.
pause
goto end

:end
pause
