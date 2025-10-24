// =========================
// PETSTORE FRONTEND GUIDE - Complete JWT & GraphQL Implementation
// =========================
// Esta guía incluye todas las funciones necesarias para interactuar con el backend
// Copiar y pegar en el proyecto de frontend

const BACKEND_URL = 'http://localhost:8080';
const GRAPHQL_ENDPOINT = `${BACKEND_URL}/graphql`;
const REST_ENDPOINT = `${BACKEND_URL}/api`;

// =========================
// 1. AUTENTICACIÓN JWT
// =========================

// Login con REST API
async function loginUser(email, password) {
    try {
        const response = await fetch(`${REST_ENDPOINT}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        
        const data = await response.json();
        
        if (response.ok && data.token) {
            localStorage.setItem('authToken', data.token);
            localStorage.setItem('userData', JSON.stringify(data.user));
            console.log('Login exitoso:', data);
            return { success: true, data };
        } else {
            console.error('Error en login:', data);
            return { success: false, error: data.message };
        }
    } catch (error) {
        console.error('Error de red:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

// Login con GraphQL
async function loginUserGraphQL(email, password) {
    try {
        const query = `
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
        
        const response = await fetch(GRAPHQL_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                query,
                variables: { email, password }
            })
        });
        
        const result = await response.json();
        
        if (result.data?.login?.success) {
            const loginData = result.data.login;
            localStorage.setItem('authToken', loginData.token);
            localStorage.setItem('userData', JSON.stringify(loginData.user));
            console.log('Login GraphQL exitoso:', loginData);
            return { success: true, data: loginData };
        } else {
            console.error('Error en login GraphQL:', result);
            return { success: false, error: 'Credenciales inválidas' };
        }
    } catch (error) {
        console.error('Error de red GraphQL:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

// Logout
function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    console.log('Logout exitoso');
}

// Verificar si el usuario está autenticado
function isAuthenticated() {
    const token = localStorage.getItem('authToken');
    return !!token;
}

// Obtener datos del usuario logueado
function getCurrentUser() {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
}

// =========================
// 2. PETICIONES AUTENTICADAS
// =========================

// Función genérica para peticiones REST autenticadas
async function makeAuthenticatedRequest(url, options = {}) {
    const token = localStorage.getItem('authToken');
    
    if (!token) {
        console.error('No hay token de autenticación');
        return { success: false, error: 'No autenticado' };
    }
    
    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
                ...options.headers
            }
        });
        
        if (response.status === 401) {
            console.error('Token expirado o inválido');
            logout();
            return { success: false, error: 'Sesión expirada' };
        }
        
        const data = await response.json();
        return { success: response.ok, data, status: response.status };
    } catch (error) {
        console.error('Error en petición autenticada:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

// Función genérica para peticiones GraphQL autenticadas
async function makeAuthenticatedGraphQLRequest(query, variables = {}) {
    const token = localStorage.getItem('authToken');
    
    if (!token) {
        console.error('No hay token de autenticación');
        return { success: false, error: 'No autenticado' };
    }
    
    try {
        const response = await fetch(GRAPHQL_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ query, variables })
        });
        
        if (response.status === 401) {
            console.error('Token expirado o inválido');
            logout();
            return { success: false, error: 'Sesión expirada' };
        }
        
        const result = await response.json();
        
        if (result.errors) {
            console.error('Errores GraphQL:', result.errors);
            return { success: false, error: result.errors[0].message };
        }
        
        return { success: true, data: result.data };
    } catch (error) {
        console.error('Error en petición GraphQL:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

// =========================
// 3. FUNCIONES DE PROMOCIONES
// =========================

// Obtener todas las promociones (REST)
async function getAllPromotions() {
    return await makeAuthenticatedRequest(`${REST_ENDPOINT}/promotions`);
}

// Obtener todas las promociones (GraphQL)
async function getAllPromotionsGraphQL() {
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
                    email
                }
                category {
                    categoryId
                    categoryName
                    description
                }
            }
        }
    `;
    return await makeAuthenticatedGraphQLRequest(query);
}

// Crear promoción (GraphQL)
async function createPromotionGraphQL(promotionInput) {
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
    return await makeAuthenticatedGraphQLRequest(mutation, { input: promotionInput });
}

// =========================
// 4. FUNCIONES DE CATEGORÍAS
// =========================

async function getAllCategoriesGraphQL() {
    const query = `
        query {
            categories {
                categoryId
                categoryName
                description
            }
        }
    `;
    return await makeAuthenticatedGraphQLRequest(query);
}

// =========================
// 5. FUNCIONES DE PRODUCTOS
// =========================

async function getAllProductsGraphQL() {
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
    return await makeAuthenticatedGraphQLRequest(query);
}

// =========================
// 6. FUNCIONES AUXILIARES
// =========================

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('es-ES');
}

function calculateDiscountedPrice(basePrice, discountValue) {
    return basePrice - (basePrice * discountValue / 100);
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// =========================
// 7. EJEMPLOS DE USO
// =========================

async function exampleLoginAndGetPromotions() {
    console.log('=== Ejemplo de Login y consulta de promociones ===');
    
    const loginResult = await loginUserGraphQL('admin@petstore.com', 'password123');
    if (!loginResult.success) {
        console.error('Error en login:', loginResult.error);
        return;
    }
    
    console.log('Usuario logueado:', getCurrentUser());
    
    const promotionsResult = await getAllPromotionsGraphQL();
    if (promotionsResult.success) {
        console.log('Promociones obtenidas:', promotionsResult.data.promotions);
    } else {
        console.error('Error obteniendo promociones:', promotionsResult.error);
    }
}

// =========================
// 8. INICIALIZACIÓN
// =========================

document.addEventListener('DOMContentLoaded', function() {
    if (isAuthenticated()) {
        console.log('Usuario autenticado:', getCurrentUser());
    } else {
        console.log('Usuario no autenticado');
    }
});
