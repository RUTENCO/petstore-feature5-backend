# 🚀 SCRIPT DE PRUEBAS PARA POWERSHELL
# Script para probar todos los endpoints de productos usando PowerShell

Write-Host "========================================" -ForegroundColor Green
Write-Host "    TESTING PRODUCT ENDPOINTS (PowerShell)" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "🔓 TESTING WITHOUT AUTHENTICATION (endpoints are now public)" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow
Write-Host ""

Write-Host "1. Testing GET /api/products (All products)" -ForegroundColor Cyan
try {
    $products = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Get
    Write-Host "✅ SUCCESS - Found $($products.Count) products" -ForegroundColor Green
    $products | Select-Object -First 3 | Format-Table productId, productName, price, @{Name="Category"; Expression={$_.category.categoryName}}
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "2. Testing GET /api/products/category/1 (Products by category 1)" -ForegroundColor Cyan
try {
    $categoryProducts = Invoke-RestMethod -Uri "http://localhost:8080/api/products/category/1" -Method Get
    Write-Host "✅ SUCCESS - Found $($categoryProducts.Count) products in category 1" -ForegroundColor Green
    $categoryProducts | Format-Table productId, productName, price
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "3. Testing GET /api/products/1 (Product by ID)" -ForegroundColor Cyan
try {
    $singleProduct = Invoke-RestMethod -Uri "http://localhost:8080/api/products/1" -Method Get
    Write-Host "✅ SUCCESS - Found product: $($singleProduct.productName)" -ForegroundColor Green
    $singleProduct | Format-List productId, productName, description, price, status
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "4. Testing GET /api/products/search?name=laptop (Search by name)" -ForegroundColor Cyan
try {
    $searchResults = Invoke-RestMethod -Uri "http://localhost:8080/api/products/search?name=laptop" -Method Get
    Write-Host "✅ SUCCESS - Found $($searchResults.Count) products containing 'laptop'" -ForegroundColor Green
    $searchResults | Format-Table productId, productName, price
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "5. Testing GET /api/products/price-range (Price range 100-500)" -ForegroundColor Cyan
try {
    $priceRangeProducts = Invoke-RestMethod -Uri "http://localhost:8080/api/products/price-range?minPrice=100&maxPrice=500" -Method Get
    Write-Host "✅ SUCCESS - Found $($priceRangeProducts.Count) products in price range 100-500" -ForegroundColor Green
    $priceRangeProducts | Format-Table productId, productName, price
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Green
Write-Host "🔑 If endpoints were PROTECTED, you would need JWT token:" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Step 1: Login to get JWT token" -ForegroundColor Cyan
Write-Host '$loginBody = @{' -ForegroundColor Gray
Write-Host '    email = "admin@petstore.com"' -ForegroundColor Gray
Write-Host '    password = "password123"' -ForegroundColor Gray
Write-Host '} | ConvertTo-Json' -ForegroundColor Gray
Write-Host '$token = (Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json").token' -ForegroundColor Gray
Write-Host ""
Write-Host "Step 2: Use token in requests" -ForegroundColor Cyan
Write-Host '$headers = @{ Authorization = "Bearer $token" }' -ForegroundColor Gray
Write-Host 'Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Get -Headers $headers' -ForegroundColor Gray
Write-Host ""

Write-Host "========================================" -ForegroundColor Green
Write-Host "Product endpoints testing completed! 🎉" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
