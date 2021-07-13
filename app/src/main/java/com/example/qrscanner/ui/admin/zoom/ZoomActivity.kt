package com.example.qrscanner.ui.admin.zoom

import android.os.Bundle
import androidx.customview.widget.ViewDragHelper
import com.bumptech.glide.Glide
import com.example.qrscanner.R
import com.github.chrisbanes.photoview.PhotoView
import com.phantomvk.slideback.SlideActivity

class ZoomActivity : SlideActivity() {
    private lateinit var photo: PhotoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom)

        val extra = intent?.extras?.getString("photoData")
        Glide.with(this).load(extra).into(findViewById(R.id.photoView))

        setSlideLayout()
    }
    private fun setSlideLayout(){
        val width=600
        val slideLayout = slideManager.slideLayout
        val density = resources.displayMetrics.density;
        slideLayout?.setEdgeSize((density * width).toInt());

    }

    override fun onContentChanged() {
        super.onContentChanged()
        val slideLayout = slideManager.slideLayout
        slideLayout?.setTrackingEdge(ViewDragHelper.EDGE_TOP)
        slideManager.slideLayout?.setTrackingEdge(ViewDragHelper.EDGE_TOP)
    }

    override fun finish() {
        super.finish()
        if (!slideManager.isSlideDisable) {
            overridePendingTransition(0, R.anim.slide_in_right);
        }
    }
}