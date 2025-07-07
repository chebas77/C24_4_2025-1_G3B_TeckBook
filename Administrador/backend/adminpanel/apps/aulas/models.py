from django.db import models
from apps.core.models import BaseModel
from apps.usuarios.models import Usuario, Seccion

class AulaVirtual(BaseModel):
    ESTADOS = [
        ('activa', 'Activa'),
        ('inactiva', 'Inactiva'),
        ('archivada', 'Archivada'),
    ]
    
    nombre = models.CharField(max_length=255, null=True, blank=True)
    titulo = models.CharField(max_length=255, null=True, blank=True)
    descripcion = models.CharField(max_length=255, null=True, blank=True)
    codigo_acceso = models.CharField(max_length=255, unique=True, null=True, blank=True)
    profesor = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    seccion = models.ForeignKey(Seccion, on_delete=models.SET_NULL, null=True, blank=True)
    estado = models.CharField(max_length=20, choices=ESTADOS, null=True, blank=True)
    fecha_inicio = models.DateField(null=True, blank=True)
    fecha_fin = models.DateField(null=True, blank=True)
    
    class Meta:
        db_table = 'aulas_virtuales'
        verbose_name = 'Aula Virtual'
        verbose_name_plural = 'Aulas Virtuales'
    
    def __str__(self):
        return f"{self.titulo} - {self.nombre}"

class AulaEstudiante(BaseModel):
    ESTADOS = [
        ('activo', 'Activo'),
        ('inactivo', 'Inactivo'),
    ]
    
    aula = models.ForeignKey(AulaVirtual, on_delete=models.CASCADE)
    estudiante = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    estado = models.CharField(max_length=20, choices=ESTADOS, null=True, blank=True)
    fecha_union = models.DateTimeField(auto_now_add=True)
    fecha_salida = models.DateTimeField(null=True, blank=True)
    fecha_inscripcion = models.DateTimeField(null=True, blank=True)
    profesor = models.ForeignKey(Usuario, related_name='estudiantes_inscritos', 
                                on_delete=models.SET_NULL, null=True, blank=True)
    
    class Meta:
        db_table = 'aula_estudiantes'
        verbose_name = 'Estudiante en Aula'
        verbose_name_plural = 'Estudiantes en Aulas'
        unique_together = ['aula', 'estudiante']
    
    def __str__(self):
        return f"{self.estudiante.nombre_completo} - {self.aula.titulo}"

class InvitacionAula(BaseModel):
    ESTADOS = [
        ('pendiente', 'Pendiente'),
        ('aceptada', 'Aceptada'),
        ('rechazada', 'Rechazada'),
        ('expirada', 'Expirada'),
    ]
    
    aula_virtual = models.ForeignKey(AulaVirtual, on_delete=models.CASCADE)
    invitado_por = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    correo_invitado = models.EmailField()
    codigo_invitacion = models.CharField(max_length=255, unique=True)
    estado = models.CharField(max_length=20, choices=ESTADOS, null=True, blank=True)
    mensaje = models.CharField(max_length=255, null=True, blank=True)
    fecha_invitacion = models.DateTimeField(auto_now_add=True)
    fecha_expiracion = models.DateTimeField()
    fecha_respuesta = models.DateTimeField(null=True, blank=True)
    
    class Meta:
        db_table = 'invitaciones_aula'
        verbose_name = 'Invitación a Aula'
        verbose_name_plural = 'Invitaciones a Aulas'
    
    def __str__(self):
        return f"Invitación a {self.correo_invitado} - {self.aula_virtual.titulo}"
