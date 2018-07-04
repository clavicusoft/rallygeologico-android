package com.rallygeologico;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ActivityQR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Inicia el objeto*/
        mScannerView=new ZXingScannerView(this);
        /*Inicia la vista*/
        setContentView(mScannerView);
        /*Llama al result Handler*/
        mScannerView.setResultHandler(this);
        /*Inicia la caara*/
        mScannerView.startCamera();
    }

    /*Logia de cifrado del QR*/
    @Override
    public void handleResult(Result result) {

        Log.v("HandlResult",result.getText());

        /*Crea la alerta*/
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Resultado del Escaneo");
        builder.setMessage(result.getText());
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

        /*opcion de continuar escaneando*/
        mScannerView.resumeCameraPreview(this);
    }
}
