package com.example.qrscanner.ui.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.qrscanner.R
import com.example.qrscanner.databinding.FragmentDialogBinding

class ScannerDialogFragment(val onClicked: () -> Unit) : DialogFragment() {
    private lateinit var binding: FragmentDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = arguments?.get("token").toString()

        binding.textView.text = token

        binding.floatingActionButtonClose.setOnClickListener {
            onClicked()
            dismiss()
        }
    }
}