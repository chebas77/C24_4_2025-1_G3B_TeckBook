from rest_framework import serializers
from djoser.serializers import UserCreateSerializer as BaseUserCreateSerializer
from djoser.serializers import UserSerializer as BaseUserSerializer
from apps.usuarios.models import Usuario, Departamento, Carrera, Ciclo, Seccion
from rest_framework import serializers
from django.utils import timezone
from django.contrib.auth.hashers import make_password
from datetime import timedelta
from apps.usuarios.models import Usuario, Departamento, Carrera
from apps.aulas.models import AulaVirtual, AulaEstudiante
from apps.anuncios.models import Anuncio, Comentario, Like, Lectura
from .models import LogModeracion, ConfiguracionSistema, EstadisticasSistema
from .models import HistorialModeracion

class HistorialModeracionSerializer(serializers.ModelSerializer):
    moderador = serializers.StringRelatedField()

    class Meta:
        model = HistorialModeracion
        fields = ['id', 'accion', 'comentario', 'fecha', 'moderador']
        
class UsuarioAdminSerializer(serializers.ModelSerializer):
    """Serializer para gestión de usuarios por administradores"""
    nombre_completo = serializers.CharField(read_only=True)
    departamento_nombre = serializers.CharField(source='departamento.nombre', read_only=True)
    carrera_nombre = serializers.CharField(source='carrera.nombre', read_only=True)
    seccion_codigo = serializers.CharField(source='seccion.codigo', read_only=True)
    tiempo_registro = serializers.SerializerMethodField()
    ultimo_acceso = serializers.SerializerMethodField()
    estadisticas_actividad = serializers.SerializerMethodField()
    estado_cuenta = serializers.SerializerMethodField()
    password = serializers.CharField(write_only=True, required=False)
    
    class Meta:
        model = Usuario
        fields = [
            'id', 'nombre', 'apellidos', 'nombre_completo', 'correo_institucional',
            'rol', 'ciclo_actual', 'telefono', 'is_active', 'profile_image_url',
            'departamento', 'departamento_nombre', 'carrera', 'carrera_nombre',
            'seccion_codigo', 'created_at', 'updated_at', 'tiempo_registro',
            'ultimo_acceso', 'estadisticas_actividad', 'estado_cuenta', 'password'
        ]
        read_only_fields = ['created_at', 'updated_at']
        extra_kwargs = {
            'password': {'write_only': True, 'required': False}
        }
    
    def create(self, validated_data):
        """Crear nuevo usuario con contraseña hasheada"""
        password = validated_data.pop('password', None)
        user = Usuario.objects.create(**validated_data)
        if password:
            user.set_password(password)
            user.save()
        return user
    
    def update(self, instance, validated_data):
        """Actualizar usuario con manejo de contraseña"""
        password = validated_data.pop('password', None)
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        
        if password:
            instance.set_password(password)
        
        instance.save()
        return instance
    
    def get_tiempo_registro(self, obj):
        """Calcula tiempo desde el registro"""
        diferencia = timezone.now() - obj.created_at
        if diferencia.days > 0:
            return f"hace {diferencia.days} día{'s' if diferencia.days > 1 else ''}"
        elif diferencia.seconds >= 3600:
            horas = diferencia.seconds // 3600
            return f"hace {horas} hora{'s' if horas > 1 else ''}"
        else:
            return "recién registrado"
    
    def get_ultimo_acceso(self, obj):
        """Estima último acceso basado en actividad reciente"""
        # Buscar la actividad más reciente del usuario
        ultimo_anuncio = Anuncio.objects.filter(autor=obj).order_by('-fecha_publicacion').first()
        ultimo_comentario = Comentario.objects.filter(usuario=obj).order_by('-fecha_creacion').first()
        ultimo_like = Like.objects.filter(usuario=obj).order_by('-fecha_creacion').first()
        
        fechas = []
        if ultimo_anuncio:
            fechas.append(ultimo_anuncio.fecha_publicacion)
        if ultimo_comentario:
            fechas.append(ultimo_comentario.fecha_creacion)
        if ultimo_like:
            fechas.append(ultimo_like.fecha_creacion)
        
        if fechas:
            ultima_actividad = max(fechas)
            diferencia = timezone.now() - ultima_actividad
            
            if diferencia.days > 7:
                return f"hace {diferencia.days} días"
            elif diferencia.days > 0:
                return f"hace {diferencia.days} día{'s' if diferencia.days > 1 else ''}"
            elif diferencia.seconds >= 3600:
                horas = diferencia.seconds // 3600
                return f"hace {horas} hora{'s' if horas > 1 else ''}"
            else:
                return "activo recientemente"
        else:
            return "sin actividad reciente"
    
    def get_estadisticas_actividad(self, obj):
        """Obtiene estadísticas de actividad del usuario"""
        hace_30_dias = timezone.now() - timedelta(days=30)
        
        anuncios_totales = Anuncio.objects.filter(autor=obj).count()
        anuncios_recientes = Anuncio.objects.filter(
            autor=obj, 
            fecha_publicacion__gte=hace_30_dias
        ).count()
        
        comentarios_totales = Comentario.objects.filter(usuario=obj).count()
        likes_dados = Like.objects.filter(usuario=obj).count()
        
        # Para estudiantes, contar aulas
        aulas_inscritas = 0
        if obj.rol == 'ESTUDIANTE':
            aulas_inscritas = AulaEstudiante.objects.filter(
                estudiante=obj, 
                estado='activo'
            ).count()
        
        # Para profesores, contar aulas que enseñan
        aulas_enseñadas = 0
        if obj.rol == 'PROFESOR':
            aulas_enseñadas = AulaVirtual.objects.filter(
                profesor=obj, 
                estado='activa'
            ).count()
        
        return {
            'anuncios_totales': anuncios_totales,
            'anuncios_recientes': anuncios_recientes,
            'comentarios_totales': comentarios_totales,
            'likes_dados': likes_dados,
            'aulas_inscritas': aulas_inscritas,
            'aulas_enseñadas': aulas_enseñadas
        }
    
    def get_estado_cuenta(self, obj):
        """Determina el estado visual de la cuenta"""
        if not obj.is_active:
            return {
                'estado': 'suspendido',
                'color': 'danger',
                'texto': 'Suspendido'
            }
        elif obj.rol == 'ADMINISTRADOR':
            return {
                'estado': 'admin',
                'color': 'primary',
                'texto': 'Administrador'
            }
        elif obj.rol == 'PROFESOR':
            return {
                'estado': 'profesor',
                'color': 'info',
                'texto': 'Profesor'
            }
        else:
            return {
                'estado': 'estudiante',
                'color': 'success',
                'texto': 'Estudiante'
            }

class AnuncioModeracionSerializer(serializers.ModelSerializer):
    """Serializer para moderación de anuncios"""
    autor_nombre = serializers.CharField(source='autor.nombre_completo', read_only=True)
    autor_rol = serializers.CharField(source='autor.rol', read_only=True)
    aula_titulo = serializers.CharField(source='aula.titulo', read_only=True)
    tiempo_publicacion = serializers.SerializerMethodField()
    estado_moderacion = serializers.SerializerMethodField()
    estadisticas = serializers.SerializerMethodField()
    riesgo_nivel = serializers.SerializerMethodField()
    
    class Meta:
        model = Anuncio
        fields = [
            'id', 'titulo', 'contenido', 'tipo', 'categoria', 'etiquetas',
            'archivo_url', 'archivo_nombre', 'archivo_tipo', 'archivo_tamaño',
            'autor', 'autor_nombre', 'autor_rol', 'aula', 'aula_titulo',
            'fecha_publicacion', 'fecha_edicion', 'activo', 'es_general',
            'fijado', 'permite_comentarios', 'total_likes', 'total_comentarios',
            'es_recurso_academico', 'tiempo_publicacion', 'estado_moderacion',
            'estadisticas', 'riesgo_nivel'
        ]
        read_only_fields = ['fecha_publicacion', 'fecha_edicion', 'total_likes', 'total_comentarios']
    
    def get_tiempo_publicacion(self, obj):
        """Calcula tiempo relativo desde la publicación"""
        diferencia = timezone.now() - obj.fecha_publicacion
        
        if diferencia.days > 0:
            return f"hace {diferencia.days} día{'s' if diferencia.days > 1 else ''}"
        elif diferencia.seconds >= 3600:
            horas = diferencia.seconds // 3600
            return f"hace {horas} hora{'s' if horas > 1 else ''}"
        elif diferencia.seconds >= 60:
            minutos = diferencia.seconds // 60
            return f"hace {minutos} minuto{'s' if minutos > 1 else ''}"
        else:
            return "recién publicado"
    
    def get_estado_moderacion(self, obj):
        """Determina el estado de moderación del anuncio"""
        if not obj.activo:
            return {
                'estado': 'censurado',
                'color': 'danger',
                'texto': 'Censurado'
            }
        elif obj.fecha_publicacion and (timezone.now() - obj.fecha_publicacion).days < 1:
            return {
                'estado': 'reciente',
                'color': 'warning',
                'texto': 'Reciente'
            }
        else:
            return {
                'estado': 'aprobado',
                'color': 'success',
                'texto': 'Activo'
            }
    
    def get_estadisticas(self, obj):
        """Obtiene estadísticas adicionales del anuncio"""
        lecturas_count = Lectura.objects.filter(anuncio=obj).count()
        comentarios_activos = Comentario.objects.filter(anuncio=obj, activo=True).count()
        
        return {
            'likes': obj.total_likes,
            'comentarios': comentarios_activos,
            'lecturas': lecturas_count,
            'engagement_rate': self._calcular_engagement(obj, lecturas_count)
        }
    
    def get_riesgo_nivel(self, obj):
        """Evalúa el nivel de riesgo del contenido para moderación"""
        # Factores de riesgo básicos
        score = 0
        
        # Contenido con palabras potencialmente problemáticas
        palabras_riesgo = ['spam', 'promocion', 'venta', 'compra', 'dinero']
        contenido_lower = obj.contenido.lower()
        
        for palabra in palabras_riesgo:
            if palabra in contenido_lower:
                score += 1
        
        # Anuncios con archivos externos
        if obj.archivo_url and not obj.archivo_url.startswith(('http://tecsup', 'https://tecsup')):
            score += 1
        
        # Anuncios muy recientes del mismo usuario
        anuncios_recientes = Anuncio.objects.filter(
            autor=obj.autor,
            fecha_publicacion__gte=timezone.now() - timedelta(hours=1)
        ).count()
        
        if anuncios_recientes > 3:
            score += 2
        
        # Determinar nivel
        if score >= 3:
            return {'nivel': 'alto', 'color': 'danger', 'score': score}
        elif score >= 1:
            return {'nivel': 'medio', 'color': 'warning', 'score': score}
        else:
            return {'nivel': 'bajo', 'color': 'success', 'score': score}
    
    def _calcular_engagement(self, obj, lecturas):
        """Calcula tasa de engagement básica"""
        total_interacciones = obj.total_likes + obj.total_comentarios + lecturas
        if obj.aula:
            total_estudiantes = AulaEstudiante.objects.filter(
                aula=obj.aula, 
                estado='activo'
            ).count()
            if total_estudiantes > 0:
                return round((total_interacciones / total_estudiantes) * 100, 2)
        return 0

class LogModeracionSerializer(serializers.ModelSerializer):
    """Serializer para logs de moderación"""
    administrador_nombre = serializers.CharField(source='administrador.nombre_completo', read_only=True)
    usuario_afectado_nombre = serializers.CharField(source='usuario_afectado.nombre_completo', read_only=True)
    anuncio_titulo = serializers.CharField(source='anuncio_afectado.titulo', read_only=True)
    tiempo_accion = serializers.SerializerMethodField()
    accion_display = serializers.CharField(source='get_accion_display', read_only=True)
    
    class Meta:
        model = LogModeracion
        fields = [
            'id', 'administrador', 'administrador_nombre', 'accion', 'accion_display',
            'usuario_afectado', 'usuario_afectado_nombre', 'anuncio_afectado',
            'anuncio_titulo', 'descripcion', 'motivo', 'datos_adicionales',
            'created_at', 'tiempo_accion'
        ]
    
    def get_tiempo_accion(self, obj):
        """Calcula tiempo relativo desde la acción"""
        diferencia = timezone.now() - obj.created_at
        
        if diferencia.days > 0:
            return f"hace {diferencia.days} día{'s' if diferencia.days > 1 else ''}"
        elif diferencia.seconds >= 3600:
            horas = diferencia.seconds // 3600
            return f"hace {horas} hora{'s' if horas > 1 else ''}"
        elif diferencia.seconds >= 60:
            minutos = diferencia.seconds // 60
            return f"hace {minutos} minuto{'s' if minutos > 1 else ''}"
        else:
            return "recién realizada"

class ConfiguracionSistemaSerializer(serializers.ModelSerializer):
    """Serializer para configuraciones del sistema"""
    valor_actual = serializers.SerializerMethodField()
    
    class Meta:
        model = ConfiguracionSistema
        fields = [
            'id', 'clave', 'valor', 'valor_actual', 'descripcion', 
            'tipo', 'categoria', 'es_publica', 'created_at', 'updated_at'
        ]
    
    def get_valor_actual(self, obj):
        """Retorna el valor convertido al tipo correcto"""
        return obj.get_valor_typed()

class EstadisticasSistemaSerializer(serializers.ModelSerializer):
    """Serializer para estadísticas del sistema"""
    valor_formateado = serializers.SerializerMethodField()
    
    class Meta:
        model = EstadisticasSistema
        fields = [
            'id', 'fecha', 'tipo_estadistica', 'categoria', 'valor',
            'valor_decimal', 'valor_formateado', 'metadata', 'created_at'
        ]
    
    def get_valor_formateado(self, obj):
        """Formatea el valor para visualización"""
        if obj.valor_decimal:
            return f"{obj.valor_decimal:,.2f}"
        else:
            return f"{obj.valor:,}"

class CensurarAnuncioSerializer(serializers.Serializer):
    """Serializer para censurar anuncios"""
    motivo = serializers.CharField(max_length=500, required=True)
    aplicar_strike = serializers.BooleanField(default=False)
    
    def validate_motivo(self, value):
        if len(value.strip()) < 10:
            raise serializers.ValidationError("El motivo debe tener al menos 10 caracteres")
        return value.strip()

class SuspenderUsuarioSerializer(serializers.Serializer):
    """Serializer para suspender usuarios"""
    motivo = serializers.CharField(max_length=500, required=True)
    
    def validate_motivo(self, value):
        if len(value.strip()) < 10:
            raise serializers.ValidationError("El motivo debe tener al menos 10 caracteres")
        return value.strip()

class CrearProfesorSerializer(serializers.ModelSerializer):
    """Serializer específico para crear profesores"""
    password = serializers.CharField(write_only=True, min_length=8)
    confirm_password = serializers.CharField(write_only=True)
    
    class Meta:
        model = Usuario
        fields = [
            'nombre', 'apellidos', 'correo_institucional', 'password', 
            'confirm_password', 'departamento', 'carrera', 'telefono', 
            'profile_image_url'
        ]
    
    def validate(self, attrs):
        if attrs['password'] != attrs['confirm_password']:
            raise serializers.ValidationError("Las contraseñas no coinciden")
        return attrs
    
    def validate_correo_institucional(self, value):
        if not value.endswith('@tecsup.edu.pe'):
            raise serializers.ValidationError("Debe ser un correo institucional de Tecsup")
        return value
    
    def create(self, validated_data):
        validated_data.pop('confirm_password')
        password = validated_data.pop('password')
        
        # Forzar rol de profesor
        validated_data['rol'] = 'PROFESOR'
        
        usuario = Usuario.objects.create(**validated_data)
        usuario.set_password(password)
        usuario.save()
        
        return usuario

class DashboardStatsSerializer(serializers.Serializer):
    """Serializer para estadísticas del dashboard"""
    estadisticas_basicas = serializers.DictField()
    publicaciones_semanales = serializers.ListField(child=serializers.DictField())
    aulas_pobladas = serializers.ListField(child=serializers.DictField())
    anuncios_por_aula = serializers.ListField(child=serializers.DictField())
    alertas_recientes = serializers.ListField(child=serializers.DictField())

class AulaPopularSerializer(serializers.Serializer):
    """Serializer para aulas populares en estadísticas"""
    id = serializers.IntegerField()
    titulo = serializers.CharField()
    codigo_acceso = serializers.CharField()
    profesor = serializers.CharField()
    total_estudiantes = serializers.IntegerField()
    total_anuncios = serializers.IntegerField()
    engagement_score = serializers.FloatField()

class EstadisticasGeneralesSerializer(serializers.Serializer):
    """Serializer para estadísticas generales"""
    periodo_dias = serializers.CharField()
    contenido = serializers.DictField()
    engagement = serializers.DictField()
    anuncios_populares = serializers.ListField(child=serializers.DictField())
    usuarios_activos = serializers.DictField()
    aulas_estadisticas = serializers.DictField()

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