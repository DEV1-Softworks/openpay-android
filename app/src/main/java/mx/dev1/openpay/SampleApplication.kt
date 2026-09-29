package mx.dev1.openpay

import android.app.Application
import mx.dev1.openpay.di.sampleAppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // The guard keeps test runners that recreate the Application (for
        // example Robolectric) from starting Koin twice in the same process.
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@SampleApplication)
                modules(sampleAppModule)
            }
        }
    }
}
