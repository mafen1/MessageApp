package com.example.messageapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.messageapp.R
import com.example.messageapp.ui.navigation.AccountRoute
import com.example.messageapp.ui.navigation.ChatListRoute
import com.example.messageapp.ui.navigation.NewsFeedRoute
import com.example.messageapp.ui.navigation.SearchRoute

import kotlin.reflect.KClass

private data class BottomNavItem(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: KClass<*>
)

private val bottomNavItems = listOf(
    BottomNavItem(
        labelRes = R.string.nav_chats,
        selectedIcon = Icons.AutoMirrored.Filled.Chat,
        unselectedIcon = Icons.AutoMirrored.Outlined.Chat,
        route = ChatListRoute::class
    ),
    BottomNavItem(
        labelRes = R.string.nav_search,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        route = SearchRoute::class
    ),
    BottomNavItem(
        labelRes = R.string.nav_news,
        selectedIcon = Icons.Filled.Newspaper,
        unselectedIcon = Icons.Outlined.Newspaper,
        route = NewsFeedRoute::class
    ),
    BottomNavItem(
        labelRes = R.string.nav_account,
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        route = AccountRoute::class
    )
)

@Composable
fun MessageBottomBar(
    currentRouteClass: KClass<*>?,
    onNavigate: (KClass<*>) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRouteClass == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.labelRes)
                    )
                },
                label = { Text(stringResource(item.labelRes)) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        onNavigate(item.route)
                    }
                }
            )
        }
    }
}
