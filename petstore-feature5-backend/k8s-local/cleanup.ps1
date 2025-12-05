# Script para limpiar recursos de Minikube

Write-Host "🗑️ Eliminando recursos de PetStore..." -ForegroundColor Yellow

# Eliminar aplicación y base de datos
kubectl delete -f k8s-local/petstore-app.yaml
kubectl delete -f k8s-local/postgres.yaml  
kubectl delete -f k8s-local/postgres-config.yaml

# Eliminar monitoreo
Write-Host "📊 Eliminando recursos de monitoreo..." -ForegroundColor Yellow
kubectl delete -f k8s-local/grafana.yaml
kubectl delete -f k8s-local/prometheus.yaml
kubectl delete -f k8s-local/prometheus-config.yaml

Write-Host "✅ Recursos eliminados" -ForegroundColor Green
Write-Host ""
Write-Host "Para detener Minikube completamente:" -ForegroundColor Yellow
Write-Host "   minikube stop" -ForegroundColor Gray
Write-Host "   minikube delete  # (elimina el cluster)" -ForegroundColor Gray
