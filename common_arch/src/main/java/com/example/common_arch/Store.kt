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
        commandHandlers.forEach {
            val event = it.handle(callback())
            if (event != null) {
                dispatch(event)
            }
        }
    }

    protected fun newState(callback: State.() -> State) {
        _state.value = callback(_state.value)
    }

}