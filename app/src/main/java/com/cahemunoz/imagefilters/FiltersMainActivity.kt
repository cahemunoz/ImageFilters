package com.cahemunoz.imagefilters

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cahemunoz.filters.FiltersActivity
import com.outsmart.outsmartpicker.MediaPicker
import com.outsmart.outsmartpicker.MediaType
import kotlinx.android.synthetic.main.activity_filters_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File


@RuntimePermissions
class FiltersMainActivity : AppCompatActivity() {

    private val mediaPicker = MediaPicker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters_main)
        setupMediaPicker()
        processButton.setOnClickListener {
            pickWithPermissionCheck()
        }
    }

    private fun setupMediaPicker() {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(mediaPicker, "mediaPicker")
        transaction.commit()
        registerReceiver(pickerChoose, IntentFilter(MediaPicker.PICKER_RESPONSE_FILTER))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pickerChoose)
    }


    @NeedsPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
    fun pick() {
        mediaPicker.pickMedia(MediaType.IMAGE)
    }

    private var pickerChoose: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val error = intent.getStringExtra(MediaPicker.PICKER_INTENT_ERROR)
            if (error == null) {
                val file = File(intent.getStringExtra(MediaPicker.PICKER_INTENT_FILE))
                var outFile = File(cacheDir, "image.jpg")
                startFilters(Uri.fromFile(file), outFile.absolutePath)
            } else {
                //Error
            }
        }
    }


    fun startFilters(input: Uri, output: String) {
        val intent = Intent(this@FiltersMainActivity, FiltersActivity::class.java).apply {
            putExtra(FiltersActivity.INTENT_INPUT_FILE_URI, input)
            putExtra(FiltersActivity.INTENT_OUTPUT_FILE, output)
        }
        startActivityForResult(intent, FiltersActivity.REQUEST_FILTER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == FiltersActivity.REQUEST_FILTER && resultCode == FiltersActivity.RESULT_OK) {
            data.let {
                val fileUri = it?.data
                filePathOutput.text = fileUri.toString()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}
