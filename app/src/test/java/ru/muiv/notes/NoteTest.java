package ru.muiv.notes;

import org.junit.Test;

import ru.muiv.notes.model.Note;

import static org.junit.Assert.assertEquals;

public class NoteTest {

    @Test
    public void noteStoresTitleCorrectly() {
        Note note = new Note(
                "1",
                "Первая заметка",
                "Текст заметки",
                "Учеба",
                "java, android",
                "Anton",
                "Anton",
                "",
                1000L
        );

        assertEquals("Первая заметка", note.getTitle());
    }

    @Test
    public void noteStoresGroupAndTagsCorrectly() {
        Note note = new Note(
                "2",
                "Android",
                "Описание",
                "MUИВ",
                "mobile, sqlite",
                "Anton",
                "Anton",
                "",
                2000L
        );

        assertEquals("MUИВ", note.getGroupName());
        assertEquals("mobile, sqlite", note.getTags());
    }

    @Test
    public void noteStoresImagePathCorrectly() {
        Note note = new Note(
                "3",
                "Заметка с картинкой",
                "Описание картинки",
                "Лабораторная",
                "image",
                "Anton",
                "Anton",
                "/data/user/0/ru.muiv.notes/files/images/test.jpg",
                3000L
        );

        assertEquals(
                "/data/user/0/ru.muiv.notes/files/images/test.jpg",
                note.getImagePath()
        );
    }
}