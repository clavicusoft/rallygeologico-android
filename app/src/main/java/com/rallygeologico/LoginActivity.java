package com.rallygeologico;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import FileManager.DownloadTask;
import SqlDatabase.LocalDB;
import SqlEntities.User;

import static android.view.View.GONE;

/**
 * Clase para manejar la actividad de login con Facebook o Google
 */
public class LoginActivity extends AppCompatActivity {

    LocalDB db;
    private Button continuar;
    private EditText username;
    private EditText password;
    private LinearLayout credentials;
    private LinearLayout progressBar;
    // Codigo de login para Google
    private static final int RC_SIGN_IN = 9001;

    private Button loginFacebookButton;
    private Button loginGoogleButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private LoginManager fbLoginManager;
    private Context context;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private User user;

    /**
     * Cuando se inicia la actividad se crea el boton de inicio con Facebook y Google
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        db = new LocalDB(this);
        //FacebookSdk.sdkInitialize(this);
        context = this;

        continuar = findViewById(R.id.btn_ingresar);

        //continuar.setVisibility(GONE);
        continuar.setOnClickListener(new View.OnClickListener() {
            /**
             * Continuar a la pantalla principal del juego
             * @param v Vista del activity
             */
            @Override
            public void onClick(View v) {
                manejarInicioSesion();
            }
        });

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        credentials = findViewById(R.id.layout_credenciales);
        progressBar = findViewById(R.id.layout_cargando);
        progressBar.setVisibility(GONE);

        /*loginFacebookButton = findViewById(R.id.btn_fb);
        loginFacebookButton.setVisibility(View.VISIBLE);
        // Asigna una lista con los permisos de login
        final List<String> listPermission = Arrays.asList("email", "public_profile", "user_hometown");
        fbLoginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        // Se registra la llamada de vuelta cuando se completa el login
        fbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            /**
             * Se ejecuta si el login es exitoso
             * @param loginResult Resultado del login
             */
           /* @Override
            public void onSuccess(LoginResult loginResult) {
                manejarInicioSesionFb();
            }

            /**
             * Se ejecuta si se cancela el login
             */
           /* @Override
            public void onCancel() {
                showAlert();
            }

            /**
             * Se ejecuta si hay un error en el login
             * @param exception Excepcion que envia el error
             */
           /* @Override
            public void onError(FacebookException exception) {
                showAlert();
            }

            /**
             * Crea una alerta y la muestra cuando no se puede hacer bien el login
             */
           /* private void showAlert() {
                new AlertDialog.Builder(context)
                        .setTitle("No se pudo iniciar sesion")
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
           /* @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };

        loginFacebookButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Se hace el login por medio de Facebook
             * @param v Vista del activity
             */
          /*  @Override
            public void onClick(View v) {
                if(tieneConexionInternet()){
                    fbLoginManager.logInWithReadPermissions(LoginActivity.this,listPermission);
                }else {
                    new AlertDialog.Builder(context)
                            .setTitle("Aviso")
                            .setMessage("Debe conectarse a internet para iniciar sesion.")
                            .setPositiveButton(R.string.ok, null)
                            .show();
                }
            }
        });

        loginGoogleButton = findViewById(R.id.btn_google);
        loginGoogleButton.setVisibility(View.VISIBLE);
        //Asigna opciones de login como permisos
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginGoogleButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Se hace el login por medio de Google
             * @param v Vista del activity
             */
         /*   @Override
            public void onClick(View v) {
                if(tieneConexionInternet()){
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }else{
                    new AlertDialog.Builder(context)
                            .setTitle("Alerta")
                            .setMessage("Debe conectarse a internet para iniciar sesion.")
                            .setPositiveButton(R.string.ok, null)
                            .show();
                }
            }
        });*/

    }

    public void manejarInicioSesion() {
        String usuario = "";
        if (username.getText().toString().contains(" ")) {
            username.setError("No se permiten espacios");
        } else {
            usuario = username.getText().toString();
        }
        String contrasena = password.getText().toString();

        if (!usuario.isEmpty() && !contrasena.isEmpty()) {
            credentials.setVisibility(GONE);
            progressBar.setVisibility(View.VISIBLE);
            user = db.selectUserByUsername(usuario, contrasena);
            if (user == null) {
                if(tieneConexionInternet()){
                    String resultado = validarCredencialesWeb(usuario, contrasena);
                    if (resultado.equalsIgnoreCase("null")) {
                        credentials.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        mostrarAlerta();
                    } else {
                        String toParse = resultado.replace("]","");
                        toParse = toParse.replace("[","");
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(toParse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(jsonObject != null){
                            user = JSONParser.getUser(jsonObject);
                            user.setPassword(contrasena);
                            String url = "http://www.rallygeologico.ucr.ac.cr" + user.getPhotoUrl();
                            new DownloadTask(context, 1, "fotoPerfil", url);
                            long id = db.insertUser(user);
                            setGameScreen();
                        }
                    }

                } else {
                    credentials.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Nombre de usuario o contraseña incorrectos.\nSi es la primera vez que ingresa, debe conectarse a Internet.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            } else {
                user.setLogged(true);
                db.updateUser(user);
                setGameScreen();
            }
        } else {
            Toast toast = Toast.makeText(context, "Por favor ingrese el nombre de usuario y la contraseña", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void mostrarAlerta() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage("Nombre de usuario o contraseña incorrectos.\nSi no se ha registrado, puede hacerlo en la página web.")
                .setPositiveButton("Ir a página web", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setWebActivity();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Utiliza el web service para ver si los credenciales existen el la bd de la web.
     *
     * @param usuario
     * @param contrasena
     * @return verdadero si el web service responde que los credenciales están correctos.
     */
    public String validarCredencialesWeb(String usuario, String contrasena) {
        String resultado = "JORGE";
        String idConsultaValor = "0";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        resultado = backgroundWorker.doInBackground(idConsultaValor, usuario, contrasena);
        return resultado;
    }

    /**
     * Pregunta si desea continuar o no con la cuenta indicada
     */
    private void manejarInicioSesionFb() {
        final Profile perfil = Profile.getCurrentProfile();
        String id = perfil.getId();
        user = db.selectUser(id);
        String name = perfil.getFirstName();
        new AlertDialog.Builder(context)
                .setTitle("Iniciar Sesion")
                .setMessage("Desea continuar como " + name + "?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = perfil.getProfilePictureUri(200, 200);
                        String url = "";
                        if (uri != null) {
                            url = uri.toString();
                            new DownloadTask(context, 1, "fotoPerfil", url);
                        }
                        Toast toast = Toast.makeText(context, "Conectado", Toast.LENGTH_SHORT);
                        toast.show();
                        /*if(user == null){
                            user.setUserId(perfil.getId());
                            user.setFirstName(perfil.getFirstName());
                            user.setLastName(perfil.getLastName());
                            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            if (object != null) {
                                                String email = JSONParser.getEmail(object);
                                                user.setEmail(email);
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,hometown,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                            user.setPhotoUrl(url);
                            db.insertUser(user);
                        }*/
                        setGameScreen();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fbLoginManager.logOut();
                    }
                })
                .show();
    }

    /**
     * Cierra la sesion de Google
     */
    private void googleSignOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    /**
     * Se llama cuando se sale de la actividad para el callbackManager de Facebook o de Google
     *
     * @param requestCode Codigo que identifica de donde viene la solicitud
     * @param resultCode  Codigo que devuelve la actividad hijo
     * @param data        Intent nuevo con el resultado devuelto de la actividad
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // Devuelve una tarea de login con el resultado
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            //Maneja el login de Facebook de a cuerdo con el resultado obtenido
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Maneja el login para ver si fue exitoso o no
     *
     * @param completedTask Tarea que se encarga de hacer el login con Google
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            user = db.selectUser(id);
            String nombre = account.getGivenName();
            new AlertDialog.Builder(context)
                    .setTitle("Iniciar Sesion")
                    .setMessage("Desea continuar como " + nombre + "?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = account.getPhotoUrl();
                            String url = "";
                            if (uri != null) {
                                url = uri.toString();
                                new DownloadTask(context, 1, "fotoPerfil", url);
                            }
                            Toast toast = Toast.makeText(context, "Conectado", Toast.LENGTH_SHORT);
                            toast.show();
                           /* if(user == null){
                                user.setUserId(account.getId());
                                user.setFirstName(account.getGivenName());
                                user.setLastName(account.getFamilyName());
                                user.setEmail(account.getEmail());
                                user.setPhotoUrl(url);
                                db.insertUser(user);
                            }*/
                            setGameScreen();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mGoogleSignInClient.signOut();
                        }
                    })
                    .show();

        } catch (ApiException e) {
            Log.w("Error", "handleSignInResult:error ", e);
            Toast toast = Toast.makeText(context, "No se pudo iniciar sesion", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Se ejecuta cuando se vuelve a iniciar el activity
     */
    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        credentials.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
    public void onStart() {
        super.onStart();
    }

    /**
     * Cambia a la actividad principal del juego
     */
    public void setGameScreen() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Revisa si el dispositivo tiene acceso a internet
     *
     * @return Verdadero si esta conectado, sino falso
     */
    public boolean tieneConexionInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Muestra el activity de inicio del juego
     */
    public void setStartScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Muestra el activity de la pagina web
     */
    public void setWebActivity() {
        Intent intent = new Intent(this, WebActivity.class);
        startActivity(intent);
    }

    /**
     * Si se presiona el boton de atras, se devuelve a la pantalla principal
     */
    @Override
    public void onBackPressed(){
        setStartScreen();
    }

}

