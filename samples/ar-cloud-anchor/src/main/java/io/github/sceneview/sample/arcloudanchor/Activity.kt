package io.github.sceneview.sample.arcloudanchor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
class Activity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    companion object {
        lateinit var appContext: Context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val launchScan = findViewById<Button>(R.id.launch_scan)
        var onGoingScan: Boolean = false
        appContext = this@Activity
        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        val apiTask = ApiTask()
        apiTask.execute()
        // Callback quand un qr code est décodé
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                //val myIntent = Intent(this, ApiTask::class.java)
                //this.startActivity(myIntent)
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        launchScan.setOnClickListener {
            if (onGoingScan){
                onGoingScan = false
                launchScan.text = "Lancer la recherche d'un QR code"
                scannerView.visibility = View.GONE
                codeScanner.stopPreview()

            }
            else{
                onGoingScan = true
                launchScan.text = "Arrêter la recherche d'un QR code"
                scannerView.visibility = View.VISIBLE
                codeScanner.startPreview()
            }
        }


    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }



}