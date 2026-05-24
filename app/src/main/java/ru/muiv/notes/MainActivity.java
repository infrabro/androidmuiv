package ru.muiv.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.muiv.notes.data.NoteRepository;
import ru.muiv.notes.model.Note;

public class MainActivity extends Activity {
    private NoteRepository repository;
    private List<Note> currentNotes;
    private ArrayAdapter<String> adapter;
    private EditText searchEditText;
    private ListView notesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repository = new NoteRepository(this);
        currentNotes = new ArrayList<Note>();

        buildLayout();
        loadNotes("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (searchEditText != null) {
            loadNotes(searchEditText.getText().toString());
        }
    }

    private void buildLayout() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(24, 160, 24, 24);

        TextView titleTextView = new TextView(this);
        titleTextView.setText("Notes");
        titleTextView.setTextSize(28);
        rootLayout.addView(titleTextView);

        searchEditText = new EditText(this);
        searchEditText.setHint("Поиск по заметкам, группам и тегам");
        rootLayout.addView(searchEditText);

        Button addButton = new Button(this);
        addButton.setText("Добавить заметку");
        rootLayout.addView(addButton);

        notesListView = new ListView(this);
        rootLayout.addView(
                notesListView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        setContentView(rootLayout);

        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<String>()
        );

        notesListView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                startActivity(intent);
            }
        });

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                if (position >= currentNotes.size()) {
                    return;
                }

                Note note = currentNotes.get(position);

                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("note_id", note.getId());
                startActivity(intent);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                loadNotes(text.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void loadNotes(String query) {
        currentNotes = repository.searchNotes(query);

        adapter.clear();

        if (currentNotes.size() == 0) {
            adapter.add("Заметок пока нет. Нажмите «Добавить заметку».");
            adapter.notifyDataSetChanged();
            return;
        }

        for (int i = 0; i < currentNotes.size(); i++) {
            Note note = currentNotes.get(i);
            adapter.add(formatNoteForList(note));
        }

        adapter.notifyDataSetChanged();
    }

    private String formatNoteForList(Note note) {
        StringBuilder builder = new StringBuilder();

        builder.append(note.getTitle());

        if (note.getGroupName() != null && note.getGroupName().trim().length() > 0) {
            builder.append("\nГруппа: ").append(note.getGroupName());
        }

        if (note.getTags() != null && note.getTags().trim().length() > 0) {
            builder.append("\nТеги: ").append(note.getTags());
        }

        builder.append("\nИзменено: ").append(formatDate(note.getUpdatedAt()));

        return builder.toString();
    }

    private String formatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return format.format(new Date(time));
    }
}