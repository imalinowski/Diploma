package com.example.common_arch

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class Store<State, Command, Event, UiEvent>(
    initialState: State,
    private val commandHandlers: List<CommandHandler<Command, Event>>
) : ViewModel() {
    protected abstract val storeScope: CoroutineScope

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: State get() = _state.value

    private val _events: MutableSharedFlow<UiEvent> = MutableSharedFlow()

    abstract fun dispatch(event: Event)

    fun collect(
        lifecycleScope: LifecycleCoroutineScope,
        render: (State) -> Unit,
        handleEvents: (UiEvent) -> Unit = {}
    ) {
        lifecycleScope.launch {
            _state.collect { render(it) }
        }
        lifecycleScope.launch {
            _events.collect { handleEvents(it) }
        }
    }

    protected fun commands(callback: () -> List<Command>) {
        val commands = callback()
        commands.forEach {
            command { it }
        }
    }

    protected fun command(callback: () -> Command) {
        val command = callback()
        storeScope.launch(Dispatchers.IO) {
            commandHandlers.forEach {
                val event = it.handle(command)
                if (event != null) {
                    dispatch(event)
                }
            }
        }
    }

    protected fun newState(callback: State.() -> State) {
        _state.value = callback(_state.value)
    }

    protected fun newEvent(callback: State.() -> UiEvent) {
        storeScope.launch {
            _events.emit(
                callback(_state.value)
            )
        }
    }
}