package se.lth.certec.mamn01.sensortest2018;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    static final float ALPHA = 0.80f;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    private SensorManager mSensorManager;
    private Sensor mSensorAcceleration;

    TextView tvAccValueX, tvAccValueY, tvAccValueZ, tvAccDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAcceleration, SensorManager.SENSOR_DELAY_NORMAL);

        tvAccValueX = (TextView) findViewById(R.id.tvAccValueX);
        tvAccValueY = (TextView) findViewById(R.id.tvAccValueY);
        tvAccValueZ = (TextView) findViewById(R.id.tvAccValueZ);
        tvAccDir = (TextView) findViewById(R.id.tvAccDir);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged (SensorEvent event){
        gravity = lowPass(event.values.clone(), gravity);

        // without gravity
        //linear_acceleration[0] = event.values[0] - gravity[0];
        //linear_acceleration[1] = event.values[1] - gravity[1];
        //linear_acceleration[2] = event.values[2] - gravity[2];

        //with gravity
        linear_acceleration[0] = event.values[0];
        linear_acceleration[1] = event.values[1];
        linear_acceleration[2] = event.values[2];

        tvAccValueX.setText("X: " + Double.toString(linear_acceleration[0]));
        tvAccValueY.setText("Y: " + Double.toString(linear_acceleration[1]));
        tvAccValueZ.setText("Z: " + Double.toString(linear_acceleration[2]));

        if (linear_acceleration[0] > 8) tvAccDir.setText("VÄNSTER");
        if (linear_acceleration[0] < -8) tvAccDir.setText("HÖGER");
        if (linear_acceleration[1] > 8) tvAccDir.setText("BAK");
        if (linear_acceleration[1] < -8) tvAccDir.setText("FRAM");
        if (linear_acceleration[2] > 8) tvAccDir.setText("UPP");
        if (linear_acceleration[2] < -8) tvAccDir.setText("NER");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

}
