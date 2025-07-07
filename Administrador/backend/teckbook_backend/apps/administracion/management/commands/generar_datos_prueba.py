from django.core.management.base import BaseCommand
from django.utils import timezone
from datetime import timedelta
import random
from apps.anuncios.models import Anuncio
from apps.usuarios.models import Usuario

class Command(BaseCommand):
    help = 'Genera datos de prueba para el dashboard del administrador'
    
    def handle(self, *args, **options):
        # Obtener usuarios existentes
        usuarios = list(Usuario.objects.filter(rol__in=['PROFESOR', 'ESTUDIANTE']))
        
        if not usuarios:
            self.stdout.write(
                self.style.ERROR('No hay usuarios disponibles. Crea algunos usuarios primero.')
            )
            return
        
        # Generar anuncios de los últimos 7 días
        tipos_anuncios = ['anuncio', 'material', 'pregunta']
        titulos_ejemplo = [
            'Examen Parcial de {}',
            'Material de Estudio - {}',
            'Consulta sobre {}',
            'Práctica de Laboratorio - {}',
            'Entrega de Proyecto {}',
            'Tutorial de {}',
            'Recordatorio: {}',
            'Nuevo Material: {}'
        ]
        
        materias = [
            'Programación', 'Base de Datos', 'Redes', 'Matemática',
            'Circuitos', 'Física', 'Química', 'Estadística'
        ]
        
        for dia in range(7):
            fecha = timezone.now() - timedelta(days=dia)
            num_anuncios = random.randint(3, 15)
            
            for _ in range(num_anuncios):
                usuario = random.choice(usuarios)
                tipo = random.choice(tipos_anuncios)
                materia = random.choice(materias)
                titulo = random.choice(titulos_ejemplo).format(materia)
                
                Anuncio.objects.create(
                    titulo=titulo,
                    contenido=f'Contenido de ejemplo para {titulo.lower()}. '
                             f'Este es un anuncio de tipo {tipo} generado automáticamente.',
                    tipo=tipo,
                    autor=usuario,
                    fecha_publicacion=fecha,
                    activo=random.choice([True, True, True, False])  # 75% activos
                )
        
        self.stdout.write(
            self.style.SUCCESS(
                'Datos de prueba generados exitosamente. '
                'Revisa el dashboard del administrador.'
            )
        )