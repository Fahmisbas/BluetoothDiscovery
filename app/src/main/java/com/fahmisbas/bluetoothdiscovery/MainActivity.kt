package com.fahmisbas.bluetoothdiscovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothDevices: ArrayList<String> = ArrayList()
    private var addresses: ArrayList<String> = ArrayList()
    private var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bluetoothDevices)


    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                tvBluetoothState.text = "Finished"
                btnSearch.isEnabled = true
            } else if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                val name = device.name
                val address = device.address
                val rssi: String =
                    intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toString()

                if (!addresses.contains(address)) {
                    addresses.add(address)
                    var deviceString = ""
                    deviceString = if (name == null || name == "") {
                        "$address RSSI $rssi dBm"
                    } else {
                        "$name RSSI $rssi dBm"
                    }

                    bluetoothDevices.add(deviceString)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listViewBluetooth.adapter = adapter

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val intentFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }

        registerReceiver(broadcastReceiver, intentFilter)

    }

    fun btnSearch(view: View) {
        tvBluetoothState.text = "Searching.."
        btnSearch.isEnabled = false
        bluetoothDevices.clear()
        addresses.clear()
        bluetoothAdapter.startDiscovery()
    }


}
