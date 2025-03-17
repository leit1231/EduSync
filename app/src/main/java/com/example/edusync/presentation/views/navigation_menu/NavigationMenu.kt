package com.example.edusync.presentation.views.navigation_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.edusync.R
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.theme.ui.AppColors

@Composable
fun NavigationMenu(
    navController: NavController,
    currentRoute: String
) {
    val navItems = listOf(
        Pair(NavRoutes.MainScreen, Pair(R.drawable.ic_schedule, R.drawable.ic_schedule_bold)),
        Pair(NavRoutes.MaterialsScreen, Pair(R.drawable.ic_materials, R.drawable.ic_materials_bold)),
        Pair(NavRoutes.FavoritiesScreen, Pair(R.drawable.ic_favorit_border, R.drawable.ic_favorit_bold)),
        Pair(NavRoutes.ProfileScreen, Pair(R.drawable.ic_person, R.drawable.ic_person_bold))
    )

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        containerColor = AppColors.Background
    ) {
        NavigationBar(
            containerColor = colorResource(id = R.color.background),
            modifier = Modifier.fillMaxSize()
        ) {
            navItems.forEach { (route, icons) ->
                val isSelected = currentRoute == route.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(route.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = if (isSelected) icons.second else icons.first),
                            contentDescription = null,
                            tint = if (isSelected) colorResource(id = R.color.background) else AppColors.SecondaryTransparent
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .height(80.dp)
                        .background(
                            color = if (isSelected) colorResource(id = R.color.primary) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                )
            }
        }
    }
}