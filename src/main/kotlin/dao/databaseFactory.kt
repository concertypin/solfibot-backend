package dao

import com.mongodb.reactivestreams.client.MongoDatabase
import org.litote.kmongo.reactivestreams.KMongo

val client = KMongo.createClient()
val database: MongoDatabase = client.getDatabase("solfibot")