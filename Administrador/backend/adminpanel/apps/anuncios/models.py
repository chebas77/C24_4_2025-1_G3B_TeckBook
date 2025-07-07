from django.db import models
from apps.core.models import BaseModel
from apps.usuarios.models import Usuario
from apps.aulas.models import AulaVirtual

class Anuncio(models.Model):
    TIPOS = [
        ('anuncio', 'Anuncio'),
        ('material', 'Material'),
        ('pregunta', 'Pregunta'),
        ('evento', 'Evento'),
    ]
    
    titulo = models.CharField(max_length=255)
    contenido = models.TextField()
    tipo = models.CharField(max_length=20, choices=TIPOS)
    archivo_url = models.URLField(max_length=500, null=True, blank=True)
    archivo_nombre = models.CharField(max_length=255, null=True, blank=True)
    archivo_tipo = models.CharField(max_length=100, null=True, blank=True)
    archivo_tamaño = models.BigIntegerField(null=True, blank=True)
    categoria = models.CharField(max_length=100, null=True, blank=True)
    etiquetas = models.JSONField(null=True, blank=True)
    permite_comentarios = models.BooleanField(default=True)
    total_likes = models.IntegerField(default=0)
    total_comentarios = models.IntegerField(default=0)
    es_recurso_academico = models.BooleanField(default=False)
    aula = models.ForeignKey(AulaVirtual, on_delete=models.CASCADE, null=True, blank=True)
    autor = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    fecha_publicacion = models.DateTimeField(auto_now_add=True)
    fecha_edicion = models.DateTimeField(null=True, blank=True)
    activo = models.BooleanField(default=True)
    es_general = models.BooleanField(default=False)  # Para anuncios generales
    fijado = models.BooleanField(default=False, null=True, blank=True)  # ✅ Agregar null=True

    censurado = models.BooleanField(default=False, help_text="Anuncio censurado por administrador")
    motivo_censura = models.TextField(null=True, blank=True, help_text="Motivo de la censura")
    admin_censurador = models.ForeignKey(
        'usuarios.Usuario',
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='anuncios_censurados',
        help_text="Administrador que censuró el anuncio"
    )
    fecha_censura = models.DateTimeField(null=True, blank=True, help_text="Fecha de censura")
    
    class Meta:
        db_table = 'anuncios'
        verbose_name = 'Anuncio'
        verbose_name_plural = 'Anuncios'
        ordering = ['-fijado', '-fecha_publicacion']
    
    def __str__(self):
        return self.titulo
    
    @property
    def esta_censurado(self):
        return self.censurado or not self.activo

    @property
    def puede_visualizar(self):
        return self.activo and not self.censurado

class Comentario(BaseModel):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    anuncio = models.ForeignKey(Anuncio, on_delete=models.CASCADE, related_name='comentarios')
    contenido = models.TextField()
    comentario_padre = models.ForeignKey('self', on_delete=models.CASCADE, null=True, blank=True)
    activo = models.BooleanField(default=True)
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'comentarios'
        verbose_name = 'Comentario'
        verbose_name_plural = 'Comentarios'
        ordering = ['fecha_creacion']
    
    def __str__(self):
        return f"Comentario de {self.usuario.nombre_completo} en {self.anuncio.titulo}"

class Like(BaseModel):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    anuncio = models.ForeignKey(Anuncio, on_delete=models.CASCADE, related_name='likes')
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'likes'
        verbose_name = 'Like'
        verbose_name_plural = 'Likes'
        unique_together = ['usuario', 'anuncio']
    
    def __str__(self):
        return f"{self.usuario.nombre_completo} - {self.anuncio.titulo}"

class Lectura(BaseModel):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    anuncio = models.ForeignKey(Anuncio, on_delete=models.CASCADE, related_name='lecturas')
    fecha_lectura = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'lecturas'
        verbose_name = 'Lectura'
        verbose_name_plural = 'Lecturas'
        unique_together = ['usuario', 'anuncio']
    
    def __str__(self):
        return f"{self.usuario.nombre_completo} leyó {self.anuncio.titulo}"
    