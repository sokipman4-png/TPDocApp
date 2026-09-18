package com.tpdoc.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tpdoc.app.ui.screens.BerelasiScreen
import com.tpdoc.app.ui.screens.DokumenScreen
import com.tpdoc.app.ui.screens.HomeScreen
import com.tpdoc.app.ui.screens.KalkulatorScreen
import com.tpdoc.app.ui.screens.KesimpulanScreen
import com.tpdoc.app.ui.screens.KriteriaScreen
import com.tpdoc.app.ui.screens.NotifikasiScreen
import com.tpdoc.app.ui.screens.PencarianScreen
import com.tpdoc.app.ui.screens.SanksiScreen
import com.tpdoc.app.ui.screens.TransaksiScreen

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Routes.HOME, "Beranda", Icons.Default.Home),
    TabItem(Routes.KALKULATOR, "Kalkulator", Icons.Default.Calculate),
    TabItem(Routes.DOKUMEN, "Dokumen", Icons.Default.Description),
    TabItem(Routes.BERELASI, "Berelasi", Icons.Default.People),
    TabItem(Routes.CARI, "Cari", Icons.Default.Search),
)

@Composable
fun TPDocApp(navController: NavHostController = rememberNavController()) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigate = { route -> navController.navigate(route) },
                )
            }
            composable(Routes.KRITERIA) { KriteriaScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.KALKULATOR) { KalkulatorScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.BERELASI) { BerelasiScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.TRANSAKSI) { TransaksiScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.DOKUMEN) { DokumenScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.SANKSI) { SanksiScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.KESIMPULAN) { KesimpulanScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.NOTIFIKASI) { NotifikasiScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.CARI) {
                PencarianScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> navController.navigate(route) },
                )
            }
        }
    }
}