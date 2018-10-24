package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import android.app.Activity;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.REQUEST_CODE_SIGN_OUT;


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
    private static final String TAG = "SheetsListActivity";
    private static final String MIME_TYPE_TEXT = "text/plain";
    private static final int REQ_PICKFILE = 4;

    private TaskCompletionSource<DriveId> mOpenItemTaskSource;

    // Variables for managing the connection
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

        gso = GoogleSignInOptions.DEFAULT_SIGN_IN;
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

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
        try
        {
            initializeDriveClient(googleSignInAccount);

            OpenFileActivityOptions openOptions = new OpenFileActivityOptions.Builder()
                    .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/vnd.google-apps.spreadsheet"))
                    .setActivityTitle(getString(R.string.select_file)).build();

            Task<DriveId> myRequestedIDTask = pickItem(openOptions);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String myChosenFileId;

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal.
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
                    mCurrentDriveId = data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    DriveFile mySelectedFile = mCurrentDriveId.asDriveFile();
                    myChosenFileId = mCurrentDriveId.getResourceId();

                    Intent goToOrdersList = new Intent (SheetsListActivity.this, OrderListActivity.class);
                    goToOrdersList.putExtra("file selected", myChosenFileId);
                    startActivityForResult(goToOrdersList,requestCode);

                } else if (resultCode == REQUEST_CODE_SIGN_OUT) {
                    revokeAccess(SheetsListActivity.this);
                }
                else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }

                break;

            case REQUEST_CODE_SIGN_OUT:
                if (resultCode == RESULT_OK) {
                    //revokeAccess(SheetsListActivity.this);
                    Intent intent = new Intent(SheetsListActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected DriveClient getDriveClient() {
        return mDriveClient;
    }

    private void revokeAccess(final Context context) {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }

}