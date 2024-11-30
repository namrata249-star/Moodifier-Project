package com.microsoft.projectoxford.emotionsample;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SuggestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        // Retrieve the mood passed from the previous activity
        String mood = getIntent().getStringExtra("MOOD");

        // Validate if the mood is not null or empty
        if (mood == null || mood.trim().isEmpty()) {
            Toast.makeText(this, "Mood not provided!", Toast.LENGTH_SHORT).show();
            finish(); // End the activity if no mood is provided
            return;
        }

        // Display the mood as a title or heading
        TextView suggestionTitle = findViewById(R.id.suggestionTitle);
        suggestionTitle.setText("Suggestions for Your Mood: " + mood);

        // Fetch suggestions from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ArrayList<String> suggestions = dbHelper.getSuggestions(mood);

        // Handle case when no suggestions are found
        if (suggestions.isEmpty()) {
            suggestions.add("No suggestions found for this mood. Try adding some!");
        }

        // Set up the ListView to display the suggestions
        ListView suggestionList = findViewById(R.id.suggestionList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                suggestions
        );
        suggestionList.setAdapter(adapter);
    }
}
