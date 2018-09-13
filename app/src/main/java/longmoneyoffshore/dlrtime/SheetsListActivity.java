package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SheetsListActivity extends AppCompatActivity
{
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private DriveFile driveFile;

    // Variables necessary to manage the disconnection
    private Button disconnectBtn;
    private TextView mStatusTextView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    GoogleSignInAccount googleSignInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheets_list_activity);

        // Get the access for user google drive
        setDrive();
        gso = GoogleSignInOptions.DEFAULT_SIGN_IN;
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        disconnectBtn = findViewById(R.id.diconnect);

        disconnectBtn.setOnClickListener(new View.OnClickListener()
        {
             @Override
             public void onClick(final View view)
             {
                revokeAccess(view.getContext());

             }
        });

        mStatusTextView = findViewById(R.id.status2);
        mStatusTextView.setText(getString(R.string.signed_in_fmt2, googleSignInAccount.getDisplayName()));


    }

    private void revokeAccess(final Context context) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        // [END_EXCLUDE]
                    }
                });
    }



    private void Logout()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void setDrive()
    {
        // Why it works? https://stackoverflow.com/questions/25031669/passing-the-googleapiclient-obj-from-one-activity-to-another
        // After the login the account will be available throughout the project

        try
        {
            // Use the last signed in account here since it already have a Drive scope.
            // Build a drive client.
            mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
            // Build a drive resource client.
            mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));

        }
        catch (Exception e)
        {
            Log.e("Google drive access","Error: cannot get drive access!");
        }
    }



}
