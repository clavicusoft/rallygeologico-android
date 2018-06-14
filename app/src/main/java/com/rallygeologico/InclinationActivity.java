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

public class InclinationActivity extends Fragment implements SensorEventListener {

    private ImageView mPointer;
    private TextView tvOrientation;
    private int intDegrees;

    private String stringOrinetation;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGravity;
    private Sensor mMagnetometer;
    private Sensor mRotation;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastGravitySet = false;
    private boolean mLastMagnetometerSet = false;
    private boolean mLastRotationSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private boolean haveGravity;
    private boolean haveRotation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_inclination, container, false);
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mPointer = (ImageView) v.findViewById(R.id.pointer);
        tvOrientation = (TextView) v.findViewById(R.id.orientation);

        this.haveGravity = this.mSensorManager.registerListener( this, this.mGravity, SensorManager.SENSOR_DELAY_GAME );
        this.haveRotation = this.mSensorManager.registerListener( this, this.mRotation, SensorManager.SENSOR_DELAY_GAME );

        if (this.haveRotation) {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mGravity);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else if( this.haveGravity ) {
            this.mSensorManager.unregisterListener( this, this.mAccelerometer );
        }
        return v;
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mGravity);
        mSensorManager.unregisterListener(this, mMagnetometer);
        mSensorManager.unregisterListener(this, mRotation);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        } else if (event.sensor == mGravity) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastGravitySet = true;
        } else if (event.sensor == mRotation) {
            mLastRotationSet = true;
        }

        if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
            // calculate th rotation matrix
            SensorManager.getRotationMatrixFromVector( mR, event.values );
        }

        if (mLastRotationSet) {
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+90)%360;
            RotateAnimation ra = new RotateAnimation(mCurrentDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            intDegrees = (int) azimuthInDegress;
            if(intDegrees == 0 || intDegrees == 360){
                stringOrinetation = "N " + intDegrees + "°";
            } else if(intDegrees > 0 && intDegrees < 90){
                stringOrinetation = "NE " + intDegrees + "°";
            } else if(intDegrees == 90){
                stringOrinetation = "E " + intDegrees + "°";
            } else if(intDegrees > 90 && intDegrees < 180){
                stringOrinetation = "SE " + intDegrees + "°";
            } else if(intDegrees == 180){
                stringOrinetation = "S " + intDegrees + "°";
            } else if(intDegrees > 180 && intDegrees < 270){
                stringOrinetation = "SW " + intDegrees + "°";
            } else if(intDegrees == 270){
                stringOrinetation = "W " + intDegrees + "°";
            } else if(intDegrees > 270 && intDegrees < 360){
                stringOrinetation = "NW " + intDegrees + "°";
            }
            tvOrientation.setText(stringOrinetation);
            mPointer.startAnimation(ra);
            mCurrentDegree = azimuthInDegress;
        }
        else if ((mLastAccelerometerSet || mLastGravitySet) && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(mCurrentDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            intDegrees = (int) azimuthInDegress;
            if(intDegrees == 0 || intDegrees == 360){
                stringOrinetation = "N " + intDegrees + "°";
            } else if(intDegrees > 0 && intDegrees < 90){
                stringOrinetation = "NE " + intDegrees + "°";
            } else if(intDegrees == 90){
                stringOrinetation = "E " + intDegrees + "°";
            } else if(intDegrees > 90 && intDegrees < 180){
                stringOrinetation = "SE " + intDegrees + "°";
            } else if(intDegrees == 180){
                stringOrinetation = "S " + intDegrees + "°";
            } else if(intDegrees > 180 && intDegrees < 270){
                stringOrinetation = "SW " + intDegrees + "°";
            } else if(intDegrees == 270){
                stringOrinetation = "W " + intDegrees + "°";
            } else if(intDegrees > 270 && intDegrees < 360){
                stringOrinetation = "NW " + intDegrees + "°";
            }
            tvOrientation.setText(stringOrinetation);
            mPointer.startAnimation(ra);
            mCurrentDegree = azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
