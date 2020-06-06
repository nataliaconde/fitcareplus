package br.com.fitcareplus.Interfaces;

import android.bluetooth.BluetoothSocket;

public interface BluetoothConnectionServiceInterface {
    void onConnectionSuccessful(BluetoothSocket socket);
    void onConnectionFailed();
    void onDeviceReady();
    void onConnectionError();
    void onConnectionClose();
}
