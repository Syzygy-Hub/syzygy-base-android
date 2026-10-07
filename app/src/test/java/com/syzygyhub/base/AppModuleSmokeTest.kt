package com.syzygyhub.base

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.syzygyhub.base.di.AppModule
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AppModuleSmokeTest {

    @Test
    fun `AppModule setup completes and container is not null`() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val appModule = AppModule(context)
        appModule.setup()
        assertNotNull(appModule.container)
    }
}
