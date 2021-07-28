package com.example.qrscanner.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.databinding.FragmentBarcodeReaderBinding
import com.example.qrscanner.ui.admin.scanner.ScannerQrViewModel

class BarcodeReaderFragment : Fragment() {
    private lateinit var binding: FragmentBarcodeReaderBinding
    private lateinit var viewModel: ScannerQrViewModel
    private var token = ""
    private var textWatcher: TextWatcher? = null

    private val cTimer = object : CountDownTimer(300, 100) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            binding.editText.setText("")
            token = ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBarcodeReaderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editText.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        Log.d("StartTAG","girdim")
        binding.editText.setText("")

       /* textWatcher = getTextWatcher()
        binding.editText.addTextChangedListener(textWatcher)*/
    }

    private fun getTextWatcher(): TextWatcher {
            return object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.d("textTAG", s.toString())
                    if (s?.isNotEmpty()!! && s.isNotBlank())
                        token += s[start] + ""
                    when (token.length) {
                        in 1..10 -> {
                            cTimer.cancel()
                            cTimer.start()
                        }
                        11 -> {
                            val profile = sendQuery()
                            cTimer.cancel()
                            profile?.idNumber = token
                            token = ""
                            val action = BarcodeReaderFragmentDirections.actionBarcodeReaderFragmentToProfileFragmentReader(profile)
                            findNavController().navigate(action)
                        }
                        else -> {
                            Log.e("Error", "something is going wrong" + token.length)
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {
                }
            }
    }
    override fun onStop() {
        super.onStop()
        binding.editText.removeTextChangedListener(textWatcher)
        Log.d("StopTAG","girdim")
    }

    private fun sendQuery(): ProfileModel? {
        viewModel = ViewModelProvider(this).get(ScannerQrViewModel::class.java)
        return viewModel.getProfile()?.get(7)
    }

/*
private val timer = runBlocking {
    launch {
        delay(1000)
        Log.d("", "girdim")
    }
}
    private fun mutableDataSetObserver() {
        viewModel.qrResult.observe(viewLifecycleOwner) {
            if (it != null && it.toString().isNumeric()) {
                when (it.toString().length) {
                    in 1..10 -> {
                        cTimer.cancel()
                        cTimer.start()
                        Log.d("0to10TAG", "started")
                    }
                    11 -> {
                        val id = viewModel.qrResult.value
                        val profile = sendQuery()
                        cTimer.cancel()
                        if (id != null) {
                            profile?.idNumber = id
                        }
                        val action = BarcodeReaderFragmentDirections.actionBarcodeReaderFragmentToProfileFragmentReader(profile)
                        findNavController().navigate(action)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "please hold the scanner to the right barcode", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun mutableDataSetObserverTimer() {
        viewModel.qrResult.observe(viewLifecycleOwner) {
            if (it != null && it.toString().isNumeric()) {
                if (timer.isCompleted && it.length != 11) {
                    Log.d("TAGFinished", viewModel.qrResult.value.toString() + "burdayim")
                    viewModel.clear()
                }

                when (it.toString().length) {
                    in 1..10 -> {
                        if (!timer.isActive)
                            timer.start()
                    }
                    11 -> {
                        val id = viewModel.qrResult.value
                        val profile = sendQuery()
                        timer.cancel(CancellationException("the barcode is read correctly"))
                        if (id != null) {
                            profile?.idNumber = id
                        }
                        val action = BarcodeReaderFragmentDirections.actionBarcodeReaderFragmentToProfileFragmentReader(profile)
                        findNavController().navigate(action)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "please hold the scanner to the right barcode", Toast.LENGTH_SHORT).show()
            }
        }

    }*/

}