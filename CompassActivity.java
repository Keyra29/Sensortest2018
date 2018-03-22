package se.lth.certec.mamn01.sensortest2018;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends AppCompatActivity implements SensorEventListener{

    // define the display assembly compass picture
    private ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    private float[] gravity = new float[3];
    // magnetic data
    private float[] geomagnetic = new float[3];
    // Rotation data
    private float[] rotation = new float[9];
    // orientation (azimuth, pitch, roll)
    private float[] orientation = new float[3];

    // device sensor manager
    private SensorManager mSensorManager;
    private Sensor sensorGravity;
    private Sensor sensorMagnetic;

    private double bearing = 0;

    static final float ALPHA = 0.15f;

    TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        image = (ImageView) findViewById(R.id.ivCompass);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.txt_azimuth);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // listen to these sensors
        mSensorManager.registerListener(this, sensorGravity,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, sensorMagnetic,
                SensorManager.SENSOR_DELAY_GAME);

        getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this, sensorGravity);
        mSensorManager.unregisterListener(this, sensorMagnetic);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get accelerometer data
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = lowPass (event.values.clone(), gravity);
//                gravity = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = lowPass(event.values.clone(), geomagnetic);
//                    geomagnetic = event.values.clone();
        }

        if ((geomagnetic != null) && (gravity != null)) {
            // get rotation matrix to get gravity and magnetic data
            SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);
            // get bearing to target
            SensorManager.getOrientation(rotation, orientation);
            // east degrees of true North
            bearing = Math.round((Math.toDegrees(orientation[0]) + 360)%360);

            String where = "NW";

            if (bearing >= 350 || bearing <= 10)
                where = "N";
            if (bearing < 350 && bearing > 280)
                where = "NW";
            if (bearing <= 280 && bearing > 260)
                where = "W";
            if (bearing <= 260 && bearing > 190)
                where = "SW";
            if (bearing <= 190 && bearing > 170)
                where = "S";
            if (bearing <= 170 && bearing > 100)
                where = "SE";
            if (bearing <= 100 && bearing > 80)
                where = "E";
            if (bearing <= 80 && bearing > 10)
                where = "NE";

            tvHeading.setText(Float.toString((float) bearing) + " deg " + where);

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    (float) -bearing,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            image.startAnimation(ra);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            currentDegree = (float) -bearing;

        } // if

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}
