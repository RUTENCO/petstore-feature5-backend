# Verificar que estamos en la raíz del proyecto
if (!(Test-Path "Dockerfile")) {
    Write-Host "❌ Error: No se encuentra Dockerfile. Debes ejecutar este script desde la raíz del proyecto." -ForegroundColor Red
    Write-Host "💡 Tip: Ejecuta desde: petstore-feature5-backend/" -ForegroundColor Yellow
    exit 1
}

# Verificar que Minikube esté corriendo
try {
    minikube status | Out-Null
    Write-Host "✅ Minikube está corriendo" -ForegroundColor Green
} catch {
    Write-Host "❌ Minikube no está corriendo. Iniciándolo..." -ForegroundColor Yellow
    minikube start
}

# Configurar Docker para usar el registro de Minikube
Write-Host "🐳 Configurando Docker para Minikube..." -ForegroundColor Cyan
& minikube docker-env | Invoke-Expression

# Construir la imagen en Minikube
Write-Host "🔨 Construyendo imagen Docker..." -ForegroundColor Cyan
docker build -t petstore-backend:latest .

# Aplicar manifiestos
Write-Host "📦 Aplicando manifiestos de Kubernetes..." -ForegroundColor Cyan
kubectl apply -f k8s-local/postgres-config.yaml
kubectl apply -f k8s-local/postgres.yaml
kubectl apply -f k8s-local/petstore-app.yaml

# Aplicar manifiestos de monitoreo
Write-Host "📊 Aplicando manifiestos de monitoreo..." -ForegroundColor Cyan
kubectl apply -f k8s-local/prometheus-config.yaml
kubectl apply -f k8s-local/prometheus.yaml
kubectl apply -f k8s-local/grafana-config.yaml
kubectl apply -f k8s-local/grafana.yaml
kubectl apply -f k8s-local/node-exporter.yaml

# Esperar que los pods estén listos
Write-Host "⏳ Esperando que los pods estén listos..." -ForegroundColor Cyan
Write-Host "   � PostgreSQL..." -ForegroundColor Gray
kubectl wait --for=condition=ready pod -l app=postgres --timeout=100s
Write-Host "   � PetStore App..." -ForegroundColor Gray
kubectl wait --for=condition=ready pod -l app=petstore --timeout=30s
Write-Host "   📊 Prometheus..." -ForegroundColor Gray
kubectl wait --for=condition=ready pod -l app=prometheus --timeout=30s
Write-Host "   📈 Grafana..." -ForegroundColor Gray
kubectl wait --for=condition=ready pod -l app=grafana --timeout=30s

Write-Host "📊 Estado de los recursos:" -ForegroundColor Cyan
kubectl get pods
kubectl get services

Write-Host ""
Write-Host "✅ ¡Despliegue completado!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Para acceder a la aplicación:" -ForegroundColor Yellow
Write-Host "   minikube service petstore-service" -ForegroundColor White
Write-Host ""
Write-Host "📊 Para acceder a Prometheus:" -ForegroundColor Yellow
Write-Host "   minikube service prometheus-service" -ForegroundColor White
Write-Host ""
Write-Host "📈 Para acceder a Grafana:" -ForegroundColor Yellow
Write-Host "   minikube service grafana-service" -ForegroundColor White
Write-Host "   Usuario: admin | Password: admin123" -ForegroundColor Gray
Write-Host ""
Write-Host "💻 Para acceder a Node Exporter:" -ForegroundColor Yellow
Write-Host "   minikube service node-exporter-service" -ForegroundColor White
Write-Host ""
Write-Host "🔗 Endpoints disponibles:" -ForegroundColor Yellow
Write-Host "   /actuator/health    # Estado de la aplicación" -ForegroundColor Gray
Write-Host "   /api/               # API REST" -ForegroundColor Gray
Write-Host "   /graphql            # GraphQL API" -ForegroundColor Gray
Write-Host "   /graphiql           # GraphQL UI" -ForegroundColor Gray
Write-Host "   /swagger-ui.html    # Swagger Documentation" -ForegroundColor Gray
Write-Host ""
Write-Host "📝 Comandos útiles:" -ForegroundColor Yellow
Write-Host "   kubectl get pods                    # Ver pods" -ForegroundColor Gray
Write-Host "   kubectl logs -f <pod-name>          # Ver logs" -ForegroundColor Gray
Write-Host "   kubectl exec -it <pod-name> -- bash # Conectar al pod" -ForegroundColor Gray
Write-Host "   minikube dashboard                  # Dashboard web" -ForegroundColor Gray
