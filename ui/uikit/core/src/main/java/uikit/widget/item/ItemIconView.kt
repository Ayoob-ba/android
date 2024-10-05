package uikit.widget.item

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.tonapps.uikit.color.accentBlueColor
import com.tonapps.uikit.color.stateList
import com.tonapps.uikit.icon.UIKitIcon
import com.tonapps.uikit.list.ListCell
import uikit.R
import uikit.drawable.DotDrawable
import uikit.extensions.setEndDrawable
import uikit.extensions.useAttributes

class ItemIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : BaseItemView(context, attrs, defStyle) {

    val textView: AppCompatTextView
    private val descriptionView: AppCompatTextView
    private val iconView: AppCompatImageView

    var text: CharSequence?
        get() = textView.text
        set(value) {
            textView.text = value
        }

    var description: String?
        get() = descriptionView.text.toString()
        set(value) {
            descriptionView.text = value
        }

    var iconRes: Int = 0
        set(value) {
            iconView.setImageResource(value)
        }

    var dot: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    textView.setEndDrawable(DotDrawable(context))
                } else {
                    textView.setEndDrawable(null)
                }
            }
        }

    init {
        inflate(context, R.layout.view_item_icon, this)

        textView = findViewById(R.id.text)
        iconView = findViewById(R.id.icon)
        descriptionView = findViewById(R.id.description)

        context.useAttributes(attrs, R.styleable.ItemIconView) {
            text = it.getString(R.styleable.ItemIconView_android_text)
            iconRes = it.getResourceId(R.styleable.ItemIconView_android_icon, UIKitIcon.ic_chevron_right_16)
            position = ListCell.from(it.getString(R.styleable.ItemIconView_position))

            val tint = it.getColor(R.styleable.ItemIconView_android_tint, 0)
            if (tint != 0) {
                setIconTintColor(tint)
            } else {
                setIconTintColor(context.accentBlueColor)
            }
        }
    }

    fun setIconTintColor(color: Int) {
        iconView.imageTintList = color.stateList
    }
}