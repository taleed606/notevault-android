package com.example.project_1220006_1220204;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class EditNoteActivity extends AppCompatActivity{

    private EditText titleText ;
    private EditText contentText ;
    private MaterialButton saveButton ;
    private DataBaseHelper dataBaseHelper ;
    private int noteId ;
    private Note note ;
    private Spinner tagSpinner ;
    private EditText newTagText ;
    private ArrayList<Tag> tags ;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tagSpinner = findViewById(R.id.spinner_edit_note_tag);
        newTagText = findViewById(R.id.editText_edit_new_tag);

        titleText = findViewById(R.id.editText_editNote_title) ;
        contentText = findViewById(R.id.editText_editNote_content) ;
        saveButton = findViewById(R.id.button_editNote_save_note) ;

        dataBaseHelper = new DataBaseHelper(this);

        noteId = getIntent().getIntExtra("NOTE_ID", -1) ;

        if (noteId == -1){
            Toast.makeText(EditNoteActivity.this, "Invalid note", Toast.LENGTH_SHORT).show() ;
            finish() ;
            return ;
        }

        // load note from db
        note = dataBaseHelper.getNoteById(noteId);

        if (note == null){
            Toast.makeText(EditNoteActivity.this, "Note not found", Toast.LENGTH_SHORT).show() ;
            finish() ;
            return ;
        }

        // show title and content
        titleText.setText(note.getTitle());
        contentText.setText(note.getContent());

        tags = dataBaseHelper.getAllTags();
        tags.add(0, new Tag(0, "No tag"));
        tags.add(new Tag(-1, "Create new tag"));
        ArrayAdapter<Tag> tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tags);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(tagAdapter);

        // view the current tag
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).getId() == note.getTagId()) {
                tagSpinner.setSelection(i);
                break;
            }
        }

        // capture the chosen item
        tagSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Tag selectedTag = (Tag) parent.getItemAtPosition(position);

                        if (selectedTag.getId() == -1){
                            newTagText.setVisibility(View.VISIBLE);
                        }
                        else{ // hide the field of new tag insertion when the user chose an available tag
                            newTagText.setVisibility(View.GONE);
                            newTagText.setText("");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titleText.getText().toString();
                String content = contentText.getText().toString();

                if (title.isEmpty()) {
                    titleText.setError("Enter note title");
                    return;
                }

                // check if this is needed
//                if (content.isEmpty()) {
//                    contentText.setError("Enter note content");
//                    return;
//                }

                note.setTitle(title);
                note.setContent(content);

                Tag selectedTag = (Tag) tagSpinner.getSelectedItem();

                int tagId = selectedTag.getId();
                if (tagId == -1){
                    String newTagTitle = newTagText.getText().toString().trim();

                    if (newTagTitle.isEmpty()){
                        newTagText.setError("Enter a tag title") ;
                        return ;
                    }

                    tagId = dataBaseHelper.getTagIdByTitle(newTagTitle);
                    if (tagId == 0){
                        // if tagId = 0 then the new tag title does not exist in the db so we can add it
                        tagId = dataBaseHelper.insertTag(newTagTitle);
                    }
                }
                note.setTagId(tagId);

                dataBaseHelper.updateNote(note) ;

                Toast.makeText(EditNoteActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                finish() ;
            }
        });

    }
}
