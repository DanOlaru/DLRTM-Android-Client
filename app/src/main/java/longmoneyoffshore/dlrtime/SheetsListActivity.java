package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.api.services.sheets.v4.model.Sheet;

//Dan
import android.app.Activity;
import android.widget.Toast;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.android.gms.common.api.Scope;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
//import static android.support.v4.media.MediaMetadataCompatApi21.Builder.build;


public class SheetsListActivity extends Activity //or Activity???
{
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private DriveFile driveFile;

    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    private static final String TAG = "BaseDriveActivity";

    private TaskCompletionSource<DriveId> mOpenItemTaskSource;


    // Variables necessary to manage the disconnection
    private Button disconnectBtn;
    private TextView mStatusTextView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    GoogleSignInAccount googleSignInAccount;
    OpenFileActivityOptions openOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheets_list);

        //signIn();
        gso = GoogleSignInOptions.DEFAULT_SIGN_IN;

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        //OK SO FAR

        Log.d("debug bro", googleSignInAccount.getDisplayName() + "  " + googleSignInAccount.getEmail());

        // Get the access for user google drive
        setDrive();

        disconnectBtn = findViewById(R.id.disconnect);

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


    protected void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
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
            initializeDriveClient(googleSignInAccount);

            //OpenFileActivityOptions openOptions = new OpenFileActivityOptions.Builder()
            //        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
            //        .setActivityTitle(getString(R.string.select_file)).build();

            //Task<DriveId> myRequestedIDTask = pickItem(openOptions);
            Task<DriveId> myRequestedIDTask = pickTextFile();

            //TODO: get the id of the sheets file I want to use and pass it to OrderListActivity

        }
        catch (Exception e)
        {
            Log.e("G drive access","FML: cannot get drive access!" + e);
        }
    }

    protected Task<DriveId> pickTextFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        .setActivityTitle(getString(R.string.select_file))
                        .build();
        return pickItem(openOptions);
    }

    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        getDriveClient().newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) (Task<IntentSender> task) -> {
                    //TODO: here the application stops
            startIntentSenderForResult(task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
            return null;
        });

        /*
        mDriveClient.newOpenFileActivityIntentSender(openOptions).continueWith(new Continuation<IntentSender, Void>() {
                @Override
                public Void then(@NonNull Task<IntentSender> task) throws Exception {
                    Log.d("pick a file ID", "HHHEHEHEHEHEHHEHEHE #3");
                    startIntentSenderForResult(task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                    return null;
                }
            });*/
        return mOpenItemTaskSource.getTask();
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);

        //mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
        //mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
        Log.d("mDriveClient+Resource", mDriveClient.toString() + " and " + mDriveResourceClient.getInstanceId());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("debug bro", googleSignInAccount.getDisplayName() + "  " + googleSignInAccount.getEmail());
    }


    protected DriveClient getDriveClient() {
        return mDriveClient;
    }
}
