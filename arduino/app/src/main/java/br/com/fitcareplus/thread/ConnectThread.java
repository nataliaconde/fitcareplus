package br.com.fitcareplus.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import br.com.fitcareplus.Interfaces.BluetoothConnectionServiceInterface;

public class ConnectThread extends Thread{

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket mmSocket;
    private BluetoothConnectionServiceInterface mmServiceInterface;

    public ConnectThread(BluetoothDevice device, BluetoothConnectionServiceInterface serviceInterface) {

        mmServiceInterface = serviceInterface;

        try {
            mmSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            mmSocket.connect();
            mmServiceInterface.onConnectionSuccessful(mmSocket);
        } catch (IOException e) {
            e.printStackTrace();
            close();
            mmServiceInterface.onConnectionFailed();
        }
    }

    private void close() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
