# 🚢 Kubernetes Local con Minikube

Configuración simple para desplegar PetStore en Minikube localmente.

## 📋 Requisitos

1. **Minikube** instalado
   ```powershell
   # Windows (Chocolatey)
   choco install minikube
   
   # O descargar desde: https://minikube.sigs.k8s.io/docs/start/
   ```

2. **kubectl** instalado
   ```powershell
   # Windows (Chocolatey)
   choco install kubernetes-cli
   ```

3. **Docker Desktop** corriendo

## 🚀 Despliegue Rápido

### 1. Iniciar Minikube
```powershell
minikube start
```

### 2. Desplegar aplicación
```powershell
.\k8s-local\deploy.ps1
```

### 3. Acceder a la aplicación
```powershell
# Abrir servicio en el navegador
minikube service petstore-service

# O obtener URL
minikube service petstore-service --url
```

## 📦 Recursos Creados

- **postgres**: Base de datos PostgreSQL
- **petstore-app**: 2 réplicas del backend
- **postgres-config**: ConfigMap con credenciales de BD
- **prometheus**: Sistema de monitoreo y alertas
- **grafana**: Dashboard de visualización
- **prometheus-config**: ConfigMap con configuración de Prometheus

## 🔧 Comandos Útiles

```powershell
# Ver estado
kubectl get pods
kubectl get services

# Ver logs
kubectl logs -f deployment/petstore-app
kubectl logs -f deployment/postgres
kubectl logs -f deployment/prometheus
kubectl logs -f deployment/grafana

# Dashboard web
minikube dashboard

# Acceder a servicios de monitoreo
minikube service prometheus-service
minikube service grafana-service

# Port forward (alternativo)
kubectl port-forward service/petstore-service 8080:8080
kubectl port-forward service/prometheus-service 9090:9090
kubectl port-forward service/grafana-service 3000:3000

# Conectar a la base de datos
kubectl exec -it deployment/postgres -- psql -U postgres -d petstore
```

## 🌐 Endpoints Disponibles

Una vez desplegado, la aplicación estará disponible en:
- **API REST**: http://minikube-ip:puerto/api/
- **GraphQL**: http://minikube-ip:puerto/graphql
- **GraphiQL**: http://minikube-ip:puerto/graphiql
- **Swagger**: http://minikube-ip:puerto/swagger-ui.html
- **Actuator**: http://minikube-ip:puerto/actuator/health
- **Prometheus**: http://minikube-ip:9090
- **Grafana**: http://minikube-ip:3000 (admin/admin123)

## 🗑️ Limpieza

```powershell
# Eliminar recursos
.\k8s-local\cleanup.ps1

# Detener Minikube
minikube stop

# Eliminar cluster (opcional)
minikube delete
```

## 📝 Configuración

### Base de Datos
- **Host**: postgres (servicio interno)
- **Puerto**: 5432
- **Usuario**: postgres
- **Password**: postgres123
- **Base de datos**: petstore

### Aplicación
- **Puerto**: 8080
- **Perfil**: dev
- **Réplicas**: 2

### Monitoreo
- **Prometheus**: Puerto 9090
- **Grafana**: Puerto 3000
- **Usuario Grafana**: admin
- **Password Grafana**: admin123

## 🔍 Troubleshooting

### Pod no inicia
```powershell
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

### Problemas de imagen
```powershell
# Asegurarse de usar el Docker de Minikube
& minikube docker-env | Invoke-Expression
docker build -t petstore-backend:latest .
```

### No se puede acceder
```powershell
# Verificar que el servicio tenga IP externa
kubectl get services
minikube service petstore-service --url
```
