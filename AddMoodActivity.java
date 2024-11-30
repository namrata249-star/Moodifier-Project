package com.microsoft.projectoxford.emotionsample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddMoodActivity extends AppCompatActivity {

    private EditText moodEditText, moviesEditText, songsEditText, seriesEditText;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood);  // Ensure this points to the correct layout file

        // Initialize the views
        moodEditText = findViewById(R.id.moodEditText);
        moviesEditText = findViewById(R.id.moviesEditText);
        songsEditText = findViewById(R.id.songsEditText);
        seriesEditText = findViewById(R.id.seriesEditText);
        addButton = findViewById(R.id.addButton);

        // Set OnClickListener for the add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user input from EditText fields
                String mood = moodEditText.getText().toString();
                String movies = moviesEditText.getText().toString();
                String songs = songsEditText.getText().toString();
                String series = seriesEditText.getText().toString();

                // Validate the input (optional but recommended)
                if (mood.isEmpty() || movies.isEmpty() || songs.isEmpty() || series.isEmpty()) {
                    Toast.makeText(AddMoodActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Instantiate the DatabaseHelper and add the data to the database
                    DatabaseHelper dbHelper = new DatabaseHelper(AddMoodActivity.this);
                    dbHelper.addMood(mood, movies, songs, series);  // Insert data into the database

                    // Provide feedback to the user
                    Toast.makeText(AddMoodActivity.this, "Mood added successfully!", Toast.LENGTH_SHORT).show();

                    // Optionally, finish the activity or clear the fields
                    // finish();  // Uncomment if you want to go back to the previous activity
                }
            }
        });
    }
}
