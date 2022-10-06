package com.famas.plugins

import com.famas.room.RoomController
import com.famas.routes.chatSocket
import com.famas.routes.getAllMessages
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val roomController by inject<RoomController>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}
