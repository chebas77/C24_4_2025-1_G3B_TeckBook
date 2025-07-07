from django.contrib.auth.models import AbstractBaseUser, BaseUserManager, PermissionsMixin
from django.db import models
from apps.core.models import BaseModel

class UsuarioManager(BaseUserManager):
    def create_user(self, correo_institucional, password=None, **extra_fields):
        if not correo_institucional:
            raise ValueError('El correo institucional es obligatorio')
        
        correo_institucional = self.normalize_email(correo_institucional)
        user = self.model(correo_institucional=correo_institucional, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user
    
    def create_superuser(self, correo_institucional, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('rol', 'ADMINISTRADOR')
        
        return self.create_user(correo_institucional, password, **extra_fields)
    
class Departamento(models.Model):
    nombre = models.CharField(max_length=255)
    codigo = models.CharField(max_length=255, unique=True, null=True, blank=True)
    activo = models.BooleanField(default=True)
    
    class Meta:
        db_table = 'departamentos'
        verbose_name = 'Departamento'
        verbose_name_plural = 'Departamentos'
    
    def __str__(self):
        return self.nombre

class Carrera(BaseModel):
    nombre = models.CharField(max_length=100)
    codigo = models.CharField(max_length=10, unique=True)
    departamento = models.ForeignKey(Departamento, on_delete=models.CASCADE)
    descripcion = models.CharField(max_length=255, null=True, blank=True)
    duracion_ciclos = models.IntegerField(null=True, blank=True)
    modalidad = models.CharField(max_length=50, null=True, blank=True)
    activo = models.BooleanField(default=True)
    
    class Meta:
        db_table = 'carreras'
        verbose_name = 'Carrera'
        verbose_name_plural = 'Carreras'
    
    def __str__(self):
        return f"{self.codigo} - {self.nombre}"

class Ciclo(models.Model):
    numero = models.IntegerField()
    nombre = models.CharField(max_length=255)
    
    class Meta:
        db_table = 'ciclos'
        verbose_name = 'Ciclo'
        verbose_name_plural = 'Ciclos'
    
    def __str__(self):
        return self.nombre

class Seccion(models.Model):
    nombre = models.CharField(max_length=255)
    codigo = models.CharField(max_length=255, unique=True, null=True, blank=True)
    ciclo = models.IntegerField(db_column='ciclo')    
    carrera = models.ForeignKey(Carrera, on_delete=models.CASCADE, null=True, blank=True)
    
    class Meta:
        db_table = 'secciones'
        verbose_name = 'Sección'
        verbose_name_plural = 'Secciones'
    
    def __str__(self):
        return f"{self.codigo} - {self.nombre}"
    
class Usuario(AbstractBaseUser, PermissionsMixin, BaseModel):
    ROLES = [
        ('ESTUDIANTE', 'Estudiante'),
        ('PROFESOR', 'Profesor'),
        ('ADMINISTRADOR', 'Administrador'),
    ]
    
    nombre = models.CharField(max_length=255, null=True, blank=True)
    apellidos = models.CharField(max_length=255, null=True, blank=True)
    correo_institucional = models.EmailField(unique=True)
    rol = models.CharField(max_length=20, choices=ROLES, null=True, blank=True)
    ciclo_actual = models.IntegerField(null=True, blank=True)
    seccion = models.ForeignKey(Seccion, on_delete=models.SET_NULL, null=True, blank=True)
    carrera = models.ForeignKey(Carrera, on_delete=models.SET_NULL, null=True, blank=True)
    departamento = models.ForeignKey(Departamento, on_delete=models.SET_NULL, null=True, blank=True)
    profile_image_url = models.URLField(null=True, blank=True)
    telefono = models.CharField(max_length=255, null=True, blank=True)
    
    # Campos adicionales para Django Admin
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)

    strikes = models.IntegerField(default=0, help_text="Número de strikes acumulados")
    activo = models.BooleanField(default=True, help_text="Usuario activo (no suspendido)")
    fecha_suspension = models.DateTimeField(null=True, blank=True, help_text="Fecha hasta cuando está suspendido")
    motivo_suspension = models.TextField(null=True, blank=True, help_text="Motivo de la suspensión")
    
    objects = UsuarioManager()
    
    USERNAME_FIELD = 'correo_institucional'
    REQUIRED_FIELDS = ['nombre', 'apellidos']
    
    class Meta:
        db_table = 'usuarios'
        verbose_name = 'Usuario'
        verbose_name_plural = 'Usuarios'
    
    def __str__(self):
        return f"{self.nombre} {self.apellidos}"
    
    @property
    def nombre_completo(self):
        return f"{self.nombre} {self.apellidos}"
    
    @property
    def is_administrador(self):
        return self.rol == 'ADMINISTRADOR'
    
    @property
    def is_profesor(self):
        return self.rol == 'PROFESOR'
    
    @property
    def is_estudiante(self):
        return self.rol == 'ESTUDIANTE'
    
    @property
    def esta_suspendido(self):
        from django.utils import timezone
        if self.fecha_suspension and self.fecha_suspension > timezone.now():
            return True
        return not self.activo

    @property
    def puede_publicar(self):
        return self.is_active and self.activo and not self.esta_suspendido