package com.example.common_arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Store<State, Command, Event>(
    initialState: State,
    private val commandHandlers: List<CommandHandler<Command, Event>>
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    abstract fun dispatch(event: Event)

    protected fun command(callback: () -> Command) {
        val command = callback()
        commandHandlers.forEach {
            val event = it.handle(command)
            if (event != null) {
                dispatch(event)
            }
        }
    }

    protected fun commands(callback: () -> List<Command>) {
        val commands = callback()
        commands.forEach {
            command { it }
        }
    }

    protected fun newState(callback: State.() -> State) {
        _state.value = callback(_state.value)
    }

}