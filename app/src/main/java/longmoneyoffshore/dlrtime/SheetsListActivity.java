package longmoneyoffshore.dlrtime;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.googleapis.util.Utils;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
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

import org.json.JSONObject;

public class SheetsListActivity extends Activity //AppCompatActivity
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

    // Variables necessary to manage the connection
    private Button disconnectBtn;
    private TextView mStatusTextView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private GoogleSignInAccount googleSignInAccount;
    private DriveContents mDriveContents;

    //Currently opened file's metadata.
    private Metadata mMetadata;
    //Drive ID of the currently opened Drive file.
    DriveId mCurrentDriveId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheets_list);

        //signIn();
        gso = GoogleSignInOptions.DEFAULT_SIGN_IN;
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        Log.d("DEBUG_BRO", googleSignInAccount.getDisplayName() + "  " + googleSignInAccount.getEmail() + " " + googleSignInAccount.getGrantedScopes());
        //OK SO FAR
        disconnectBtn = findViewById(R.id.disconnect);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(final View view) {revokeAccess(view.getContext());}
        });

        mStatusTextView = findViewById(R.id.status2);
        mStatusTextView.setText(getString(R.string.signed_in_fmt2, googleSignInAccount.getDisplayName()));

        setDrive();
    }

    private void setDrive()
    {
        // Why it works? https://stackoverflow.com/questions/25031669/passing-the-googleapiclient-obj-from-one-activity-to-another
        // After the login the account will be available throughout the project
        try
        {
            initializeDriveClient(googleSignInAccount);

            OpenFileActivityOptions openOptions = new OpenFileActivityOptions.Builder()
                    //.setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain")) //TODO: select sheet files specifically
                    .setActivityTitle(getString(R.string.select_file)).build();

            Task<DriveId> myRequestedIDTask = pickItem(openOptions);

            //Log.d("Return SOME result", myRequestedIDTask.getResult().encodeToString());
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
                    mCurrentDriveId = data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID); //TODO ok so far!
                    DriveFile mySelectedFile = mCurrentDriveId.asDriveFile();
                    //Log.d("SCOPES", googleSignInAccount.getGrantedScopes().toString());

                    myChosenFileId = mCurrentDriveId.getResourceId();
                    //Log.d("SCOPES", myChosenFileId);
                    Log.d("FILE_TO_OPEN_B4", mySelectedFile.getDriveId().toString());

                    retrieveContents(mySelectedFile);
                    //loadCurrentFile(mySelectedFile);

                    //DriveFile mySelectedFile = getParcelFileDescriptor(mCurrentDriveId);
                    /*
                    mDriveResourceClient.getMetadata(mySelectedFile).continueWithTask(new Continuation<Metadata, Task<DriveContents>>() {
                                @Override
                                public Task<DriveContents> then(@NonNull Task<Metadata> task) {
                                    if (task.isSuccessful()) {
                                        //mMetadata = task.getResult();
                                        return mDriveResourceClient.openFile(mySelectedFile, DriveFile.MODE_READ_ONLY);
                                    } else {
                                        Log.d("NOSUCCESS", "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM" + mDriveContents.toString());
                                        return Tasks.forException(task.getException());
                                    }
                                }
                            }).addOnSuccessListener(new OnSuccessListener<DriveContents>() {
                            @Override
                            public void onSuccess(DriveContents driveContents) {
                                mDriveContents = driveContents;
                                Log.d("WEHAVEFILE", "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ" + mDriveContents.toString());
                                InputStream myFileInputStream = mDriveContents.getInputStream();
                                String myFileContents = myFileInputStream.toString();
                                Log.e("INPUTSTREAM", "CONTENTS FROM INPUT STREAM" + myFileContents);
                                //refreshUiFromCurrentFile();
                            }}).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Unable to retrieve file metadata and contents.", e);
                                }
                            }); */

                    //DriveFile selectedFile = mCurrentDriveId.asDriveFile();
                    //InputStream myOutputFile = null;
                    //String myStringFile = selectedFile.toString();

                    //mDriveContents = null;
                    //DriveContents driveContents;
                    /*
                    try {
                        String contents = Utils.readFromInputStream(mDriveContents.getInputStream());

                        //mContentsEditText.setText(contents);
                    } catch (IOException e) {
                        Log.e(TAG, "IOException while reading from contents input stream", e);
                        //showToast(R.string.msg_errreading);
                        //mSaveButton.setEnabled(false);
                    } */
                    //(driveId.asDriveFile());
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

    private void retrieveContents(DriveFile file) {
        Task<DriveContents> openFileTask =
                Drive.getDriveResourceClient(SheetsListActivity.this, googleSignInAccount).openFile(file, DriveFile.MODE_READ_ONLY);

        openFileTask.continueWithTask(task -> {
            DriveContents contents = task.getResult();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                String resultSheet = builder.toString();
                Log.d("DISPLAY_CONTENTS", resultSheet);
                Intent goToOrdersList = new Intent (SheetsListActivity.this, OrderListActivity.class);
                goToOrdersList.putExtra("file selected", resultSheet);
                startActivity(goToOrdersList);

            }

            Task<Void> discardTask = Drive.getDriveResourceClient(SheetsListActivity.this, googleSignInAccount).discardContents(contents);
            return discardTask;
        })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "UNABLE TO READ CONTENTS", e);
                    //showMessage(getString(R.string.read_failed));
                    finish();
                });
    }

    private void loadCurrentFile(DriveFile file) {
        Log.d(TAG, "Retrieving...");
        // Retrieve and store the file metadata and contents.
        mDriveResourceClient.getMetadata(file)
                .continueWithTask(new Continuation<Metadata, Task<DriveContents>>() {
                    @Override
                    public Task<DriveContents> then(@NonNull Task<Metadata> task) {
                        if (task.isSuccessful()) {
                            mMetadata = task.getResult();
                            Log.d("!!!DISPLAY_CONTENTS", mMetadata.toString());
                            return mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
                        } else {
                            return Tasks.forException(task.getException());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DriveContents>() {
            @Override
            public void onSuccess(DriveContents driveContents) {
                //mDriveContents = driveContents;
                String myStringFile = driveContents.toString();

                Log.d("!!!DISPLAY_CONTENTS", myStringFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to retrieve file metadata and contents.", e);
            }
        });
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


/*
    private void refreshUiFromCurrentFile() {
        Log.d(TAG, "Refreshing...");
        if (mCurrentDriveId == null) {
            //mSaveButton.setEnabled(false);
            return;
        }
        if (mMetadata == null || mDriveContents == null) {
            return;
        }

        try {
            InputStream myFileInputStream = mDriveContents.getInputStream();
            String myFileContents = myFileInputStream.toString();
            Log.e("INPUTSTREAM", "CONTENTS FROM INPUT STREAM" + myFileContents);

            //String contents = Utils.readFromInputStream(mDriveContents.getInputStream());
            //mContentsEditText.setText(contents);
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading from contents input stream", e);
            //showToast(R.string.msg_errreading);
            //mSaveButton.setEnabled(false);
        }
    }

    */