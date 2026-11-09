package com.example.project_1220006_1220204;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "NOTE_APP_DB";
    private static final int DB_VERSION = 2;
    private static final String TABLE_USER = "USER";
    private static final String COLUMN_EMAIL = "EMAIL";
    private static final String COLUMN_FIRST_NAME = "FIRST_NAME";
    private static final String COLUMN_LAST_NAME = "LAST_NAME";
    private static final String COLUMN_PASSWORD = "PASSWORD";



    private static final String TABLE_NOTE = "NOTE";

    private static final String COLUMN_NOTE_ID = "ID";
    private static final String COLUMN_NOTE_EMAIL = "EMAIL";
    private static final String COLUMN_NOTE_TITLE = "TITLE";
    private static final String COLUMN_NOTE_CONTENT = "CONTENT";
    private static final String COLUMN_NOTE_DATE = "CREATION_DATE";
    private static final String COLUMN_NOTE_FAVORITE = "FAVORITE";



    private static final String TABLE_TAG = "TAG";
    private static final String COLUMN_TAG_ID = "ID";
    private static final String COLUMN_TAG_TITLE = "TITLE";
    private static final String COLUMN_NOTE_TAG_ID = "TAG_ID";


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create one USER table with EMAIL as the primary key
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_USER + " (" +
                        COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                        COLUMN_FIRST_NAME + " TEXT, " +
                        COLUMN_LAST_NAME + " TEXT, " +
                        COLUMN_PASSWORD + " TEXT)"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_TAG + " (" +
                        COLUMN_TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TAG_TITLE + " TEXT NOT NULL UNIQUE)"
        );

        // create new note table with the foreign key email
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_NOTE + " (" +
                        COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOTE_EMAIL + " TEXT NOT NULL, " +
                        COLUMN_NOTE_TITLE + " TEXT, " +
                        COLUMN_NOTE_CONTENT + " TEXT, " +
                        COLUMN_NOTE_DATE + " TEXT, " +
                        COLUMN_NOTE_FAVORITE + " INTEGER, " +
                        COLUMN_NOTE_TAG_ID + " INTEGER, " +

                        "FOREIGN KEY (" + COLUMN_NOTE_EMAIL + ") REFERENCES " +
                        TABLE_USER + "(" + COLUMN_EMAIL + "), " +

                        "FOREIGN KEY (" + COLUMN_NOTE_TAG_ID + ") REFERENCES " +
                        TABLE_TAG + "(" + COLUMN_TAG_ID + ") " +
                        "ON DELETE SET NULL)"
        );
    }

    // this is used to enable foreign keys
    @Override
    public void onConfigure(SQLiteDatabase sqLiteDatabase) {
        super.onConfigure(sqLiteDatabase);
        sqLiteDatabase.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(sqLiteDatabase);
    }

    //Returns true if the insert worked, and false if something went wrong
    //like trying to insert an email that already exists....
    public boolean insertUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, user.getmEmail());
        contentValues.put(COLUMN_FIRST_NAME, user.getmFirstName());
        contentValues.put(COLUMN_LAST_NAME, user.getmLastName());
        contentValues.put(COLUMN_PASSWORD, user.getmPassword());

        // insert() returns -1 if the row could not be inserted
        long result = sqLiteDatabase.insert(TABLE_USER, null, contentValues);
        return result != -1;
    }

    //Checks if a given email is already registered
    //if so, show "this email is already registered"
    public boolean isEmailRegistered(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + " = ?",
                new String[]{email}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    //Checks whether the given email/password pair matches a row in the USER table
    public boolean isValidLogin(String email, String password) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE " +
                        COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{email, password}
        );

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    //used to show first and last names in profile section
    public User getUserByEmail(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + " = ?",
                new String[]{email}
        );

        User user = null;
        if (cursor.moveToFirst()) {
            // EMAIL, FIRST_NAME, LAST_NAME, PASSWORD
            user = new User(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        }
        cursor.close();
        return user;
    }

    //Saves changes made on the Profile screen
    //email is never changed (primary key)
    public boolean updateUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FIRST_NAME, user.getmFirstName());
        contentValues.put(COLUMN_LAST_NAME, user.getmLastName());
        contentValues.put(COLUMN_PASSWORD, user.getmPassword());

        int rowsAffected = sqLiteDatabase.update(
                TABLE_USER,
                contentValues,
                COLUMN_EMAIL + " = ?",
                new String[]{user.getmEmail()}
        );

        return rowsAffected > 0;
    }

    // insert new note
    public void insertNote(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NOTE_EMAIL, note.getUserEmail());
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_NOTE_DATE, note.getCreationDate());
        contentValues.put(COLUMN_NOTE_FAVORITE, note.isFavorite() ? 1 : 0);
        if (note.getTagId() > 0){
            contentValues.put(COLUMN_NOTE_TAG_ID, note.getTagId());
        }
        else{
            contentValues.putNull(COLUMN_NOTE_TAG_ID);
        }

        sqLiteDatabase.insert(TABLE_NOTE, null, contentValues);
    }

    public Cursor getNotesByEmail(String email) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        return sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTE +
                        " WHERE " + COLUMN_NOTE_EMAIL + " = ?",
                new String[]{email}
        );
    }

    public Cursor getFavNotesByEmail(String email) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        return sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTE +
                        " WHERE " + COLUMN_NOTE_EMAIL + " = ?" +
                        " AND " + COLUMN_NOTE_FAVORITE + " = 1",
                new String[]{email}
        );
    }

    public Note getNoteById(int id){

        SQLiteDatabase sqLiteDatabase = getReadableDatabase() ;

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTE +
                        " WHERE " + COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        Note note = null ;

        if (cursor.moveToFirst()){
            note = new Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5) == 1,
                    cursor.isNull(6) ? 0 : cursor.getInt(6)
            );
        }

        cursor.close() ;
        return note ;
    }

    public void updateNote(Note note) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_NOTE_DATE, note.getCreationDate());
        contentValues.put(COLUMN_NOTE_FAVORITE, note.isFavorite() ? 1 : 0);
        if (note.getTagId() > 0){
            contentValues.put(COLUMN_NOTE_TAG_ID, note.getTagId());
        }
        else{
            contentValues.putNull(COLUMN_NOTE_TAG_ID);
        }

        sqLiteDatabase.update(
                TABLE_NOTE,
                contentValues,
                COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(note.getId())}
        );
    }

    public void deleteNote(int noteId, String email) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(
                TABLE_NOTE,
                COLUMN_NOTE_ID + " = ? AND " + COLUMN_NOTE_EMAIL + " = ?",
                new String[]{String.valueOf(noteId), email}
        );
    }

    public Cursor searchForNotes(String email, String searchText, String searchInOption, String sortByOption, int tagId , boolean favoriteOnly){

        if (searchText == null){
            searchText = "" ;
        }

        if (searchInOption == null){
            searchInOption = "Any" ;
        }

        if (sortByOption == null){
            sortByOption = "" ;
        }

        // convert to lower case because contains() function accepts only lower case
        searchText = searchText.trim() ;
        searchInOption = searchInOption.toLowerCase() ;
        sortByOption = sortByOption.toLowerCase() ;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NOTE +
                " WHERE " + COLUMN_NOTE_EMAIL + " = ?" ;

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(email);

        if (favoriteOnly){
            query += " AND " + COLUMN_NOTE_FAVORITE + " = ?" ;
            arguments.add("1");
        }

        if (tagId > 0){
            query += " AND " + COLUMN_NOTE_TAG_ID + " = ?" ;
            arguments.add(String.valueOf(tagId));
        }

        if (!searchText.isEmpty()){

            String searchValue = "%" + searchText + "%" ;

            if (searchInOption.equalsIgnoreCase("Title")){

                query += " AND " + COLUMN_NOTE_TITLE + " LIKE ?" ;
                arguments.add(searchValue);
            }
            else if (searchInOption.equalsIgnoreCase("Content")){

                query += " AND " + COLUMN_NOTE_CONTENT + " LIKE ?" ;
                arguments.add(searchValue);
            }
            else{ // Any

                query += " AND (" + COLUMN_NOTE_TITLE + " LIKE ? OR " + COLUMN_NOTE_CONTENT + " LIKE ?)";

                arguments.add(searchValue);
                arguments.add(searchValue);
            }
        }

        String selectedSortOption =
                sortByOption == null ? "" : sortByOption.toLowerCase();

        if (
                selectedSortOption.equals("title: a - z") ||
                selectedSortOption.equals("title: z - a")
        ){

            // collate nocase means to ignore case
            query += " ORDER BY " + COLUMN_NOTE_TITLE + " COLLATE NOCASE" ;

            if (selectedSortOption.equals("title: z - a")) {
                query += " DESC" ;
            }
            else{
                query += " ASC";
            }
        }
        else{ // date

            String dateExpression =
                    "substr(" + COLUMN_NOTE_DATE + ", 7, 4) || " + // year
                            "substr(" + COLUMN_NOTE_DATE + ", 4, 2) || " + // month
                            "substr(" + COLUMN_NOTE_DATE + ", 1, 2)" ; // day

            query += " ORDER BY " + dateExpression ;

            if (selectedSortOption.equals("newest first")) {
                query += " DESC" ;
            }
            else {
                query += " ASC" ;
            }
        }

        return sqLiteDatabase.rawQuery(query, arguments.toArray(new String[0]));
    }

    public int insertTag(String title) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TAG_TITLE, title.trim());

        long result = sqLiteDatabase.insert(TABLE_TAG, null, contentValues);

        return (int) result; // result is either the id or -1 in case of failuer
    }

    public int getTagIdByTitle(String title) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT " + COLUMN_TAG_ID +
                        " FROM " + TABLE_TAG +
                        " WHERE " + COLUMN_TAG_TITLE +
                        " = ? COLLATE NOCASE",
                new String[]{title.trim()}
        );

        int tagId = 0;
        if (cursor.moveToFirst()) {
            tagId = cursor.getInt(0);
        }

        cursor.close();

        return tagId; // return the tagid if it exists. If does not, return zero
    }

    public String getTagTitleById(int tagId) {

        if (tagId <= 0) {
            return "";
        }

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT " + COLUMN_TAG_TITLE +
                        " FROM " + TABLE_TAG +
                        " WHERE " + COLUMN_TAG_ID + " = ?",
                new String[]{String.valueOf(tagId)}
        );

        String title = "";

        if (cursor.moveToFirst()){
            title = cursor.getString(0);
        }

        cursor.close();

        return title;
    }

    public Cursor getNotesByTag(String email, int tagId) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        return sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTE +
                        " WHERE " + COLUMN_NOTE_EMAIL + " = ?" +
                        " AND " + COLUMN_NOTE_TAG_ID + " = ?",
                new String[]{
                        email,
                        String.valueOf(tagId)
                }
        );
    }

    public ArrayList<Tag> getAllTags() {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT " + COLUMN_TAG_ID + ", " +
                        COLUMN_TAG_TITLE +
                        " FROM " + TABLE_TAG +
                        " ORDER BY " + COLUMN_TAG_TITLE +
                        " COLLATE NOCASE ASC",
                null
        );

        ArrayList<Tag> tags = new ArrayList<>();

        while (cursor.moveToNext()) {
            tags.add(new Tag(cursor.getInt(0), cursor.getString(1)));
        }

        cursor.close();

        return tags;
    }
}
