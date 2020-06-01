package br.com.fitcareplus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Connection extends BaseActivity {

    private static final int LONG_DELAY = 3500; // 3.5 seconds
    private static final int SHORT_DELAY = 2000; // 2 seconds
    private  static final int REQUEST_ENABLE_BT = 0;
    private  static final int REQUEST_DISCOVER_BT = 1;


    BluetoothAdapter mBlueAdapter;
    Switch bluetoothConnection;
    ImageView imagebluetoothConnection;
    Switch switchDiscoverable;;
    ImageView imageswitchDiscoverable;
    ImageView loadindGif;
    List<String> ArrayList = new ArrayList<String>();
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // don’t set any content view here, since its already set in DrawerActivity
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.activity_frame);
        // inflate the custom activity layout
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_connection, null,false);
        // add the custom layout of this activity to frame layout.
        frameLayout.addView(activityView);
        // now you can do all your other stuffs

        //To know if Bluetooth is enabled
        bluetoothConnection = (Switch) findViewById(R.id.bluetoothConnection);
        imagebluetoothConnection = (ImageView) findViewById(R.id.imagebluetoothConnection);
        switchDiscoverable = (Switch) findViewById(R.id.switchDiscoverable);
        imageswitchDiscoverable = (ImageView) findViewById(R.id.imagebluetoothDiscoverable);

        listview = (ListView) findViewById(R.id.listview_devices);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Seu codigo aqui

            }
        });


        loadindGif = (ImageView) findViewById(R.id.gifImageView);
        loadindGif.setVisibility(View.INVISIBLE);

        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        //checking BT availability
        if(mBlueAdapter == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(Connection.this);
            builder.setCancelable(false);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            String message = getString(R.string.deviceDoesntSupportBluetooth);
            builder.setTitle(getString(R.string.notAbleToConnect));

            builder.setMessage(message);
            builder.setPositiveButton(getString(R.string.contactSupportTeam), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Connection.this, Contact.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } else {
            if (mBlueAdapter.isEnabled()) {
                getPairedDevices(false);
                setImagesAndColorsConnection(true);

                if (mBlueAdapter.isDiscovering()) {
                    setImagesAndColorsDiscovering(true);
                } else {
                    setImagesAndColorsDiscovering(false);
                }
            } else {
                setImagesAndColorsConnection(false);
                setImagesAndColorsDiscovering(false);
                getPairedDevices(true);
            }
        }

        bluetoothConnection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(!isChecked){
                    //turn off bluetooth
                    if(mBlueAdapter.enable()) {
                       mBlueAdapter.disable();
                        getPairedDevices(true);
                    }
                    setImagesAndColorsConnection(false);
                    setImagesAndColorsDiscovering(false);
                    loadindGif.setVisibility(View.INVISIBLE);
                } else {
                    if(!mBlueAdapter.enable()){
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BT);
                    } else {
                        Log.d("device", "Bluetooth is already on");
                        getPairedDevices(false);
                    }
                }
            }
        });

        switchDiscoverable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    if(mBlueAdapter.enable()){
                        if(!mBlueAdapter.isDiscovering()){
                            loadindGif.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            startActivityForResult(intent, REQUEST_DISCOVER_BT);
                            setImagesAndColorsDiscovering(true);

                            getPairedDevices(false);
                        }

                    } else {
                        Log.d("device", "Bluetooth is not on");
                    }
                } else {
                    //turn off bluetooth
                    if(mBlueAdapter.enable()) {
                        mBlueAdapter.disable();
                        imagebluetoothConnection.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorGrey));
                    }
                }
            }
        });





    }

    public void setImagesAndColorsConnection(Boolean isConnected){
        if(isConnected){
            bluetoothConnection.setChecked(true);
            imagebluetoothConnection.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorBluetooth));
        } else {
            bluetoothConnection.setChecked(false);
            imagebluetoothConnection.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorGrey));
        }
    }
    public void setImagesAndColorsDiscovering(Boolean isConnected){
        if(isConnected){
            switchDiscoverable.setChecked(true);
            imageswitchDiscoverable.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorBluetooth));
        } else {
            switchDiscoverable.setChecked(false);
            imageswitchDiscoverable.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorGrey));
        }
    }

    public void getPairedDevices(Boolean clearArray){
        ArrayAdapter<String> arrayAdapter = null;

        if(clearArray || !mBlueAdapter.isEnabled()){
            ArrayList.clear();
        } else {
            ArrayList.clear();
            Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                ArrayList.add(device.getName());
            }
        }
        arrayAdapter = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            ArrayList);
        loadindGif.setVisibility(View.INVISIBLE);
        arrayAdapter.notifyDataSetChanged();
        listview.setAdapter(arrayAdapter);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(requestCode == RESULT_OK){
                    setImagesAndColorsConnection(true);
                } else {
                    setImagesAndColorsConnection(false);
                    setImagesAndColorsDiscovering(false);
                }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
