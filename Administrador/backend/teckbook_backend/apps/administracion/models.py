from django.db import models

# Create your models here.
# apps/administracion/models.py
# Crear este archivo completo en tu proyecto

from django.db import models
from apps.core.models import BaseModel

class LogModeracion(BaseModel):
    """Log de acciones de moderación realizadas por administradores"""
    ACCIONES = [
        ('censurar_anuncio', 'Censurar Anuncio'),
        ('descensurar_anuncio', 'Descensurar Anuncio'),
        ('strike_usuario', 'Aplicar Strike a Usuario'),
        ('suspender_usuario', 'Suspender Usuario'),
        ('reactivar_usuario', 'Reactivar Usuario'),
        ('crear_profesor', 'Crear Profesor'),
        ('eliminar_anuncio', 'Eliminar Anuncio'),
        ('advertencia_usuario', 'Advertencia a Usuario'),
    ]
    
    # Usar IDs simples sin foreign keys por ahora
    administrador = models.ForeignKey(
        'usuarios.Usuario', 
        on_delete=models.CASCADE, 
        related_name='acciones_moderacion'
    )
    accion = models.CharField(max_length=30, choices=ACCIONES)
    usuario_afectado = models.ForeignKey(
        'usuarios.Usuario', 
        on_delete=models.CASCADE, 
        related_name='logs_recibidos',
        null=True, 
        blank=True
    )
    anuncio_afectado = models.ForeignKey(
        'anuncios.Anuncio', 
        on_delete=models.CASCADE,
        null=True, 
        blank=True
    )
    descripcion = models.TextField()
    motivo = models.TextField(null=True, blank=True)
    datos_adicionales = models.JSONField(null=True, blank=True)
    
    class Meta:
        db_table = 'logs_moderacion'
        verbose_name = 'Log de Moderación'
        verbose_name_plural = 'Logs de Moderación'
        ordering = ['-created_at']
    
    def __str__(self):
        return f"Admin {self.administrador_id} - {self.get_accion_display()}"
    
    # Métodos para obtener objetos relacionados manualmente
    def get_administrador(self):
        from apps.usuarios.models import Usuario
        try:
            return Usuario.objects.get(id=self.administrador_id)
        except Usuario.DoesNotExist:
            return None
    
    def get_usuario_afectado(self):
        from apps.usuarios.models import Usuario
        if self.usuario_afectado_id:
            try:
                return Usuario.objects.get(id=self.usuario_afectado_id)
            except Usuario.DoesNotExist:
                return None
        return None
    
    def get_anuncio_afectado(self):
        from apps.anuncios.models import Anuncio
        if self.anuncio_afectado_id:
            try:
                return Anuncio.objects.get(id=self.anuncio_afectado_id)
            except Anuncio.DoesNotExist:
                return None
        return None

class ConfiguracionSistema(BaseModel):
    """Configuraciones del sistema - tabla independiente"""
    clave = models.CharField(max_length=100, unique=True)
    valor = models.TextField()
    descripcion = models.TextField(blank=True)
    tipo = models.CharField(
        max_length=20,
        choices=[
            ('entero', 'Entero'),
            ('decimal', 'Decimal'), 
            ('texto', 'Texto'),
            ('booleano', 'Booleano'),
            ('json', 'JSON'),
        ],
        default='texto'
    )
    categoria = models.CharField(max_length=50, default='general')
    es_publica = models.BooleanField(default=False)
    
    class Meta:
        db_table = 'configuracion_sistema'
        verbose_name = 'Configuración del Sistema'
        verbose_name_plural = 'Configuraciones del Sistema'
    
    def __str__(self):
        return f"{self.clave}: {self.valor}"
    
    def get_valor_typed(self):
        """Retorna el valor convertido al tipo correcto"""
        if self.tipo == 'entero':
            return int(self.valor)
        elif self.tipo == 'decimal':
            return float(self.valor)
        elif self.tipo == 'booleano':
            return self.valor.lower() in ['true', '1', 'yes', 'si']
        elif self.tipo == 'json':
            import json
            return json.loads(self.valor)
        else:
            return self.valor

class EstadisticasSistema(BaseModel):
    """Estadísticas del sistema - tabla independiente"""
    fecha = models.DateField()
    tipo_estadistica = models.CharField(max_length=50)
    categoria = models.CharField(max_length=50)
    valor = models.IntegerField()
    valor_decimal = models.DecimalField(max_digits=10, decimal_places=2, null=True, blank=True)
    metadata = models.JSONField(null=True, blank=True)
    
    class Meta:
        db_table = 'estadisticas_sistema'
        verbose_name = 'Estadística del Sistema'
        verbose_name_plural = 'Estadísticas del Sistema'
        unique_together = ['fecha', 'tipo_estadistica', 'categoria']
    
    def __str__(self):
        return f"{self.fecha} - {self.tipo_estadistica}: {self.valor}"

# Clase auxiliar para manejar moderación sin modificar Anuncio
class ModeracionAnuncio:
    """Clase auxiliar para manejar moderación de anuncios"""
    
    @staticmethod
    def censurar_anuncio(anuncio_id, admin_id, motivo):
        """Censura un anuncio desactivándolo"""
        from apps.anuncios.models import Anuncio
        try:
            anuncio = Anuncio.objects.get(id=anuncio_id)
            anuncio.activo = False
            anuncio.save()
            
            # Registrar en log
            LogModeracion.objects.create(
                administrador_id=admin_id,
                accion='censurar_anuncio',
                anuncio_afectado_id=anuncio_id,
                usuario_afectado_id=anuncio.autor_id,
                descripcion=f"Anuncio '{anuncio.titulo}' censurado",
                motivo=motivo
            )
            return True
        except Anuncio.DoesNotExist:
            return False
    
    @staticmethod
    def descensurar_anuncio(anuncio_id, admin_id):
        """Reactiva un anuncio censurado"""
        from apps.anuncios.models import Anuncio
        try:
            anuncio = Anuncio.objects.get(id=anuncio_id)
            anuncio.activo = True
            anuncio.save()
            
            # Registrar en log
            LogModeracion.objects.create(
                administrador_id=admin_id,
                accion='descensurar_anuncio',
                anuncio_afectado_id=anuncio_id,
                usuario_afectado_id=anuncio.autor_id,
                descripcion=f"Anuncio '{anuncio.titulo}' reactivado"
            )
            return True
        except Anuncio.DoesNotExist:
            return False
    
    @staticmethod
    def aplicar_strike(usuario_id, admin_id, motivo):
        """Aplica un strike a un usuario"""
        LogModeracion.objects.create(
            administrador_id=admin_id,
            accion='strike_usuario',
            usuario_afectado_id=usuario_id,
            descripcion=f"Strike aplicado a usuario ID {usuario_id}",
            motivo=motivo
        )
        
        # Contar strikes del usuario
        strikes = LogModeracion.objects.filter(
            usuario_afectado_id=usuario_id,
            accion='strike_usuario'
        ).count()
        
        return strikes
    
    @staticmethod
    def suspender_usuario(usuario_id, admin_id, motivo):
        """Suspende un usuario"""
        from apps.usuarios.models import Usuario
        try:
            usuario = Usuario.objects.get(id=usuario_id)
            usuario.is_active = False
            usuario.save()
            
            # Registrar en log
            LogModeracion.objects.create(
                administrador_id=admin_id,
                accion='suspender_usuario',
                usuario_afectado_id=usuario_id,
                descripcion=f"Usuario '{usuario.nombre_completo}' suspendido",
                motivo=motivo
            )
            return True
        except Usuario.DoesNotExist:
            return False
    
    @staticmethod
    def reactivar_usuario(usuario_id, admin_id):
        """Reactiva un usuario suspendido"""
        from apps.usuarios.models import Usuario
        try:
            usuario = Usuario.objects.get(id=usuario_id)
            usuario.is_active = True
            usuario.save()
            
            # Registrar en log
            LogModeracion.objects.create(
                administrador_id=admin_id,
                accion='reactivar_usuario',
                usuario_afectado_id=usuario_id,
                descripcion=f"Usuario '{usuario.nombre_completo}' reactivado"
            )
            return True
        except Usuario.DoesNotExist:
            return False