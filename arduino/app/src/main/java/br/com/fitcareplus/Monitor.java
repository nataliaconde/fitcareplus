package br.com.fitcareplus;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.fitcareplus.adapters.DevicesAdapter;
import br.com.fitcareplus.dialogs.LoadingDialog;
import br.com.fitcareplus.service.BluetoothConnectionService;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Monitor extends AppCompatActivity {

    final int REQUEST_ENABLE_BLUETOOTH = 0;
    final String DESCONHECIDO = "Desconhecido";

    View mainContext;

    ListView deviceListView;
    TextView temperatureTextView;
    TextView pulseTextView;
    TextView deviceNameTextView;
    Button measureTemperatureButton;
    Button measurePulseButton;
    View selectDeviceCard;

    ArrayList<BluetoothDevice> deviceList;
    DevicesAdapter devicesAdapter;
    String deviceName;

    BluetoothAdapter bluetoothAdapter;
    BluetoothConnectionService connectionService;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        deviceListView = findViewById(R.id.device_list);
        temperatureTextView = findViewById(R.id.temperature);
        pulseTextView = findViewById(R.id.pulse);
        deviceNameTextView = findViewById(R.id.device_name);
        measureTemperatureButton = findViewById(R.id.measure_temperature);
        measurePulseButton = findViewById(R.id.measure_pulse);
        selectDeviceCard = findViewById(R.id.select_device_card);
        mainContext = findViewById(R.id.main);

        measureTemperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionService.writeToDevice("T");
            }
        });

        measurePulseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionService.writeToDevice("P");
            }
        });


        loadingDialog = new LoadingDialog(Monitor.this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            showBluetoothNotAvailableAlert();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        deviceList = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0) {
            deviceList.addAll(pairedDevices);
        }

        devicesAdapter = new DevicesAdapter(this, deviceList);
        deviceListView.setAdapter(devicesAdapter);
        startBroadcast();

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapter, View view, int position, long arg) {
                BluetoothDevice device = devicesAdapter.getItem(position);
                startConnectionTransaction(device);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBroadcast();
    }

    private void startConnectionTransaction(BluetoothDevice device) {
        final boolean wasBondedBefore = bluetoothAdapter.getBondedDevices().contains(device);
        deviceName = device.getName() == null ? "Desconhecido" : device.getName();

        if(wasBondedBefore) {
            connect(device);
        } else {
            device.createBond();
        }
    }

    private final BroadcastReceiver bluetoothDiscoveryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!deviceList.contains(device)) {
                    devicesAdapter.add(device);
                }
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch(device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        loadingDialog.show();
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        loadingDialog.dismiss();
                        showSnackbar("Dispositivo pareado com sucesso");
                        connect(device);
                        break;

                    case BluetoothDevice.BOND_NONE:
                        loadingDialog.dismiss();
                        showSnackbar("Falha ao parear com o dispositivo");
                        break;
                }
            }
        }
    };

    private void startBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bluetoothDiscoveryReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    private void stopBroadcast() {
        unregisterReceiver(bluetoothDiscoveryReceiver);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case BluetoothConnectionService.CONNECTING:
                    if((Boolean)message.obj) {
                        setConnectingState();
                    }
                    break;
                case BluetoothConnectionService.CONNECTION_FAILED:
                    if((Boolean)message.obj) {
                        loadingDialog.dismiss();
                        showSnackbar("A conexão falhou.");
                        setSelectDeviceState();
                    }
                    break;
                case BluetoothConnectionService.CONNECTION_ERROR:
                    if((Boolean)message.obj) {
                        showSnackbar("Erro.");
                        setSelectDeviceState();
                    }
                    break;
                case BluetoothConnectionService.CONNECTION_CLOSED:
                    if((Boolean)message.obj) {
                        showSnackbar("A conexão foi fechada.");
                        setSelectDeviceState();
                    }
                    break;
                case BluetoothConnectionService.CONNECTION_SUCCESSFUL:
                    if((Boolean)message.obj) {
                        loadingDialog.dismiss();
                        showSnackbar("Connectado com sucesso.");
                        setConnectedDeviceState();
                    }
                    break;
                case BluetoothConnectionService.READY:
                    if((Boolean)message.obj) {
                        setConnectedDeviceReadyState();
                        showSnackbar("Dispositivo pronto.");
                    }
                    break;
                case BluetoothConnectionService.INCOMING_MESSAGE:
                    String incomingMessage = (String) message.obj;
                    if(!incomingMessage.isEmpty()) {
                        messageHandler(incomingMessage);
                    }
                    break;
            }
        }
    };


    private void connect(BluetoothDevice device) {
        bluetoothAdapter.cancelDiscovery();
        loadingDialog.show();
        connectionService = new BluetoothConnectionService(device, handler);
        connectionService.startBluetoothConnection();
    }

    private void setConnectingState() {
        setApplicationState(DESCONHECIDO, false, false, false);
    }

    private void setSelectDeviceState() {
        setApplicationState(DESCONHECIDO, true, false, false);
        bluetoothAdapter.startDiscovery();
    }

    private void setConnectedDeviceState() {
        setApplicationState(deviceName, false, false, false);
    }

    private void setConnectedDeviceReadyState() {
        setApplicationState(deviceName, false, true, true);
    }

    private void setApplicationState(String newDeviceName, Boolean deviceListViewEnable, Boolean measureTemperatureButtonEnable, Boolean measurePulseButtonEnable) {
        deviceNameTextView.setText(newDeviceName);
        selectDeviceCard.setVisibility(deviceListViewEnable ? View.VISIBLE : View.INVISIBLE);
        measureTemperatureButton.setVisibility(measureTemperatureButtonEnable ? View.VISIBLE : View.INVISIBLE);
        measurePulseButton.setVisibility(measurePulseButtonEnable ? View.VISIBLE : View.INVISIBLE);
    }

    private void showBluetoothNotAvailableAlert() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Dispositivo Incompatível");
        alertBuilder.setMessage("O seu dispositivo não suporta bluetooth, portanto é incompatível o aplicativo.");

        alertBuilder.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });

        alertBuilder.show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(mainContext, message, Snackbar.LENGTH_SHORT).show();
    }

    private String getMeasure(String incomingMessage) {
        return incomingMessage.substring(3);
    }

    private void messageHandler(String incomingMessage) {

        switch (incomingMessage.charAt(0)) {
            case 'T':
                String temperature = getMeasure(incomingMessage);
                temperatureTextView.setText(temperature + " °C");
                sendParseCall("temperature", temperature);
                break;
            case 'P':
                String pulse = getMeasure(incomingMessage + " BPM");
                pulseTextView.setText(pulse);
                sendParseCall("pulse", pulse);
                break;
        }
    }

    private void sendParseCall(String what, String value) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user", String.valueOf(ParseUser.getCurrentUser()));
        params.put("objectId", String.valueOf(ParseUser.getCurrentUser().getObjectId()));

        params.put(what, value);

            ParseCloud.callFunctionInBackground("addDataMeasurement", params, new FunctionCallback<String>() {
            @Override
            public void done(String object, ParseException e) {
                if(e == null) {
                    Toast.makeText(Monitor.this, object, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
