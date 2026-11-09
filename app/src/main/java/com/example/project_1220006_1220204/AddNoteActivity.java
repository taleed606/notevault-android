package com.example.project_1220006_1220204;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity{

    private EditText titleText ;
    private EditText contentText ;
    private Button saveButton ;
    private DataBaseHelper dataBaseHelper ;
    private Spinner tagSpinner ;
    private EditText newTagText ;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tagSpinner = findViewById(R.id.spinner_note_tag) ;
        newTagText = findViewById(R.id.editText_new_tag) ;

        titleText = findViewById(R.id.editText_note_title) ;
        contentText = findViewById(R.id.editText_note_content) ;
        saveButton = findViewById(R.id.button_save_note) ;

        dataBaseHelper = new DataBaseHelper(this) ;

        // WelcomeActivity passed us the signed-in user's email through the Intent.
        String email = getIntent().getStringExtra("USER_EMAIL") ;

        ArrayList<Tag> tags = dataBaseHelper.getAllTags();

        tags.add(0, new Tag(0, "No tag"));
        tags.add(new Tag(-1, "Create new tag"));

        ArrayAdapter<Tag> tagAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tags);

        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(tagAdapter);

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

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String title = titleText.getText().toString() ;
                String content = contentText.getText().toString() ;

                if (title.isEmpty()){
                    titleText.setError("Enter note title") ;
                    return ;
                }

                // check if this is needed or , i think the content can be empty
//                if (content.isEmpty()) {
//                    contentText.setError("Enter note content");
//                    return;
//                }

                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(new Date());

                Tag selectedTag = (Tag) tagSpinner.getSelectedItem();

                int tagId = selectedTag.getId();
                if (tagId == -1){
                    String newTagTitle = newTagText.getText().toString().trim();

                    if (newTagTitle.isEmpty()){
                        newTagText.setError("Enter a tag title");
                        return;
                    }

                    tagId = dataBaseHelper.getTagIdByTitle(newTagTitle);
                    if (tagId == 0){
                        // if tagId = 0 then the new tag title does not exist in the db so we can add it
                        tagId = dataBaseHelper.insertTag(newTagTitle);
                    }
                }

                Note note = new Note(0, email, title, content, date, false, tagId);

                dataBaseHelper.insertNote(note) ;

                Toast.makeText(AddNoteActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
