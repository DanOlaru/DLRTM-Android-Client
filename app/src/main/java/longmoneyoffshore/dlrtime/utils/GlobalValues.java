package longmoneyoffshore.dlrtime.utils;

import com.google.android.gms.maps.model.LatLng;

import longmoneyoffshore.dlrtime.utils.TransportClients.Client;

public class GlobalValues {
    public static final String APP_API_KEY = "AIzaSyDCos2tFqSvd80yDcIZgl1_x9Zk1zWQ1RI";
    public static final String US_ANONYMIZER_PREFIX = "*67";

    public static final LatLng ChicagoLocale = new LatLng(41.8781, -87.6298);
    public static final String THE_FIRST_DOWNLOAD_SHEET = "https://spreadsheets.google.com/tq?key=16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY";

    public static final int RC_SIGN_IN = 9001;
    public static final int RC_SIGN_OUT = 9002;
    public static final int RC_RECOVERABLE = 9003;
    //public static final int RC_RECOVERABLE = 9002;

    public static final int REQUEST_CODE_SIGN_IN = 0;
    public static final int REQUEST_CODE_SIGN_OUT = 5;

    //write back data to Google Sheets
    public static final int UPDATE_FIELD = 2001;
    public static final int APPEND_FIELD = 2002;
    public static final int DELETE_FIELD = 2003;

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    //OrderListActivity & IndividualClientActivity
    public static final int INDIVIDUAL_ORDER_CHANGED=1005;
    public static final int CLICK_INDIVIDUAL_ORDER = 1006;
    public static final int CREATE_NEW_ORDER=1007;

    //
    public final String dummyFileID = "16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY";
    public final static Client blankClient = new Client("N/A","N/A","N/A","N/A",0,0,0,0,0,"N/A");

}
