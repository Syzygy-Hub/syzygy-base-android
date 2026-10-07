package com.syzygyhub.base.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DIGraphTest {
    private lateinit var appModule: AppModule

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        appModule = AppModule(context)
        appModule.setup()
    }

    @Test
    fun `container resolves all registered singletons without throwing`() {
        assertNotNull(appModule.container)
    }
}
