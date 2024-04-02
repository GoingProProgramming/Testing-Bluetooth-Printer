package uk.co.goingproprogramming.tbp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.co.goingproprogramming.tbp.printer.IPrinterBixolon
import uk.co.goingproprogramming.tbp.printer.IPrinterBluetooth
import uk.co.goingproprogramming.tbp.printer.IPrinterBrother
import uk.co.goingproprogramming.tbp.printer.IPrinterZebra
import uk.co.goingproprogramming.tbp.printer.PrinterBixolon
import uk.co.goingproprogramming.tbp.printer.PrinterBluetooth
import uk.co.goingproprogramming.tbp.printer.PrinterBrother
import uk.co.goingproprogramming.tbp.printer.PrinterZebra
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModulePrinter {
    @Provides
    @Singleton
    fun providePrinterBluetooth(
        @ApplicationContext appContext: Context,
    ): IPrinterBluetooth =
        PrinterBluetooth(appContext)

    @Provides
    @Singleton
    fun providePrinterZebra(): IPrinterZebra =
        PrinterZebra()

    @Provides
    @Singleton
    fun providePrinterBrother(
        @ApplicationContext appContext: Context,
    ): IPrinterBrother =
        PrinterBrother(appContext)

    @Provides
    @Singleton
    fun providePrinterBixolon(
        @ApplicationContext appContext: Context,
    ): IPrinterBixolon =
        PrinterBixolon(appContext)
}