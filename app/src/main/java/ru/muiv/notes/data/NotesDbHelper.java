package ru.muiv.notes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NOTES = "notes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_GROUP_NAME = "group_name";
    public static final String COLUMN_TAGS = "tags";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_LAST_EDITOR = "last_editor";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_DELETED = "deleted";

    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT NOT NULL, " +
                COLUMN_GROUP_NAME + " TEXT, " +
                COLUMN_TAGS + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_LAST_EDITOR + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT, " +
                COLUMN_UPDATED_AT + " INTEGER, " +
                COLUMN_DELETED + " INTEGER DEFAULT 0" +
                ");";

        db.execSQL(createNotesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NOTES + " ADD COLUMN " + COLUMN_IMAGE_PATH + " TEXT");
        }
    }
}