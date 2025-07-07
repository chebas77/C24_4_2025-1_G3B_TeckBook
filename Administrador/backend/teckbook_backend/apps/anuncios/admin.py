from django.contrib import admin
from .models import Anuncio, Comentario, Like, Lectura

@admin.register(Anuncio)
class AnuncioAdmin(admin.ModelAdmin):
    list_display = ('titulo', 'autor', 'tipo', 'aula', 'activo', 'fecha_publicacion')
    list_filter = ('tipo', 'activo', 'es_general', 'es_recurso_academico', 'fecha_publicacion')
    search_fields = ('titulo', 'contenido', 'autor__nombre', 'aula__titulo')
    readonly_fields = ('total_likes', 'total_comentarios', 'fecha_publicacion', 'fecha_edicion')
    
    fieldsets = (
        ('Contenido', {'fields': ('titulo', 'contenido', 'tipo', 'categoria')}),
        ('Archivos', {'fields': ('archivo_url', 'archivo_nombre', 'archivo_tipo', 'archivo_tamaño')}),
        ('Configuración', {'fields': ('autor', 'aula', 'activo', 'es_general', 'fijado')}),
        ('Opciones', {'fields': ('permite_comentarios', 'es_recurso_academico', 'etiquetas')}),
        ('Estadísticas', {'fields': ('total_likes', 'total_comentarios')}),
        ('Fechas', {'fields': ('fecha_publicacion', 'fecha_edicion')}),
    )

@admin.register(Comentario)
class ComentarioAdmin(admin.ModelAdmin):
    list_display = ('usuario', 'anuncio', 'activo', 'fecha_creacion')
    list_filter = ('activo', 'fecha_creacion')
    search_fields = ('contenido', 'usuario__nombre', 'anuncio__titulo')

@admin.register(Like)
class LikeAdmin(admin.ModelAdmin):
    list_display = ('usuario', 'anuncio', 'fecha_creacion')
    list_filter = ('fecha_creacion',)
    search_fields = ('usuario__nombre', 'anuncio__titulo')

@admin.register(Lectura)
class LecturaAdmin(admin.ModelAdmin):
    list_display = ('usuario', 'anuncio', 'fecha_lectura')
    list_filter = ('fecha_lectura',)
    search_fields = ('usuario__nombre', 'anuncio__titulo')