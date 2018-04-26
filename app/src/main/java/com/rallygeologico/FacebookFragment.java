package com.rallygeologico;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.ShareDialog;

import java.io.InputStream;
import java.util.Arrays;

import static android.view.View.GONE;

public class FacebookFragment extends Fragment{

    private LoginButton loginButton;
    private ImageView ivFotoPerfil;
    private TextView tvSaludo;
    private TextView tvBienvenido;
    private Button btnContinuar;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;

    /**
     * Constructor donde se inicializa el SDK de Facebook
     * @param savedInstanceState Estado de la instancia
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());
        // Other app specific specialization
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Se crea la vista del fragmento de Facebook
     * @param inflater
     * @param parent
     * @param savedInstanceState
     * @return Devuelve la vista creada
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_facebook, parent, false);
        tvBienvenido = v.findViewById(R.id.tv_welcome);
        loginButton = v.findViewById(R.id.loginButton);
        loginButton.setFragment(this);
        ivFotoPerfil = v.findViewById(R.id.profilePicture);
        tvSaludo = v.findViewById(R.id.greeting);
        btnContinuar = v.findViewById(R.id.btn_cont_login);
        btnContinuar.setVisibility(GONE);
        ivFotoPerfil = v.findViewById(R.id.profilePicture);
        ivFotoPerfil.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        callbackManager = CallbackManager.Factory.create();
        // Se registra la llamada del login para ver si la conexion fue exitosa
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            /**
             * Muestra un mensaje Toast si se hace login exitosamente
             * @param resultadoLogin
             */
            @Override
            public void onSuccess(LoginResult resultadoLogin) {
                Toast toast = Toast.makeText(getActivity(), "Conectado", Toast.LENGTH_SHORT);
                toast.show();
                actulizarPantalla();
            }

            /**
             * Envia una alerta al usuario si se cancela el login
             */
            @Override
            public void onCancel() {
                showAlert();
                actulizarPantalla();
            }

            /**
             * Envia una alerta al usuario si hay un error en el login
             * @param excepcion Excepcion que envia el error
             */
            @Override
            public void onError(FacebookException excepcion) {
                showAlert();
                actulizarPantalla();
            }

            /**
             * Crea una alerta con un mensaje de error
             */
            private void showAlert() {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.cancelled)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }

        });

        profileTracker = new ProfileTracker() {
            /**
             * Actualiza la interfaz si detecta un cambio en el usuario
             * @param perfilViejo Perfil antiguo a cambiar
             * @param perfilActual Perfil actual que sera el nuevo
             */
            @Override
            protected void onCurrentProfileChanged(Profile perfilViejo, Profile perfilActual) {
                actulizarPantalla();
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return v;
    }

    /**
     * Actualiza la interfaz si se retoma la activity
     */
    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getActivity());
        actulizarPantalla();
    }

    /**
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getActivity());
    }

    /**
     * Se deja de rastrear el perfil si se destruye el fragmento
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    /**
     * Actualiza la interfaz del fragmento con la informacion del perfil del usuario si esta conectado
     */
    private void actulizarPantalla() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
            new LoadProfileImage(ivFotoPerfil).execute(profile.getProfilePictureUri(200, 200).toString());
            tvSaludo.setText(String.format(getString(R.string.hello_user), profile.getFirstName()) );
            btnContinuar.setVisibility(View.VISIBLE);
        } else {
            tvSaludo.setText(null);
            btnContinuar.setVisibility(View.GONE);
            ivFotoPerfil.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Tarea asíncrona que se corre en el fondo para cargar la imangen de perfil de Facebook del url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

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

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                Bitmap resized = Bitmap.createScaledBitmap(result,200,200, true);
                bmImage.setImageBitmap(resized);

            }
        }
    }

}