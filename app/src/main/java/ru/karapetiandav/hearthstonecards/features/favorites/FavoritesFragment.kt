package ru.karapetiandav.hearthstonecards.features.favorites


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayoutManager
import com.redmadrobot.lib.sd.LoadingStateDelegate
import kotlinx.android.synthetic.main.fragment_favorites.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.base.BackPressHandler
import ru.karapetiandav.hearthstonecards.base.fragment.BaseFragment
import ru.karapetiandav.hearthstonecards.base.layout.ErrorLayoutData
import ru.karapetiandav.hearthstonecards.extensions.setupBackButton
import ru.karapetiandav.hearthstonecards.features.favorites.ui.adapter.FavoritesAdapter
import ru.karapetiandav.hearthstonecards.features.favorites.ui.state.FavoritesData
import ru.karapetiandav.hearthstonecards.features.favorites.ui.state.FavoritesError
import ru.karapetiandav.hearthstonecards.features.favorites.ui.state.FavoritesLoading
import ru.karapetiandav.hearthstonecards.features.favorites.ui.state.FavoritesViewState
import ru.karapetiandav.hearthstonecards.lifecycle.observe

class FavoritesFragment : BaseFragment() {

    private lateinit var screenState: LoadingStateDelegate

    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setupBackButton(toolbar, viewModel::onBackPressed)


        screenState = LoadingStateDelegate(favorites_list, loading, error_layout)

        observe(viewModel.state, ::onStateChanged)

        viewModel.loadFavorites()
    }

    private fun onStateChanged(viewState: FavoritesViewState) {
        when (viewState) {
            is FavoritesData -> {
                screenState.showContent()
                favorites_list.adapter = FavoritesAdapter(viewState.data)
                favorites_list.layoutManager = FlexboxLayoutManager(context)
            }
            is FavoritesLoading -> {
                screenState.showLoading()
            }
            is FavoritesError -> {
                screenState.showStub(
                    ErrorLayoutData(
                        R.drawable.ic_error_24dp,
                        getString(R.string.error_title),
                        getString(R.string.error_description)
                    )
                )
            }
        }
    }
}
