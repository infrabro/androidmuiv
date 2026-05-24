package ru.muiv.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ru.muiv.notes.data.NoteRepository;
import ru.muiv.notes.model.Note;
import ru.muiv.notes.util.FileStorage;
import ru.muiv.notes.util.MarkdownFormatter;

public class EditNoteActivity extends Activity {
    private static final int REQUEST_PICK_IMAGE = 1001;

    private NoteRepository repository;
    private String noteId;
    private String imagePath;

    private EditText titleEditText;
    private EditText contentEditText;
    private EditText groupEditText;
    private EditText tagsEditText;
    private EditText editorEditText;
    private TextView statusTextView;
    private TextView imageStatusTextView;
    private ImageView imagePreview;
    private Button deleteButton;
    private Button removeImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repository = new NoteRepository(this);
        noteId = getIntent().getStringExtra("note_id");
        imagePath = "";

        buildLayout();

        if (noteId != null) {
            loadNote(noteId);
        } else {
            statusTextView.setText("Новая заметка");
            deleteButton.setEnabled(false);
            updateImagePreview();
        }
    }

    private void buildLayout() {
        ScrollView scrollView = new ScrollView(this);

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(24, 160, 24, 24);

        scrollView.addView(rootLayout);

        statusTextView = new TextView(this);
        statusTextView.setTextSize(22);
        rootLayout.addView(statusTextView);

        titleEditText = new EditText(this);
        titleEditText.setHint("Название заметки");
        rootLayout.addView(titleEditText);

        contentEditText = new EditText(this);
        contentEditText.setHint("Текст заметки");
        contentEditText.setMinLines(6);
        contentEditText.setGravity(android.view.Gravity.TOP);
        rootLayout.addView(contentEditText);

        Button previewButton = new Button(this);
        previewButton.setText("Предпросмотр Markdown");
        rootLayout.addView(previewButton);

        groupEditText = new EditText(this);
        groupEditText.setHint("Группа, например: Учеба");
        rootLayout.addView(groupEditText);

        tagsEditText = new EditText(this);
        tagsEditText.setHint("Теги через запятую, например: java, android");
        rootLayout.addView(tagsEditText);

        editorEditText = new EditText(this);
        editorEditText.setHint("Автор / редактор");
        editorEditText.setText("Локальный пользователь");
        rootLayout.addView(editorEditText);

        Button chooseImageButton = new Button(this);
        chooseImageButton.setText("Загрузить картинку");
        rootLayout.addView(chooseImageButton);

        removeImageButton = new Button(this);
        removeImageButton.setText("Удалить картинку");
        rootLayout.addView(removeImageButton);

        imageStatusTextView = new TextView(this);
        imageStatusTextView.setTextSize(16);
        rootLayout.addView(imageStatusTextView);

        imagePreview = new ImageView(this);
        imagePreview.setAdjustViewBounds(true);
        imagePreview.setMaxHeight(600);
        rootLayout.addView(
                imagePreview,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        Button saveButton = new Button(this);
        saveButton.setText("Сохранить");
        rootLayout.addView(saveButton);

        deleteButton = new Button(this);
        deleteButton.setText("Удалить");
        rootLayout.addView(deleteButton);

        Button backButton = new Button(this);
        backButton.setText("Назад");
        rootLayout.addView(backButton);

        setContentView(scrollView);

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMarkdownPreview();
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectedImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadNote(String id) {
        Note note = repository.getNoteById(id);

        if (note == null) {
            Toast.makeText(this, "Заметка не найдена", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        statusTextView.setText("Редактирование заметки");

        titleEditText.setText(note.getTitle());
        contentEditText.setText(note.getContent());
        groupEditText.setText(note.getGroupName());
        tagsEditText.setText(note.getTags());
        imagePath = note.getImagePath();

        if (note.getLastEditor() != null && note.getLastEditor().length() > 0) {
            editorEditText.setText(note.getLastEditor());
        }

        updateImagePreview();
    }

    private void showMarkdownPreview() {
        String markdownText = contentEditText.getText().toString();

        TextView previewTextView = new TextView(this);
        previewTextView.setTextSize(18);
        previewTextView.setPadding(32, 32, 32, 32);

        SpannableStringBuilder formattedText = MarkdownFormatter.toSpannable(markdownText);
        previewTextView.setText(formattedText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Предпросмотр Markdown");
        builder.setView(previewTextView);
        builder.setPositiveButton("Закрыть", null);
        builder.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Выберите картинку"), REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri == null) {
                Toast.makeText(this, "Не удалось выбрать картинку", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                imagePath = FileStorage.copyImageToInternalStorage(this, selectedImageUri);
                updateImagePreview();
                Toast.makeText(this, "Картинка загружена", Toast.LENGTH_SHORT).show();
            } catch (Exception exception) {
                Toast.makeText(this, "Ошибка загрузки картинки", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateImagePreview() {
        if (imagePath == null || imagePath.trim().length() == 0) {
            imageStatusTextView.setText("Картинка не загружена");
            imagePreview.setImageDrawable(null);
            removeImageButton.setEnabled(false);
            return;
        }

        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            imageStatusTextView.setText("Картинка не найдена");
            imagePreview.setImageDrawable(null);
            removeImageButton.setEnabled(false);
            return;
        }

        imageStatusTextView.setText("Картинка загружена: " + imageFile.getName());
        imagePreview.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        removeImageButton.setEnabled(true);
    }

    private void removeSelectedImage() {
        imagePath = "";
        updateImagePreview();
        Toast.makeText(this, "Картинка удалена из заметки", Toast.LENGTH_SHORT).show();
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String groupName = groupEditText.getText().toString().trim();
        String tags = tagsEditText.getText().toString().trim();
        String editor = editorEditText.getText().toString().trim();

        if (title.length() == 0) {
            Toast.makeText(this, "Введите название заметки", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.length() == 0) {
            content = "";
        }

        if (editor.length() == 0) {
            editor = "Локальный пользователь";
        }

        if (imagePath == null) {
            imagePath = "";
        }

        if (noteId == null) {
            repository.createNote(title, content, groupName, tags, editor, imagePath);
            Toast.makeText(this, "Заметка создана", Toast.LENGTH_SHORT).show();
        } else {
            repository.updateNote(noteId, title, content, groupName, tags, editor, imagePath);
            Toast.makeText(this, "Заметка обновлена", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление заметки");
        builder.setMessage("Вы действительно хотите удалить эту заметку?");

        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                repository.deleteNote(noteId);
                Toast.makeText(EditNoteActivity.this, "Заметка удалена", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}