from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import Usuario, Departamento, Carrera, Ciclo, Seccion

@admin.register(Usuario)
class UsuarioAdmin(BaseUserAdmin):
    list_display = ('correo_institucional', 'nombre', 'apellidos', 'rol', 'is_active', 'created_at')
    list_filter = ('rol', 'is_active', 'departamento', 'carrera')
    search_fields = ('correo_institucional', 'nombre', 'apellidos')
    ordering = ('-created_at',)
    
    fieldsets = (
        (None, {'fields': ('correo_institucional', 'password')}),
        ('Información Personal', {'fields': ('nombre', 'apellidos', 'telefono', 'profile_image_url')}),
        ('Información Académica', {'fields': ('rol', 'ciclo_actual', 'seccion', 'carrera', 'departamento')}),
        ('Permisos', {'fields': ('is_active', 'is_staff', 'is_superuser')}),
        ('Fechas Importantes', {'fields': ('created_at', 'updated_at')}),
    )
    
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('correo_institucional', 'nombre', 'apellidos', 'rol', 'password1', 'password2'),
        }),
    )
    
    readonly_fields = ('created_at', 'updated_at')

@admin.register(Departamento)
class DepartamentoAdmin(admin.ModelAdmin):
    list_display = ('nombre', 'codigo', 'activo')
    list_filter = ('activo',)
    search_fields = ('nombre', 'codigo')

@admin.register(Carrera)
class CarreraAdmin(admin.ModelAdmin):
    list_display = ('nombre', 'codigo', 'departamento', 'duracion_ciclos', 'activo')
    list_filter = ('departamento', 'activo', 'modalidad')
    search_fields = ('nombre', 'codigo')

@admin.register(Ciclo)
class CicloAdmin(admin.ModelAdmin):
    list_display = ('numero', 'nombre')
    ordering = ('numero',)

@admin.register(Seccion)
class SeccionAdmin(admin.ModelAdmin):
    list_display = ('codigo', 'nombre', 'ciclo', 'carrera')
    list_filter = ('ciclo', 'carrera')
    search_fields = ('codigo', 'nombre')
