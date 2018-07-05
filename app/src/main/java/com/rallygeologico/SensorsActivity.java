package com.rallygeologico;

import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import static android.content.Context.SENSOR_SERVICE;

public class SensorsActivity extends Fragment implements SensorEventListener {

    private ImageView image;
    private TextView txt_pitch;
    private float currentDegree = 0f;
    private int angle = 0;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float[] mAccelerometerReading = new float[3];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_sensors, container, false);
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        image = (ImageView) v.findViewById(R.id.fondoSensor);
        txt_pitch = (TextView) v.findViewById(R.id.tvHeading);
        return v;
    }

    /**
     * Get updates from the accelerometer and magnetometer at a constant rate.
     * To make batch operations more efficient and reduce power consumption, provide support for delaying updates to the application.
     */
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Don't receive any more updates from either sensor.
     */
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);

            // valores invertidos
            int y = (int)event.values[0];
            int x = (int)event.values[1];
            if(y>=0 && x<=0) angle = x*10;
            if(x<=0 && y<=0) angle = (y*10)-90;
            if(x>=0 && y<=0) angle = (-x*10)-180;
            if(x>=0 && y>=0) angle = (-y*10)-270;
            angle = -angle;
        }
        float degree = (float)angle;
        txt_pitch.setText(Integer.toString(angle));
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                degree,
                currentDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
