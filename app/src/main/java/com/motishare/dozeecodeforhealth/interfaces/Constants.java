package com.motishare.dozeecodeforhealth.interfaces;

public interface Constants {

    /*String baseURL = "https://6c829492-4c01-4a59-bbb3-801076aa173d.mock.pstmn.io/api/";*/
    String baseURL = "https://f2a8b123-adbb-4c6a-beba-f3d3d42eea86.mock.pstmn.io/api/";



    String ANDROID = "ANDROID";
    String APP = "Dozee Health";
    String DEVICE_TYPE = ANDROID;
    String USER_PUSH_TOKEN = "user_push_token";

    String IS_USER_LOGGED_IN = "is_user_logged_in";

    /* variables used for loaders*/
    enum LOADER_TYPE {
        NORMAL, SWIPE, PAGINATION
    }

    int PERMISSION_REQUEST_CODE_CG = 100;

    int PERMISSION_REQUEST_CODE_LOCATION = 44;

    String DATE = "yyyyMMdd_HHmmss";
    String DATE2 = "dd-MM-yyyy";
    String DATE3 = "dd MMM yyyy , hh:mm a";
    String DATE4 = "dd MMMM yyyy";
    String DATE5 = "dd MM yyyy";
    String DATE6 = "yyyy-MM-dd";
    String DATE7 = "dd/MM/yyyy";
    String DATE8 = "MMMM";
    String DATE9 = "yyyy";
    String DATE10= "MM";
    String DATE11 = "yyyy-MM-dd,HH:mm";
    String DATE12 = "yyyy/MM/dd";
    String DATE13 = "yyyy-MM-dd HH:mm:ss";
    String DATE14 = "MMM dd yyyy, hh:mm a";
    String DATE15 = "E dd MMM yyyy";
    String DATE16 = "MMM dd, yyyy";
    String DATE17 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    String DATE18 = "MMM yyyy";
    String DATE19 = "MMM dd yyyy HH:mm:ss";
    String DATE20 = "EEE MMM dd HH:mm:ss Z yyyy";
    String DATE21 = "dd MMM";
    String DATE22 = "yyyy";


    String TIME = "HH:mm:ss";
    String TIME2 = "hh:mm a";
    String TIME3 = "HH:mm";

    //SHARED PREFRENCE KEYS
    String ATTENDANCESTATUS = "Attendance Status";
    String LOCATIONADDRESS = "Current Address";

    int SPLASH_TIMER = 3000;
    String SUCCESS = "success";
    String ERROR = "error";


}