package com.example.project_1220006_1220204;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class NoteContentFragment extends Fragment {

    public NoteContentFragment(){
        // Required empty constructor
    }

    public static NoteContentFragment newInstance(String content, String creationDate) {

        NoteContentFragment fragment = new NoteContentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("CONTENT", content);
        bundle.putString("CREATION_DATE", creationDate);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_content, container, false);

        TextView contentTextView = view.findViewById(R.id.textView_note_content) ;
        TextView creationDateTextView = view.findViewById(R.id.textView_note_creation_date) ;

        String content = "" ;
        String creationDate = "" ;

        if (getArguments() != null){
            content = getArguments().getString("CONTENT", "") ;
            creationDate = getArguments().getString("CREATION_DATE", "") ;
        }

        contentTextView.setText(content) ;
        creationDateTextView.setText("Created: " + creationDate) ;
        return view ;
    }
}
