package com.example.qrscanner.ui.executive

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.qrscanner.R
import com.example.qrscanner.databinding.BottomSheetUnvalidBarcodeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetUnvalidFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetUnvalidBarcodeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_unvalid_barcode, container, false)
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setDimAmount(0F)
        dialog.window?.setWindowAnimations(-1)
        return dialog
    }


}