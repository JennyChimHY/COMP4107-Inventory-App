package com.example.inventoryapp

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.inventoryapp.ui.theme.InventoryAppTheme

data class Home(val name: String, val type: String) {
    companion object {
        val data = listOf(
            Home("Books", "book"),
            Home("Games", "game"),
            Home("Gifts", "gift"),
            Home("Materials", "material")
        )
    }
}

@Composable
fun HomeNav(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    var _id: String? = ""
    var page by remember{ mutableStateOf<Int>(1) }
    var type: String? = "" //initialize

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            HomeScreen(navController)
        }

//        composable("login") {
//            if (globalLoginStatus)  //navigation: if login then home else go to login
//                HomeScreen(navController)
//            else
//                LoginScreen(snackbarHostState)
//        }

        composable("item/{type}") { backStackEntry ->
            page = 1 //initialze
            Log.d("under Nav item/{type} ", "initial page = $page")
            type = backStackEntry.arguments?.getString("type")
            ItemScreen(navController,snackbarHostState, type) //info from loginView.postLogin //feeds.value
        }

        //detail page
        composable("detail/{_id}") { backStackEntry ->
            DetailScreen(_id = backStackEntry.arguments?.getString("_id"))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    LazyColumn {
        items(Home.data) { home -> //for-each (~for-each)
            ListItem(
                headlineText = { Text(home.name) },
                modifier = Modifier.clickable {
                    navController.navigate("item/${home.type}") //pass to item page
                },
                leadingContent = {
                    when(home.type) {
                        "book" -> Icon(
                                Icons.Filled.Menu,
                                contentDescription = null
                            )
                        "game" -> Icon(
                                Icons.Filled.Face,
                        contentDescription = null
                            )
                        "gift" -> Icon(
                                Icons.Filled.Person,
                        contentDescription = null
                            )
                        "material" -> Icon(
                            Icons.Filled.Build,
                            contentDescription = null
                        )
                    }
                }
            )
            Divider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    InventoryAppTheme() {
        // DeptNav(rememberNavController())
    }
}
