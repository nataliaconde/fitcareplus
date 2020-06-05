package br.com.fitcareplus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.List;

public class PacientDetail extends BaseActivity {
    float[] tempArray = new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0 ,0 ,0 ,0 ,0 };
    int[] pulseArray = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0 ,0 ,0 ,0 ,0 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // don’t set any content view here, since its already set in DrawerActivity
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.activity_frame);
        // inflate the custom activity layout
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_pacient_detail, null,false);
        // add the custom layout of this activity to frame layout.
        frameLayout.addView(activityView);
        // now you can do all your other stuffs

        Intent intent = getIntent();

        final ListView listView = (ListView) findViewById(R.id.listViewHistory);

        ParseUser user = ParseUser.getCurrentUser();
        if(user == null ){
            onBackPressed();
        }


        final ArrayList<String> values = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Measurement");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.setLimit(5);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> measurementList, ParseException e) {
                if (e == null) {
                    int index = 0;
                        for (ParseObject meansure : measurementList) {
                        // This does not require a network access.
                        String temperature = meansure.getString("temperature");
                        if(temperature != null){
                            values.add(temperature + "º C");
                            tempArray[index] = (Float.parseFloat(temperature));
                        }

                        String pulse = meansure.getString("pulse");
                        if(pulse != null){
                            values.add(pulse + "BPM");
                            pulseArray[index] = (Integer.parseInt(pulse));
                        }

                        index++;
                    }
                    showChart(tempArray, pulseArray, 5);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PacientDetail.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, values);

                    listView.setAdapter(adapter);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });



        TextView name = (TextView) findViewById(R.id.pacientName);
        String IntentName = intent.getStringExtra("name");

        if (IntentName != null) {
            name.setText(IntentName);
        } else {
            SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            name.setText(saved_values.getString("username", ""));
        }




    }

    public void showChart(float[] tempArray, int[] pulseArray, int limit){

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Média das medições");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> tempData = new ArrayList<>();

        for(int tempIndex=0; tempIndex<limit; tempIndex++){
            Float currentTemp = Float.valueOf(0);
            Integer currentPulse = 0;
            try {
                currentTemp = tempArray[tempIndex];
                currentPulse = pulseArray[tempIndex];

            }
            catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            tempData.add(new CustomDataEntry(tempIndex, currentTemp, currentPulse));
        }
        Set set1 = Set.instantiate();
        set1.data(tempData);
        Mapping series1Mapping = set1.mapAs("{ x: 'x', value: 'value' }");
        Set set2 = Set.instantiate();
        set2.data(tempData);
        Mapping series2Mapping = set2.mapAs("{ x: 'x', value: 'value2' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Temperatura ºC");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Pulso - BPM");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }
    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(int x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }

    }
}
