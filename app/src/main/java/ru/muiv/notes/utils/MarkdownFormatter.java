package ru.muiv.notes.util;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class MarkdownFormatter {

    public static SpannableStringBuilder toSpannable(String markdown) {
        SpannableStringBuilder result = new SpannableStringBuilder();

        if (markdown == null || markdown.length() == 0) {
            result.append("");
            return result;
        }

        String normalizedText = markdown.replace("\r\n", "\n");
        String[] lines = normalizedText.split("\n", -1);

        for (int i = 0; i < lines.length; i++) {
            appendFormattedLine(result, lines[i]);

            if (i < lines.length - 1) {
                result.append("\n");
            }
        }

        return result;
    }

    private static void appendFormattedLine(SpannableStringBuilder result, String line) {
        String trimmed = line.trim();

        if (trimmed.startsWith("# ")) {
            int start = result.length();
            appendInlineFormattedText(result, trimmed.substring(2));
            int end = result.length();

            result.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            result.setSpan(
                    new RelativeSizeSpan(1.4f),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            return;
        }

        if (trimmed.startsWith("## ")) {
            int start = result.length();
            appendInlineFormattedText(result, trimmed.substring(3));
            int end = result.length();

            result.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            result.setSpan(
                    new RelativeSizeSpan(1.2f),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            return;
        }

        if (trimmed.startsWith("- ")) {
            int start = result.length();
            appendInlineFormattedText(result, trimmed.substring(2));
            int end = result.length();

            result.setSpan(
                    new BulletSpan(20),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            return;
        }

        appendInlineFormattedText(result, line);
    }

    private static void appendInlineFormattedText(SpannableStringBuilder result, String text) {
        int i = 0;

        while (i < text.length()) {
            if (startsWith(text, i, "**")) {
                int closeIndex = text.indexOf("**", i + 2);

                if (closeIndex > i) {
                    int start = result.length();
                    result.append(text.substring(i + 2, closeIndex));
                    int end = result.length();

                    result.setSpan(
                            new StyleSpan(Typeface.BOLD),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );

                    i = closeIndex + 2;
                    continue;
                }
            }

            if (startsWith(text, i, "*")) {
                int closeIndex = text.indexOf("*", i + 1);

                if (closeIndex > i) {
                    int start = result.length();
                    result.append(text.substring(i + 1, closeIndex));
                    int end = result.length();

                    result.setSpan(
                            new StyleSpan(Typeface.ITALIC),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );

                    i = closeIndex + 1;
                    continue;
                }
            }

            result.append(text.substring(i, i + 1));
            i++;
        }
    }

    private static boolean startsWith(String text, int position, String pattern) {
        if (position + pattern.length() > text.length()) {
            return false;
        }

        return text.substring(position, position + pattern.length()).equals(pattern);
    }

    public static String toPlainText(String markdown) {
        if (markdown == null) {
            return "";
        }

        return markdown
                .replace("**", "")
                .replace("*", "")
                .replace("# ", "")
                .replace("## ", "")
                .replace("- ", "");
    }
}