package com.example.inventoryapp

import android.util.Log
import com.example.inventoryapp.KtorClient.httpClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class HttpBinResponse( //Item
    val args: Map<String, String>,
    val data: String,
    val files: Map<String, String>,
    val form: Map<String, String>,
    val headers: Map<String, String>,
    val json: String?,
    val origin: String,
    val url: String,
)

@Serializable
data class HttpLogResponse( //Login
    val first_name: String?, //200 success
    val last_name: String? ,
    val token: String? ,
    val error: String? //400 invalid
)

@Serializable
data class HttpBorrowConsumeResponse(
    val message: String?,
    val error: String?
)

@OptIn(ExperimentalSerializationApi::class)
object KtorClient {
    private var token: String = ""
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            }

            ) // enable the client to perform JSON serialization
        }
        install(Logging)
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("Authorization", token)
        }
//        expectSuccess = true //allow error
    }


    suspend fun getFeeds(type: String?, page: Int): List<Item> { //Item list
        return httpClient.get("https://comp4107.azurewebsites.net/inventory?type=$type&page=$page").body()  //conversion: text from the api --> list of feeds with sepcific type
    }

    suspend fun getDetail(_id: String?): Item { //Item list
        var result = httpClient.get("https://comp4107.azurewebsites.net/inventory/$_id")
        Log.d("detail result: ", result.toString())
        return result.body() //conversion: text from the api --> list of feeds with sepcific type
//        return httpClient.get("https://comp4107.azurewebsites.net/inventory/$_id").body()
    }

    suspend fun getSearch(keyword: String?): List<Item> { //Search function
        return httpClient.get("https://comp4107.azurewebsites.net/inventory?keyword=$keyword").body()  //conversion: text from the api --> list of feeds with sepcific type
    }

    suspend fun postLogin(login: Info): User { //Login function
//        Log.d("Enter PostLog", "success")
        val response: HttpLogResponse = httpClient.post("https://comp4107.azurewebsites.net/user/login") {
            setBody(login) //data class is fine in converting to json
        }.body()

        token = response.token?:""

        var user = User(response.first_name, response.last_name, response.token, response.error)
        return user
    }

    suspend fun postBorrow(itemId: String): Boolean { //Book/Game --> return Borrowed = true

//        Log.d("Before postBorrow", "Enter")

        val response: HttpBorrowConsumeResponse = httpClient.post("https://comp4107.azurewebsites.net/user/borrow/${itemId}") {
            //setBody(itemId) //No need extra input, no data encryption
        }.body()
//        Log.d("After postBorrow", response.toString())
//        Log.d("BMsg", response.message?:"no msg")

        return response.message != null
    }

    suspend fun postReturn(itemId: String): Boolean { //Book/Game --> return Borrowed = true

//        Log.d("Before postBorrow", "Enter")

        val response: HttpBorrowConsumeResponse = httpClient.post("https://comp4107.azurewebsites.net/user/return/${itemId}") {
        }.body()
//        Log.d("After return", response.toString())
//        Log.d("Return ,msg", response.message?:"no msg")
        return response.message != null
    }

    suspend fun postConsume(itemId: String): Boolean { //Gift/Material --> return consume = true

        Log.d("Before postConsume", "Enter $itemId")
        val response: HttpBorrowConsumeResponse = httpClient.post("https://comp4107.azurewebsites.net/user/consume/${itemId}") {
//            contentType(ContentType.Application.Json)
            //setBody(itemId)
        }.body()

//        Log.d("After postConsume", response.toString())
//        Log.d("CMsg", response.message?:"no msg")

        return response.message != null
    }
}



