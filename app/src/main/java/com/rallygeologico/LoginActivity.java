package com.rallygeologico;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;

import static android.view.View.GONE;

/**
 * Clase para manejar la actividad de login con Facebook o Google
 */
public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private boolean fbSignIn;
    private boolean googleSignIn;
    private Button loginFacebookButton;
    private Button loginGoogleButton;
    private Button logoutFacebookButton;
    private Button logoutGoogleButton;
    private Button continuarLogin;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private LoginManager fbLoginManager;
    private Context context;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * Cuando se inicia la actividad se crea el boton de inicio con Facebook y Google
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(this);
        context = this;

        continuarLogin = findViewById(R.id.btn_continuar);
        continuarLogin.setVisibility(GONE);
        continuarLogin.setOnClickListener(new View.OnClickListener() {
            /**
             * Se cambia de pantalla cuando se da click en el boton de continuar
             * cuando se completa el inicio de sesion
             * @param view Vista del activity
             */
            @Override
            public void onClick(View view) {
                setGameScreen(view);
            }
        });

        loginFacebookButton = findViewById(R.id.btn_fb);
        //logoutFacebookButton = findViewById(R.id.btn_fb_logout);
        //logoutFacebookButton.setVisibility(GONE);
        final List<String> listPermission = Arrays.asList("email", "public_profile", "user_hometown");
        fbLoginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        // Se registra la llamada de vuelta cuando se completa el login
        fbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            /**
             * Se ejecuta si el login es exitoso
             * @param loginResult Resultado del login
             */
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast toast = Toast.makeText(context, "Conectado", Toast.LENGTH_SHORT);
                toast.show();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                updateUI(account);
            }

            /**
             * Se ejecuta si se cancela el login
             */
            @Override
            public void onCancel() {
                showAlert();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                updateUI(account);
            }

            /**
             * Se ejecuta si hay un error en el login
             * @param exception Excepcion que envia el error
             */
            @Override
            public void onError(FacebookException exception) {
                showAlert();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                updateUI(account);
            }

            /**
             * Crea una alerta y la muestra cuando no se puede hacer bien el login
             */
            private void showAlert() {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.cancelled)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }

        });

        //Rastreador de perfil
        profileTracker = new ProfileTracker() {
            /**
             * Revisa si hubo algun cambio en el perfil de Facebook del usuario
             * @param oldProfile Perfil antiguo
             * @param currentProfile Perfil actual
             */
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                updateUI(account);
            }
        };

        loginFacebookButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Se hace el login por medio de Facebook
             * @param v Vista del activity
             */
            @Override
            public void onClick(View v) {
                if(!fbSignIn){
                    fbLoginManager.logInWithReadPermissions(LoginActivity.this,listPermission);
                } else {
                    fbLoginManager.logOut();
                }
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                updateUI(account);
            }
        });

        /*logoutFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLoginManager.logOut();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                updateUI(account);
            }
        });*/

        loginGoogleButton = findViewById(R.id.btn_google);
        //logoutGoogleButton = findViewById(R.id.btn_google_logout);
        //logoutGoogleButton.setVisibility(GONE);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginGoogleButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Se hace el login por medio de Google o logout
             * @param view Vista del activity
             */
            @Override
            public void onClick(View view) {
                if(!googleSignIn){
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }else{
                    googleSignOut();
                }
            }
        });

        /*logoutGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignOut();
            }
        });*/

    }

    /**
     * Cierra la sesion de Google
     */
    private void googleSignOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    /**
     * Se llama cuando se sale de la actividad para el callbackManager de Facebook o de Google
     * @param requestCode Codigo que identifica de donde viene la solicitud
     * @param resultCode Codigo que devuelve la actividad hijo
     * @param data Intent nuevo con el resultado devuelto de la actividad
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Maneja el login para ver si fue exitoso o no
     * @param completedTask Tarea que se encarga de hacer el login con Google
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast toast = Toast.makeText(context, "Conectado", Toast.LENGTH_SHORT);
            toast.show();
            updateUI(account);
        } catch (ApiException e) {
            Log.w("Error", "handleSignInResult:error ", e);
            Toast toast = Toast.makeText(context, "No se pudo conectar", Toast.LENGTH_SHORT);
            toast.show();
            updateUI(null);
        }
    }

    /**
     * Se actualiza la interfaz cuando se vuelve a iniciar el activity
     */
    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * Se registra en el log cuando se pausa el activity
     */
    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    /**
     * Se deja de rastrear el perfil cuando se destruye el activity
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    /**
     * Se actualiza la interfaz apenas inicia la activity
     */
    @Override
    public void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * Actualiza la interfaz habilitando los botones de Facebook, Google o continuar
     * dependiendo si el usuario esta conectado o no
     */
    private void updateUI(@Nullable GoogleSignInAccount account) {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();
        fbSignIn = enableButtons && profile != null;
        googleSignIn = account != null;

        // Si el usuario esta conectado se actualiza la interfaz
        if (fbSignIn || googleSignIn) {
            if(fbSignIn){
                loginFacebookButton.setText("Salir de Facebook");
            } else if(!fbSignIn) {
                loginFacebookButton.setText("Conectar con Facebook");
            }
            if(googleSignIn){
                loginGoogleButton.setText("Salir de Google");
            } else if(!googleSignIn){
                loginGoogleButton.setText("Conectar con Google");
            }
            continuarLogin.setVisibility(View.VISIBLE);
        } else {
            continuarLogin.setVisibility(View.GONE);
            loginFacebookButton.setText("Conectar con Facebook");
            loginGoogleButton.setText("Conectar con Google");
        }
    }

    /**
     * Cambia a la actividad principal del juego
     * @param view Vista del activity
     */
    public void setGameScreen(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
