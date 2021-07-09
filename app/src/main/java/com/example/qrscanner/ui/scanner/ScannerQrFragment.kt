package com.example.qrscanner.ui.scanner

import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.qrscanner.databinding.FragmentScannerBinding
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerQrFragment : Fragment() {
    private lateinit var executorService: ExecutorService
    private lateinit var imageAnalysis: ImageAnalysis
    private var _binding: FragmentScannerBinding? = null
    private var time: Long = 0L
    private val binding get() = _binding!!
    private var dialog = ScannerDialogFragment {
        dialogClosed = true
    }

    private var dialogClosed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //dialog.onViewCreated(view, savedInstanceState)
        time = System.currentTimeMillis()
        runScanner()
    }

    private fun runScanner() {

        setImageAnalysis()
        if (dialogClosed)
            bindCameraUseCases()
    }

    private fun setImageAnalysis() {
        executorService = Executors.newSingleThreadExecutor()
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(720, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

        imageAnalysis.setAnalyzer(executorService, {
            scanBarcodes(it)
        })
    }

    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val previewUseCase = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraView.surfaceProvider)
            }

            cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase, imageAnalysis)
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun scanBarcodes(image: ImageProxy) {
        //set detector options
        val options = BarcodeScannerOptions.Builder()//hizlandirmak icin sadece qr code okuyacagini soyluyorum
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            )
            .build()

        val scanner = BarcodeScanning.getClient(options)//get detector

        image.image.let { thisImage ->
            val inputImage = InputImage.fromMediaImage(thisImage!!, image.imageInfo.rotationDegrees)

            scanner.process(inputImage).addOnSuccessListener { barcodes ->
                // [START get_barcodes]
                for (barcode in barcodes) {
//                    val bounds = barcode.boundingBox
//                    val corners = barcode.cornerPoints
//                    val rawValue = barcode.rawValue
                    val valueType = barcode.valueType
                    Log.d("ValueTypeTAG", valueType.toString())
                    when (valueType) {
                        Barcode.TYPE_URL -> {
                            //  val title = barcode.url!!.title
                            val url = barcode.url!!.url

                        }
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                        }
                        Barcode.TYPE_UNKNOWN -> {
                            Log.e("DashBoardFragmentTAG", "Unkown type of QR code")
                        }
                        Barcode.TYPE_TEXT -> {
                            if (dialogClosed) {
                                dialog.arguments = Bundle().apply {
                                    putString("token", barcode.rawValue.toString())
                                }
                                dialog.show(requireActivity().supportFragmentManager, "scanner_dialog_fragment")
                                dialogClosed = false
                            }
                        }
                    }
                }

            }.addOnFailureListener { }.addOnCompleteListener {
                image.image?.close()
                image.close()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}