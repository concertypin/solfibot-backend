package dao

import com.mongodb.kotlin.client.coroutine.MongoClient
import settings.mongoDBconnectionURL

val client = MongoClient.create(mongoDBconnectionURL)
val database = client.getDatabase("solfibot")