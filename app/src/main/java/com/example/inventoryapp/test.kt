//package com.example.inventoryapp
//
//
//
//var selectedItem by remember { mutableStateOf(0) }
//val navController = rememberNavController()
//val snackbarHostState = remember { SnackbarHostState() } //lab11
//
//
//
//    NavigationBar {
//        items.forEachIndexed { index, item ->
//            NavigationBarItem(
//                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
//                label = { Text(item) },
//            )
//        },
//snackbarHost = { SnackbarHost(snackbarHostState) },