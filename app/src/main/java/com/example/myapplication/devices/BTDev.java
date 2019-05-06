package com.example.myapplication.devices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.myapplication.PckManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;


public class BTDev extends Thread implements Connection {
    private static final UUID MY_UUID = java.util.UUID.fromString("f21b7d92-bf0c-4cd5-8f45-bcf24fc3e088");
    private  BluetoothSocket mmSocket;
    private  BluetoothDevice mmDevice;
    private ArrayList<BluetoothDevice> availableDevices = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter bluetoothAdapter;
    private MyBluetoothService myBluetoothService;
    ConnectedThread connectedThread;
    private Handler handler; // handler that gets info from Bluetooth service

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    public ArrayList getAvailableDevicesReadAble()
    {
        ArrayList availableDevicesReadable = new ArrayList();

        for (BluetoothDevice device : (ArrayList<BluetoothDevice>) availableDevices) {
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
            availableDevicesReadable.add(deviceHardwareAddress + " (" + deviceName + ")");
        }
        return availableDevicesReadable;
    }

    @Override
    public void initDevice() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("","Device doesn't support Bluetooth\n");
        }
        else {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    availableDevices.add(device);
                }
            }
        }
    }

    @Override
    public void sendData(byte[] dataBytes) {
        if (connectedThread != null) connectedThread.write(dataBytes);
        else Log.i("Btdev","connectedThread not exitst");
    }

    @Override
    public String receiveData() {
        return null;
    }

    @Override
    public void initConnection(Object device) {
        BluetoothSocket tmp = null;
        mmDevice = (BluetoothDevice) availableDevices.get((int)device);
        try {
            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            Log.e("initConnection", "Inic socked for " + mmDevice.getAddress());

        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
        run_connection();

    }


    public void run_connection() {
        Thread thread = new Thread(){
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            public void run(){
                    bluetoothAdapter.cancelDiscovery();
                try {
                    Log.e("BTDev", "CONECTED 1/3");
                    sleep(1000);
                    mmSocket.connect();
                    sleep(1000);

                    Log.e("BTDev", "CONECTED 2/3");

                } catch (IOException connectException) {
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) {
                        Log.e(TAG, "Could not close the client socket", closeException);
                    }
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("BTDev", "CONECTED 3/3");
                connectedThread = new ConnectedThread(mmSocket);
                connectedThread.start();
            }
        };
        thread.start();
        Log.e("thread is alive", Boolean.toString(thread.isAlive()));
//        Log.e("conncdThread is alive", Boolean.toString(connectedThread.isAlive()));

//        byte[] b = "First message".getBytes();
//        connectedThread.write(b);
    }


    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            BTDev.MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(BTDev.MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    @Override
    public void cancel() {
        try {
            if (mmSocket != null) {
                mmSocket.close();
                Log.e(TAG, "Close the connection");
            }

        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    public boolean isConnected() {
        boolean isConnected = false;
        try {
            if (mmSocket != null) {
                isConnected = mmSocket.isConnected();
                Log.i("BTDev", "BTDev.isConnected()"+Boolean.toString(isConnected));
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Could not close the client socket", e);
        }

        return isConnected;
    }



    }

