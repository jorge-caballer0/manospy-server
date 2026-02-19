# ManosPy App

## Descripción
Aplicación Android desarrollada con **Kotlin** y **Jetpack Compose**, con navegación modular y roles diferenciados (Cliente y Profesional).  
El backend se gestiona con **Supabase** y **Node.js/Sequelize**, desplegado en **Render**, y el control de versiones está en **GitHub**.  
La arquitectura sigue el patrón **Clean Architecture**, donde la UI reacciona a estados (`StateFlow`) emitidos por los ViewModels tras consultar al Repositorio.

---

## 📂 Estructura del Proyecto

### Carpeta raíz
- `app/`: Módulo principal de la aplicación (Android App).
- `admin/`: Módulo para la aplicación de administración.
- `backend/`: Lógica del servidor (Node.js/Sequelize).
- `build.gradle.kts`: Configuración global de Gradle.
- `settings.gradle.kts`: Registro de los módulos `:app`, `:admin`, `:backend`.

---

### 📱 Módulo Principal (:app) - Cliente & Profesional
Ubicación: `app/src/main/java/com/example/manospy/`

#### 📁 navigation/
- `Screen.kt`: Definición de todas las rutas de la App (Splash, Login, Register, Main flows).
- `BottomNavScreen.kt`: Definición de iconos y etiquetas para pestañas de Cliente y Profesional.

#### 📁 ui/screens/
**Comunes y Onboarding**
- `SplashScreen.kt`: Logo animado y verificación de sesión.
- `LoginScreen.kt`: Acceso con selector de rol (Cliente/Pro).
- `ForgotPasswordScreen.kt`: Recuperación de cuenta.
- `TermsAndConditionsScreen.kt` / `PrivacyPolicyScreen.kt`: Documentación legal.
- `LoadingScreen.kt` / `ErrorScreen.kt`: Estados visuales globales.

**Flujo Cliente**
- `ClientOnboardingScreenStep1/2/3.kt`: Tutorial inicial.
- `CreateClientAccountScreen.kt`: Registro real de clientes.
- `ClientHomeScreen.kt`: Dashboard con categorías de servicios.
- `ServiceSelectionScreen.kt`: Selección de servicios.
- `RequestConfirmationScreen.kt`: Confirmación de pedido.
- `WaitingProfessionalScreen.kt`: Animación de búsqueda.
- `ClientHistoryScreen.kt`: Historial de servicios.
- `ChatScreen.kt`: Mensajería con el profesional.
- `ReviewScreen.kt`: Calificación del servicio.

**Flujo Profesional**
- `ProfessionalOnboardingScreenStep1/2/3.kt`: Tutorial inicial.
- `ProfessionalRegisterStep1/2/3Screen.kt`: Registro multi-paso.
- `ProfessionalValidationPendingScreen.kt`: Estado PENDING.
- `ProfessionalValidationRejectedScreen.kt`: Estado REJECTED.
- `ProfessionalAccountApprovedScreen.kt`: Estado ACTIVE.
- `ProfessionalHomeScreen.kt`: Dashboard Pro.
- `ProfessionalRequestsScreen.kt`: Solicitudes entrantes.
- `ProfessionalBookingsScreen.kt`: Reservas confirmadas.
- `ReputationScreen.kt`: Reseñas y estrellas.
- `ProfessionalMetricsScreen.kt`: Estadísticas de desempeño.
- `ProfessionalProfileScreen.kt`: Perfil público y disponibilidad.

#### 📁 ui/viewmodel/
- `MainViewModel.kt`: Estado global (Usuario actual, Sesión).
- `AuthViewModel.kt`: Lógica de Login y Registro.
- `ProfessionalRegisterViewModel.kt`: Manejo de datos de registro Pro.
- `ServiceViewModel.kt`: Lógica de pedidos y reservas.

#### 📁 data/
- `api/ApiService.kt`: Contratos de la API (Retrofit).
- `local/SessionManager.kt`: Token y datos de sesión.
- `model/Models.kt`: Objetos (User, ServiceRequest, Message, etc.).
- `repository/AppRepository.kt`: Punto único de acceso a datos.

---

### 👑 Módulo Administrador (:admin)
Ubicación: `admin/src/main/java/com/example/manospy/admin/`

- `LoginScreen.kt`: Acceso seguro.
- `AdminReportsDashboardScreen.kt`: Analytics.
- `ManageClientsScreen.kt`: Gestión de usuarios.
- `ValidateProfessionalsScreen.kt`: Lista de espera.
- `AdminProfessionalFullViewScreen.kt`: Revisión de documentos.
- `AdminRejectionFormScreen.kt`: Formulario de rechazo.
- `AdminChatScreen.kt`: Monitor de conversaciones.

---

### ⚙️ Módulo Backend (:backend)
Ubicación: `backend/`

- `index.js`: Servidor Node.js con PostgreSQL (Supabase).
- `package.json`: Dependencias (Express, JWT, Sequelize).

---

## 🚀 Flujo Lógico de la App

1. **Arranque (Splash):** Verifica Token → si no existe, va a Login; si existe, detecta rol.
2. **Registro Pro:** Completa 3 pasos → servidor marca como `PENDING` → app bloquea en pantalla de revisión.
3. **Aprobación (Admin):** Admin revisa documentos → aprueba → estado cambia a `ACTIVE`.
4. **Uso Profesional:** Pro accede a mapa y solicitudes en tiempo real.
5. **Ciclo Cliente:** Cliente solicita → servidor busca → Pro acepta → se crea reserva → se abre chat → Pro finaliza → Cliente califica.

---

## 🎯 Objetivo en VS Code

Este proyecto se abre en VS Code únicamente para:
- Analizar la **lógica y flujo de navegación**.
- Corregir inconsistencias en llamadas y parámetros.
- Mantener compatibilidad con Android Studio y Gradle.
- No modificar diseño ni formato visual.
- Respetar la arquitectura modular (Cliente vs Profesional).
- Mantener la integración con Supabase, Render y GitHub.
