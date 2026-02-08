package com.example.messageapp

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

fun View.setupBottomNavigation(
    bottomNavigationView: BottomNavigationView,
    currentDestinationId: Int,
    snackbarMessage: String = "Вы уже находитесь на данном экране",
    actions: BottomNavigationActions.() -> Unit
) {
    val navController = findNavController()
    bottomNavigationView.setupWithNavController(navController)

    val navigationActions = BottomNavigationActions(navController).apply(actions)

    bottomNavigationView.setOnItemSelectedListener { item ->
        if (item.itemId == currentDestinationId) {
            Snackbar.make(this, snackbarMessage, Snackbar.LENGTH_SHORT).show()
            true
        } else {
            navigationActions.getNavigationAction(item.itemId)?.invoke() ?: false
        }
    }
}

class BottomNavigationActions(private val navController: NavController) {
    private val actions = mutableMapOf<Int, () -> Boolean>()

    fun navigateWithDirections(itemId: Int, directionsProvider: () -> NavDirections) {
        actions[itemId] = {
            navController.navigate(directionsProvider())
            true
        }
    }

    fun navigateById(itemId: Int, destinationId: Int) {
        actions[itemId] = {
            navController.navigate(destinationId)
            true
        }
    }

    internal fun getNavigationAction(itemId: Int): (() -> Boolean)? = actions[itemId]
}