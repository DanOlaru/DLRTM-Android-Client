/*
 * Author: Dan Olaru, (c) 2018
 */

package longmoneyoffshore.dlrtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.RC_RECOVERABLE;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.RC_SIGN_IN;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.REQUEST_CODE_SIGN_IN;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.REQUEST_CODE_SIGN_OUT;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final String KEY_ACCOUNT = "key_account";
    private static final String CONTACTS_SCOPE = "https://www.googleapis.com/auth/contacts.readonly";


    public GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    private SignInButton signInButton;
    private Button signOutBtn;
    private Button disconnectBtn;

    //Google Drive access


    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        //mStatusTextView = findViewById(R.id.status);
        //mDetailTextView = findViewById(R.id.detail);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);

        /*
        //this is the original one that worked
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE).requestScopes(new Scope(Scopes.DRIVE_APPFOLDER)) //this last request added by Dan
                .requestEmail()
                .build(); */


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_FILE))
                //.requestScopes(new Scope(Scopes.PROFILE))
                //.requestScopes(new Scope(Scopes.DRIVE_FULL)) //causes the account to seize up and not show the picker
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        //signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        if (account != null )
        {
            updateUI(account);
            //Launch SheetsListActivity
            Intent intent = new Intent(this, SheetsListActivity.class);
            startActivity(intent);
            //startActivityForResult(intent, RC_SIGN_IN);
        }
        /*
        if (account != null && GoogleSignIn.hasPermissions(account, new Scope(Scopes.DRIVE_APPFOLDER))) {
            updateUI(account);
        } else {
            updateUI(null);
        }*/
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if the user is already signed in and all required scopes are granted
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && GoogleSignIn.hasPermissions(account, new Scope(Scopes.DRIVE_APPFOLDER))) {
            updateUI(account);
        } else {
            updateUI(null);
        }
    } */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        switch(requestCode) {
            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");
                break;
            case REQUEST_CODE_SIGN_OUT:
                //signOut();
                revokeAccess();
                break;
        }*/

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "Sign in request code");
            // Called after user is signed in.
            if (resultCode == RESULT_OK) {

                Log.i(TAG, "Signed in successfully.");
                //GoogleSignInOptions gso = GoogleSignInOptions.DEFAULT_SIGN_IN;
                //GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                //Launch of SheetsListActivity
                Intent intent = new Intent(this, SheetsListActivity.class);
                startActivity(intent);
//                intent.putExtra("googleDrive", mDriveClient);

                // The Task returned from this call is always completed, no need to attach
//                // a listener.
//                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                handleSignInResult(task);
            }
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);

        } else {
            //mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            //case R.id.disconnect_button:
            //    revokeAccess();
            //    break;
        }
    }

    protected void onRecoverableAuthException(UserRecoverableAuthIOException recoverableException) {
        Log.w(TAG, "onRecoverableAuthException", recoverableException);
        startActivityForResult(recoverableException.getIntent(), RC_RECOVERABLE);
    }
}
