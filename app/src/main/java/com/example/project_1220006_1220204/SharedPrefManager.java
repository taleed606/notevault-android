package com.example.project_1220006_1220204;

import android.content.Context;
import android.content.SharedPreferences;


 //Singleton class for reading or writing SharedPreferences
 //for remembering the user's email address

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "NoteAppPreferences";
    private static final int SHARED_PREF_PRIVATE = Context.MODE_PRIVATE;

    //key to store or retrieve the remembered email
    private static final String KEY_REMEMBERED_EMAIL = "rememberedEmail";
    private static final String KEY_SORT_OPTION = "sortOption";
    private static SharedPrefManager ourInstance = null;
    private static SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, SHARED_PREF_PRIVATE);
        editor = sharedPreferences.edit();
    }

    static SharedPrefManager getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }
        ourInstance = new SharedPrefManager(context);
        return ourInstance;
    }

    public boolean writeString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }

    public String readString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void rememberEmail(String email) {
        writeString(KEY_REMEMBERED_EMAIL, email);
    }

    public void forgetEmail() {
        //just writing an empty string back over the old value
        writeString(KEY_REMEMBERED_EMAIL, "");
    }

    public String getRememberedEmail() {
        return readString(KEY_REMEMBERED_EMAIL, "");
    }

    // we need to concat the key with the email since we need a unique key for each use
    // ************************** CHECK IF WE NEED TO DO THIS FOR THE REMEBMEBR EMAIL
    public void saveSortOption(String email, String sortOption) {
        writeString(KEY_SORT_OPTION + "_" + email, sortOption);
    }

    public String getSortOption(String email) {
        return readString(
                KEY_SORT_OPTION + "_" + email,
                "Newest first"
        );
    }

}
