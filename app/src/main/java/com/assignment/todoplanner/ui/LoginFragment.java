package com.assignment.todoplanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.assignment.todoplanner.R;
import com.assignment.todoplanner.database.SharedPreferencesConfig;
import com.assignment.todoplanner.viewmodel.MainViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {

    private static final int REQUEST_CODE = 10101;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private SharedPreferencesConfig preferencesConfig;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        preferencesConfig = new SharedPreferencesConfig(requireActivity().getApplicationContext());
        if (preferencesConfig.readLogInStatus())
            navigateToDashboard(false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);
        SignInButton signInButton = view.findViewById(R.id.login_google_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(v -> signIn());
        return view;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        try {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d("TAG", "firebaseAuthWithGoogle: " + (isNew ? "new user" : "old user"));
                            updateUI(user, isNew);
                        } catch (Exception e) {
                            Log.e("TAG", "firebaseAuthWithGoogle: " + e.getMessage());
                        }
                    } else {
                        Log.e("TAG", "signInWithCredential:failure", task.getException());
                        updateUI(null, false);
                    }
                });
    }

    private void updateUI(FirebaseUser result, boolean isNewUser) {
        if (result != null) {
            preferencesConfig.writeLogInStatus(true);
            preferencesConfig.writeUserName(result.getDisplayName());
            preferencesConfig.writeUserEmail(result.getEmail());
            new ViewModelProvider(requireActivity()).get(MainViewModel.class).updateUser();
            Toast.makeText(getContext(), "Welcome " + result.getDisplayName(), Toast.LENGTH_LONG).show();
            navigateToDashboard(isNewUser);
        }
    }

    private void navigateToDashboard(boolean isNewUser) {
        LoginFragmentDirections.ActionLoginFragmentToDashboardFragment action = LoginFragmentDirections.actionLoginFragmentToDashboardFragment(isNewUser);
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(action);
    }
}