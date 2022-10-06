package com.famas.di

import com.famas.Constants
import com.famas.data.MessageDataSource
import com.famas.data.MessageDatasourceImpl
import com.famas.room.RoomController
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        KMongo.createClient().getDatabase(Constants.DATABASE_NAME).coroutine
    }

    single<MessageDataSource> {
        MessageDatasourceImpl(get())
    }

    single {
        RoomController(get())
    }
}