package com.example.manospy.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object RegisterClient : Screen("register_client")
    object ForgotPassword : Screen("forgot_password")
    object TermsAndConditions : Screen("terms_and_conditions")
    object PrivacyPolicy : Screen("privacy_policy")

    // Professional Registration Flow
    object RegisterProfessionalStep1 : Screen("register_professional_step1")
    object RegisterProfessionalStep2 : Screen("register_professional_step2")
    object RegisterProfessionalStep3 : Screen("register_professional_step3")

    // Validation States
    object ProfessionalPending : Screen("professional_pending")
    object ProfessionalApproved : Screen("professional_approved")
    object ProfessionalRejected : Screen("professional_rejected")
    object ProfessionalError : Screen("professional_error")

    // Main App Flows
    object ClientMain : Screen("client_main")
    object ProfessionalMain : Screen("professional_main")
    // ❌ AdminMain eliminado porque pertenece a otra app

    // Onboarding Flows - Client
    object ClientOnboardingStep1 : Screen("client_onboarding_step1")
    object ClientOnboardingStep2 : Screen("client_onboarding_step2")
    object ClientOnboardingStep3 : Screen("client_onboarding_step3")

    // Onboarding Flows - Professional
    object ProfessionalOnboardingStep1 : Screen("professional_onboarding_step1")
    object ProfessionalOnboardingStep2 : Screen("professional_onboarding_step2")
    object ProfessionalOnboardingStep3 : Screen("professional_onboarding_step3")

    // Feature Screens
    object Chat : Screen("chat/{id}") {
        fun createRoute(id: String) = "chat/$id"
    }
    object ChatAudit : Screen("chat_audit/{id}") {
        fun createRoute(id: String) = "chat_audit/$id"
    }

    // Specialized Flows
    object RequestService : Screen("request_service")
    object RequestConfirmation : Screen("request_confirmation")
    object WaitingProfessional : Screen("waiting_professional/{id}") {
        fun createRoute(id: String) = "waiting_professional/$id"
    }

    // New Reservation Flow
    object NewReservationStep1 : Screen("new_reservation_step1")
    object NewReservationStep2 : Screen("new_reservation_step2")
    object ServiceCategories : Screen("service_categories")
    object ReservationPending : Screen("reservation_pending/{id}") {
        fun createRoute(id: String) = "reservation_pending/$id"
    }
    object ReservationAccepted : Screen("reservation_accepted/{id}") {
        fun createRoute(id: String) = "reservation_accepted/$id"
    }
    object ReservationDetail : Screen("reservation_detail/{id}?pending={pending}") {
        fun createRoute(id: String, pending: Boolean = false) = "reservation_detail/$id?pending=$pending"
    }
    object ServiceCompletedReview : Screen("service_completed_review/{id}") {
        fun createRoute(id: String) = "service_completed_review/$id"
    }

    // Professional Screens
    object ProfessionalOffers : Screen("professional_offers")
    object ProfessionalOfferDetail : Screen("professional_offer_detail")
    object ProfessionalRequests : Screen("professional_requests")

    // Support
    object Support : Screen("support")
    object ChatRating : Screen("chat_rating/{id}/{name}") {
        fun createRoute(id: String, name: String) = "chat_rating/$id/$name"
    }
}

sealed class BottomNavScreen(val route: String, val label: String) {
    // Client Tabs
    object ClientHome : BottomNavScreen("client_home", "Inicio")
    object ClientSolicitar : BottomNavScreen("client_solicitar", "Solicitar")
    object ClientReservas : BottomNavScreen("client_reservas", "Reservas")
    object ClientChat : BottomNavScreen("client_chat", "Chat")
    object ClientPerfil : BottomNavScreen("client_perfil", "Perfil")

    // Professional Tabs
    object ProfHome : BottomNavScreen("prof_home", "Inicio")
    object ProfSolicitudes : BottomNavScreen("prof_solicitudes", "Solicitudes")
    object ProfReservas : BottomNavScreen("prof_reservas", "Reservas")
    object ProfChat : BottomNavScreen("prof_chat", "Chat")
    object ProfPerfil : BottomNavScreen("prof_perfil", "Perfil")
}
