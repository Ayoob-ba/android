package com.tonapps.tonkeeper.ui.screen.collectibles.list

import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.facebook.drawee.generic.RoundingParams
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.tonapps.tonkeeper.ui.screen.nft.NftScreen
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.color.backgroundHighlightedColor
import com.tonapps.uikit.color.stateList
import com.tonapps.uikit.list.BaseListHolder
import uikit.extensions.getDimension
import uikit.extensions.round
import uikit.navigation.Navigation
import uikit.widget.FrescoView

class Holder(parent: ViewGroup): BaseListHolder<Item>(parent, R.layout.view_collectibles) {

    private val radius = context.getDimension(uikit.R.dimen.cornerMedium)

    private val imageView = findViewById<FrescoView>(R.id.image)
    private val titleView = findViewById<AppCompatTextView>(R.id.title)
    private val collectionView = findViewById<AppCompatTextView>(R.id.collection)

    init {
        itemView.foreground = RippleDrawable(context.backgroundHighlightedColor.stateList, null, null)

        itemView.round(radius.toInt())
        imageView.hierarchy.roundingParams = RoundingParams.fromCornersRadii(radius, radius, 0f, 0f)
    }

    override fun onBind(item: Item) {
        itemView.setOnClickListener {
            Navigation.from(context)?.add(NftScreen.newInstance(item.entity))
        }
        loadImage(item.imageURI)
        titleView.text = item.title
        collectionView.text = item.collectionName
    }

    private fun loadImage(uri: Uri) {
        val builder = ImageRequestBuilder.newBuilderWithSource(uri)
        builder.resizeOptions = ResizeOptions.forSquareSize(192)
        imageView.setImageRequest(builder.build())
    }
}