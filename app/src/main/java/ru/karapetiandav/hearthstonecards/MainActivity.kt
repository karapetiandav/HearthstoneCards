package ru.karapetiandav.hearthstonecards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.base.BackPressHandler
import ru.karapetiandav.hearthstonecards.main.viewmodel.MainViewModel
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    private val navigatorHolder: NavigatorHolder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            mainViewModel.onActivityCreate()
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(SupportAppNavigator(this, R.id.container))
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onBackPressed() {
        val fragment = this.supportFragmentManager.findFragmentById(R.id.container)
        (fragment as? BackPressHandler)?.onBackPressed()?.takeIf { !it }?.let { super.onBackPressed() }
    }
}
