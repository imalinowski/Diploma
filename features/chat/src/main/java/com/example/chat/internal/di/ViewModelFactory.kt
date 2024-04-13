package com.malinowski.diploma.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

/**
 * @author i.malinowski
 **/

class ViewModelFactory @Inject constructor(
    private val creators: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators.entries
            .firstOrNull { (vmClass, _) -> modelClass.isAssignableFrom(vmClass) }
            ?.value ?: error("Unknown ViewModel class: $modelClass")
        @Suppress("UNCHECKED_CAST")
        return creator.get() as T
    }
}

@Module
abstract class ViewModelBuilderModule {

    @Binds
    abstract fun bindViewModelFactory(
        factory: ViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WifiDirectViewModel::class)
    abstract fun bindViewModel(viewModel: WifiDirectViewModel): ViewModel

}

@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
