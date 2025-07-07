from django.urls import path, include
from rest_framework.routers import DefaultRouter
from rest_framework_simplejwt.views import TokenRefreshView
from . import views

# Router para ViewSets
router = DefaultRouter()
router.register(r'dashboard', views.DashboardViewSet, basename='dashboard')
router.register(r'moderacion', views.ModeracionViewSet, basename='moderacion')
router.register(r'usuarios', views.GestionUsuariosViewSet, basename='usuarios')
router.register(r'estadisticas', views.EstadisticasViewSet, basename='estadisticas')

app_name = 'administracion'

urlpatterns = [
    # Incluir todas las rutas del router
    path('', include(router.urls)),

    path('auth/login/', views.AdminLoginView.as_view(), name='admin-login'),
    path('auth/logout/', views.AdminLogoutView.as_view(), name='admin-logout'),
    path('auth/refresh/', TokenRefreshView.as_view(), name='admin-token-refresh'),
    
    # Vista específica del dashboard (compatibilidad con tu implementación actual)
    path('dashboard-data/', views.DashboardDataView.as_view(), name='dashboard-data'),
]