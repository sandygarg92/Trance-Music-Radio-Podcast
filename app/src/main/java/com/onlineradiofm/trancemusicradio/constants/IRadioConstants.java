package com.onlineradiofm.trancemusicradio.constants;


import android.Manifest;

import com.onlineradiofm.trancemusicradio.R;

public interface IRadioConstants {

    boolean DEBUG = false;

    String TAG = "DCM";

    //TODO MUST UPDATE these params for each application
    String ANDROID_10_DIR_PUBLIC_DOWNLOAD = "TranceRadio";
    String ANDROID_10_DIR_PUBLIC_RECORD = "RadioTranceRecording";

    String DIR_CACHE = "trancemusic";
    String ANDROID9_DIR_RECORDS = "records";
    String ANDROID9_DIR_DOWNLOADED = "downloaded";

    String DOWNLOAD_SEPARATOR = "_#_";
    String[] LIST_STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    String[] LIST_STORAGE_PERMISSIONS_13 = {
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    };
    String[] LIST_STORAGE_PERMISSIONS_14 = {
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            Manifest.permission.POST_NOTIFICATIONS
    };

    String FOLDER_API = "/apiV2/";
    String KEY_OPEN_ADS_FREQ = "open_ads_freq";

    long TIME_OUT_MAX_TIME_WAIT = 6000;
    long FIREBASE_CACHE_EXPIRATION = 1800; //second , 0.5 hour

    int LIMIT_ITUNES_NORMAL = 200; // it must to be in range (0 - 200]

    //TODO CHROME CAST PARAMS
    int PRELOAD_TIME_S = 20;
    String URL_IMAGE_DEFAULT_FOR_CHROME_CAST = "https://lh3.googleusercontent.com/iha5ezPoBN0otT_6nud42HL6Kc2rECmRKhlEC7hX_LttKveVMrkOQA_B6lHgrruhiQ=s360-rw";
    int ACTION_PLAY_NOW = 1;
    int ACTION_PLAY_NEXT = 2;
    int ACTION_ADD_QUEUE = 3;

    long DARK_MODE_THEME_ID = -1;
    long LIGHT_MODE_THEME_ID = 1;

    String DARK_MODE_BG_COLOR = "#000000";

    int NATIVE_FREQ = 9; // frequency native ads
    boolean IS_SMALL_NATIVE = false;

    int TOP_ITEM_PER_PAGE = 10; // if you want to edit this param, you need to edit MAX_ITEM_TOP in xradio_constants.php
    String TYPE_EDITOR_CHOICE = "editor";
    String TYPE_NEW_RELEASE = "new_release";

    String TYPE_COUNT_FAV = "fav";
    String TYPE_COUNT_VIEW = "view";

    int ID_EDITOR_CHOICE = -1;

    int MINIMUM_DELTA_RECORD = 2000; // 5 seconds
    int MAXIMUM_DELTA_RECORD = 240; // 20 minutes

    String YOUR_CONTACT_EMAIL = "appsoup.com@gmail.com";
    String URL_WEBSITE = "";

    String URL_PRIVACY_GOOGLE = "https://www.appsoup.com/apps-privacy-policy/?applink=applink";
    String URL_TERM_OF_USE = "https://www.appsoup.com/app-terms-conditions/?applink=applink";
    String URL_PRIVACY_POLICY = "https://www.appsoup.com/apps-privacy-policy/?applink=applink";
    String URL_MORE_APPS = "https://www.appsoup.com/radio-apps-online-android/?applink=applink";
    String URL_CHANGELOG = "";
    String URL_FAQ = "";

    boolean SHOW_ADS = true; //enable all ads
    boolean SHOW_NATIVE_ADS = true; //enable native ads
    boolean SHOW_SPLASH_INTERSTITIAL_ADS = true; //enable interstitial splash ads

    int INTERSTITIAL_FREQUENCY = 5; //click each item radio to show this one

    String ADMOB_TEST_DEVICE = "D4BE0E7875BD1DDE0C1C7C9CF169EB6E";
    String FACEBOOK_TEST_DEVICE = "D4BE0E7875BD1DDE0C1C7C9CF169EB6E";

    int NUMBER_ITEM_PER_PAGE = 50;
    int MAX_PAGE = 100;

    int TYPE_TAB_LIVE = 2;
    int TYPE_TAB_SEARCH = 3;
    int TYPE_TAB_SETTING = 4;
    int TYPE_TAB_FAVORITE = 5;
    int TYPE_TAB_LIBRARIES = 6;
    int TYPE_DETAIL_GENRE = 7;
    int TYPE_DETAIL_GENREPOD = 7;
    int TYPE_SEARCH = 8;
    int TYPE_DETAIL_COUNTRY = 13;
    int TYPE_DETAIL_TOP = 15;
    int TYPE_TAB_PODCAST = 20;
    int TYPE_DETAIL_PODCAST = 17;
    int TYPE_USER_FAV_RADIOS = 18;
    int TYPE_MY_DOWNLOAD = 19;
    int TYPE_LAST_PLAYED_RADIO = 20;
    int TYPE_ADD_RADIO = 21;
    int TYPE_MY_RADIO = 22;
    int TYPE_PODCAST_DOWNLOADED = 23;

    String KEY_ALLOW_MORE = "allow_more";
    String KEY_IS_TAB = "is_tab";
    String KEY_TYPE_FRAGMENT = "type";
    String KEY_ALLOW_READ_CACHE = "read_cache";
    String KEY_ALLOW_REFRESH = "allow_refresh";
    String KEY_ALLOW_SHOW_HEADER = "allow_show_no_data";
    String KEY_READ_CACHE_WHEN_NO_DATA = "cache_when_no_data";
    String KEY_GENRE_ID = "cat_id";
    String KEY_COUNTRY_ID = "country_id";
    String KEY_SEARCH = "search_data";
    String KEY_TYPE_TOP = "type_top";
    String KEY_MODEL = "model";

    String KEY_NUMBER_ITEM_PER_PAGE = "number_item_page";
    String KEY_MAX_PAGE = "max_page";
    String KEY_OFFLINE_DATA = "offline_data";
    int UI_FLAT_GRID = 1;
    int UI_FLAT_LIST = 2;
    int UI_CARD_GRID = 3;
    int UI_CARD_LIST = 4;
    int UI_MAGIC_GRID = 5;

    String TAG_FRAGMENT_DETAIL_GENRE = "TAG_FRAGMENT_DETAIL_GENRE";
    String TAG_FRAGMENT_DETAIL_TOP_MODEL = "TAG_FRAGMENT_DETAIL_TOP_MODEL";
    String TAG_FRAGMENT_DETAIL_SEARCH = "TAG_FRAGMENT_DETAIL_SEARCH";
    String TAG_FRAGMENT_DETAIL_COUNTRY = "TAG_FRAGMENT_DETAIL_COUNTRY";
    String TAG_FRAGMENT_PROFILE = "TAG_FRAGMENT_PROFILE";
    String TAG_FRAGMENT_DETAIL_PODCAST = "TAG_FRAGMENT_DETAIL_PODCAST";
    String TAG_FRAGMENT_USER_FAV = "TAG_FRAGMENT_USER_FAV";
    String TAG_FRAGMENT_DOWNLOAD = "TAG_FRAGMENT_DOWNLOAD";
    String TAG_FRAGMENT_ADD_RADIO = "TAG_FRAGMENT_ADD_RADIO";
    String TAG_FRAGMENT_MY_RADIO = "TAG_FRAGMENT_MY_RADIO";

    boolean ALLOW_DRAG_DROP_WHEN_EXPAND = true;

    int MAX_SLEEP_MODE = 240;
    int MIN_SLEEP_MODE = 5;
    int STEP_SLEEP_MODE = 5;

    //params for recording function
    String DIR_TEMP = ".temp";
    String FORMAT_SAVED = ".mp3";
    String RECORD_TEMP_FILE = "temp_record";
    String DATE_PATTERN = "yyyyMMdd_HHmmss";

    String PREFIX_CONTENT = "content://";

    int TYPE_BASIC_MEMBER = 1;
    int TYPE_PRO_MEMBER = 2;
    int TYPE_GOLD_MEMBER = 3;
    int TYPE_VIP_MEMBER = 4;

    int[] IMG_MEMBERS = {R.drawable.ic_medal_1month_60dp, R.drawable.ic_medal_3months_60dp, R.drawable.ic_medal_6months_60dp, R.drawable.ic_medal_1year_60dp};
    int[] TYPE_MEMBERS = {TYPE_BASIC_MEMBER, TYPE_PRO_MEMBER, TYPE_GOLD_MEMBER, TYPE_VIP_MEMBER};

    long DELTA_TIME_CHECK_VIEW = 30000; // 30 seconds

    int STATUS_BANNED_ACCOUNT = 407;
    int STATUS_INVALID_ACCOUNT = 409;
    int STATUS_WRONG_USER_PASS = 209;

    int MAX_LENGTH_USER_NAME = 20;
    int MAX_LENGTH_TITLE = 25;

    int RATE_EFFECT = 10;
}
