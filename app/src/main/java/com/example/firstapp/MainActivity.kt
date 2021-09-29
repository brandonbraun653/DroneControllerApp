package com.example.firstapp

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.firstapp.databinding.ActivityMainBinding

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permissionType
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var button: Button = findViewById(R.id.scan_button)
        button.setOnClickListener {
            startBleScan()
        }
    }

    private fun startBleScan() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
            requestLocationPermission()
        } else {
            // TODO: Actually perform the scan
        }
    }

    private fun requestLocationPermission() {
        if( isLocationPermissionGranted ) {
            return
        }
        runOnUiThread{
            requestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            LOCATION_PERMISSION_REQUEST_CODE -> {
                if( grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    startBleScan()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if( resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }
}