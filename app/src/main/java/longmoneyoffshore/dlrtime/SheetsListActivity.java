package longmoneyoffshore.dlrtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResourceClient;

public class SheetsListActivity extends AppCompatActivity
{

    //dummy
    private int temp;
    
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private DriveFile driveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheets_list_activity);

        // Get the access for user google drive
        setDrive();


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
