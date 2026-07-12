package com.example.messageapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.messageapp.ui.chatListScreen.ChatListScreen
import com.example.messageapp.ui.chatListScreen.ChatListViewModel
import com.example.messageapp.ui.components.MessageBottomBar
import com.example.messageapp.ui.screen.account.AccountScreen
import com.example.messageapp.ui.screen.auth.AuthScreen
import com.example.messageapp.ui.screen.chat.ChatScreen
import com.example.messageapp.ui.screen.news.CreatePostScreen
import com.example.messageapp.ui.screen.news.NewsFeedScreen
import com.example.messageapp.ui.screen.onboarding.OnboardingScreen
import com.example.messageapp.ui.screen.search.SearchScreen
import com.example.messageapp.ui.screen.welcome.WelcomeScreen

private fun currentMainTabUser(currentBackStackEntry: androidx.navigation.NavBackStackEntry?): Pair<String, String>? {
    val destination = currentBackStackEntry?.destination ?: return null
    return when {
        destination.hasRoute(ChatListRoute::class) -> {
            val route = currentBackStackEntry.toRoute<ChatListRoute>()
            route.userName to route.name
        }
        destination.hasRoute(SearchRoute::class) -> {
            val route = currentBackStackEntry.toRoute<SearchRoute>()
            route.userName to route.name
        }
        destination.hasRoute(NewsFeedRoute::class) -> {
            val route = currentBackStackEntry.toRoute<NewsFeedRoute>()
            route.userName to route.name
        }
        destination.hasRoute(AccountRoute::class) -> {
            val route = currentBackStackEntry.toRoute<AccountRoute>()
            route.userName to route.name
        }
        else -> null
    }
}

@Composable
fun MessageAppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val currentMainTabRoute = when {
        currentDestination?.hasRoute(ChatListRoute::class) == true -> ChatListRoute::class
        currentDestination?.hasRoute(SearchRoute::class) == true -> SearchRoute::class
        currentDestination?.hasRoute(NewsFeedRoute::class) == true -> NewsFeedRoute::class
        currentDestination?.hasRoute(AccountRoute::class) == true -> AccountRoute::class
        else -> null
    }

    val showBottomBar = currentMainTabRoute != null

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MessageBottomBar(
                    currentRouteClass = currentMainTabRoute,
                    onNavigate = { routeClass ->
                        val (userName, name) = currentMainTabUser(currentBackStackEntry) ?: return@MessageBottomBar
                        val route = when (routeClass) {
                            ChatListRoute::class -> ChatListRoute(userName, name)
                            SearchRoute::class -> SearchRoute(userName, name)
                            NewsFeedRoute::class -> NewsFeedRoute(userName, name)
                            AccountRoute::class -> AccountRoute(userName, name)
                            else -> null
                        }
                        route?.let {
                            navController.navigate(it) {
                                popUpTo(navController.graph.id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = WelcomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<WelcomeRoute> {
                WelcomeScreen(
                    onNavigateToSearch = { userName, name ->
                        navController.navigate(SearchRoute(userName, name)) {
                            popUpTo(WelcomeRoute) { inclusive = true }
                        }
                    },
                    onNavigateToOnboarding = {
                        navController.navigate(OnboardingRoute) {
                            popUpTo(WelcomeRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<OnboardingRoute> {
                OnboardingScreen {
                    navController.navigate(AuthRoute) {
                        popUpTo(OnboardingRoute) { inclusive = true }
                    }
                }
            }

            composable<AuthRoute> {
                AuthScreen(
                    onAuthSuccess = { userName, name ->
                        navController.navigate(SearchRoute(userName, name)) {
                            popUpTo(AuthRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<SearchRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<SearchRoute>()
                SearchScreen(
                    userName = args.userName,
                    name = args.name,
                    onNavigateToChatList = { userName, name ->
                        navController.navigate(ChatListRoute(userName, name))
                    },
                    onNavigateToNews = { userName, name ->
                        navController.navigate(NewsFeedRoute(userName, name))
                    },
                    onNavigateToAccount = {
                        navController.navigate(AccountRoute(args.userName, args.name))
                    },
                    onNavigateToChat = { otherUserName, otherName ->
                        navController.navigate(
                            ChatRoute(
                                currentUserName = args.userName,
                                currentName = args.name,
                                otherUserName = otherUserName,
                                otherName = otherName
                            )
                        )
                    }
                )
            }

            composable<ChatListRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<ChatListRoute>()
                val viewModel: ChatListViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(args.userName, args.name) {
                    viewModel.loadChatList(args.userName, args.name)
                }

                ChatListScreen(
                    uiState = uiState,
                    onUserClick = { selectedUser ->
                        navController.navigate(
                            ChatRoute(
                                currentUserName = args.userName,
                                currentName = args.name,
                                otherUserName = selectedUser.username,
                                otherName = selectedUser.name
                            )
                        )
                    },
                    onRetry = { viewModel.loadChatList(args.userName, args.name) }
                )
            }

            composable<ChatRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<ChatRoute>()
                ChatScreen(
                    currentUserName = args.currentUserName,
                    currentName = args.currentName,
                    otherUserName = args.otherUserName,
                    otherName = args.otherName,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<NewsFeedRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<NewsFeedRoute>()
                NewsFeedScreen(
                    userName = args.userName,
                    name = args.name,
                    onNavigateToSearch = { userName, name ->
                        navController.navigate(SearchRoute(userName, name))
                    },
                    onNavigateToChatList = { userName, name ->
                        navController.navigate(ChatListRoute(userName, name))
                    },
                    onNavigateToAccount = {
                        navController.navigate(AccountRoute(args.userName, args.name))
                    },
                    onNavigateToCreatePost = { userName, name ->
                        navController.navigate(CreatePostRoute(userName, name))
                    }
                )
            }

            composable<CreatePostRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<CreatePostRoute>()
                CreatePostScreen(
                    userName = args.userName,
                    name = args.name,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<AccountRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<AccountRoute>()
                AccountScreen(
                    userName = args.userName,
                    name = args.name,
                    onNavigateToSearch = { userName, name ->
                        navController.navigate(SearchRoute(userName, name))
                    },
                    onNavigateToChatList = { userName, name ->
                        navController.navigate(ChatListRoute(userName, name))
                    },
                    onNavigateToNews = { userName, name ->
                        navController.navigate(NewsFeedRoute(userName, name))
                    },
                    onLogout = {
                        navController.navigate(OnboardingRoute) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
