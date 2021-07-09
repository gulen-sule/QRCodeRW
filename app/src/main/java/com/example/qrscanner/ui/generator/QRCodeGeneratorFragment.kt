package com.example.qrscanner.ui.generator

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.example.qrscanner.data.ProfileModel
import com.example.qrscanner.databinding.FragmentQrcodeGeneratorBinding
import com.example.qrscanner.ui.scanner.ScannerDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class QRCodeGeneratorFragment : Fragment() {
    private var _binding: FragmentQrcodeGeneratorBinding? = null

    private lateinit var timer: CountDownTimer
    private val bitMatrixWidth = 600
    private val bitMatrixHeight = 600
    private val binding get() = _binding!!
    var profile: ProfileModel? = null
    var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQrcodeGeneratorBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.contentLoadingProgressBar.progress = 0
        return root
    }

    var initTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile = getResponse()
        val token = profile!!.token

        binding.imageView.setImageBitmap(generateQr(token!!))

        initTime = (profile!!.time * 1000).toLong()

        binding.contentLoadingProgressBar.max = initTime.toInt()

        timer = object : CountDownTimer(initTime, 100) {
            override fun onTick(millisUntilFinished: Long) {
                binding.contentLoadingProgressBar.incrementProgressBy(100)
            }

            override fun onFinish() {
                //binding.contentLoadingProgressBar.incrementProgressBy(200)
                binding.imageView.setImageBitmap(bitmap)
                start()
                barcodeInit()

            }
        }
        timer.start()

        barcodeInit()
    }

    fun barcodeInit() {
        Handler(requireContext().mainLooper).post {
            binding.contentLoadingProgressBar.progress = 0
            profile = getResponse()
            bitmap = generateQr(profile?.token.toString())
        }
    }

    private fun getResponse(): ProfileModel {
        val token = UUID.randomUUID().toString() + UUID.randomUUID().toString().substring(0, 4)
        Log.e("tokenTAG", token)
        return ProfileModel(token = token, time = 10)
    }

    private fun generateQr(content: String): Bitmap? {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, bitMatrixWidth, bitMatrixHeight)
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrixWidth) {
            for (y in 0 until bitMatrixHeight) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}