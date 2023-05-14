package dao

import com.mongodb.reactivestreams.client.MongoDatabase
import org.litote.kmongo.reactivestreams.KMongo
import settings.mongoDBconnectionURL

val client = KMongo.createClient(mongoDBconnectionURL)
val database: MongoDatabase = client.getDatabase("solfibot")