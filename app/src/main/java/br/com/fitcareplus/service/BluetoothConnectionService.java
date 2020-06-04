package br.com.fitcareplus.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import br.com.fitcareplus.Interfaces.BluetoothConnectionServiceInterface;
import br.com.fitcareplus.factory.MessageHandlerFactory;
import br.com.fitcareplus.thread.ConnectThread;
import br.com.fitcareplus.thread.ConnectedThread;

public class BluetoothConnectionService implements BluetoothConnectionServiceInterface {

    private Boolean mmDeviceReady = false;
    private ConnectThread mmConnectThread;
    private ConnectedThread mmConnectedThread;
    private BluetoothDevice mmDevice;
    private Handler mmHandler;
    private MessageHandlerFactory mmHandlerFactory;

    public static final int CONNECTING = 1;
    public static final int CONNECTION_SUCCESSFUL = 2;
    public static final int CONNECTION_FAILED = 3;
    public static final int CONNECTION_ERROR = 4;
    public static final int CONNECTION_CLOSED = 5;
    public static final int READY = 6;
    public static final int INCOMING_MESSAGE = 7;

    public BluetoothConnectionService(BluetoothDevice device, Handler handler) {
        mmDevice = device;
        mmHandler = handler;
        mmHandlerFactory = new MessageHandlerFactory(handler);
    }

    @Override
    public void onConnectionSuccessful(BluetoothSocket socket) {
        mmHandlerFactory.sendMessage(CONNECTION_SUCCESSFUL, true);
        mmConnectedThread = new ConnectedThread(socket, mmHandler, this);
        mmConnectedThread.start();
    }

    @Override
    public void onConnectionFailed() {
        mmHandlerFactory.sendMessage(CONNECTION_FAILED, true);
    }

    @Override
    public void onDeviceReady() {
        mmHandlerFactory.sendMessage(READY, true);
        mmDeviceReady = true;
    }

    @Override
    public void onConnectionError() {
        if(mmConnectedThread != null) {
            mmConnectedThread.cancel();
        }
        mmHandlerFactory.sendMessage(BluetoothConnectionService.CONNECTION_ERROR, true);
    }

    @Override
    public void onConnectionClose() {
        mmConnectedThread.cancel();
        mmHandlerFactory.sendMessage(BluetoothConnectionService.CONNECTION_CLOSED, true);
    }

    public void startBluetoothConnection() {
        mmHandlerFactory.sendMessage(CONNECTING, true);
        mmConnectThread = new ConnectThread(mmDevice, this);
        mmConnectThread.start();
    }

    public void writeToDevice(String message) {
        if (mmDeviceReady) {
            mmConnectedThread.write(message.getBytes());
        }
    }
}
