package com.rallygeologico;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import SqlDatabase.LocalDB;
import SqlEntities.Site;

public class finishRallyActivity extends AppCompatActivity {

    Button btnPublish;
    TextView petrocoins;
    MediaPlayer mp;
    int rallyId;
    LocalDB localDB;
    List<Site> sites;
    int contadorPuntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent(); // gets the previously created intent
        rallyId= myIntent.getIntExtra("rallyId",0);

        setContentView(R.layout.finishrally);

        localDB=new LocalDB(this);

        sites= localDB.selectAllSitesFromRally(rallyId);

        contadorPuntos=0;

        for(int ite=0;ite<sites.size();++ite)
        {
            if (sites.get(ite).getStatus() == 2) {
            contadorPuntos=contadorPuntos+ sites.get(ite).getPointsForVisit();
            }
            if (sites.get(ite).getStatus() == 3) {
                contadorPuntos=contadorPuntos+ sites.get(ite).getPointsForVisit();
            }
        }

        petrocoins=findViewById(R.id.points_rally_game_screen);
        petrocoins.setText("Puntos obtenidos: "+Integer.toString(contadorPuntos));

        btnPublish= findViewById( R.id.btnPublish);

        btnPublish= findViewById( R.id.btnPublish);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             publicarresultados();
            }
        });

        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(3000);
        mp = MediaPlayer.create(this, R.raw.alertadesonido);
        mp.start();

    }

    public void publicarresultados(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

}
