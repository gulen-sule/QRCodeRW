package com.example.qrscanner.ui.admin.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.qrscanner.R
import com.example.qrscanner.data.api.models.profile.ProfileModel
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
    private lateinit var viewModel: ScannerQrViewModel
    private lateinit var cameraProvider: ProcessCameraProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PackageManager.PERMISSION_GRANTED)
            }
        }
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
        bindCameraUseCases()
    }

    private fun setImageAnalysis() {
        executorService = Executors.newSingleThreadExecutor()
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(width, height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

        imageAnalysis.setAnalyzer(executorService, {
            scanBarcodes(it)
        })
    }

    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
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
                    val valueType = barcode.valueType
                    Log.d("ValueTypeTAG", valueType.toString())
                    when (valueType) {
                        Barcode.TYPE_URL -> {
                            val url = barcode.url!!.url
                        }
                        Barcode.TYPE_UNKNOWN -> {
                            Log.e("DashBoardFragmentTAG", "Unkown type of QR code")
                        }
                        Barcode.TYPE_TEXT -> {
                            Log.d("currentDestTAG1", findNavController().currentDestination?.id.toString())
                            if (findNavController().currentDestination?.id == R.id.scannerQrFragment) {
                                val profile = sendQuery()
                                val action = ScannerQrFragmentDirections.actionScannerQrFragmentToProfileFragment(profile)
                                findNavController().navigate(action)
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

    private fun sendQuery(): ProfileModel? {
        viewModel = ViewModelProvider(this).get(ScannerQrViewModel::class.java)
        return viewModel.getProfile()?.get(9)
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroyView() {
        cameraProvider.shutdown()
        super.onDestroyView()
        _binding = null
    }
}