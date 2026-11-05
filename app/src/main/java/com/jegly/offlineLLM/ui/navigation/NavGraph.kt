package com.jegly.offlineLLM.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jegly.offlineLLM.ui.screens.AboutScreen
import com.jegly.offlineLLM.ui.screens.ChatScreen
import com.jegly.offlineLLM.ui.screens.HelpScreen
import com.jegly.offlineLLM.ui.screens.KnowledgeBaseScreen
import com.jegly.offlineLLM.ui.screens.ModelConfigScreen
import com.jegly.offlineLLM.ui.screens.OnboardingScreen
import com.jegly.offlineLLM.ui.screens.SettingsScreen
import com.jegly.offlineLLM.ui.screens.ZimReaderScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Routes.Chat.route) {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Chat.route) {
            ChatScreen(
                onNavigateToSettings = {
                    navController.navigate(Routes.Settings.route)
                }
            )
        }

        composable(Routes.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Routes.About.route) },
                onNavigateToHelp = { navController.navigate(Routes.Help.route) },
                onNavigateToModelConfig = { navController.navigate(Routes.ModelConfig.route) },
                onNavigateToKnowledgeBase = { navController.navigate(Routes.KnowledgeBase.route) },
            )
        }

        composable(Routes.About.route) {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.Help.route) {
            HelpScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.ModelConfig.route) {
            ModelConfigScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.KnowledgeBase.route) {
            KnowledgeBaseScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.ZimReader.route,
            arguments = listOf(
                navArgument("zimPath") { type = NavType.StringType },
                navArgument("entryPath") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            ZimReaderScreen(
                zimPath = backStackEntry.arguments?.getString("zimPath") ?: "",
                entryPath = backStackEntry.arguments?.getString("entryPath") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
