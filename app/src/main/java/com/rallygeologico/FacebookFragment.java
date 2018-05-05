package com.rallygeologico;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static android.view.View.GONE;

/**
 * Fragmento para desplegar la pantalla de inicio de sesion con Facebook
 */
public class FacebookFragment extends Fragment {

    private LoginButton loginButton;
    private ImageView profilePicImageView;
    private TextView greeting;
    private Button continuar_login;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;

    /**
     * Se ejecuta al crearse la actividad
     * @param savedInstanceState Estado actual de la aplicacion
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());
    }

    /**
     * Se llama cuando se sale de la actividad para el callbackManager de Facebook
     * @param requestCode Codigo que identifica de donde viene la solicitud
     * @param resultCode Codigo que devuelve la actividad hijo
     * @param data Intent nuevo con el resultado devuelto de la actividad
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Crea la vista cuando se inicializa el fragmento
     * @param inflater Vista para cargar la pantalla
     * @param parent Vista padre del fragmento
     * @param savedInstanceState Estado de la aplicacion
     * @return Vista con el fagmento almacenado
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_facebook, parent, false);
        loginButton = v.findViewById(R.id.loginButton);
        loginButton.setFragment(this);
        List<String> listPermission = Arrays.asList("email", "public_profile", "user_hometown");
        loginButton.setReadPermissions(listPermission);

        profilePicImageView = v.findViewById(R.id.profilePicture);
        greeting = v.findViewById(R.id.greeting);

        continuar_login = v.findViewById(R.id.btn_cont_login);
        continuar_login.setVisibility(GONE);

        profilePicImageView = v.findViewById(R.id.profilePicture);
        profilePicImageView.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

        callbackManager = CallbackManager.Factory.create();
        // Se registra la llamada de vuelta cuando se completa el login
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            /**
             * Se ejecuta si el login es exitoso
             * @param loginResult Resultado del login
             */
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast toast = Toast.makeText(getActivity(), "Conectado", Toast.LENGTH_SHORT);
                toast.show();
                updateUI();
            }

            /**
             * Se ejecuta si se cancela el login
             */
            @Override
            public void onCancel() {
                showAlert();
                updateUI();
            }

            /**
             * Se ejecuta si hay un error en el login
             * @param exception Excepcion que envia el error
             */
            @Override
            public void onError(FacebookException exception) {
                showAlert();
                updateUI();
            }

            /**
             * Crea una alerta y la muestra cuando no se puede hacer bien el login
             */
            private void showAlert() {
                new AlertDialog.Builder(getActivity())
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
                updateUI();
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Se agregan opciones que se deseen ejecutar cuando se hace click en
             * el boton de login
             * @param v Vista del fragmento
             */
            @Override
            public void onClick(View v) {
            }
        });

        continuar_login.setOnClickListener(new View.OnClickListener() {
            /**
             * Se cambia de pantalla cuando se da click en el boton de continuar
             * cuando se completa el inicio de sesion
             * @param view Vista del fragmento
             */
            @Override
            public void onClick(View view) {
                setGameScreen(view);
            }
        });
        return v;
    }

    /**
     * Se actualiza la interfaz cuando se vuelve a iniciar el fragmento
     */
    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getActivity());
        updateUI();
    }

    /**
     * Se registra en el log cuando se pausa el fragmento
     */
    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getActivity());
    }

    /**
     * Se deja de rastrear el perfil cuando se destruye el fragmento
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    /**
     * Actualiza la interfaz con los datos de login dependiendo si el
     * usuario esta conectado o no
     */
    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();

        // Si el usuario esta conectado y se puede obtener su perfil se actualiza
        // la foto de perfil y el nombre y se activa el boton de continuar
        if (enableButtons && profile != null) {
            new LoadProfileImage(profilePicImageView).execute(profile.getProfilePictureUri(200, 200).toString());
            greeting.setText(String.format(getString(R.string.hello_user), profile.getFirstName()) );
            continuar_login.setVisibility(View.VISIBLE);
        } else {
            greeting.setText(null);
            profilePicImageView.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            continuar_login.setVisibility(View.GONE);
        }
    }

    /**
     * Cambia a la actividad principal del juego
     * @param view Vista del fragmento
     */
    public void setGameScreen(View view) {
        Intent intent = new Intent(getActivity(), GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Clase para cargar la imagen de perfil de Facebook del usuario
     * */
    public static class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        /**
         * Constructor que inicializa la imagen
         * @param bmImage
         */
        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        /**
         * Tarea asincrona para cargar la imagen de perfil de facebook desde url
         * */
        protected Bitmap doInBackground(String... uri) {
            String url = uri[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        /**
         * Carga la imagen despues de obtenerla en el metodo anterior
         * @param result resultado obtenido de la url
         */
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                Bitmap resized = Bitmap.createScaledBitmap(result,200,200, true);
                bmImage.setImageBitmap(resized);

            }
        }
    }

}