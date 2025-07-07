from django.contrib import admin
from .models import AulaVirtual, AulaEstudiante, InvitacionAula

@admin.register(AulaVirtual)
class AulaVirtualAdmin(admin.ModelAdmin):
    list_display = ('titulo', 'nombre', 'profesor', 'estado', 'fecha_inicio', 'created_at')
    list_filter = ('estado', 'fecha_inicio', 'profesor__departamento')
    search_fields = ('titulo', 'nombre', 'codigo_acceso', 'profesor__nombre')
    readonly_fields = ('codigo_acceso', 'created_at', 'updated_at')
    
    fieldsets = (
        ('Información Básica', {'fields': ('nombre', 'titulo', 'descripcion')}),
        ('Configuración', {'fields': ('codigo_acceso', 'profesor', 'seccion', 'estado')}),
        ('Fechas', {'fields': ('fecha_inicio', 'fecha_fin')}),
        ('Metadatos', {'fields': ('created_at', 'updated_at')}),
    )

@admin.register(AulaEstudiante)
class AulaEstudianteAdmin(admin.ModelAdmin):
    list_display = ('estudiante', 'aula', 'estado', 'fecha_union')
    list_filter = ('estado', 'fecha_union', 'aula__estado')
    search_fields = ('estudiante__nombre', 'estudiante__correo_institucional', 'aula__titulo')

@admin.register(InvitacionAula)
class InvitacionAulaAdmin(admin.ModelAdmin):
    list_display = ('correo_invitado', 'aula_virtual', 'invitado_por', 'estado', 'fecha_invitacion')
    list_filter = ('estado', 'fecha_invitacion', 'fecha_expiracion')
    search_fields = ('correo_invitado', 'aula_virtual__titulo', 'invitado_por__nombre')
    readonly_fields = ('codigo_invitacion',)