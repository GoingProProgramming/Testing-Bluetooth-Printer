package uk.co.goingproprogramming.tbp.application

import android.app.Application
import com.bugfender.sdk.Bugfender
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Bugfender.init(this, "xxlbWQR2whyKSvIIcgClWNZZSt1G0zu2", false, true)
        Bugfender.enableCrashReporting()
    }
}