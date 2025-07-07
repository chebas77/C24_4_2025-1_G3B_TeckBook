from django.core.management.base import BaseCommand
from django.contrib.auth import get_user_model
from apps.usuarios.models import Departamento

User = get_user_model()

class Command(BaseCommand):
    help = 'Crea un usuario administrador para TeckBook'
    
    def add_arguments(self, parser):
        parser.add_argument('--email', type=str, help='Email del administrador')
        parser.add_argument('--nombre', type=str, help='Nombre del administrador')
        parser.add_argument('--apellidos', type=str, help='Apellidos del administrador')
        parser.add_argument('--password', type=str, help='Contraseña del administrador')
    
    def handle(self, *args, **options):
        email = options['email'] or 'admin@tecsup.edu.pe'
        nombre = options['nombre'] or 'Administrador'
        apellidos = options['apellidos'] or 'Sistema'
        password = options['password'] or 'admin123'
        
        if User.objects.filter(correo_institucional=email).exists():
            self.stdout.write(
                self.style.WARNING(f'El usuario {email} ya existe')
            )
            return
        
        # Crear o obtener departamento de administración
        dept_admin, created = Departamento.objects.get_or_create(
            codigo='ADM',
            defaults={
                'nombre': 'Administración del Sistema',
                'activo': True
            }
        )
        
        # Crear usuario administrador
        admin_user = User.objects.create_user(
            correo_institucional=email,
            password=password,
            nombre=nombre,
            apellidos=apellidos,
            rol='ADMINISTRADOR',
            departamento=dept_admin,
            is_staff=True,
            is_superuser=True
        )
        
        self.stdout.write(
            self.style.SUCCESS(
                f'Usuario administrador creado exitosamente:\n'
                f'Email: {email}\n'
                f'Nombre: {nombre} {apellidos}\n'
                f'Contraseña: {password}'
            )
        )