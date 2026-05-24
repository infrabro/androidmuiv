package ru.muiv.notes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.muiv.notes.model.Note;

public class NoteRepository {
    private NotesDbHelper dbHelper;

    public NoteRepository(Context context) {
        dbHelper = new NotesDbHelper(context);
    }

    public List<Note> getAllNotes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Note> notes = new ArrayList<Note>();

        Cursor cursor = db.query(
                NotesDbHelper.TABLE_NOTES,
                null,
                NotesDbHelper.COLUMN_DELETED + " = 0",
                null,
                null,
                null,
                NotesDbHelper.COLUMN_UPDATED_AT + " DESC"
        );

        while (cursor.moveToNext()) {
            notes.add(cursorToNote(cursor));
        }

        cursor.close();
        return notes;
    }

    public List<Note> searchNotes(String query) {
        if (query == null || query.trim().length() == 0) {
            return getAllNotes();
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Note> notes = new ArrayList<Note>();

        String likeQuery = "%" + query.trim() + "%";

        Cursor cursor = db.query(
                NotesDbHelper.TABLE_NOTES,
                null,
                NotesDbHelper.COLUMN_DELETED + " = 0 AND (" +
                        NotesDbHelper.COLUMN_TITLE + " LIKE ? OR " +
                        NotesDbHelper.COLUMN_CONTENT + " LIKE ? OR " +
                        NotesDbHelper.COLUMN_GROUP_NAME + " LIKE ? OR " +
                        NotesDbHelper.COLUMN_TAGS + " LIKE ?" +
                        ")",
                new String[]{likeQuery, likeQuery, likeQuery, likeQuery},
                null,
                null,
                NotesDbHelper.COLUMN_UPDATED_AT + " DESC"
        );

        while (cursor.moveToNext()) {
            notes.add(cursorToNote(cursor));
        }

        cursor.close();
        return notes;
    }

    public Note getNoteById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                NotesDbHelper.TABLE_NOTES,
                null,
                NotesDbHelper.COLUMN_ID + " = ? AND " + NotesDbHelper.COLUMN_DELETED + " = 0",
                new String[]{id},
                null,
                null,
                null
        );

        Note note = null;

        if (cursor.moveToFirst()) {
            note = cursorToNote(cursor);
        }

        cursor.close();
        return note;
    }

    public String createNote(
            String title,
            String content,
            String groupName,
            String tags,
            String author,
            String imagePath
    ) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String id = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(NotesDbHelper.COLUMN_ID, id);
        values.put(NotesDbHelper.COLUMN_TITLE, title);
        values.put(NotesDbHelper.COLUMN_CONTENT, content);
        values.put(NotesDbHelper.COLUMN_GROUP_NAME, groupName);
        values.put(NotesDbHelper.COLUMN_TAGS, tags);
        values.put(NotesDbHelper.COLUMN_AUTHOR, author);
        values.put(NotesDbHelper.COLUMN_LAST_EDITOR, author);
        values.put(NotesDbHelper.COLUMN_IMAGE_PATH, imagePath);
        values.put(NotesDbHelper.COLUMN_UPDATED_AT, currentTime);
        values.put(NotesDbHelper.COLUMN_DELETED, 0);

        db.insert(NotesDbHelper.TABLE_NOTES, null, values);
        return id;
    }

    public void updateNote(
            String id,
            String title,
            String content,
            String groupName,
            String tags,
            String lastEditor,
            String imagePath
    ) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesDbHelper.COLUMN_TITLE, title);
        values.put(NotesDbHelper.COLUMN_CONTENT, content);
        values.put(NotesDbHelper.COLUMN_GROUP_NAME, groupName);
        values.put(NotesDbHelper.COLUMN_TAGS, tags);
        values.put(NotesDbHelper.COLUMN_LAST_EDITOR, lastEditor);
        values.put(NotesDbHelper.COLUMN_IMAGE_PATH, imagePath);
        values.put(NotesDbHelper.COLUMN_UPDATED_AT, System.currentTimeMillis());

        db.update(
                NotesDbHelper.TABLE_NOTES,
                values,
                NotesDbHelper.COLUMN_ID + " = ?",
                new String[]{id}
        );
    }

    public void deleteNote(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesDbHelper.COLUMN_DELETED, 1);
        values.put(NotesDbHelper.COLUMN_UPDATED_AT, System.currentTimeMillis());

        db.update(
                NotesDbHelper.TABLE_NOTES,
                values,
                NotesDbHelper.COLUMN_ID + " = ?",
                new String[]{id}
        );
    }

    private Note cursorToNote(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_TITLE));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_CONTENT));
        String groupName = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_GROUP_NAME));
        String tags = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_TAGS));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_AUTHOR));
        String lastEditor = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_LAST_EDITOR));
        String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_IMAGE_PATH));
        long updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_UPDATED_AT));

        return new Note(id, title, content, groupName, tags, author, lastEditor, imagePath, updatedAt);
    }
}