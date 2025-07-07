# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Anuncios(models.Model):
    id = models.BigAutoField(primary_key=True)
    titulo = models.CharField(max_length=255)
    contenido = models.TextField()
    tipo = models.CharField(max_length=255)
    archivo_url = models.CharField(max_length=500, blank=True, null=True)
    archivo_nombre = models.CharField(max_length=255, blank=True, null=True)
    archivo_tipo = models.CharField(max_length=100, blank=True, null=True)
    archivo_tamaño = models.BigIntegerField(blank=True, null=True)
    categoria = models.CharField(max_length=100, blank=True, null=True)
    etiquetas = models.JSONField(blank=True, null=True)
    permite_comentarios = models.IntegerField(blank=True, null=True)
    total_likes = models.IntegerField(blank=True, null=True)
    total_comentarios = models.IntegerField(blank=True, null=True)
    es_recurso_academico = models.IntegerField(blank=True, null=True)
    aula = models.ForeignKey('AulasVirtuales', models.DO_NOTHING, blank=True, null=True)
    autor = models.ForeignKey('Usuarios', models.DO_NOTHING)
    fecha_publicacion = models.DateTimeField(blank=True, null=True)
    fecha_edicion = models.DateTimeField(blank=True, null=True)
    activo = models.IntegerField(blank=True, null=True)
    es_general = models.IntegerField(blank=True, null=True)
    fijado = models.TextField(blank=True, null=True)  # This field type is a guess.

    class Meta:
        managed = False
        db_table = 'anuncios'


class AulaEstudiantes(models.Model):
    id = models.BigAutoField(primary_key=True)
    aula = models.ForeignKey('AulasVirtuales', models.DO_NOTHING)
    estudiante = models.ForeignKey('Usuarios', models.DO_NOTHING)
    estado = models.CharField(max_length=255, blank=True, null=True)
    fecha_union = models.DateTimeField(blank=True, null=True)
    fecha_salida = models.DateTimeField(blank=True, null=True)
    created_at = models.DateTimeField(blank=True, null=True)
    fecha_inscripcion = models.DateTimeField(blank=True, null=True)
    profesor_id = models.BigIntegerField(blank=True, null=True)
    updated_at = models.DateTimeField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'aula_estudiantes'


class AulasVirtuales(models.Model):
    nombre = models.CharField(max_length=255, blank=True, null=True)
    titulo = models.CharField(max_length=255, blank=True, null=True)
    descripcion = models.CharField(max_length=255, blank=True, null=True)
    codigo_acceso = models.CharField(unique=True, max_length=255, blank=True, null=True)
    profesor = models.ForeignKey('Usuarios', models.DO_NOTHING)
    seccion = models.ForeignKey('Secciones', models.DO_NOTHING, blank=True, null=True)
    estado = models.CharField(max_length=255, blank=True, null=True)
    fecha_inicio = models.DateField(blank=True, null=True)
    fecha_fin = models.DateField(blank=True, null=True)
    created_at = models.DateTimeField(blank=True, null=True)
    updated_at = models.DateTimeField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'aulas_virtuales'


class AuthGroup(models.Model):
    name = models.CharField(unique=True, max_length=150)

    class Meta:
        managed = False
        db_table = 'auth_group'


class AuthGroupPermissions(models.Model):
    id = models.BigAutoField(primary_key=True)
    group = models.ForeignKey(AuthGroup, models.DO_NOTHING)
    permission = models.ForeignKey('AuthPermission', models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'auth_group_permissions'
        unique_together = (('group', 'permission'),)


class AuthPermission(models.Model):
    name = models.CharField(max_length=255)
    content_type = models.ForeignKey('DjangoContentType', models.DO_NOTHING)
    codename = models.CharField(max_length=100)

    class Meta:
        managed = False
        db_table = 'auth_permission'
        unique_together = (('content_type', 'codename'),)


class Carreras(models.Model):
    nombre = models.CharField(max_length=100)
    codigo = models.CharField(unique=True, max_length=10)
    departamento = models.ForeignKey('Departamentos', models.DO_NOTHING)
    activo = models.IntegerField(blank=True, null=True)
    created_at = models.DateTimeField(blank=True, null=True)
    descripcion = models.CharField(max_length=255, blank=True, null=True)
    duracion_ciclos = models.IntegerField(blank=True, null=True)
    modalidad = models.CharField(max_length=50, blank=True, null=True)
    updated_at = models.DateTimeField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'carreras'


class Ciclos(models.Model):
    numero = models.IntegerField()
    nombre = models.CharField(max_length=255)

    class Meta:
        managed = False
        db_table = 'ciclos'


class Comentarios(models.Model):
    id = models.BigAutoField(primary_key=True)
    usuario = models.ForeignKey('Usuarios', models.DO_NOTHING)
    anuncio = models.ForeignKey(Anuncios, models.DO_NOTHING)
    contenido = models.TextField()
    comentario_padre = models.ForeignKey('self', models.DO_NOTHING, blank=True, null=True)
    activo = models.IntegerField(blank=True, null=True)
    fecha_creacion = models.DateTimeField(blank=True, null=True)
    fecha_actualizacion = models.DateTimeField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'comentarios'


class Departamentos(models.Model):
    nombre = models.CharField(max_length=255)
    codigo = models.CharField(unique=True, max_length=255, blank=True, null=True)
    activo = models.IntegerField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'departamentos'


class DjangoAdminLog(models.Model):
    action_time = models.DateTimeField()
    object_id = models.TextField(blank=True, null=True)
    object_repr = models.CharField(max_length=200)
    action_flag = models.PositiveSmallIntegerField()
    change_message = models.TextField()
    content_type = models.ForeignKey('DjangoContentType', models.DO_NOTHING, blank=True, null=True)
    user_id = models.BigIntegerField()

    class Meta:
        managed = False
        db_table = 'django_admin_log'


class DjangoContentType(models.Model):
    app_label = models.CharField(max_length=100)
    model = models.CharField(max_length=100)

    class Meta:
        managed = False
        db_table = 'django_content_type'
        unique_together = (('app_label', 'model'),)


class DjangoMigrations(models.Model):
    id = models.BigAutoField(primary_key=True)
    app = models.CharField(max_length=255)
    name = models.CharField(max_length=255)
    applied = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'django_migrations'


class DjangoSession(models.Model):
    session_key = models.CharField(primary_key=True, max_length=40)
    session_data = models.TextField()
    expire_date = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'django_session'


class InvitacionesAula(models.Model):
    id = models.BigAutoField(primary_key=True)
    aula_virtual = models.ForeignKey(AulasVirtuales, models.DO_NOTHING)
    invitado_por = models.ForeignKey('Usuarios', models.DO_NOTHING)
    correo_invitado = models.CharField(max_length=255)
    codigo_invitacion = models.CharField(unique=True, max_length=255)
    estado = models.CharField(max_length=255, blank=True, null=True)
    mensaje = models.CharField(max_length=255, blank=True, null=True)
    fecha_invitacion = models.DateTimeField(blank=True, null=True)
    fecha_expiracion = models.DateTimeField()
    fecha_respuesta = models.DateTimeField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'invitaciones_aula'


class Lecturas(models.Model):
    id = models.BigAutoField(primary_key=True)
    usuario = models.ForeignKey('Usuarios', models.DO_NOTHING)
    anuncio = models.ForeignKey(Anuncios, models.DO_NOTHING)
    fecha_lectura = models.DateTimeField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'lecturas'
        unique_together = (('usuario', 'anuncio'), ('usuario', 'anuncio'),)


class Likes(models.Model):
    id = models.BigAutoField(primary_key=True)
    usuario = models.ForeignKey('Usuarios', models.DO_NOTHING)
    anuncio = models.ForeignKey(Anuncios, models.DO_NOTHING)
    fecha_creacion = models.DateTimeField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'likes'
        unique_together = (('usuario', 'anuncio'), ('usuario', 'anuncio'),)


class Secciones(models.Model):
    nombre = models.CharField(max_length=255)
    codigo = models.CharField(unique=True, max_length=255, blank=True, null=True)
    ciclo = models.ForeignKey(Ciclos, models.DO_NOTHING, db_column='ciclo')
    carrera = models.ForeignKey(Carreras, models.DO_NOTHING, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'secciones'


class Usuarios(models.Model):
    nombre = models.CharField(max_length=255, blank=True, null=True)
    apellidos = models.CharField(max_length=255, blank=True, null=True)
    correo_institucional = models.CharField(unique=True, max_length=255, blank=True, null=True)
    password = models.CharField(max_length=255)
    rol = models.CharField(max_length=255, blank=True, null=True)
    ciclo_actual = models.ForeignKey(Ciclos, models.DO_NOTHING, db_column='ciclo_actual', blank=True, null=True)
    seccion = models.ForeignKey(Secciones, models.DO_NOTHING, blank=True, null=True)
    carrera = models.ForeignKey(Carreras, models.DO_NOTHING, blank=True, null=True)
    departamento = models.ForeignKey(Departamentos, models.DO_NOTHING, blank=True, null=True)
    profile_image_url = models.CharField(max_length=255, blank=True, null=True)
    telefono = models.CharField(max_length=255, blank=True, null=True)
    created_at = models.DateTimeField(blank=True, null=True)
    updated_at = models.DateTimeField(blank=True, null=True)
    last_login = models.DateTimeField(blank=True, null=True)
    is_superuser = models.IntegerField()
    is_active = models.IntegerField()
    is_staff = models.IntegerField()

    class Meta:
        managed = False
        db_table = 'usuarios'
