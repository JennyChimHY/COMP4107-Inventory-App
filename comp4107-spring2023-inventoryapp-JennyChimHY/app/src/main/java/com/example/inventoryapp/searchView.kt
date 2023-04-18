package com.example.inventoryapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(snackbarHostState: SnackbarHostState, navController: NavHostController) {
    val padding = 16.dp
    var searchString by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var message = ""  //initialze

    val result = produceState( //lab9, default: empty
        initialValue = listOf<Item>(),
        key1 = searchString, //refresh once page updated
        producer = { //only ONE producer
            value = KtorClient.getSearch(searchString) //get type from
        }
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Search ", fontSize = 16.sp,
                modifier = Modifier.padding(18.dp)
            )
            TextField(
                maxLines = 1,
                value = searchString,
                onValueChange = { searchString = it }
            )
        }

        Spacer(Modifier.size(padding))

        Button(onClick = {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Searched.") //bug cannot show card
            }
        }) {
            Text(text = "Search")
        }
    }


    LazyColumn {
        items(result.value!!) { item ->
            Card(
                onClick = {
                    coroutineScope.launch {
                        navController.navigate("detail/${item._id}") //new name
                    }
                },
            ) {
                AsyncImage(
                    model = item.image,
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(15f / 9f) //ratio for image and description
                )

                Spacer(Modifier.size(16.dp))

                Column() {
                    Text("Title: ${item.title}", Modifier.align(Alignment.Start))
                    Spacer(Modifier.size(16.dp))
                    Text("Description: ${item.description}", Modifier.align(Alignment.Start))
                }

                Spacer(Modifier.size(16.dp))

                //Borrow & Return / Consume
                if (globalLoginStatus) {
                    Button(onClick = {
                        coroutineScope.launch {
                            if (item.type == "book" || item.type == "game") {
                                when (item.borrower ?: "") {
                                    "none" -> {
                                        var borrow: Boolean? = KtorClient.postBorrow(item._id ?: "")
                                        if (borrow == true) {
                                            snackbarHostState.showSnackbar(
                                                "Borrow Successfully. The item has been borrowed by ${globalLoginInfo.last_name ?: ""} ${globalLoginInfo.first_name ?: ""}."
                                            )
                                        } else {
                                            snackbarHostState.showSnackbar(
                                                "Borrow Failed!"
                                            )
                                        }
                                    }
                                    "me" -> {
                                        var borrow: Boolean? = KtorClient.postReturn(item._id ?: "")
                                        if (borrow == true) { //fade out null and false
                                            snackbarHostState.showSnackbar(
                                                "Return Successfully."
                                            )
                                        } else {
                                            snackbarHostState.showSnackbar(
                                                "Return Failed!"
                                            )
                                        }
                                    }
                                    else -> {
                                        snackbarHostState.showSnackbar(
                                            "No action can be done."
                                        )
                                    }
                                }
                            } else if (item.type == "gift" || item.type == "material") {
                                if ((item.remaining ?: 0) > 0) {
                                    var consume: Boolean? = KtorClient.postConsume(item._id ?: "")
                                    if (consume == true) {
                                        snackbarHostState.showSnackbar(
                                            "Consume Successfully."
                                        )
                                    } else {
                                        snackbarHostState.showSnackbar(
                                            "Consume Failed!"
                                        )
                                    }
                                } else snackbarHostState.showSnackbar(
                                    "Sold out!"
                                )
                            }
                        }
                    }) {
                        if (item.type == "book" || item.type == "game") {
                            when (item.borrower) {
                                "none" -> Text(text = "Borrow")
                                "me" -> Text(text = "Return")
                                else -> Text(text = "The item is borrowed by ${item.borrower}")
                            }
                        } else if (item.type == "gift" || item.type == "material") {
                            if ((item.remaining ?: 0) > 0) {
                                Text(text = "Consume")
                            } else Text(text = "Sold")
                        }
                    }
                }
                Divider()
            }
            Spacer(Modifier.size(16.dp))
        }
    }
}