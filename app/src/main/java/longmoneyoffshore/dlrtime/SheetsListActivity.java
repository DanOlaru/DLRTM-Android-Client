package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.android.gms.common.api.Scope;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import com.google.android.gms.drive.MetadataChangeSet;
//import static android.support.v4.media.MediaMetadataCompatApi21.Builder.build;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.util.Log;

public class SheetsListActivity extends AppCompatActivity  //or Activity???
{
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private DriveFile driveFile;

    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int NEXT_AVAILABLE_REQUEST_CODE = 1;
    private static final int RESULT_OK = -1;
    private static final int REQUEST_CODE_OPENER = NEXT_AVAILABLE_REQUEST_CODE + 1;
    private static final String TAG = "BaseDriveActivity";
    private static final String MIME_TYPE_TEXT = "text/plain";
    private static final int RC_SIGN_IN = 9001;
    private static final int REQ_PICKFILE = 4;

    private TaskCompletionSource<DriveId> mOpenItemTaskSource;

    // Variables necessary to manage the disconnection
    private Button disconnectBtn;
    private TextView mStatusTextView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    GoogleSignInAccount googleSignInAccount;
    //OpenFileActivityOptions openOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheets_list);

        //signIn();
        gso = GoogleSignInOptions.DEFAULT_SIGN_IN;
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        Log.d("debug bro", googleSignInAccount.getDisplayName() + "  " + googleSignInAccount.getEmail());
        //OK SO FAR

        setDrive();

        disconnectBtn = findViewById(R.id.disconnect);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(final View view) {revokeAccess(view.getContext());}
        });

        mStatusTextView = findViewById(R.id.status2);
        mStatusTextView.setText(getString(R.string.signed_in_fmt2, googleSignInAccount.getDisplayName()));

    }

    private void setDrive()
    {
        // Why it works? https://stackoverflow.com/questions/25031669/passing-the-googleapiclient-obj-from-one-activity-to-another
        // After the login the account will be available throughout the project
        try
        {
            initializeDriveClient(googleSignInAccount);

            OpenFileActivityOptions openOptions = new OpenFileActivityOptions.Builder()
                    //.setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain")) //TODO: I need to select sheet files
                    .setActivityTitle(getString(R.string.select_file)).build();

            Task<DriveId> myRequestedIDTask = pickItem(openOptions);
            //Task<DriveId> myRequestedIDTask = pickTextFile();

            Log.d("Return SOME result", myRequestedIDTask.getResult().encodeToString());

            //TODO: get the id of the sheets file I want to use and pass it to OrderListActivity
        }
        catch (Exception e)
        {
            Log.e("G drive access","cannot get drive access" + e);
        }
    }

    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();

        getDriveClient().newOpenFileActivityIntentSender(openOptions)
                    .continueWith((Continuation<IntentSender, Void>) (Task<IntentSender> task) -> {
                        try {
                            SheetsListActivity.this.startIntentSenderForResult(task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.w(TAG, "Unable to send intent — error: ", e);
                        }
                        return null;
                    });

        /*
         //coding without the lambda
        mDriveClient.newOpenFileActivityIntentSender(openOptions).continueWith(new Continuation<IntentSender, Void>() {
                @Override
                public Void then(@NonNull Task<IntentSender> task) throws Exception {
                    SheetsListActivity.this.startIntentSenderForResult(task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                    return null;
                }
            });*/
        return mOpenItemTaskSource.getTask();
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);

        //various testing crapola
        //mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
        //mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
        //DriveClient mDriveClient1 = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
        //DriveResourceClient mDriveResourceClient1 = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
        //Log.d("mDriveClient1+Resource1", mDriveClient1.toString() + " and " + mDriveResourceClient1.getInstanceId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Log.d("REQUESTCODE", "Able to send intent — error XXXXXXXXXXXXXXXXXXXXXXX: " + requestCode);
        //Log.d("RESULTCODE", "Able to send intent — error YYYYYYYYYYYYYYYYYYYYYYYYY: " + resultCode);

        String myChosenFileId;

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

                Task<GoogleSignInAccount> getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra( OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    //Log.d("DRIVEID", "DRIVE ID IS  ZZZZZZZZZZZZZZZZZZZZZZZZ: " + driveId.getResourceId());

                    myChosenFileId = driveId.getResourceId();

                    DriveFile selectedFile = driveId.asDriveFile();

                    Log.d ("PICKFILE", selectedFile.toString());

                    //DriveFile selectedFile = Drive.DriveApi.getFile(googleApiClient, driveId);

                    //String fileId = myChosenFileId;
                    //OutputStream outputStream = new ByteArrayOutputStream();
                    //DriveFile.files().get(fileId).executeMediaAndDownloadTo(outputStream);


                    Intent goToOrdersList = new Intent (SheetsListActivity.this, OrderListActivity.class);
                    goToOrdersList.putExtra("file selected", myChosenFileId);

                    //startActivity(goToOrdersList);
                    //mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected DriveClient getDriveClient() {
        return mDriveClient;
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
}
