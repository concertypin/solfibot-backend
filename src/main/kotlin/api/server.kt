package api

import Chatbot.twitchClient
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.http.api.response.ResponseGeneralError
import models.http.api.response.ResponseRoot

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, ResponseRoot(twitchClient.chat.channels))
        }
        post("/channel/{channelName}") {
            //join
            val channelName = call.parameters["channelName"]
            twitchClient.chat.joinChannel(channelName)
            call.respond(HttpStatusCode.OK)
        }
        delete("/channel/{channelName}") {
            //join
            val channelName = call.parameters["channelName"]
            if (channelName in twitchClient.chat.channels) {
                twitchClient.chat.leaveChannel(channelName)
                call.respond(HttpStatusCode.OK)
            } else
                call.respond(
                    HttpStatusCode.NotFound,
                    ResponseGeneralError("NoSuchUser", "No such user in joined channel.")
                )
        }
    }
}