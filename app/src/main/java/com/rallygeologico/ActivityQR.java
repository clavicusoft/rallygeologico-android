package com.rallygeologico;

import android.app.Dialog;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ActivityQR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private Dialog qrDialog;
    Button botonobservar;
    TextView botoncerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        qrDialog=new Dialog(this);
        qrDialog.setContentView(R.layout.alertaqr);

        /*Inicia el objeto*/
        mScannerView=new ZXingScannerView(this);
        /*Inicia la vista*/
        setContentView(mScannerView);
        /*Llama al result Handler*/
        mScannerView.setResultHandler(this);
        /*Inicia la camara*/
        mScannerView.startCamera();
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }


    /*Logia de cifrado del QR*/
    @Override
    public void handleResult(Result result) {

        /*Actualizo la vista*/
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(3000);

        Log.v("HandlResult",result.getText());
        /*Crea la alerta*/
      /*  AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Resultado del Escaneo");
        builder.setMessage(result.getText());
        AlertDialog alertDialog=builder.create();
        alertDialog.show();*/
        /*opcion de continuar escaneando*/

        /*Ben quiso probar esto :3*/


        /*Llenar el activity*/
        ImageView imagen= qrDialog.findViewById(R.id.qr_iv_alerta_imagen);
        imagen.setImageResource(getResources().getIdentifier( "qr", "drawable", getPackageName()));

        TextView secreto= qrDialog.findViewById( R.id.qr_tv_alerta_secreto);
        secreto.setText("¡Has encontrado un QR!");

        TextView nom= qrDialog.findViewById( R.id.qr_tv_alerta_nombre);
        nom.setText(result.getText());

        TextView valor= qrDialog.findViewById( R.id.qr_tv_alerta_valor);
        valor.setText("50 "+ " Petrocoins");


        /*Asigna los botones*/
        botoncerrar= qrDialog.findViewById( R.id.qr_btn_close);
        botoncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrDialog.hide();
            }
        });

        botonobservar= qrDialog.findViewById( R.id.qr_btn_observar);
        botonobservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrDialog.hide();
            }
        });

        qrDialog.show();

        mScannerView.resumeCameraPreview(this);

/*
        Intent i= new Intent(this,WebActivity.class);
        i.putExtra("URL",result.getText());
        startActivity(i);
*/

    }
}
