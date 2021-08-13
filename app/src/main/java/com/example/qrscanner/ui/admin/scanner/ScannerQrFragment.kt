package com.example.qrscanner.ui.admin.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.qrscanner.R
import com.example.qrscanner.data.api.models.barcodeResponse.BarcodeResponse
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.databinding.FragmentScannerBinding
import com.example.qrscanner.ui.admin.BottomSheetUnvalidFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


open class ScannerQrFragment : Fragment() {
    private lateinit var executorService: ExecutorService
    private lateinit var imageAnalysis: ImageAnalysis
    private var _binding: FragmentScannerBinding? = null
    private var time: Long = 0L
    private val binding get() = _binding!!
    private var viewModel: ScannerQrViewModel = ScannerQrViewModel()
    private lateinit var cameraProvider: ProcessCameraProvider
    private var token: String? = null
    private var bottomSheet = BottomSheetUnvalidFragment()
    var bottomSheetBehaviour: BottomSheetBehavior<View>? = null
    private var soundVibrateCalled = false
    private var alertDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PackageManager.PERMISSION_GRANTED)
            }
        }
        bottomSheet.onCreateView(inflater, container, savedInstanceState)
        alertDialog = AlertDialog.Builder(requireContext()).create()
        val window = alertDialog!!.window
        window?.setGravity(Gravity.BOTTOM)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        soundVibrateCalled = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheet.onViewCreated(view, savedInstanceState)
        bottomSheetBehaviour = BottomSheetBehavior.from(requireActivity().findViewById(R.id.bottomSheetLayout))
        bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_HIDDEN

        time = System.currentTimeMillis()
        runScanner()
    }

    private fun runScanner() {
        setImageAnalysis()
        bindCameraUseCases()
        Toast.makeText(requireContext(), "Scan the qr code on the User screen", Toast.LENGTH_SHORT).show()
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


    @SuppressLint("UnsafeOptInUsageError")
    private fun scanBarcodes(image: ImageProxy) {

        //set detector options
        val options = BarcodeScannerOptions.Builder()//hizlandirmak icin sadece qr code okuyacagini soyluyorum
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            ).build()

        val scanner = BarcodeScanning.getClient(options)//get detector

        image.image.let { thisImage ->
            val inputImage = InputImage.fromMediaImage(thisImage!!, image.imageInfo.rotationDegrees)

            scanner.process(inputImage).addOnSuccessListener { barcodes ->
                // [START get_barcodes]
                for (barcode in barcodes) {
                    val wrongCode = barcode.displayValue
                    val valueType = barcode.valueType

                    if (valueType == Barcode.TYPE_TEXT) {
                        if (!alertDialog!!.isShowing)
                            getProfile(barcode)
                    } else
                        showToastMsg(wrongCode)
                }
            }.addOnFailureListener { }.addOnCompleteListener {
                image.image?.close()
                image.close()
            }
        }
    }

    private fun getProfile(barcode: Barcode) {
        Log.d("wrongCodeTAG", barcode.displayValue.toString())
        viewModel.getProfile(barcode.displayValue) { barcode_response ->
            if (barcode_response != null) {
                if (barcode_response.status == 0) {
                    if (!soundVibrateCalled) {
                        soundPool().also { vibratePhone() }
                        soundVibrateCalled = true
                    }
                    switchToProfileFrag(barcode_response)
                } else {
                    invalidBarcodeBottomSheet(barcode_response.status)
                }
            } else {
                invalidBarcodeBottomSheet(7)
            }
        }
    }

    private fun switchToProfileFrag(response: BarcodeResponse) {
        if (findNavController().currentDestination?.id == R.id.scannerQrFragment) {
            val action = ScannerQrFragmentDirections.actionScannerQrFragmentToProfileFragment(response.token)
            findNavController().navigate(action)
        }
    }


    private fun soundPool() {
        val pl = SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        pl.load(requireContext(), R.raw.beep_short, 0)
        pl.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, sampleId, _ ->
            soundPool.play(sampleId, 1f, 1f, 0, 0, 1f);
        })
    }

    private fun showToastMsg(wrongCode: String?) {
        if (!(token.equals(wrongCode))) {
            Log.d("wrongCodeTAG", wrongCode.toString())
            token = wrongCode
            val toast = Toast.makeText(requireContext(), "Please hold the camera to the right barcode", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroyView() {
        cameraProvider.shutdown()
        super.onDestroyView()
        _binding = null
    }

    private fun vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }


    private fun invalidBarcodeBottomSheet(status: Int) {
        if (!alertDialog!!.isShowing) {
            alertDialog!!.setTitle("Failed")
            val message = giveDeniedMessage(status)
            alertDialog!!.setMessage(message)
            alertDialog!!.show()
        }
        /*if (!isBottomSOpened) {
            val message = giveDeniedMessage(status)
            requireActivity().findViewById<TextView>(R.id.bottomSheetText).text = message
            bottomSheetBehaviour!!.state = BottomSheetBehavior.STATE_EXPANDED
        }*/
    }

    private fun giveDeniedMessage(status: Int): String {
        return when (status) {
            1 -> "You do not have executive's permission"
            2 -> "This barcode is expired"
            3 -> "You do not have controller mission"
            4 -> "There is none person in the list with this uuid"
            5 -> "Permission is not available"
            6 -> "Your permission is denied"
            7 -> "This token is not valid"
            else -> "Unknown error is occurred"
        }
    }

}