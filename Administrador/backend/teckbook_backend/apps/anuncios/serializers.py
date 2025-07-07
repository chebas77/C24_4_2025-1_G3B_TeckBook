from rest_framework import serializers
from apps.anuncios.models import Anuncio, Comentario, Like, Lectura
from apps.usuarios.models import Usuario
from apps.aulas.models import AulaVirtual
from apps.usuarios.serializers import UsuarioSerializer

class AnuncioSerializer(serializers.ModelSerializer):
    # Campos calculados para el frontend
    autor_nombre = serializers.SerializerMethodField()
    aula_titulo = serializers.SerializerMethodField()
    
    class Meta:
        model = Anuncio
        fields = [
            'id', 'titulo', 'contenido', 'tipo', 'categoria',
            'autor', 'autor_nombre', 'aula', 'aula_titulo',
            'fecha_publicacion', 'activo', 'censurado',
            'motivo_censura', 'total_likes', 'total_comentarios'
        ]
    
    def get_autor_nombre(self, obj):
        return f"{obj.autor.nombre} {obj.autor.apellidos}" if obj.autor else ""
    
    def get_aula_titulo(self, obj):
        return obj.aula.titulo if obj.aula else "General"

class AutorBasicoSerializer(serializers.ModelSerializer):
    """Serializer básico para mostrar información del autor"""
    nombre_completo = serializers.CharField(read_only=True)
    
    class Meta:
        model = Usuario
        fields = ['id', 'nombre', 'apellidos', 'nombre_completo', 
                 'correo_institucional', 'rol', 'profile_image_url']

class AulaBasicaSerializer(serializers.ModelSerializer):
    """Serializer básico para mostrar información del aula"""
    
    class Meta:
        model = AulaVirtual
        fields = ['id', 'titulo', 'nombre', 'codigo_acceso', 'estado']

class AnuncioModerationSerializer(serializers.ModelSerializer):
    """Serializer para moderación de anuncios"""
    autor = AutorBasicoSerializer(read_only=True)
    aula = AulaBasicaSerializer(read_only=True)
    tiempo_publicacion = serializers.SerializerMethodField()
    estado_moderacion = serializers.SerializerMethodField()
    estadisticas = serializers.SerializerMethodField()
    
    class Meta:
        model = Anuncio
        fields = [
            'id', 'titulo', 'contenido', 'tipo', 'categoria', 'etiquetas',
            'archivo_url', 'archivo_nombre', 'archivo_tipo', 'archivo_tamaño',
            'autor', 'aula', 'fecha_publicacion', 'fecha_edicion',
            'activo', 'es_general', 'fijado', 'permite_comentarios',
            'total_likes', 'total_comentarios', 'es_recurso_academico',
            'tiempo_publicacion', 'estado_moderacion', 'estadisticas'
        ]
    
    def get_tiempo_publicacion(self, obj):
        """Calcula tiempo relativo desde la publicación"""
        from django.utils import timezone
        from datetime import timedelta
        
        ahora = timezone.now()
        diferencia = ahora - obj.fecha_publicacion
        
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
        return {
            'likes': obj.total_likes,
            'comentarios': obj.total_comentarios,
            'lecturas': obj.lecturas.count() if hasattr(obj, 'lecturas') else 0,
            'engagement_rate': self._calcular_engagement(obj)
        }
    
    def _calcular_engagement(self, obj):
        """Calcula tasa de engagement básica"""
        total_interacciones = obj.total_likes + obj.total_comentarios
        if obj.aula:
            total_estudiantes = obj.aula.aulaestudiante_set.filter(estado='activo').count()
            if total_estudiantes > 0:
                return round((total_interacciones / total_estudiantes) * 100, 2)
        return 0

class UsuarioModerationSerializer(serializers.ModelSerializer):
    """Serializer para moderación de usuarios"""
    departamento = serializers.CharField(source='departamento.nombre', read_only=True)
    carrera = serializers.CharField(source='carrera.nombre', read_only=True)
    ciclo_actual_nombre = serializers.CharField(source='ciclo_actual.nombre', read_only=True)
    seccion_codigo = serializers.CharField(source='seccion.codigo', read_only=True)
    nombre_completo = serializers.CharField(read_only=True)
    tiempo_registro = serializers.SerializerMethodField()
    estado_cuenta = serializers.SerializerMethodField()
    estadisticas_actividad = serializers.SerializerMethodField()
    ultimo_acceso = serializers.SerializerMethodField()
    
    class Meta:
        model = Usuario
        fields = [
            'id', 'nombre', 'apellidos', 'nombre_completo', 'correo_institucional',
            'rol', 'ciclo_actual_nombre', 'seccion_codigo', 'carrera', 'departamento',
            'profile_image_url', 'telefono', 'is_active', 'created_at', 'updated_at',
            'tiempo_registro', 'estado_cuenta', 'estadisticas_actividad', 'ultimo_acceso'
        ]
    
    def get_tiempo_registro(self, obj):
        """Calcula tiempo desde el registro"""
        from django.utils import timezone
        
        ahora = timezone.now()
        diferencia = ahora - obj.created_at
        
        if diferencia.days > 0:
            return f"hace {diferencia.days} día{'s' if diferencia.days > 1 else ''}"
        elif diferencia.seconds >= 3600:
            horas = diferencia.seconds // 3600
            return f"hace {horas} hora{'s' if horas > 1 else ''}"
        else:
            return "recién registrado"
    
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
    
    def get_estadisticas_actividad(self, obj):
        """Obtiene estadísticas de actividad del usuario"""
        from django.utils import timezone
        from datetime import timedelta
        
        hace_30_dias = timezone.now() - timedelta(days=30)
        
        anuncios_totales = Anuncio.objects.filter(autor=obj).count()
        anuncios_recientes = Anuncio.objects.filter(
            autor=obj, 
            fecha_publicacion__gte=hace_30_dias
        ).count()
        
        comentarios_totales = Comentario.objects.filter(usuario=obj).count()
        comentarios_recientes = Comentario.objects.filter(
            usuario=obj,
            fecha_creacion__gte=hace_30_dias
        ).count()
        
        likes_dados = Like.objects.filter(usuario=obj).count()
        
        return {
            'anuncios_totales': anuncios_totales,
            'anuncios_recientes': anuncios_recientes,
            'comentarios_totales': comentarios_totales,
            'comentarios_recientes': comentarios_recientes,
            'likes_dados': likes_dados,
            'actividad_score': self._calcular_actividad_score(
                anuncios_recientes, comentarios_recientes, likes_dados
            )
        }
    
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
            from django.utils import timezone
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
    
    def _calcular_actividad_score(self, anuncios, comentarios, likes):
        """Calcula un score de actividad del usuario"""
        # Peso: anuncios (3 puntos), comentarios (2 puntos), likes (1 punto)
        score = (anuncios * 3) + (comentarios * 2) + likes
        
        # Categorizar el score
        if score >= 50:
            return {'valor': score, 'nivel': 'muy_activo', 'color': 'success'}
        elif score >= 20:
            return {'valor': score, 'nivel': 'activo', 'color': 'info'}
        elif score >= 5:
            return {'valor': score, 'nivel': 'moderado', 'color': 'warning'}
        else:
            return {'valor': score, 'nivel': 'bajo', 'color': 'secondary'}

class DashboardStatsSerializer(serializers.Serializer):
    """Serializer para estadísticas del dashboard"""
    total_usuarios = serializers.IntegerField()
    usuarios_activos = serializers.IntegerField()
    publicaciones_hoy = serializers.IntegerField()
    contenido_pendiente = serializers.IntegerField()
    total_aulas = serializers.IntegerField()
    aulas_activas = serializers.IntegerField()
    
    publicaciones_semanales = serializers.ListField(
        child=serializers.DictField()
    )
    aulas_pobladas = serializers.ListField(
        child=serializers.DictField()
    )
    anuncios_por_aula = serializers.ListField(
        child=serializers.DictField()
    )
    alertas_recientes = serializers.ListField(
        child=serializers.DictField()
    )

class EstadisticasDetalladas(serializers.Serializer):
    """Serializer para estadísticas detalladas"""
    periodo = serializers.CharField()
    anuncios_por_dia = serializers.ListField(
        child=serializers.DictField()
    )
    usuarios_activos_por_dia = serializers.ListField(
        child=serializers.DictField()
    )
    engagement_por_tipo = serializers.ListField(
        child=serializers.DictField()
    )

class AulaPopularSerializer(serializers.Serializer):
    """Serializer para aulas populares"""
    id = serializers.IntegerField()
    titulo = serializers.CharField()
    codigo_acceso = serializers.CharField()
    profesor = serializers.CharField()
    total_estudiantes = serializers.IntegerField()
    total_anuncios = serializers.IntegerField()
    total_likes = serializers.IntegerField()
    total_comentarios = serializers.IntegerField()
    engagement_score = serializers.FloatField()

class ActividadRecenteSerializer(serializers.Serializer):
    """Serializer para actividades recientes"""
    tipo = serializers.CharField()
    usuario = serializers.CharField()
    accion = serializers.CharField()
    fecha = serializers.DateTimeField()
    aula = serializers.CharField()