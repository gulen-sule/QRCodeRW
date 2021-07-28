package com.example.qrscanner.ui.user.generator

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.qrscanner.R
import com.example.qrscanner.data.ResponseModel
import com.example.qrscanner.databinding.FragmentQrcodeGeneratorBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*

class QRCodeGeneratorFragment : Fragment() {
    private var _binding: FragmentQrcodeGeneratorBinding? = null

    private lateinit var timer: CountDownTimer
    private val bitMatrixWidth = 1000
    private val bitMatrixHeight = 1000
    private val binding get() = _binding!!
    var profile: ResponseModel? = null
    var bitmap: Bitmap? = null
    private var countDown = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQrcodeGeneratorBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.contentLoadingProgressBar.progress = 3
        return root
    }

    var initTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile = getResponse()
        val token = profile!!.token

        binding.imageView.setImageBitmap(generateQr(token!!))

        initTime = (profile!!.time * 1000).toLong()
        countDown = profile?.time!!
        binding.countBackTimeText.text = profile?.time.toString()
        binding.contentLoadingProgressBar.max = initTime.toInt()
        Log.d("millisTAG", initTime.toInt().toString())

        val count = Handler(requireContext().mainLooper)
        count.post {
            timer = object : CountDownTimer(initTime, 98) {
                override fun onTick(millisUntilFinished: Long) {
                    if (countDown == 0) {
                        binding.countBackTimeText.text = (binding.countBackTimeText.text.toString().toInt() - 1).toString()
                        countDown = 10
                    }
                    countDown--
                    binding.contentLoadingProgressBar.incrementProgressBy(105)
                    Log.d("millisTAG", millisUntilFinished.toInt().toString())
                }

                override fun onFinish() {
                    binding.imageView.setImageBitmap(bitmap)
                    binding.countBackTimeText.text = 0.toString()
                    binding.countBackTimeText.text = profile!!.time.toString()
                    barcodeInit(count)

                }
            }
        }
        barcodeInit(count)

    }

    fun barcodeInit(count: Handler) {
        countDown = profile?.time!!
        Handler(requireContext().mainLooper).post {
            binding.contentLoadingProgressBar.progress = 0
            profile = getResponse()
            bitmap = generateQr(profile?.token.toString())
        }.also { count.post { timer.start() } }
    }

    private fun getResponse(): ResponseModel {
        val token = UUID.randomUUID().toString() + UUID.randomUUID().toString().substring(0, 4)
        Log.e("tokenTAG", token)
        return ResponseModel(token = token, time = 10)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun generateQr(content: String): Bitmap? {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, bitMatrixWidth, bitMatrixHeight)
        val draw = requireContext().getDrawable(R.drawable.qr_code_bitmap_photo)
        val bitmap = draw?.toBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)
        // Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrixWidth) {
            for (y in 0 until bitMatrixHeight) {
                if (bitMatrix.get(x, y)) {
                    bitmap?.setPixel(x, y, Color.BLACK)
                }
            }
        }
        return bitmap
    }

    //to add an image to the center
    fun Bitmap.addOverlayToCenter(overlayBitmap: Bitmap): Bitmap {
        val bitmap2Width = overlayBitmap.width
        val bitmap2Height = overlayBitmap.height
        val marginLeft = (this.width * 0.5 - bitmap2Width * 0.5).toFloat()
        val marginTop = (this.height * 0.5 - bitmap2Height * 0.5).toFloat()
        val canvas = Canvas(this)
        canvas.drawBitmap(this, Matrix(), null)
        canvas.drawBitmap(overlayBitmap, marginLeft, marginTop, null)
        return this
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