package ru.karapetiandav.hearthstonecards.base.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.redmadrobot.lib.sd.StubState
import com.redmadrobot.lib.sd.ZeroStubView
import kotlinx.android.synthetic.main.error_layout.view.*
import ru.karapetiandav.hearthstonecards.R

class ErrorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle), ZeroStubView {

    init {
        inflate(context, R.layout.error_layout, this)
    }

    override fun setUpZero(state: StubState) {
        state as ErrorLayoutData
        error_layout_icon.setImageResource(state.icon)
        error_layout_title.text = state.title
        error_layout_description.text = state.description
    }
}