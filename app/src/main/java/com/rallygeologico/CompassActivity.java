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

/**
 * Fragmento que se encarga de mostrar la brujula en la pantalla de realidad aumentada
 */
public class CompassActivity extends Fragment implements SensorEventListener {

    private ImageView mPointer; // Imagen del apuntador de la brujula
    private TextView tvOrientation; // Texto que indica la orientacion de la brujula
    private int intDegrees; // Cacula los grados del azimut

    // Variables para trabajar con los sensores
    private String stringOrinetation;
    private SensorManager mSensorManager;

    // Sensores del dispositivo
    private Sensor mAccelerometer;
    private Sensor mGravity;
    private Sensor mMagnetometer;
    private Sensor mRotation;

    // Guarda los valores del vector de orientacion
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];

    // Booleanos que indican si ya se uso el sensor respectivo
    private boolean mLastAccelerometerSet = false;
    private boolean mLastGravitySet = false;
    private boolean mLastMagnetometerSet = false;
    private boolean mLastRotationSet = false;

    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    // Indica si el dispositivo posee los respectivos sensores
    private boolean haveGravity;
    private boolean haveRotation;

    /**
     * Crea la vista del fragmento con la brujula y su aguja
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return La vista del fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inicializa las variables y los sensores respectivos
        View v = inflater.inflate(R.layout.activity_compass, container, false);
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mPointer = (ImageView) v.findViewById(R.id.pointer);
        tvOrientation = (TextView) v.findViewById(R.id.orientation);

        this.haveGravity = this.mSensorManager.registerListener( this, this.mGravity, SensorManager.SENSOR_DELAY_GAME );
        this.haveRotation = this.mSensorManager.registerListener( this, this.mRotation, SensorManager.SENSOR_DELAY_GAME );

        // Si tiene sensor de vector de rotacion, desactiva los demas
        if (this.haveRotation) {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mGravity);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else if( this.haveGravity ) { // Si tiene sensor de gravedad, desactiva el acelerometro
            this.mSensorManager.unregisterListener( this, this.mAccelerometer );
        }
        return v;
    }

    /**
     * Si se reanuda el fragmento se vueven a registrar los sensores
     */
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Si se pausa el fragmento, se desactivan los sensores
     */
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mGravity);
        mSensorManager.unregisterListener(this, mMagnetometer);
        mSensorManager.unregisterListener(this, mRotation);
    }

    /**
     * Maneja los cambios de sensores y de orientacion
     * @param event Evento que obtiene el resultado del sensor mas reciente
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Dependiendo de cual sensor se use, se copian los valores en un arreglo
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

        // Si los resultados son del vector de rotacion, hay que obtener los valores por separado
        if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
            // calculate th rotation matrix
            SensorManager.getRotationMatrixFromVector( mR, event.values );
        }

        // Si se esta usando el vector de rotacion, se actualiza la brujula con sus valores
        if (mLastRotationSet) {
            // Se obtiene la orientacion del sensor en radianes y se pasa a grados
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            // Se hace una animacion que gire la aguja con respecto a la orientacion actual
            RotateAnimation ra = new RotateAnimation(mCurrentDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            intDegrees = (int) azimuthInDegress;
            // Se asocia la orientacion de la aguja dependiendo del valor del azimut
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
        // Si se usan los otros sensores, entonces se usa la brujula con esos valores
        else if ((mLastAccelerometerSet || mLastGravitySet) && mLastMagnetometerSet) {
            // Se obtiene la orientacion del sensor en radianes y se pasa a grados
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            // Se hace una animacion que gire la aguja con respecto a la orientacion actual
            RotateAnimation ra = new RotateAnimation(mCurrentDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            intDegrees = (int) azimuthInDegress;
            // Se asocia la orientacion de la aguja dependiendo del valor del azimut
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
