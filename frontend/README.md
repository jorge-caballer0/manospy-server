# Frontend - ManosPy 2.0

Esta carpeta contiene la estructura del frontend Android modificado.

## 📁 Estructura

```
frontend/
└── app/
    └── src/main/java/com/example/manospy/
        ├── ui/screens/
        │   └── SplashScreen.kt          ✅ Modificado
        ├── ui/viewmodel/
        │   └── MainViewModel.kt          ✅ Modificado
        ├── data/local/
        │   └── SessionManager.kt         ✅ Modificado
        └── data/model/
            └── Models.kt                 ✅ Modificado
```

## 🚀 Cómo subir a GitHub

### Opción 1: Copiar archivos modificados a raíz
```powershell
# Los archivos ya están en:
# C:\Users\ACER2025\Documents\ManosPy2.0\ManosPy\app\src\...

# Para subir manualmente:
# 1. Copia todos los archivos de app/ a la raíz del repo
# 2. Ejecuta:
git add app/
git add gradle/
git add build.gradle.kts
git add settings.gradle.kts
git commit -m "Add: Frontend Android con 4 correcciones"
git push origin main
```

### Opción 2: Subir desde esta carpeta
```powershell
# Copiar esta estructura a la raíz del repo
xcopy frontend\app\ ..\app\ /E /I

# Luego subir normalmente
git add .
git push
```

## ✅ Archivos Modificados

- ✅ **SplashScreen.kt** - LaunchedEffect(Unit) para iniciar verificación
- ✅ **SessionManager.kt** - getUserRole(), getUserStatus(), guardar rol/status
- ✅ **MainViewModel.kt** - Agregar _userRole y _userStatus StateFlows
- ✅ **Models.kt** - Extender User con services[], cities[], certificates[], IDs

## 📝 Nota

Los archivos reales están en `C:\Users\ACER2025\Documents\ManosPy2.0\ManosPy\app\`

Esta carpeta `frontend/` es solo para organizar y subir a GitHub manualmente.
