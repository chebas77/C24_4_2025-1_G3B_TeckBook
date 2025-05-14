# TecBook - TECSUP

## Convenciones del repositorio

Este proyecto sigue el enfoque Trunk Based Development con las siguientes convenciones:

### Ramas
- **main**: Rama principal, siempre desplegable
- **feature/nombre-caracteristica**: Para nuevas funcionalidades (duración: días)
- **bugfix/nombre-bug**: Para corrección rápida de errores (duración: menos de un día)
- **hotfix/nombre-hotfix**: Para correcciones urgentes en producción
- **release/numero-version**: Para preparar versiones de lanzamiento

### Flujo de trabajo
1. Crear rama desde main
2. Desarrollar y hacer commits frecuentes
3. Solicitar merge a main (pull request)
4. Después de aprobar, realizar merge
5. Eliminar la rama de característica

### Etiquetas (tags)
- Las versiones se etiquetan con la convención: v[MAYOR].[MENOR].[PARCHE]
- Ejemplo: v1.0.0, v1.2.3

### Commits
- Usar mensajes descriptivos
- Formato recomendado: `[tipo]: descripción breve`
- Tipos: feat, fix, docs, style, refactor, test
DEPENDENCIAS
-npm axios
-npm install
