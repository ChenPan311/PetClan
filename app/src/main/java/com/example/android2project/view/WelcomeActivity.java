package com.example.android2project.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.android2project.R;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.view.fragments.LoginDetailsFragment;
import com.example.android2project.view.fragments.LoginRegistrationFragment;
import com.example.android2project.view.fragments.SignUpDetailsFragment;
import com.example.android2project.view.fragments.UserDetailsFragment;
import com.example.android2project.view.fragments.UserPictureFragment;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.WelcomeViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity implements
        LoginRegistrationFragment.LoginRegisterFragmentListener,
        SignUpDetailsFragment.SignUpDetailsListener,
        UserDetailsFragment.UserDetailsListener,
        UserPictureFragment.UserPictureListener,
        LoginDetailsFragment.LoginDetailsListener {

    private WelcomeViewModel mViewModel;
    Observer<Boolean> mUserDeletedObserver;
    Observer<String> mUserSignInObserver;

    private final String SIGN_REG_FRAG = "sign_registration_fragment";
    private final String SIGN_UP_FRAG = "sign_up_fragment";
    private final String USER_DETAILS_FRAG = "user_details_fragment";
    private final String USER_PIC_FRAG = "user_picture_fragment";
    private final String LOGIN_DETAILS_FRAG = "login_details_fragment";

    private final String TAG = "WelcomeActivity";

    private AlertDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(this,
                ViewModelEnum.Welcome)).get(WelcomeViewModel.class);

        if (mViewModel.isUserLoggedIn()) {
            startMainActivity();
        }

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(WelcomeActivity.this,
                        new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                final String newToken = instanceIdResult.getToken();
                                Log.d("newToken: ", newToken);

                                mViewModel.setUserToken(newToken);
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: " + e.getMessage());
                    }
                });

        mUserDeletedObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Snackbar.make(findViewById(R.id.root_layout),getResources().getString(R.string.registration_aborted),
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        };

        mUserSignInObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mLoadingDialog.dismiss();
                startMainActivity();
            }
        };

        mViewModel.getUserSignInSucceed().observe(this, mUserSignInObserver);

        mViewModel.getUserDeletionSucceed().observe(this, mUserDeletedObserver);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginRegistrationFragment.newInstance(), SIGN_REG_FRAG)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (getSupportFragmentManager().findFragmentByTag(LOGIN_DETAILS_FRAG) != null) {
            Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(LOGIN_DETAILS_FRAG))
                    .onActivityResult(requestCode, resultCode, data);
        }
        if (getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG) != null) {
            Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG))
                    .onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * <-------Sets LoginRegistrationFragment buttons------->
     **/
    @Override
    public void onJoin() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.root_layout, SignUpDetailsFragment.newInstance(), SIGN_UP_FRAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSignIn(String screenName) {
        if (screenName.equals("LoginRegistration")) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.root_layout, LoginDetailsFragment.newInstance(), LOGIN_DETAILS_FRAG)
                    .addToBackStack(null)
                    .commit();
        } else if (screenName.equals("LoginDetails")) {
            startMainActivity();
        }
    }

    @Override
    public void onSignInAsGuest(AlertDialog loadingDialog) {
        mLoadingDialog = loadingDialog;
        mViewModel.signInAsGuest();
    }

    /**
     * <-------Sets SignUpDetailsFragment buttons------->
     **/
    @Override
    public void onNext(String screenName) {
        if (screenName.equals("SignUpDetails")) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.root_layout, UserDetailsFragment.newInstance(), USER_DETAILS_FRAG)
                    .addToBackStack(null)
                    .commit();
        } else if (screenName.equals("UserDetails")) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.root_layout, UserPictureFragment.newInstance(), USER_PIC_FRAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * <-------Sets UserDetailsFragment buttons------->
     **/
    @Override
    public void onFacebook(String screenName) {
        Log.d(TAG, "onFacebook");
        startMainActivity();
    }

    @Override
    public void onGoogle(String screenName) {
        startMainActivity();
    }

    @Override
    public void onFinish() {
        startIntroActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startIntroActivity() {
        Intent intent = new Intent(WelcomeActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /**<-------If the new user returned to the SignUpDetailsFragment, delete this user------->**/
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG);
        if (fragment != null && fragment.isVisible()) {
            mViewModel.deleteUserFromAuth();
        }
    }

    @Override
    protected void onDestroy() {
        mViewModel.getUserDeletionSucceed().removeObserver(mUserDeletedObserver);
        super.onDestroy();
    }
}
