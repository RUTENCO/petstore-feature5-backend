@echo off
echo ========================================
echo    TESTING PRODUCT ENDPOINTS
echo ========================================
echo.

echo 🔓 TESTING WITHOUT AUTHENTICATION (if endpoints are public)
echo ----------------------------------------
echo.

echo 1. Testing GET /api/products (All products)
curl -X GET http://localhost:8080/api/products
echo.
echo.

echo 2. Testing GET /api/products/category/1 (Products by category 1)
curl -X GET http://localhost:8080/api/products/category/1
echo.
echo.

echo 3. Testing GET /api/products/1 (Product by ID)
curl -X GET http://localhost:8080/api/products/1
echo.
echo.

echo 4. Testing GET /api/products/search?name=laptop (Search by name)
curl -X GET "http://localhost:8080/api/products/search?name=laptop"
echo.
echo.

echo 5. Testing GET /api/products/price-range (Price range)
curl -X GET "http://localhost:8080/api/products/price-range?minPrice=100&maxPrice=500"
echo.
echo.

echo ========================================
echo 🔑 If endpoints are PROTECTED, get token first:
echo ========================================
echo.
echo Step 1: Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"admin@petstore.com\",\"password\":\"password123\"}"
echo.
echo.
echo Step 2: Copy the token and use it like this:
echo curl -X GET http://localhost:8080/api/products -H "Authorization: Bearer YOUR_TOKEN_HERE"
echo.
echo ========================================

echo Product endpoints testing completed!
pause
