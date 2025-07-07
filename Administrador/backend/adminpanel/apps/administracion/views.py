from rest_framework import viewsets, status, permissions
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.pagination import PageNumberPagination
from rest_framework import serializers
from django.db.models import Count, Q
from django.utils import timezone
from datetime import timedelta

from apps.usuarios.models import Usuario
from apps.aulas.models import AulaVirtual, AulaEstudiante
from apps.anuncios.models import Anuncio, Comentario, Like, Lectura
from apps.anuncios.serializers import AnuncioSerializer  # Asegúrate de tenerlo
from apps.usuarios.serializers import UsuarioSerializer  # Asegúrate de tenerlo
from .serializers import HistorialModeracionSerializer

from .models import ModeracionAnuncio, HistorialModeracion
from datetime import timedelta
from django.utils import timezone

from rest_framework_simplejwt.tokens import RefreshToken
from rest_framework_simplejwt.views import TokenObtainPairView
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer
from django.contrib.auth import authenticate
from django.contrib.auth.hashers import check_password

from rest_framework_simplejwt.authentication import JWTAuthentication
from rest_framework_simplejwt.token_blacklist.models import OutstandingToken, BlacklistedToken


class IsAdministradorPermission(permissions.BasePermission):
    """Permiso personalizado para administradores autenticados"""
    def has_permission(self, request, view):
        return (
            request.user and 
            request.user.is_authenticated and 
            hasattr(request.user, 'rol') and
            request.user.rol == 'ADMINISTRADOR' and
            request.user.is_active and
            request.user.activo
        )
    
class AdminLogoutView(APIView):
    """Vista de logout para administradores"""
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAdministradorPermission]
    
    def post(self, request):
        try:
            refresh_token = request.data.get('refresh')
            if refresh_token:
                token = RefreshToken(refresh_token)
                token.blacklist()
            
            return Response({
                'mensaje': 'Logout exitoso'
            }, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({
                'error': 'Error al cerrar sesión'
            }, status=status.HTTP_400_BAD_REQUEST)

class StandardResultsSetPagination(PageNumberPagination):
    """Paginación estándar"""
    page_size = 20
    page_size_query_param = 'page_size'
    max_page_size = 100

class AdminTokenObtainPairSerializer(TokenObtainPairSerializer):
    """Serializer personalizado para login de administradores"""
    
    def validate(self, attrs):
        # Obtener credenciales
        correo = attrs.get('correo_institucional') or attrs.get('username')
        password = attrs.get('password')
        
        if not correo or not password:
            raise serializers.ValidationError('Correo y contraseña son obligatorios')
        
        try:
            # Buscar usuario por correo
            user = Usuario.objects.get(correo_institucional=correo)
            
            # Verificar contraseña usando Django's check_password
            if not check_password(password, user.password):
                raise serializers.ValidationError('Credenciales inválidas')
            
            # Verificar que sea administrador
            if user.rol != 'ADMINISTRADOR':
                raise serializers.ValidationError('Acceso denegado: Solo administradores')
            
            # Verificar que esté activo
            if not user.is_active or not user.activo:
                raise serializers.ValidationError('Cuenta inactiva')
            
            # Generar tokens
            refresh = RefreshToken.for_user(user)
            
            # Actualizar último login
            user.last_login = timezone.now()
            user.save(update_fields=['last_login'])
            
            return {
                'refresh': str(refresh),
                'access': str(refresh.access_token),
                'user': {
                    'id': user.id,
                    'nombre': user.nombre,
                    'apellidos': user.apellidos,
                    'correo_institucional': user.correo_institucional,
                    'rol': user.rol
                }
            }
            
        except Usuario.DoesNotExist:
            raise serializers.ValidationError('Credenciales inválidas')
        except Exception as e:
            raise serializers.ValidationError(f'Error de autenticación: {str(e)}')

class AdminLoginView(TokenObtainPairView):
    """Vista de login específica para administradores"""
    serializer_class = AdminTokenObtainPairSerializer
    
    def post(self, request, *args, **kwargs):
        response = super().post(request, *args, **kwargs)
        if response.status_code == 200:
            response.data['mensaje'] = 'Login exitoso'
        return response

# ==================== DASHBOARD ====================

class DashboardDataView(APIView):
    """Vista del dashboard con datos reales"""
    permission_classes = [IsAdministradorPermission]
    authentication_classes = [JWTAuthentication]
    
    def get(self, request):
        # Fechas para cálculos
        hoy = timezone.now().date()
        
        # Estadísticas básicas
        total_usuarios = Usuario.objects.count()
        usuarios_activos = Usuario.objects.filter(is_active=True).count()
        
        publicaciones_hoy = Anuncio.objects.filter(
            fecha_publicacion__date=hoy,
            activo=True
        ).count()
        
        contenido_pendiente = Anuncio.objects.filter(
            activo=True,
            fecha_publicacion__gte=timezone.now() - timedelta(hours=24)
        ).count()
        
        total_aulas = AulaVirtual.objects.count()
        aulas_activas = AulaVirtual.objects.filter(estado='activa').count()
        
        estadisticas_basicas = {
            'totalUsuarios': total_usuarios,
            'usuariosActivos': usuarios_activos,
            'publicacionesHoy': publicaciones_hoy,
            'contenidoPendiente': contenido_pendiente,
            'totalAulas': total_aulas,
            'aulasActivas': aulas_activas
        }
        
        # Publicaciones por día de la semana
        publicaciones_semanales = []
        dias_semana = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom']
        
        for i in range(7):
            fecha = hoy - timedelta(days=6-i)
            publicaciones = Anuncio.objects.filter(
                fecha_publicacion__date=fecha,
                activo=True
            ).count()
            publicaciones_semanales.append({
                'dia': dias_semana[i],
                'publicaciones': publicaciones
            })
        
        # Top 5 aulas más pobladas
        aulas_pobladas = AulaVirtual.objects.filter(
            estado='activa'
        ).annotate(
            estudiantes=Count('aulaestudiante', filter=Q(aulaestudiante__estado='activo'))
        ).order_by('-estudiantes')[:5]
        
        aulas_pobladas_data = [{
            'nombre': aula.titulo,
            'estudiantes': aula.estudiantes,
            'codigo': aula.codigo_acceso
        } for aula in aulas_pobladas]
        
        # Anuncios por aula (para gráfico circular)
        anuncios_por_aula = AulaVirtual.objects.filter(
            estado='activa'
        ).annotate(
            total_anuncios=Count('anuncio', filter=Q(anuncio__activo=True))
        ).filter(total_anuncios__gt=0).order_by('-total_anuncios')[:6]
        
        anuncios_por_aula_data = [{
            'name': aula.titulo[:20] + '...' if len(aula.titulo) > 20 else aula.titulo,
            'value': aula.total_anuncios
        } for aula in anuncios_por_aula]
        
        # Alertas recientes
        alertas_recientes = []
        
        if contenido_pendiente > 0:
            alertas_recientes.append({
                'id': 1,
                'tipo': 'contenido',
                'mensaje': f'{contenido_pendiente} nueva(s) publicación(es) para revisar',
                'tiempo': 'Últimas 24h'
            })
        
        usuarios_nuevos_hoy = Usuario.objects.filter(
            created_at__date=hoy
        ).count()
        
        if usuarios_nuevos_hoy > 0:
            alertas_recientes.append({
                'id': 2,
                'tipo': 'usuario',
                'mensaje': f'{usuarios_nuevos_hoy} nuevo(s) usuario(s) registrado(s)',
                'tiempo': 'Hoy'
            })
        
        data = {
            'estadisticas_basicas': estadisticas_basicas,
            'publicaciones_semanales': publicaciones_semanales,
            'aulasPobladas': aulas_pobladas_data,
            'anuncios_por_aula': anuncios_por_aula_data,
            'alertasRecientes': alertas_recientes
        }
        
        return Response(data, status=status.HTTP_200_OK)

class DashboardViewSet(viewsets.ViewSet):
    """ViewSet del dashboard"""
    permission_classes = [IsAdministradorPermission]
    authentication_classes = [JWTAuthentication]
    
    def list(self, request):
        view = DashboardDataView()
        view.request = request
        return view.get(request)

# ==================== MODERACIÓN ====================

class ModeracionViewSet(viewsets.GenericViewSet):
    queryset = Anuncio.objects.all()
    serializer_class = AnuncioSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAdministradorPermission]

    def list(self, request):
        """
        Listar anuncios para moderación (GET /api/administracion/moderacion/)
        Permite filtrar por estado (censurado/no censurado) y búsqueda por título/contenido.
        """
        estado = request.GET.get('estado')
        buscar = request.GET.get('buscar')

        queryset = Anuncio.objects.all()

        if estado == 'oculto':
            queryset = queryset.filter(censurado=True)
        elif estado == 'visible':
            queryset = queryset.filter(censurado=False)

        if buscar:
            queryset = queryset.filter(
                Q(titulo__icontains=buscar) | Q(contenido__icontains=buscar)
            )

        queryset = queryset.select_related('autor', 'aula').order_by('-fecha_publicacion')
        paginator = StandardResultsSetPagination()
        page = paginator.paginate_queryset(queryset, request)

        data = [{
            'id': anuncio.id,
            'titulo': anuncio.titulo,
            'contenido': anuncio.contenido[:200] + '...' if len(anuncio.contenido) > 200 else anuncio.contenido,
            'tipo': anuncio.tipo,
            'autor_nombre': f'{anuncio.autor.nombre} {anuncio.autor.apellidos}',
            'autor_rol': anuncio.autor.rol,
            'aula_titulo': anuncio.aula.titulo if anuncio.aula else 'General',
            'fecha_publicacion': anuncio.fecha_publicacion,
            'total_likes': anuncio.total_likes,
            'total_comentarios': anuncio.total_comentarios,
            'activo': anuncio.activo,
            'estado': 'oculto' if anuncio.censurado else 'visible'
        } for anuncio in page]

        return paginator.get_paginated_response(data)
    
    @action(detail=True, methods=['get'])
    def historial_moderacion(self, request, pk=None):
        anuncio = self.get_object()
        historial = anuncio.historial_moderacion.order_by('-fecha')
        serializer = HistorialModeracionSerializer(historial, many=True)
        return Response({'historial': serializer.data})

    def retrieve(self, request, pk=None):
        """Obtener un anuncio específico"""
        try:
            anuncio = Anuncio.objects.select_related('autor', 'aula').get(pk=pk)
            data = {
                'id': anuncio.id,
                'titulo': anuncio.titulo,
                'contenido': anuncio.contenido,
                'tipo': anuncio.tipo,
                'autor_nombre': f"{anuncio.autor.nombre} {anuncio.autor.apellidos}",
                'autor_rol': anuncio.autor.rol,
                'aula_titulo': anuncio.aula.titulo if anuncio.aula else 'General',
                'fecha_publicacion': anuncio.fecha_publicacion,
                'total_likes': anuncio.total_likes,
                'total_comentarios': anuncio.total_comentarios,
                'activo': anuncio.activo,
                'censurado': anuncio.censurado,
                'motivo_censura': anuncio.motivo_censura
            }
            return Response(data)
        except Anuncio.DoesNotExist:
            return Response({'error': 'Anuncio no encontrado'}, status=status.HTTP_404_NOT_FOUND)

    @action(detail=False, methods=['get'])
    def contenido_pendiente(self, request):
        tipo = request.GET.get('tipo')
        aula_id = request.GET.get('aula_id')
        queryset = Anuncio.objects.filter(activo=True)

        if tipo:
            queryset = queryset.filter(tipo=tipo)
        if aula_id:
            queryset = queryset.filter(aula_id=aula_id)

        queryset = queryset.select_related('autor', 'aula').order_by('-fecha_publicacion')
        paginator = StandardResultsSetPagination()
        page = paginator.paginate_queryset(queryset, request)

        data = [{
            'id': anuncio.id,
            'titulo': anuncio.titulo,
            'contenido': anuncio.contenido[:200] + '...' if len(anuncio.contenido) > 200 else anuncio.contenido,
            'tipo': anuncio.tipo,
            'autor_nombre': anuncio.autor.nombre_completo,
            'autor_rol': anuncio.autor.rol,
            'aula_titulo': anuncio.aula.titulo if anuncio.aula else 'General',
            'fecha_publicacion': anuncio.fecha_publicacion,
            'total_likes': anuncio.total_likes,
            'total_comentarios': anuncio.total_comentarios,
            'activo': anuncio.activo
        } for anuncio in page]

        return paginator.get_paginated_response(data)

    @action(detail=True, methods=['post'])
    def censurar_anuncio(self, request, pk=None):
        motivo = request.data.get('motivo', '')
        aplicar_strike = request.data.get('aplicar_strike', False)
        
        if not motivo:
            return Response({'error': 'El motivo es obligatorio'}, status=status.HTTP_400_BAD_REQUEST)

        try:
            # Obtener el anuncio para verificaciones y obtener el autor
            anuncio = Anuncio.objects.select_related('autor').get(pk=pk)
            
            if anuncio.censurado:
                return Response({'error': 'El anuncio ya está censurado.'}, status=status.HTTP_400_BAD_REQUEST)

            # ✅ Usar update() para evitar problemas con campos binarios
            Anuncio.objects.filter(pk=pk).update(
                censurado=True,
                motivo_censura=motivo,
                admin_censurador_id=request.user.id,
                fecha_censura=timezone.now()
            )
            
            # Registrar SIEMPRE el historial de censura
            HistorialModeracion.objects.create(
                anuncio=anuncio,
                moderador=request.user,
                accion='censurar',
                comentario=motivo
            )
            
            # Aplicar strike al autor si se solicitó
            strike_aplicado = False
            if aplicar_strike and anuncio.autor.rol != 'ADMINISTRADOR':
                anuncio.autor.strikes += 1
                anuncio.autor.save()
                strike_aplicado = True
                
                # Si llega a 3 strikes, suspender automáticamente
                if anuncio.autor.strikes >= 3:
                    anuncio.autor.activo = False
                    anuncio.autor.fecha_suspension = timezone.now() + timedelta(days=7)
                    anuncio.autor.motivo_suspension = f"Suspensión automática por acumular {anuncio.autor.strikes} strikes"
                    anuncio.autor.save()
                    # (Opcional) Puedes registrar otro historial aquí si quieres dejar constancia de la suspensión

            return Response({
                'mensaje': 'Anuncio censurado exitosamente',
                'strike_aplicado': strike_aplicado,
                'total_strikes_autor': anuncio.autor.strikes if aplicar_strike else None
            })

        except Anuncio.DoesNotExist:
            return Response({'error': 'Anuncio no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        except Exception as e:
            import traceback
            print("ERROR AL CENSURAR:")
            print(traceback.format_exc())
            return Response({'error': f'Error interno: {str(e)}'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

    @action(detail=True, methods=['post'])
    def descensurar_anuncio(self, request, pk=None):
        try:
            # Obtener el anuncio para verificaciones
            anuncio = Anuncio.objects.get(pk=pk)
            
            if not anuncio.censurado:
                return Response({'error': 'El anuncio ya está visible.'}, status=status.HTTP_400_BAD_REQUEST)

            # ✅ Usar update() para evitar problemas con campos binarios
            Anuncio.objects.filter(pk=pk).update(
                censurado=False,
                motivo_censura=None,
                admin_censurador_id=None,
                fecha_censura=None
            )

            # Registrar el historial de descensura
            HistorialModeracion.objects.create(
                anuncio=anuncio,
                moderador=request.user,
                accion='descensurar',
                comentario='Anuncio reactivado por moderador'
            )

            return Response({'mensaje': 'Anuncio reactivado exitosamente'})
            
        except Anuncio.DoesNotExist:
            return Response({'error': 'Anuncio no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        except Exception as e:
            import traceback
            print("ERROR AL DESCENSURAR:")
            print(traceback.format_exc())
            return Response({'error': f'Error interno: {str(e)}'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

# ==================== GESTIÓN DE USUARIOS ====================

class GestionUsuariosViewSet(viewsets.GenericViewSet):
    queryset = Usuario.objects.all()
    serializer_class = UsuarioSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAdministradorPermission]

    def retrieve(self, request, pk=None):
        """Obtener un usuario específico"""
        try:
            usuario = Usuario.objects.select_related('departamento', 'carrera', 'seccion').get(pk=pk)
            data = {
                'id': usuario.id,
                'nombre': usuario.nombre,
                'apellidos': usuario.apellidos,
                'nombre_completo': usuario.nombre_completo,
                'correo_institucional': usuario.correo_institucional,
                'rol': usuario.rol,
                'strikes': usuario.strikes,
                'activo': usuario.activo,
                'departamento_nombre': usuario.departamento.nombre if usuario.departamento else None,
                'carrera_nombre': usuario.carrera.nombre if usuario.carrera else None,
                'is_active': usuario.is_active,
                'created_at': usuario.created_at
            }
            return Response(data)
        except Usuario.DoesNotExist:
            return Response({'error': 'Usuario no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
    def list(self, request):
        queryset = Usuario.objects.select_related('departamento', 'carrera', 'seccion').all()
        rol = request.GET.get('rol')
        activo = request.GET.get('activo')
        buscar = request.GET.get('buscar')
        strikes = request.GET.get('strikes')  # ✅ Solución 1

        if rol:
            queryset = queryset.filter(rol=rol)
        if activo is not None:
            queryset = queryset.filter(is_active=activo.lower() == 'true')
        if strikes is not None:  # ✅ Solución 1
            queryset = queryset.filter(strikes=int(strikes))
        if buscar:
            queryset = queryset.filter(
                Q(nombre__icontains=buscar) |
                Q(apellidos__icontains=buscar) |
                Q(correo_institucional__icontains=buscar)
            )

        queryset = queryset.order_by('-created_at')
        paginator = StandardResultsSetPagination()
        page = paginator.paginate_queryset(queryset, request)

        # ✅ AQUÍ VA LA SOLUCIÓN 4 - Reemplaza el data existente
        data = [{
            'id': usuario.id,
            'nombre': usuario.nombre,
            'apellidos': usuario.apellidos,
            'nombre_completo': usuario.nombre_completo,
            'correo_institucional': usuario.correo_institucional,
            'rol': usuario.rol,
            'strikes': usuario.strikes,  # ✅ AGREGAR ESTO
            'activo': usuario.activo,    # ✅ AGREGAR ESTO
            'departamento_nombre': usuario.departamento.nombre if usuario.departamento else None,
            'carrera_nombre': usuario.carrera.nombre if usuario.carrera else None,
            'is_active': usuario.is_active,
            'created_at': usuario.created_at
        } for usuario in page]

        return paginator.get_paginated_response(data)
    
    @action(detail=True, methods=['post'])
    def suspender_usuario(self, request, pk=None):
        motivo = request.data.get('motivo', '')
        if not motivo:
            return Response({'error': 'El motivo es obligatorio'}, status=status.HTTP_400_BAD_REQUEST)

        try:
            usuario = Usuario.objects.get(id=pk)
            if usuario.rol == 'ADMINISTRADOR':
                return Response({'error': 'No se puede suspender a un administrador'}, status=status.HTTP_400_BAD_REQUEST)

            # Suspender usuario directamente
            usuario.activo = False
            usuario.fecha_suspension = timezone.now() + timedelta(days=7)  # 7 días
            usuario.motivo_suspension = motivo
            usuario.save()

            return Response({'mensaje': 'Usuario suspendido exitosamente'})

        except Usuario.DoesNotExist:
            return Response({'error': 'Usuario no encontrado'}, status=status.HTTP_404_NOT_FOUND)

    @action(detail=True, methods=['post'])
    def reactivar_usuario(self, request, pk=None):
        try:
            usuario = Usuario.objects.get(id=pk)
            
            if usuario.rol == 'ADMINISTRADOR':
                return Response({'error': 'No se puede reactivar a un administrador'}, status=status.HTTP_400_BAD_REQUEST)
            
            # Reactivar usuario directamente
            usuario.activo = True
            usuario.fecha_suspension = None
            usuario.motivo_suspension = None
            usuario.save()
            
            return Response({'mensaje': 'Usuario reactivado exitosamente'})
            
        except Usuario.DoesNotExist:
            return Response({'error': 'Usuario no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        except Exception as e:
            return Response({'error': f'Error interno: {str(e)}'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        
    @action(detail=True, methods=['post'])
    def aplicar_strike(self, request, pk=None):
        motivo = request.data.get('motivo', '')
        if not motivo:
            return Response({'error': 'El motivo es obligatorio'}, status=status.HTTP_400_BAD_REQUEST)

        try:
            usuario = Usuario.objects.get(id=pk)
            if usuario.rol == 'ADMINISTRADOR':
                return Response({'error': 'No se puede aplicar strike a un administrador'}, status=status.HTTP_400_BAD_REQUEST)

            # Aplicar strike
            usuario.strikes += 1
            usuario.save()

            # Si llega a 3 strikes, suspender automáticamente
            if usuario.strikes >= 3:
                usuario.activo = False
                usuario.fecha_suspension = timezone.now() + timedelta(days=7)
                usuario.motivo_suspension = f"Suspensión automática por acumular {usuario.strikes} strikes"
                usuario.save()

            return Response({
                'mensaje': f'Strike aplicado. Total strikes: {usuario.strikes}',
                'total_strikes': usuario.strikes,
                'suspendido_automaticamente': usuario.strikes >= 3
            })

        except Usuario.DoesNotExist:
            return Response({'error': 'Usuario no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
# ==================== ESTADÍSTICAS ====================

class EstadisticasViewSet(viewsets.ViewSet):
    """ViewSet para estadísticas"""
    permission_classes = [IsAdministradorPermission]
    authentication_classes = [JWTAuthentication]
    
    @action(detail=False, methods=['get'])
    def generales(self, request):
        """Estadísticas generales del sistema"""
        
        # Periodo de análisis
        periodo = request.GET.get('periodo', '30')
        fecha_inicio = timezone.now() - timedelta(days=int(periodo))
        
        # Estadísticas de contenido
        total_anuncios = Anuncio.objects.count()
        anuncios_activos = Anuncio.objects.filter(activo=True).count()
        anuncios_periodo = Anuncio.objects.filter(
            fecha_publicacion__gte=fecha_inicio
        ).count()
        
        # Estadísticas de engagement
        total_likes = Like.objects.count()
        total_comentarios = Comentario.objects.count()
        total_lecturas = Lectura.objects.count()
        
        data = {
            'periodo_dias': periodo,
            'contenido': {
                'total_anuncios': total_anuncios,
                'anuncios_activos': anuncios_activos,
                'anuncios_periodo': anuncios_periodo,
                'tasa_crecimiento': round(anuncios_periodo / max(int(periodo), 1), 2)
            },
            'engagement': {
                'total_likes': total_likes,
                'total_comentarios': total_comentarios,
                'total_lecturas': total_lecturas,
                'promedio_likes_por_anuncio': round(total_likes / max(total_anuncios, 1), 2),
                'promedio_comentarios_por_anuncio': round(total_comentarios / max(total_anuncios, 1), 2)
            }
        }
        
        return Response(data)