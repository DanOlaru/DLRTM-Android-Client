package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class SheetsListActivity extends AppCompatActivity //or Activity???
{



    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private DriveFile driveFile;

    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;



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
        setContentView(R.layout.activity_sheets_list);

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
            mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
            mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));

            ///////////////////////////////by Dan

            OpenFileActivityOptions openOptions = new OpenFileActivityOptions.Builder()
                    .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                    .setActivityTitle(getString(R.string.select_file)).build();

            //Task<DriveId> myRequestedIDTask = pickItem(openOptions);

            mDriveClient.newOpenFileActivityIntentSender(openOptions).continueWith((Continuation<IntentSender, Void>) task -> {
                startIntentSenderForResult(
                        task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                return null;
                });

            Task<DriveId> myRequestedIDTask = mDriveClient.getDriveId("text/plain");
            String mySheetId = myRequestedIDTask.getResult().encodeToString();
            Log.d("pick a file ID", mySheetId);

            //get the id of the sheets file I want to use and pass it to OrderListActivity
            /////////////////////////////////end by Dan

        }
        catch (Exception e)
        {
            Log.e("Google drive access","Error: cannot get drive access!");
        }
    }
/*
    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
                mDriveClient.newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) task -> {
                    startIntentSenderForResult(
                            task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                    return null;
                });
        return mOpenItemTaskSource.getTask();
    }

    */

}
