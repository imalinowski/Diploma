package com.example.common_arch

interface CommandHandler<Command, Event> {
    suspend fun handle(command: Command): Event?
}