package com.example.inventoryapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.inventoryapp.ui.theme.InventoryAppTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Item( //json map the key into corresponding var automatically, no order needed
    val _id: String?,
    val title: String?,
    val author: String? = null,
    val year: String? = null,
    val isbn: String? = null, //skip deserialization
    val image: String?,
    val quantity: Int? = null,
    val donatedBy: String? = null,
    val description: String?,
    val category: String? = null,
    val amount: Int? = null,
    val unitPrice: Int? = null,
    val publisher: String? = null,
    val location: String?,
    val remark: String?,
    val type: String?,
    val borrower: String? = null,
    val remaining: Int? = null
) {
    companion object {
        var data =
            Item(
                _id = null,
                title = null,
                author = null,
                year = null,
                isbn = null, //skip deserialization
                image = null,
                quantity = null,
                donatedBy = null,
                description = null,
                category = null,
                amount = null,
                unitPrice = null,
                publisher = null,
                location = null,
                remark = null,
                type = null,
                borrower = null,
                remaining = null
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
//    feeds: List<Item>
    type: String?
) {

    //pagination
    var page by remember { mutableStateOf(1) }  //initialze
    Log.d("under Nav item/{type} ", "initial page = $page")
    val feeds = produceState( //lab9, default: empty
        initialValue = listOf<Item>(),
        key1 = page, //refresh once page updated
        producer = { //only ONE producer
            value =
                KtorClient.getFeeds(type, page) //get type from
        }
    )


    val coroutineScope = rememberCoroutineScope() //lab11
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row(verticalAlignment = Alignment.CenterVertically) { //put button before lazycolumn
            Column(horizontalAlignment = Alignment.CenterHorizontally) { //pagination2
                Button(onClick = {
                    if (page > 1) {
                        page--
                        Log.d("Pagination-Prev", "page: $page")
                    }
                }) {
                    Text(text = "Previous")
                }
            }

            Spacer(Modifier.size(16.dp))
            Text(text = "$page")
            Spacer(Modifier.size(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) { //pagination2
                Button(onClick = {
                    page++
                    Log.d("Pagination-Next", "page: $page")
                }) {
                    Text(text = "Next")
                }
            }
        }

        LazyColumn { //Scrollable in default

            items(feeds.value) { item ->
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
//                    Box(Modifier.fillMaxSize()) {
                        Text("Title: ${item.title}", Modifier.align(Alignment.Start))
                        Spacer(Modifier.size(16.dp))
                        Text("Description: ${item.description}", Modifier.align(Alignment.Start))
                        if(item.type == "gift" || item.type == "material") {
                            Text("Remaining: ${item.remaining}", Modifier.align(Alignment.Start))
                        }
//                    }
                    }

                    Spacer(Modifier.size(16.dp))

                    //Borrow & Return / Consume
                    if (globalLoginStatus) {
                        Button(onClick = {
                            coroutineScope.launch {
                                if (item.type == "book" || item.type == "game") {
                                    when (item.borrower ?: "") {
                                        "none" -> {
                                            var borrow: Boolean? = KtorClient.postBorrow(item._id?:"")
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
                                            var borrow: Boolean? = KtorClient.postReturn(item._id?:"")
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
                                        var consume: Boolean? = KtorClient.postConsume(item._id?:"")
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    _id: String?
) {
    val feeds = produceState( //lab9, default: empty
        initialValue = Item.data,
        producer = {
            value =
                KtorClient.getDetail(_id) //get type from
        }
    )

    Column { //Scrollable in default
        Card(
        ) {
            AsyncImage(
                model = feeds.value.image,
                contentDescription = null,
                modifier = Modifier.aspectRatio(15f / 9f) //ratio for image and description
            )

            Spacer(Modifier.size(16.dp))

            Column() {
                Text("Title: ${feeds.value.title}", Modifier.align(Alignment.Start))
                Spacer(Modifier.size(16.dp))
                Text("Description: ${feeds.value.description}", Modifier.align(Alignment.Start))

            }

            Spacer(Modifier.size(16.dp))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemPreview() {
    InventoryAppTheme() {
        // ItemScreen(Item.data)
    }
}



