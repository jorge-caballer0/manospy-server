package com.example.manospy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.local.SessionManager
import com.example.manospy.data.repository.AppRepository
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.screens.ChatScreen
import com.example.manospy.ui.screens.ChatRatingScreen
import com.example.manospy.ui.screens.ClientOnboardingScreenStep1
import com.example.manospy.ui.screens.ClientOnboardingScreenStep2
import com.example.manospy.ui.screens.ClientOnboardingScreenStep3
import com.example.manospy.ui.screens.CreateClientAccountScreen
import com.example.manospy.ui.screens.ForgotPasswordScreen
import com.example.manospy.ui.screens.LoginScreen
import com.example.manospy.ui.screens.MainContent
import com.example.manospy.ui.screens.PrivacyPolicyScreen
import com.example.manospy.ui.screens.ProfessionalAccountApprovedScreen
import com.example.manospy.ui.screens.ProfessionalEntryPoint
import com.example.manospy.ui.screens.ProfessionalOnboardingScreenStep2
import com.example.manospy.ui.screens.ProfessionalOnboardingScreenStep3
import com.example.manospy.ui.screens.ProfessionalOnboardingScreenStep1
import com.example.manospy.ui.screens.ProfessionalRegisterStep1Screen
import com.example.manospy.ui.screens.ProfessionalRegisterStep2Screen
import com.example.manospy.ui.screens.ProfessionalRegisterStep3Screen
import com.example.manospy.ui.screens.ProfessionalValidationPendingScreen
import com.example.manospy.ui.screens.RequestConfirmationScreen
import com.example.manospy.ui.screens.SplashScreen
import com.example.manospy.ui.screens.SupportScreen
import com.example.manospy.ui.screens.TermsAndConditionsScreen
import com.example.manospy.ui.screens.WaitingProfessionalScreen
import com.example.manospy.ui.screens.client.NewReservationStep1Screen
import com.example.manospy.ui.screens.client.NewReservationStep2Screen
import com.example.manospy.ui.screens.client.ReservationAcceptedScreen
import com.example.manospy.ui.screens.client.ServiceCompletedReviewScreen
import com.example.manospy.ui.screens.client.ServiceCategoryScreen
import com.example.manospy.ui.screens.ProfessionalOffersScreen
import com.example.manospy.ui.screens.ProfessionalOfferDetailScreen
import com.example.manospy.ui.screens.ProfessionalRequestsScreen
import com.example.manospy.ui.theme.ManosPyTheme
import com.example.manospy.ui.theme.Primary
import androidx.compose.ui.graphics.toArgb
import com.example.manospy.ui.viewmodel.AuthViewModel
import com.example.manospy.ui.viewmodel.AuthViewModelFactory
import com.example.manospy.ui.viewmodel.MainViewModel
import com.example.manospy.ui.viewmodel.ProfessionalRegisterViewModel
import com.example.manospy.ui.viewmodel.ProfessionalRegisterViewModelFactory
import com.example.manospy.ui.viewmodel.SplashViewModel
import com.example.manospy.ui.viewmodel.SplashViewModelFactory
import com.example.manospy.ui.viewmodel.ServiceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar RetrofitClient con contexto para acceso al token
        RetrofitClient.initialize(this)

        val repository = AppRepository(RetrofitClient.apiService)

        // Configurar barra de notificaciones azul
        val statusBarColor = Primary.toArgb()
        window.statusBarColor = statusBarColor
        window.navigationBarColor = statusBarColor
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = 0
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        try {
            val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
            insetsController?.isAppearanceLightStatusBars = false
        } catch (e: Exception) {
            // Fallback para versiones antiguas
        }

        setContent {
            ManosPyTheme {
                AppNavigation(repository)
            }
        }
    }
}

@Composable
fun AppNavigation(repository: AppRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Crear SessionManager
    val sessionManager = SessionManager(context)

    // ViewModels con factories corregidos
    val mainViewModel: MainViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(sessionManager)
    )
    val professionalViewModel: ProfessionalRegisterViewModel = viewModel(
        factory = ProfessionalRegisterViewModelFactory(sessionManager)
    )
    val splashViewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(
            context = context,
            apiService = RetrofitClient.apiService,
            mainViewModel = mainViewModel
        )
    )

    val serviceViewModel: ServiceViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                splashViewModel = splashViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    val user = mainViewModel.currentUser.value
                    val destination = when (user?.role) {
                        "client" -> Screen.ClientMain.route
                        "professional" -> Screen.ProfessionalMain.route
                        else -> Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
            androidx.compose.runtime.LaunchedEffect(Unit) {
                splashViewModel.start()
            }
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    mainViewModel.setUser(user)
                    val shouldShowOnboarding = !sessionManager.hasOnboardingBeenShown(user.role)
                    val destination = when {
                        // Si es profesional con estado específico
                        user.role == "professional" && user.status == "rejected" -> Screen.ProfessionalRejected.route
                        user.role == "professional" && user.status == "error" -> Screen.ProfessionalError.route
                        user.role == "professional" && user.status != "approved" -> Screen.ProfessionalPending.route
                        // Si está aprobado pero no vio onboarding, mostrar onboarding
                        shouldShowOnboarding && user.role == "client" -> Screen.ClientOnboardingStep1.route
                        shouldShowOnboarding && user.role == "professional" -> Screen.ProfessionalOnboardingStep1.route
                        // Normal flow
                        user.role == "client" -> Screen.ClientMain.route
                        user.role == "professional" -> Screen.ProfessionalMain.route
                        else -> Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegisterClient = {
                    navController.navigate(Screen.RegisterClient.route)
                },
                onNavigateToRegisterProfessional = {
                    navController.navigate(Screen.RegisterProfessionalStep1.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.RegisterClient.route) {
            CreateClientAccountScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { _ ->
                    // El usuario debe iniciar sesión manualmente
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.RegisterClient.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToTerms = { navController.navigate(Screen.TermsAndConditions.route) },
                onNavigateToPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) }
            )
        }

        composable(Screen.RegisterProfessionalStep1.route) {
            ProfessionalRegisterStep1Screen(
                viewModel = professionalViewModel,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.RegisterProfessionalStep2.route) },
                onCancel = { navController.popBackStack() }
            )

        }

        composable(Screen.RegisterProfessionalStep2.route) {
            ProfessionalRegisterStep2Screen(
                viewModel = professionalViewModel,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.RegisterProfessionalStep3.route) }
            )
        }

        composable(Screen.RegisterProfessionalStep3.route) {
            ProfessionalRegisterStep3Screen(
                viewModel = professionalViewModel,
                onBack = { navController.popBackStack() },
                onFinish = { user ->
                    if (user != null) {
                        mainViewModel.setUser(user)
                    }
                    navController.navigate(Screen.ProfessionalPending.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Client
        composable(Screen.ClientOnboardingStep1.route) {
            ClientOnboardingScreenStep1(
                onNext = { navController.navigate(Screen.ClientOnboardingStep2.route) },
                onSkip = {
                    sessionManager.setOnboardingCompleted("client")
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(Screen.ClientOnboardingStep1.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ClientOnboardingStep2.route) {
            ClientOnboardingScreenStep2(
                onNext = { navController.navigate(Screen.ClientOnboardingStep3.route) },
                onSkip = {
                    sessionManager.setOnboardingCompleted("client")
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(Screen.ClientOnboardingStep1.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ClientOnboardingStep3.route) {
            ClientOnboardingScreenStep3(
                onFinish = {
                    sessionManager.setOnboardingCompleted("client")
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(Screen.ClientOnboardingStep1.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Professional
        composable(Screen.ProfessionalOnboardingStep1.route) {
            ProfessionalOnboardingScreenStep1(
                onNext = { navController.navigate(Screen.ProfessionalOnboardingStep2.route) },
                onSkip = {
                    sessionManager.setOnboardingCompleted("professional")
                    navController.navigate(Screen.ProfessionalMain.route) {
                        popUpTo(Screen.ProfessionalOnboardingStep1.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProfessionalOnboardingStep2.route) {
            ProfessionalOnboardingScreenStep2(
                onNext = { navController.navigate(Screen.ProfessionalOnboardingStep3.route) },
                onSkip = {
                    sessionManager.setOnboardingCompleted("professional")
                    navController.navigate(Screen.ProfessionalMain.route) {
                        popUpTo(Screen.ProfessionalOnboardingStep1.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProfessionalOnboardingStep3.route) {
            ProfessionalOnboardingScreenStep3(
                onFinish = {
                    sessionManager.setOnboardingCompleted("professional")
                    navController.navigate(Screen.ProfessionalMain.route) {
                        popUpTo(Screen.ProfessionalOnboardingStep1.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProfessionalMain.route) {
            ProfessionalEntryPoint(
                navController = navController,
                userId = mainViewModel.currentUser.value?.id ?: ""
            )
        }

        composable(Screen.ProfessionalPending.route) {
            ProfessionalValidationPendingScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }

        composable(Screen.ProfessionalApproved.route) {
            ProfessionalAccountApprovedScreen(navController = navController)
        }

        composable(Screen.ClientMain.route) {
            MainContent(
                mainViewModel = mainViewModel,
                repository = repository,
                onLogout = {
                    authViewModel.logout()
                    mainViewModel.setUser(null)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Chat.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ChatScreen(navController = navController, reservationIdOrChatId = id)
        }

        composable(
            Screen.WaitingProfessional.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            WaitingProfessionalScreen(
                reservationId = id,
                viewModel = serviceViewModel,
                onProfessionalFound = {
                    navController.navigate(Screen.ReservationAccepted.createRoute(id)) {
                        popUpTo(Screen.WaitingProfessional.route) { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Screen.RequestConfirmation.route) {
            RequestConfirmationScreen(
                onBack = {
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onGoToRequests = {
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Support.route) {
            SupportScreen(navController = navController)
        }

        // New Reservation Flow
        composable(Screen.NewReservationStep1.route + "/{service}", arguments = listOf(navArgument("service") { type = NavType.StringType })) {
            NewReservationStep1Screen(navController = navController, viewModel = serviceViewModel)
        }

        composable(Screen.NewReservationStep1.route) { 
            NewReservationStep1Screen(navController = navController, viewModel = serviceViewModel)
        }

        composable(Screen.NewReservationStep2.route) {
            NewReservationStep2Screen(navController = navController, viewModel = serviceViewModel)
        }

        composable(Screen.ServiceCategories.route) {
            ServiceCategoryScreen(navController = navController)
        }

        composable(Screen.ProfessionalOffers.route) {
            ProfessionalOffersScreen(navController = navController, serviceViewModel = serviceViewModel)
        }

        composable(Screen.ProfessionalOfferDetail.route) {
            ProfessionalOfferDetailScreen(navController = navController, viewModel = serviceViewModel)
        }

        composable(Screen.ProfessionalRequests.route) {
            ProfessionalRequestsScreen(navController = navController)
        }

        composable(
            Screen.ReservationAccepted.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ReservationAcceptedScreen(navController = navController, reservationId = id, viewModel = serviceViewModel)
        }

        composable(
            Screen.ServiceCompletedReview.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ServiceCompletedReviewScreen(navController = navController, reservationId = id)
        }

        composable(
            Screen.ChatRating.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            ChatRatingScreen(
                professionalName = name,
                reservationId = id,
                onSubmit = { _, _ ->
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = {
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
