// =========================
// PETSTORE API TESTING SUITE
// =========================
// Script completo de pruebas para REST API y GraphQL
// Ejecutar con: node test-api.js

const API_BASE = 'http://localhost:8080';
const GRAPHQL_ENDPOINT = `${API_BASE}/graphql`;
const REST_ENDPOINT = `${API_BASE}/api`;

// =========================
// FUNCIONES AUXILIARES
// =========================

async function makeRequest(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        const data = await response.json();
        return { status: response.status, data, success: response.ok };
    } catch (error) {
        return { error: error.message, success: false };
    }
}

async function makeGraphQLRequest(query, variables = {}, token = null) {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    try {
        const response = await fetch(GRAPHQL_ENDPOINT, {
            method: 'POST',
            headers,
            body: JSON.stringify({ query, variables })
        });
        
        const result = await response.json();
        return { status: response.status, data: result, success: response.ok };
    } catch (error) {
        return { error: error.message, success: false };
    }
}

function logTest(testName) {
    console.log(`\n${'='.repeat(50)}`);
    console.log(`🧪 ${testName}`);
    console.log('='.repeat(50));
}

function logResult(success, message, data = null) {
    console.log(success ? `✅ ${message}` : `❌ ${message}`);
    if (data) {
        console.log('📄 Datos:', JSON.stringify(data, null, 2));
    }
}

// =========================
// TESTS DE AUTENTICACIÓN
// =========================

async function testRestLogin() {
    logTest('TEST REST LOGIN');
    
    const loginData = {
        email: 'admin@petstore.com',
        password: 'password123'
    };
    
    const result = await makeRequest(`${REST_ENDPOINT}/auth/login`, {
        method: 'POST',
        body: JSON.stringify(loginData)
    });
    
    console.log('Status:', result.status);
    
    if (result.success && result.data?.token) {
        logResult(true, 'Login REST exitoso - Token recibido', {
            token: result.data.token.substring(0, 20) + '...',
            user: result.data.user
        });
        return result.data.token;
    } else {
        logResult(false, 'Login REST falló', result.data);
        return null;
    }
}

async function testGraphQLLogin() {
    logTest('TEST GRAPHQL LOGIN');
    
    const mutation = `
        mutation Login($email: String!, $password: String!) {
            login(email: $email, password: $password) {
                success
                token
                user {
                    userId
                    userName
                    email
                    role {
                        roleId
                        roleName
                    }
                }
            }
        }
    `;
    
    const variables = {
        email: 'admin@petstore.com',
        password: 'password123'
    };
    
    const result = await makeGraphQLRequest(mutation, variables);
    
    console.log('Status:', result.status);
    
    if (result.success && result.data?.data?.login?.success) {
        const loginData = result.data.data.login;
        logResult(true, 'Login GraphQL exitoso', {
            token: loginData.token?.substring(0, 20) + '...',
            user: loginData.user
        });
        return loginData.token;
    } else {
        logResult(false, 'Login GraphQL falló', result.data);
        return null;
    }
}

// =========================
// TESTS DE PROMOCIONES
// =========================

async function testGetPromotionsRest(token) {
    logTest('TEST GET PROMOTIONS REST');
    
    if (!token) {
        logResult(false, 'No hay token para autenticar');
        return;
    }
    
    const result = await makeRequest(`${REST_ENDPOINT}/promotions`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    
    if (result.success) {
        logResult(true, `Promociones obtenidas (${result.data?.length || 0})`, 
                 result.data?.slice(0, 2)); // Solo mostrar las primeras 2
    } else {
        logResult(false, 'Error obteniendo promociones', result.data);
    }
}

async function testGetPromotionsGraphQL(token) {
    logTest('TEST GET PROMOTIONS GRAPHQL');
    
    if (!token) {
        logResult(false, 'No hay token para autenticar');
        return;
    }
    
    const query = `
        query {
            promotions {
                promotionId
                promotionName
                description
                startDate
                endDate
                discountValue
                status {
                    statusId
                    statusName
                }
                user {
                    userId
                    userName
                }
                category {
                    categoryId
                    categoryName
                }
            }
        }
    `;
    
    const result = await makeGraphQLRequest(query, {}, token);
    
    if (result.success && result.data?.data?.promotions) {
        const promotions = result.data.data.promotions;
        logResult(true, `Promociones GraphQL obtenidas (${promotions.length})`,
                 promotions.slice(0, 2)); // Solo mostrar las primeras 2
    } else {
        logResult(false, 'Error obteniendo promociones GraphQL', result.data);
    }
}

async function testCreatePromotionGraphQL(token) {
    logTest('TEST CREATE PROMOTION GRAPHQL');
    
    if (!token) {
        logResult(false, 'No hay token para autenticar');
        return;
    }
    
    const mutation = `
        mutation CreatePromotion($input: PromotionInput!) {
            createPromotion(promotionInput: $input) {
                promotionId
                promotionName
                description
                startDate
                endDate
                discountValue
                status {
                    statusName
                }
                category {
                    categoryName
                }
            }
        }
    `;
    
    const variables = {
        input: {
            promotionName: "Test Promotion API",
            description: "Promoción creada desde test automatizado",
            startDate: "2025-09-23",
            endDate: "2025-12-31",
            discountValue: 25.0,
            statusId: 1,
            categoryId: 1
        }
    };
    
    const result = await makeGraphQLRequest(mutation, variables, token);
    
    if (result.success && result.data?.data?.createPromotion) {
        logResult(true, 'Promoción creada exitosamente', result.data.data.createPromotion);
    } else {
        logResult(false, 'Error creando promoción', result.data);
    }
}

// =========================
// TESTS DE CATEGORÍAS Y PRODUCTOS
// =========================

async function testGetCategoriesGraphQL(token) {
    logTest('TEST GET CATEGORIES GRAPHQL');
    
    if (!token) {
        logResult(false, 'No hay token para autenticar');
        return;
    }
    
    const query = `
        query {
            categories {
                categoryId
                categoryName
                description
            }
        }
    `;
    
    const result = await makeGraphQLRequest(query, {}, token);
    
    if (result.success && result.data?.data?.categories) {
        const categories = result.data.data.categories;
        logResult(true, `Categorías obtenidas (${categories.length})`, categories.slice(0, 3));
    } else {
        logResult(false, 'Error obteniendo categorías', result.data);
    }
}

async function testGetProductsGraphQL(token) {
    logTest('TEST GET PRODUCTS GRAPHQL');
    
    if (!token) {
        logResult(false, 'No hay token para autenticar');
        return;
    }
    
    const query = `
        query {
            products {
                productId
                productName
                basePrice
                sku
                category {
                    categoryId
                    categoryName
                }
                promotion {
                    promotionId
                    promotionName
                    discountValue
                }
            }
        }
    `;
    
    const result = await makeGraphQLRequest(query, {}, token);
    
    if (result.success && result.data?.data?.products) {
        const products = result.data.data.products;
        logResult(true, `Productos obtenidos (${products.length})`, products.slice(0, 2));
    } else {
        logResult(false, 'Error obteniendo productos', result.data);
    }
}

// =========================
// SUITE PRINCIPAL DE PRUEBAS
// =========================

async function runAllTests() {
    console.log('🚀 INICIANDO SUITE DE PRUEBAS PETSTORE API');
    console.log('🕒 Fecha:', new Date().toLocaleString());
    console.log('🌐 Backend URL:', API_BASE);
    
    try {
        // 1. Autenticación
        console.log('\n' + '🔐 PRUEBAS DE AUTENTICACIÓN'.padEnd(50, '='));
        const restToken = await testRestLogin();
        const graphqlToken = await testGraphQLLogin();
        
        // Usar el token que funcionó
        const token = graphqlToken || restToken;
        
        if (!token) {
            console.log('\n❌ No se pudo obtener token de autenticación. Abortando pruebas.');
            return;
        }
        
        // 2. Promociones
        console.log('\n' + '🎯 PRUEBAS DE PROMOCIONES'.padEnd(50, '='));
        await testGetPromotionsRest(token);
        await testGetPromotionsGraphQL(token);
        await testCreatePromotionGraphQL(token);
        
        // 3. Otras entidades
        console.log('\n' + '📦 PRUEBAS DE OTRAS ENTIDADES'.padEnd(50, '='));
        await testGetCategoriesGraphQL(token);
        await testGetProductsGraphQL(token);
        
        console.log('\n' + '✅ SUITE DE PRUEBAS COMPLETADA'.padEnd(50, '='));
        
    } catch (error) {
        console.log('\n❌ Error en suite de pruebas:', error.message);
    }
}

// =========================
// TESTS INDIVIDUALES
// =========================

async function testLoginOnly() {
    console.log('🧪 Probando solo login...');
    await testRestLogin();
    await testGraphQLLogin();
}

async function testPromotionsOnly() {
    console.log('🧪 Probando solo promociones...');
    const token = await testGraphQLLogin();
    if (token) {
        await testGetPromotionsGraphQL(token);
        await testCreatePromotionGraphQL(token);
    }
}

// =========================
// EJECUCIÓN
// =========================

// Verificar si se ejecuta directamente
if (require.main === module) {
    const args = process.argv.slice(2);
    
    if (args.includes('--login-only')) {
        testLoginOnly();
    } else if (args.includes('--promotions-only')) {
        testPromotionsOnly();
    } else {
        runAllTests();
    }
}

// Exportar funciones para uso en otros módulos
module.exports = {
    runAllTests,
    testRestLogin,
    testGraphQLLogin,
    testGetPromotionsGraphQL,
    testCreatePromotionGraphQL,
    testGetCategoriesGraphQL,
    testGetProductsGraphQL
};
async function testVerifyToken(token) {
    console.log('\n🔍 Verificando token...');
    
    const result = await makeRequest(`${API_BASE}/api/auth/verify`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    
    console.log('Status:', result.status);
    console.log('Response:', result.data);
    
    if (result.status === 200) {
        console.log('✅ Token válido');
    } else {
        console.log('❌ Token inválido');
    }
}

// Test 3: Obtener información del usuario
async function testGetUserInfo(token) {
    console.log('\n👤 Obteniendo info del usuario...');
    
    const result = await makeRequest(`${API_BASE}/api/auth/me`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    
    console.log('Status:', result.status);
    console.log('Response:', result.data);
    
    if (result.status === 200) {
        console.log('✅ Información obtenida correctamente');
    } else {
        console.log('❌ Error obteniendo información');
    }
}

// Test 4: Probar sin token (debe fallar)
async function testWithoutToken() {
    console.log('\n🚫 Probando endpoint protegido sin token...');
    
    const result = await makeRequest(`${API_BASE}/api/auth/me`, {
        method: 'GET'
    });
    
    console.log('Status:', result.status);
    console.log('Response:', result.data);
    
    if (result.status === 401 || result.status === 403) {
        console.log('✅ Correctamente bloqueado sin token');
    } else {
        console.log('❌ ERROR: Permitió acceso sin token');
    }
}

// Ejecutar todas las pruebas
async function runAllTests() {
    console.log('🚀 Iniciando pruebas de la API\n');
    
    // Test login
    const token = await testLogin();
    
    if (token) {
        // Tests con token válido
        await testVerifyToken(token);
        await testGetUserInfo(token);
    }
    
    // Test sin token
    await testWithoutToken();
    
    console.log('\n✨ Pruebas completadas');
}

// Ejecutar si es llamado directamente
if (typeof window === 'undefined') {
    // Node.js
    const fetch = require('node-fetch');
    runAllTests();
} else {
    // Browser - exponer funciones globalmente
    window.testAPI = {
        runAllTests,
        testLogin,
        testVerifyToken,
        testGetUserInfo,
        testWithoutToken
    };
}
