package com.example.qrscanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class FragmentBound<T>(val layoutId: Int) : Fragment() {
   // private lateinit var binding: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       //binding= DataBindingUtil.inflate(inflater, layoutId, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}