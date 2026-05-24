package ru.muiv.notes;

import org.junit.Test;

import ru.muiv.notes.util.MarkdownFormatter;

import static org.junit.Assert.assertEquals;

public class MarkdownFormatterTest {

    @Test
    public void markdownFormatterRemovesFirstLevelHeader() {
        String markdown = "# Заголовок";
        String result = MarkdownFormatter.toPlainText(markdown);

        assertEquals("Заголовок", result);
    }

    @Test
    public void markdownFormatterRemovesSecondLevelHeader() {
        String markdown = "## Подзаголовок";
        String result = MarkdownFormatter.toPlainText(markdown);

        assertEquals("Подзаголовок", result);
    }

    @Test
    public void markdownFormatterRemovesBoldMarkup() {
        String markdown = "**Жирный текст**";
        String result = MarkdownFormatter.toPlainText(markdown);

        assertEquals("Жирный текст", result);
    }

    @Test
    public void markdownFormatterRemovesItalicMarkup() {
        String markdown = "*Курсив*";
        String result = MarkdownFormatter.toPlainText(markdown);

        assertEquals("Курсив", result);
    }

    @Test
    public void markdownFormatterRemovesListMarkup() {
        String markdown = "- Первый пункт";
        String result = MarkdownFormatter.toPlainText(markdown);

        assertEquals("Первый пункт", result);
    }
}