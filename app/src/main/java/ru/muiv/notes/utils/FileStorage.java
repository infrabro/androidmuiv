package ru.muiv.notes.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileStorage {

    public static String copyImageToInternalStorage(Context context, Uri sourceUri) throws Exception {
        File imagesDir = new File(context.getFilesDir(), "images");

        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        String fileName = "note_image_" + System.currentTimeMillis() + ".jpg";
        File destinationFile = new File(imagesDir, fileName);

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(sourceUri);
            outputStream = new FileOutputStream(destinationFile);

            if (inputStream == null) {
                throw new Exception("Не удалось открыть выбранное изображение");
            }

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();

            return destinationFile.getAbsolutePath();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}