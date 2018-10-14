package longmoneyoffshore.dlrtime.utils;

import com.google.android.gms.maps.model.LatLng;

public class GlobalValues {
    public static final String APP_API_KEY = "AIzaSyDCos2tFqSvd80yDcIZgl1_x9Zk1zWQ1RI";
    public static final String US_ANONYMIZER_PREFIX = "*67";

    public static final LatLng ChicagoLocale = new LatLng(41.8781, -87.6298);
    public static final String THE_FIRST_DOWNLOAD_SHEET = "https://spreadsheets.google.com/tq?key=16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY";

    public static final int RC_SIGN_IN = 9001;
    public static final int RC_RECOVERABLE = 9002;

    public static final int REQUEST_CODE_SIGN_IN = 0;
    public static final int REQUEST_CODE_SIGN_OUT = 5;

    //write back data to Google Sheets
    public static final int UPDATE_FIELD = 2001;
    public static final int APPEND_FIELD = 2002;

}
