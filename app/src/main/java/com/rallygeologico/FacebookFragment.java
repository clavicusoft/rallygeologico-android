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

public class FacebookFragment extends Fragment{

    private LoginButton loginButton;
    private ImageView profilePicImageView;
    private TextView greeting;
    private TextView welcome;
    private Button continuar_login;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_facebook, parent, false);
        welcome = v.findViewById(R.id.tv_welcome);

        loginButton = v.findViewById(R.id.loginButton);
        // If using in a fragment
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
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast toast = Toast.makeText(getActivity(), "Conectado", Toast.LENGTH_SHORT);
                toast.show();
                updateUI();
            }

            @Override
            public void onCancel() {
                showAlert();
                updateUI();
            }

            @Override
            public void onError(FacebookException exception) {
                showAlert();
                updateUI();
            }

            private void showAlert() {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.cancelled)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }

        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AccessToken.getCurrentAccessToken() != null){
                    continuar_login.setVisibility(v.GONE);
                }
            }
        });

        continuar_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProfileScreen(view);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getActivity());
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();

        if (enableButtons && profile != null) {
            new LoadProfileImage(profilePicImageView).execute(profile.getProfilePictureUri(200, 200).toString());
            greeting.setText(String.format(getString(R.string.hello_user), profile.getFirstName()) );
            continuar_login.setVisibility(View.VISIBLE);
        } else {
            greeting.setText(null);
            profilePicImageView.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        }
    }

    public void setProfileScreen(View view) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    public static class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
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