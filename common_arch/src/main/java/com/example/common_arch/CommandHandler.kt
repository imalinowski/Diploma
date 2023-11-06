package com.example.common_arch

interface CommandHandler<Command, Event> {
    fun handle(command: Command): Event?
}