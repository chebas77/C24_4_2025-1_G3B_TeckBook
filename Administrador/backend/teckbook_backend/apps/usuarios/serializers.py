from rest_framework import serializers
from djoser.serializers import UserCreateSerializer as BaseUserCreateSerializer
from djoser.serializers import UserSerializer as BaseUserSerializer
from .models import Usuario, Departamento, Carrera, Ciclo, Seccion

class DepartamentoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Departamento
        fields = ['id', 'nombre', 'codigo', 'activo']

class CarreraSerializer(serializers.ModelSerializer):
    departamento = DepartamentoSerializer(read_only=True)
    
    class Meta:
        model = Carrera
        fields = ['id', 'nombre', 'codigo', 'departamento', 'descripcion', 
                 'duracion_ciclos', 'modalidad', 'activo']

class CicloSerializer(serializers.ModelSerializer):
    class Meta:
        model = Ciclo
        fields = ['id', 'numero', 'nombre']

class SeccionSerializer(serializers.ModelSerializer):
    ciclo = CicloSerializer(read_only=True)
    carrera = CarreraSerializer(read_only=True)
    
    class Meta:
        model = Seccion
        fields = ['id', 'nombre', 'codigo', 'ciclo', 'carrera']

class UsuarioSerializer(BaseUserSerializer):
    departamento = DepartamentoSerializer(read_only=True)
    carrera = CarreraSerializer(read_only=True)
    ciclo_actual = CicloSerializer(read_only=True)
    seccion = SeccionSerializer(read_only=True)
    nombre_completo = serializers.CharField(read_only=True)
    
    class Meta(BaseUserSerializer.Meta):
        model = Usuario
        fields = [
            'id', 'nombre', 'apellidos', 'nombre_completo', 'correo_institucional',
            'rol', 'ciclo_actual', 'seccion', 'carrera', 'departamento',
            'profile_image_url', 'telefono', 'is_active', 'created_at', 'updated_at'
        ]

class UsuarioCreateSerializer(BaseUserCreateSerializer):
    class Meta(BaseUserCreateSerializer.Meta):
        model = Usuario
        fields = [
            'nombre', 'apellidos', 'correo_institucional', 'password',
            'rol', 'ciclo_actual', 'seccion', 'carrera', 'departamento',
            'telefono'
        ]