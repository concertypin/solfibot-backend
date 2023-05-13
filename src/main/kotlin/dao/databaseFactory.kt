import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import models.db.userData.ListenerData
import models.db.userData.StreamerData
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class DatabaseUserData : RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var userid: String = ""
    
    var listener: ListenerData = ListenerData()
    var streamer: StreamerData = StreamerData()
}

val config = RealmConfiguration.create(setOf(DatabaseUserData::class))
val realm = Realm.open(config)