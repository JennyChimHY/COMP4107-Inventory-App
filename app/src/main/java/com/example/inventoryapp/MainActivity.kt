package com.example.inventoryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.inventoryapp.ui.theme.InventoryAppTheme


//todo: readme and more documentation
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun ScaffoldScreen() {
        var selectedItem by remember { mutableStateOf(0) }
        val items = listOf("Item", "Search", "Login")
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() } //lab11

//    val feeds = produceState( //lab9, default: empty
//        initialValue = listOf<Item>(),
//        producer = {
//            value = KtorClient.getFeeds()
//        }
//    )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Inventory System") }
                )
            },
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index }
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },  //lab11
            content = { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding),
                ) {

                    when (selectedItem) {
                        0 -> HomeNav(navController, snackbarHostState)
                        1 -> SearchScreen(snackbarHostState, navController)//Search()     DeptNav(navController, snackbarHostState)
                        2 -> LoginScreen(snackbarHostState)//test only     ItineraryScreen(snackbarHostState)
                    }
                }
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("Android")
                    ScaffoldScreen()
                }
            }
        }
    }
}
//    InventoryAppTheme {
//        Greeting("Android")
//    }