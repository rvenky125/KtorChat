package com.famas.room

import com.famas.data.MessageDataSource
import com.famas.data.model.Message
import com.fasterxml.jackson.databind.ser.SerializerFactory
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.cio.websocket.*
import io.ktor.jackson.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.concurrent.ConcurrentHashMap
import kotlin.Throws

class RoomController(
    private val messageDataSource: MessageDataSource,
) {
    private val members = ConcurrentHashMap<String, Member>()

    @Throws(MemberAlreadyExistsException::class)
    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if (members.contains(username)) {
            throw MemberAlreadyExistsException
        }
        members[username] = Member(
            username = username,
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendMessage(senderUsername: String, message: String) {
        members.values.forEach { member ->
            val messageEntity = Message(
                text = message,
                username = senderUsername,
                timestamp = System.currentTimeMillis()
            )
            messageDataSource.insertMessage(messageEntity)

            val parsedMessage = jacksonObjectMapper().writeValueAsString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun sendBinary(frame: Frame.Binary) {
        members.values.forEach { member ->
            member.socket.send(frame)
        }
    }

    suspend fun getAllMessages() : List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }
}