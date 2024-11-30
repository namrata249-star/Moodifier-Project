package com.microsoft.projectoxford.emotionsample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.snackbar.Snackbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database and add sample data
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Add moods and their suggestions
        dbHelper.addMood("Happy",
                "The Pursuit of Happyness, Inside Out",
                "Happy Song, Can't Stop the Feeling",
                "Friends, The Big Bang Theory");

        dbHelper.addMood("Sad",
                "The Fault in Our Stars, A Walk to Remember",
                "Someone Like You, Fix You",
                "Grey's Anatomy, This Is Us");

        dbHelper.addMood("Angry",
                "Fight Club, John Wick",
                "Killing In The Name, Lose Yourself",
                "Breaking Bad, The Punisher");

        dbHelper.addMood("Relaxed",
                "The Secret Life of Walter Mitty, Eat Pray Love",
                "Weightless, Sunflower",
                "The Office, Parks and Recreation");

        dbHelper.addMood("Fear",
                "The Conjuring, Insidious",
                "Disturbia, Thriller",
                "Stranger Things, The Haunting of Hill House");

        dbHelper.addMood("Surprise",
                "The Sixth Sense, Inception",
                "Surprise, Sia - Chandelier",
                "Black Mirror, Westworld");

        dbHelper.addMood("Contempt",
                "The Wolf of Wall Street, American Psycho",
                "Bad Guy, Numb",
                "House of Cards, Dexter");

        dbHelper.addMood("Disgust",
                "The Texas Chainsaw Massacre, Saw",
                "Smells Like Teen Spirit, The Weeknd - Blinding Lights",
                "Hannibal, Bates Motel");

        dbHelper.addMood("Neutral",
                "The Social Network, The Intern",
                "Counting Stars, Blinding Lights",
                "The Office, Parks and Recreation");

        // Optionally log all entries in the database to confirm the data
        dbHelper.logAllEntries();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void activityRecognize(View v) {
        if(isNetworkAvailable()) {
            Intent intent = new Intent(this, RecognizeActivity.class);
            startActivity(intent);
        } else {
            Snackbar.make(v, "This requires Internet Connection", Snackbar.LENGTH_LONG).show();
        }
    }

    public void Happy(View view) {
        Intent intent = new Intent(this, ChangeMoodActivity.class);
        intent.putExtra("mood", "happy");
        startActivity(intent);
    }

    public void Sad(View view) {
        Intent intent = new Intent(this, ChangeMoodActivity.class);
        intent.putExtra("mood", "sad");
        startActivity(intent);
    }

    public void Angry(View view) {
        Intent intent = new Intent(this, ChangeMoodActivity.class);
        intent.putExtra("mood", "angry");
        startActivity(intent);
    }

    public void Contempt(View view) {
        Intent intent = new Intent(this, ChangeMoodActivity.class);
        intent.putExtra("mood", "contempt");
        startActivity(intent);
    }

    public void Disgust(View view) {
        Intent intent = new Intent(this, ChangeMoodActivity.class);
        intent.putExtra("mood", "disgust");
        startActivity(intent);
    }

    public void Fear(View view) {
        Intent intent = new Intent(this, ChangeMoodActivity.class);
        intent.putExtra("mood", "fear");
        startActivity(intent);
    }

    public void Neutral(View view) {
        Intent intent = new Intent(this, ChangeMoodActivity.class);
        intent.putExtra("mood", "neutral");
        startActivity(intent);
    }

    public void Surprise(View view) {
        Intent intent = new Intent(this, ChangeMoodActivity.class);
        intent.putExtra("mood", "surprise");
        startActivity(intent);
    }
}
