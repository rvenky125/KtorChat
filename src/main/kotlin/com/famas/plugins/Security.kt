package com.famas.plugins

import com.famas.session.ChatSession
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.sessions.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import org.koin.ktor.ext.get

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("CHAT_SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<ChatSession>() == null) {
            val username = call.parameters["username"] ?: "Guest"
            call.sessions.set(ChatSession(username, generateSessionId()))
        }
    }
}
