package ru.karapetiandav.hearthstonecards.base.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment: Fragment() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    protected fun Disposable.disposeOnViewDestroy(): Disposable {
        compositeDisposable.add(this)
        return this
    }
}