from rest_framework import serializers
from .models import AulaVirtual, AulaEstudiante, InvitacionAula
from apps.usuarios.serializers import UsuarioSerializer

class AulaVirtualSerializer(serializers.ModelSerializer):
    profesor = UsuarioSerializer(read_only=True)
    total_estudiantes = serializers.SerializerMethodField()
    total_anuncios = serializers.SerializerMethodField()
    estudiante_inscrito = serializers.SerializerMethodField()
    fecha_creacion = serializers.DateTimeField(source='created_at', read_only=True)
    
    class Meta:
        model = AulaVirtual
        fields = [
            'id', 'nombre', 'titulo', 'descripcion', 'codigo_acceso',
            'profesor', 'seccion', 'estado', 'fecha_inicio', 'fecha_fin',
            'fecha_creacion', 'total_estudiantes', 'total_anuncios',
            'estudiante_inscrito', 'created_at', 'updated_at'
        ]
    
    def get_total_estudiantes(self, obj):
        return obj.aulaestudiante_set.filter(estado='activo').count()
    
    def get_total_anuncios(self, obj):
        return obj.anuncio_set.filter(activo=True).count()
    
    def get_estudiante_inscrito(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated and request.user.rol == 'ESTUDIANTE':
            return obj.aulaestudiante_set.filter(
                estudiante=request.user, 
                estado='activo'
            ).exists()
        return False

class AulaEstudianteSerializer(serializers.ModelSerializer):
    estudiante = UsuarioSerializer(read_only=True)
    aula = AulaVirtualSerializer(read_only=True)
    tiempo_inscripcion = serializers.SerializerMethodField()
    
    class Meta:
        model = AulaEstudiante
        fields = [
            'id', 'aula', 'estudiante', 'estado', 'fecha_union',
            'fecha_salida', 'fecha_inscripcion', 'tiempo_inscripcion'
        ]
    
    def get_tiempo_inscripcion(self, obj):
        from django.utils import timezone
        fecha_ref = obj.fecha_union or obj.created_at
        if fecha_ref:
            diferencia = timezone.now() - fecha_ref
            if diferencia.days > 0:
                return f"hace {diferencia.days} día{'s' if diferencia.days > 1 else ''}"
            elif diferencia.seconds >= 3600:
                horas = diferencia.seconds // 3600
                return f"hace {horas} hora{'s' if horas > 1 else ''}"
            else:
                return "recientemente"
        return "fecha no disponible"

class InvitacionAulaSerializer(serializers.ModelSerializer):
    aula_virtual = AulaVirtualSerializer(read_only=True)
    invitado_por = UsuarioSerializer(read_only=True)
    tiempo_invitacion = serializers.SerializerMethodField()
    estado_visual = serializers.SerializerMethodField()
    
    class Meta:
        model = InvitacionAula
        fields = [
            'id', 'aula_virtual', 'invitado_por', 'correo_invitado',
            'codigo_invitacion', 'estado', 'mensaje', 'fecha_invitacion',
            'fecha_expiracion', 'fecha_respuesta', 'tiempo_invitacion',
            'estado_visual'
        ]
    
    def get_tiempo_invitacion(self, obj):
        from django.utils import timezone
        diferencia = timezone.now() - obj.fecha_invitacion
        
        if diferencia.days > 0:
            return f"hace {diferencia.days} día{'s' if diferencia.days > 1 else ''}"
        elif diferencia.seconds >= 3600:
            horas = diferencia.seconds // 3600
            return f"hace {horas} hora{'s' if horas > 1 else ''}"
        else:
            return "recientemente"
    
    def get_estado_visual(self, obj):
        if obj.estado == 'pendiente':
            return {'color': 'warning', 'texto': 'Pendiente'}
        elif obj.estado == 'aceptada':
            return {'color': 'success', 'texto': 'Aceptada'}
        elif obj.estado == 'rechazada':
            return {'color': 'danger', 'texto': 'Rechazada'}
        else:
            return {'color': 'secondary', 'texto': 'Expirada'}