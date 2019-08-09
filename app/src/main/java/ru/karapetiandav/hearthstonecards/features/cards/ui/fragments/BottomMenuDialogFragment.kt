package ru.karapetiandav.hearthstonecards.features.cards.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_menu.*
import org.koin.android.ext.android.inject
import ru.karapetiandav.hearthstonecards.R
import ru.terrakok.cicerone.Router

class BottomMenuDialogFragment : BottomSheetDialogFragment() {

    private val router: Router by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nav_view.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.account -> {
                    true
                }
                else -> false
            }
        }
    }
}