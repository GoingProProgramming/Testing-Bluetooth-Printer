package uk.co.goingproprogramming.tbp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import uk.co.goingproprogramming.tbp.services.ServiceNavigation
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ModuleServices {

    @Binds
    @Singleton
    abstract fun bindServiceNavigation(
        serviceNavigation: ServiceNavigation,
    ): IServiceNavigation
}