package com.example.edusync.presentation.views.navigation_menu

import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.edusync.R
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.theme.ui.AppColors
import kotlinx.coroutines.launch

@Composable
fun NavigationMenu(
    navigator: Navigator,
    currentRoute: String? = null
) {
    val navItems = listOf(
        Destination.MainScreen() to Pair(R.drawable.ic_schedule, R.drawable.ic_schedule_bold),
        Destination.MaterialsScreen to Pair(R.drawable.ic_materials, R.drawable.ic_materials_bold),
        Destination.FavoritiesScreen to Pair(R.drawable.ic_favorit_border, R.drawable.ic_favorit_bold),
        Destination.ProfileScreen to Pair(R.drawable.ic_person, R.drawable.ic_person_bold)
    )

    val scope = rememberCoroutineScope()

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
            navItems.forEach { (destination, icons) ->
                val isSelected = currentRoute == destination::class.qualifiedName
                Log.d("MainScreen", "$currentRoute")

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            scope.launch {
                                navigator.navigate(destination)
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