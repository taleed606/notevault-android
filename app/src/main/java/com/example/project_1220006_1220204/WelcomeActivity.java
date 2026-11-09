package com.example.project_1220006_1220204;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import android.graphics.drawable.GradientDrawable;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {

    private TextView textViewWelcomeMessage;
    private ActionBarDrawerToggle actionBarDrawerToggle ;
    private DrawerLayout drawerLayout ;
    private Toolbar toolbar ;
    private NavigationView navigationView ;

    private DataBaseHelper dataBaseHelper ;

    private Button addNote ;
    private String email ;
    private LinearLayout notesLayout ;
    private SharedPrefManager sharedPrefManager ;
    private Spinner spinner_search_tag ;
    private boolean animateFavoriteHearts = false ; // this will be true when we enter the favorite page
    private boolean inFavoritePage = false ;

    private EditText searchText ;
    private Spinner spinner_search_in ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sharedPrefManager = SharedPrefManager.getInstance(this) ;

        Button searchButton = findViewById(R.id.button_search);
        searchText = findViewById(R.id.editText_search);

        dataBaseHelper = new DataBaseHelper(this);

        notesLayout = (LinearLayout) findViewById(R.id.notes_layout);

        toolbar = (Toolbar)findViewById(R.id.toolbar) ;
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout) ;
        navigationView = (NavigationView)findViewById(R.id.navigation_view) ;

        actionBarDrawerToggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.open , R.string.close) ;

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // color of the 3 dashed menu button
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(
                ContextCompat.getColor(this, android.R.color.white)
        );

        textViewWelcomeMessage = findViewById(R.id.textView_welcome_message);

        // MainActivity passed us the signed-in user's email through the Intent.
        email = getIntent().getStringExtra("USER_EMAIL") ;

        // the following works when a menu option is chosen
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId() ;

            if (id == R.id.nav_all_notes){
                // get the last sort option used
                String savedSortOption = sharedPrefManager.getSortOption(email) ;

                // set the search tag to "Any"
                spinner_search_tag.setSelection(0) ;

                // clear the search box
                spinner_search_in.setSelection(0) ;
                searchText.setText("") ;
                searchText.clearFocus() ;

                inFavoritePage = false ;

                toolbar.setTitle("My Notes") ;

                Cursor notes = dataBaseHelper.
                        searchForNotes(email, "", "Any", savedSortOption, 0 , inFavoritePage);
                showNotes(notes) ;
            }
            else if (id == R.id.nav_favorites){
                animateFavoriteHearts = true ; // enable heart animation when visiting favorite page
                inFavoritePage = true ;

                String savedSortOption = sharedPrefManager.getSortOption(email) ;

                // set the search tag to "Any"
                spinner_search_tag.setSelection(0) ;

                // clear the search box
                spinner_search_in.setSelection(0) ;
                searchText.setText("") ;
                searchText.clearFocus() ;

                toolbar.setTitle("Favorite Notes") ;

                Cursor notes = dataBaseHelper.searchForNotes(
                        email,
                        "",
                        "Any",
                        savedSortOption,
                        0,
                        inFavoritePage
                );
                showNotes(notes) ;
            }
            else if (id == R.id.nav_sort_date_ascending){
                showSortedNotes("Oldest first") ;
            }
            else if (id == R.id.nav_sort_date_descending){
                showSortedNotes("Newest first") ;
            }
            else if (id == R.id.nav_sort_alphabetical_ascending){
                showSortedNotes("Title: A - Z") ;
            }
            else if (id == R.id.nav_sort_alphabetical_descending){
                showSortedNotes("Title: Z - A") ;
            }
            else if (id == R.id.nav_profile){
                Intent intent = new Intent(WelcomeActivity.this, ProfileActivity.class) ;
                intent.putExtra("USER_EMAIL", email) ;
                startActivity(intent) ;
            }
            else if (id == R.id.nav_logout){
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);

                // this makes sure Back can't take the user back into the app after logging out.
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawers();

            return true ;
        });

        // apply the search options to spinner
        String[] options_search = { "Any", "Title" , "Content"} ;
        spinner_search_in = (Spinner) findViewById(R.id.spinner_search_in);
        ArrayAdapter<String> obj_search = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, options_search);
        spinner_search_in.setAdapter(obj_search);

        spinner_search_tag = (Spinner) findViewById(R.id.spinner_search_tag);

        // tag listener to apply the changes immediately
        spinner_search_tag.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // get the chosen tag
                        Tag selectedTag = (Tag) parent.getItemAtPosition(position) ;
                        int selected_tag_id = selectedTag.getId() ;

                        String search_text = searchText.getText().toString() ;
                        String search_in_option = spinner_search_in.getSelectedItem().toString() ;

                        String sort_by_option = sharedPrefManager.getSortOption(email) ;

                        Cursor notes = dataBaseHelper.searchForNotes(
                                email,
                                search_text,
                                search_in_option,
                                sort_by_option,
                                selected_tag_id,
                                inFavoritePage
                        );

                        showNotes(notes) ;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        addNote = (Button) findViewById(R.id.button_add_note) ;
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, AddNoteActivity.class);
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });

        // search button takes the text, search in, and tag options
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String search_text = searchText.getText().toString() ;
                String search_in_option = spinner_search_in.getSelectedItem().toString() ;
                String sort_by_option = sharedPrefManager.getSortOption(email) ;

                Tag selectedTag = (Tag) spinner_search_tag.getSelectedItem() ;
                int selected_tag_id = selectedTag == null ? 0 : selectedTag.getId() ;

                Cursor notes = dataBaseHelper.
                        searchForNotes(email , search_text , search_in_option , sort_by_option , selected_tag_id , inFavoritePage) ;
                showNotes(notes) ;

                // clear the text from the search box
                searchText.clearFocus() ;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume() ;

        // refresh the name
        User currentUser = dataBaseHelper.getUserByEmail(email);

        if (currentUser == null){
            Toast.makeText(WelcomeActivity.this, "Error", Toast.LENGTH_SHORT).show() ;
            finish() ;
            return ;
        }

        String name = currentUser.getmFirstName();
        textViewWelcomeMessage.setText("Welcome, " + name + "!") ;

        loadTagSpinner() ;

        // get the save sort option
        String savedSortOption = sharedPrefManager.getSortOption(email);

        // set the option in the spinner
        if (savedSortOption.equals("Oldest first")){
            navigationView.setCheckedItem(R.id.nav_sort_date_ascending);
        }
        else if (savedSortOption.equals("Newest first")){
            navigationView.setCheckedItem(R.id.nav_sort_date_descending);
        }
        else if (savedSortOption.equals("Title: A - Z")){
            navigationView.setCheckedItem(R.id.nav_sort_alphabetical_ascending);
        }
        else if (savedSortOption.equals("Title: Z - A")){
            navigationView.setCheckedItem(R.id.nav_sort_alphabetical_descending);
        }

        Tag selectedTag = (Tag) spinner_search_tag.getSelectedItem() ;
        int selected_tag_id = selectedTag == null ? 0 : selectedTag.getId() ;

        // get the notes based on the search options
        Cursor cursor = dataBaseHelper.searchForNotes(
                email,
                "",
                "Any",
                savedSortOption,
                selected_tag_id,
                inFavoritePage
        );

        showNotes(cursor) ;
    }

    private void showNotes(Cursor cursor){

        notesLayout.removeAllViews() ;

        while (cursor.moveToNext()){

            int noteId = cursor.getInt(0) ;
            String title = cursor.getString(2) ;
            String content = cursor.getString(3) ;
            String creationDate = cursor.getString(4) ;
            boolean favorite = cursor.getInt(5) == 1 ;
            int tagId = cursor.isNull(6) ? 0 : cursor.getInt(6) ;
            String tagTitle = dataBaseHelper.getTagTitleById(tagId) ;

            LinearLayout completeNoteLayout = new LinearLayout(this) ;
            completeNoteLayout.setOrientation(LinearLayout.VERTICAL) ;
            completeNoteLayout.setPadding(20, 20, 20, 20) ;

            FrameLayout noteAnimationContainer = new FrameLayout(this) ;
            noteAnimationContainer.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            ImageView animatedHeartImage = new ImageView(this) ;
            animatedHeartImage.setImageResource(R.drawable.ic_favorite_filled) ;
            animatedHeartImage.setColorFilter(android.graphics.Color.RED) ;
            animatedHeartImage.setVisibility(View.INVISIBLE) ; // hide it initially

            FrameLayout.LayoutParams animatedHeartParams = new FrameLayout.LayoutParams(100, 100) ;
            animatedHeartParams.gravity = android.view.Gravity.CENTER ;
            animatedHeartImage.setLayoutParams(animatedHeartParams) ;

            LinearLayout noteLayout = new LinearLayout(this) ;
            noteLayout.setOrientation(LinearLayout.HORIZONTAL) ;
            noteLayout.setGravity(android.view.Gravity.CENTER_VERTICAL) ;

            TextView titleTextView = new TextView(this) ;

            titleTextView.setText(title) ;
            titleTextView.setTextSize(20) ;
            titleTextView.setTypeface(null, Typeface.BOLD) ;
            titleTextView.setGravity(android.view.Gravity.CENTER_VERTICAL);

            TextView tagTextView = new TextView(this) ;
            tagTextView.setText("#" + tagTitle) ;
            tagTextView.setTextSize(13) ;
            tagTextView.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.color_primary_variant
                    )
            );

            LinearLayout.LayoutParams tagTextParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            tagTextParams.setMargins(116, 4, 0, 8);
            tagTextView.setLayoutParams(tagTextParams);

            titleTextView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1
                    )
            );

            LinearLayout buttonsLayout = new LinearLayout(this);
            buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonsLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

            ImageButton favoriteButton = new ImageButton(this);
            favoriteButton.setImageResource(R.drawable.ic_favorite);
            favoriteButton.setColorFilter(
                    ContextCompat.getColor(this, R.color.color_primary)
            );
            favoriteButton.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            favoriteButton.setPadding(18, 18, 18, 18);

            if (favorite){ // check if the image is marked favorite
                favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
                favoriteButton.setColorFilter(android.graphics.Color.RED);

                if (animateFavoriteHearts){
                    animatedHeartImage.clearAnimation() ;
                    animatedHeartImage.setVisibility(View.VISIBLE) ;
                    animatedHeartImage.startAnimation(
                            AnimationUtils.loadAnimation(
                                    WelcomeActivity.this,
                                    R.anim.heart_scale
                            )
                    );
                }
            }
            else{
                favoriteButton.setImageResource(R.drawable.ic_favorite); // set the button as an empty heart
                favoriteButton.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_primary)
                );
            }

            LinearLayout.LayoutParams favoriteButtonParams =
                    new LinearLayout.LayoutParams(76, 76);
            favoriteButtonParams.setMargins(6, 0, 6, 0);
            favoriteButton.setLayoutParams(favoriteButtonParams);

            ImageButton editButton = new ImageButton(this);
            editButton.setImageResource(R.drawable.ic_edit);
            editButton.setColorFilter(android.graphics.Color.WHITE);
            editButton.setPadding(20, 20, 20, 20);

            GradientDrawable editBackground = new GradientDrawable();
            editBackground.setShape(GradientDrawable.OVAL);
            editBackground.setColor(ContextCompat.getColor(this, R.color.color_primary));
            editButton.setBackground(editBackground);

            LinearLayout.LayoutParams editButtonParams =
                    new LinearLayout.LayoutParams(76, 76);
            editButtonParams.setMargins(6, 0, 6, 0);
            editButton.setLayoutParams(editButtonParams);

            ImageButton collapseButton = new ImageButton(this);
            collapseButton.setImageResource(R.drawable.ic_expand_more);
            collapseButton.setPadding(10, 10, 10, 10);
            collapseButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);

            GradientDrawable collapseBackground = new GradientDrawable();
            collapseBackground.setShape(GradientDrawable.OVAL);
            collapseBackground.setColor(
                    ContextCompat.getColor(this, R.color.color_primary)
            );

            collapseButton.setBackground(collapseBackground);

            LinearLayout.LayoutParams collapseButtonParams =
                    new LinearLayout.LayoutParams(76, 76);

            collapseButtonParams.setMargins(0, 0, 40, 0);
            collapseButton.setLayoutParams(collapseButtonParams);

            ImageButton optionsButton = new ImageButton(this);
            optionsButton.setImageResource(R.drawable.ic_more_vert);
            optionsButton.setPadding(20, 20, 20, 20);

            GradientDrawable optionsBackground = new GradientDrawable();
            optionsBackground.setShape(GradientDrawable.OVAL);
            optionsBackground.setColor(
                    ContextCompat.getColor(this, R.color.color_primary)
            );
            optionsButton.setBackground(optionsBackground);

            LinearLayout.LayoutParams optionsButtonParams =
                    new LinearLayout.LayoutParams(76, 76);
            optionsButtonParams.setMargins(6, 0, 6, 0);
            optionsButton.setLayoutParams(optionsButtonParams);

            buttonsLayout.addView(favoriteButton);
            buttonsLayout.addView(editButton);
            buttonsLayout.addView(optionsButton);

            LinearLayout titleLayout = new LinearLayout(this) ;
            titleLayout.setOrientation(LinearLayout.HORIZONTAL) ;
            titleLayout.setGravity(android.view.Gravity.CENTER_VERTICAL) ;

            titleLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1
                    )
            );

            titleTextView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            titleLayout.addView(collapseButton) ;
            titleLayout.addView(titleTextView) ;

            noteLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR) ;

            noteLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            noteLayout.addView(titleLayout) ;
            noteLayout.addView(buttonsLayout) ;

            FrameLayout fragmentContainer = new FrameLayout(this) ;

            int containerId = View.generateViewId() ;

            fragmentContainer.setId(containerId) ;

            fragmentContainer.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            completeNoteLayout.addView(noteLayout) ;

            if (!tagTitle.isEmpty()){
                completeNoteLayout.addView(tagTextView) ;
            }

            completeNoteLayout.addView(fragmentContainer) ;

            NoteContentFragment noteFragment = NoteContentFragment.newInstance(content, creationDate) ;

            final boolean[] expanded = {false} ;

            // this is a listener on the button beside the title
            collapseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction() ;

                    if (!expanded[0]){
                        fragmentTransaction.add(
                                containerId,
                                noteFragment,
                                "NOTE_" + noteId
                        );

                        fragmentTransaction.commit();

                        collapseButton.setImageResource(
                                R.drawable.ic_expand_less
                        );
                        expanded[0] = true;
                    }
                    else{
                        fragmentTransaction.remove(noteFragment);
                        fragmentTransaction.commit();

                        collapseButton.setImageResource(
                                R.drawable.ic_expand_more
                        );
                        expanded[0] = false;
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(
                            WelcomeActivity.this,
                            EditNoteActivity.class
                    );

                    intent.putExtra("NOTE_ID", noteId);
                    intent.putExtra("USER_EMAIL", email);

                    startActivity(intent);
                }
            });

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Note note = dataBaseHelper.getNoteById(noteId);

                    if (note != null){

                        boolean newFavoriteValue = !note.isFavorite();

                        note.setFavorite(newFavoriteValue);
                        dataBaseHelper.updateNote(note);

                        if (newFavoriteValue){
                            favoriteButton.setImageResource(
                                    R.drawable.ic_favorite_filled
                            );
                            favoriteButton.setColorFilter(
                                    android.graphics.Color.RED
                            );

                            animatedHeartImage.clearAnimation() ;
                            animatedHeartImage.setVisibility(View.VISIBLE) ;
                            animatedHeartImage.startAnimation(
                                    AnimationUtils.loadAnimation(
                                            WelcomeActivity.this,
                                            R.anim.heart_scale
                                    )
                            );
                        }
                        else{
                            animatedHeartImage.clearAnimation() ;
                            animatedHeartImage.setVisibility(View.INVISIBLE) ;

                            favoriteButton.setImageResource(
                                    R.drawable.ic_favorite
                            );
                            favoriteButton.setColorFilter(
                                    ContextCompat.getColor(
                                            WelcomeActivity.this,
                                            R.color.color_primary
                                    )
                            );

                        }

                        if (inFavoritePage && !newFavoriteValue){

                            String savedSortOption = sharedPrefManager.getSortOption(email) ;

                            Tag selectedTag = (Tag) spinner_search_tag.getSelectedItem() ;
                            int selected_tag_id = selectedTag == null ? 0 : selectedTag.getId() ;

                            Cursor cursor = dataBaseHelper.searchForNotes(
                                    email,
                                    "",
                                    "Any",
                                    savedSortOption,
                                    selected_tag_id,
                                    true
                            );

                            showNotes(cursor) ;
                        }
                    }
                }
            });

            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popupMenu = new PopupMenu(WelcomeActivity.this, optionsButton);

                    popupMenu.getMenu().add(0, 1, 0, "Share");
                    popupMenu.getMenu().add(0, 2, 1, "Delete");

                    popupMenu.setOnMenuItemClickListener(item -> {

                        // share
                        if (item.getItemId() == 1){
                            Intent gmailIntent = new Intent(Intent.ACTION_SENDTO);

                            gmailIntent.setData(Uri.parse("mailto:"));
                            gmailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                            gmailIntent.putExtra(Intent.EXTRA_TEXT, content);
                            startActivity(gmailIntent) ;

                            return true ;
                        }
                        // delete
                        else if (item.getItemId() == 2){
                            dataBaseHelper.deleteNote(noteId , email);
                            Toast.makeText(WelcomeActivity.this, "Note deleted", Toast.LENGTH_SHORT).show() ;

                            String savedSortOption = sharedPrefManager.getSortOption(email) ;
                            Tag selectedTag = (Tag) spinner_search_tag.getSelectedItem() ;
                            int selected_tag_id = selectedTag == null ? 0 : selectedTag.getId() ;

                            Cursor cursor = dataBaseHelper.searchForNotes(
                                    email,
                                    "",
                                    "Any",
                                    savedSortOption,
                                    selected_tag_id,
                                    inFavoritePage
                            );

                            showNotes(cursor) ;
                            return true ;
                        }

                        return false;
                    });

                    popupMenu.show();
                }
            });

            noteAnimationContainer.addView(completeNoteLayout) ;
            noteAnimationContainer.addView(animatedHeartImage) ;
            notesLayout.addView(noteAnimationContainer);
        }

        animateFavoriteHearts = false ;

        cursor.close();
    }

    private void showSortedNotes(String sortOption){

        sharedPrefManager.saveSortOption(email, sortOption);

        String search_text = searchText.getText().toString() ;
        String search_in_option = spinner_search_in.getSelectedItem().toString() ;

        Tag selectedTag = (Tag) spinner_search_tag.getSelectedItem() ;
        int selected_tag_id = selectedTag == null ? 0 : selectedTag.getId() ;

        Cursor cursor = dataBaseHelper.searchForNotes(
                email,
                search_text,
                search_in_option,
                sortOption,
                selected_tag_id,
                inFavoritePage
        );

        showNotes(cursor) ;
    }

    private void loadTagSpinner(){

        int selectedTagId = 0 ;

        if (spinner_search_tag.getSelectedItem() instanceof Tag){
            Tag selectedTag = (Tag) spinner_search_tag.getSelectedItem() ;
            selectedTagId = selectedTag.getId() ;
        }

        ArrayList<Tag> tagOptions = dataBaseHelper.getAllTags() ;
        tagOptions.add(0, new Tag(0, "Any")) ;

        ArrayAdapter<Tag> tagAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tagOptions
        ) ;

        tagAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        ) ;

        spinner_search_tag.setAdapter(tagAdapter) ;

        for (int i = 0 ; i < tagOptions.size() ; i++){
            if (tagOptions.get(i).getId() == selectedTagId){
                spinner_search_tag.setSelection(i) ;
                break ;
            }
        }
    }
}
