package com.microsoft.projectoxford.emotionsample;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SplashActivity extends AppCompatActivity {

    private boolean isAnimationFinished = false; // Flag to track animation status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ensure splash screen layout contains imgLogo
        Animation bounceAnim = AnimationUtils
                .loadAnimation(getApplicationContext(), R.anim.splash_logo);

        // Start animation on logo
        findViewById(R.id.imgLogo).setAnimation(bounceAnim);

        // Animation listener to handle actions after animation ends
        bounceAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // No action needed here
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Proceed to next activity when animation finishes
                animationsFinished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No action needed here
            }
        });

        // Safety timer in case animation fails or user interaction is required
        new Handler().postDelayed(() -> {
            if (!isAnimationFinished) {
                animationsFinished();
            }
        }, 3000); // 3 seconds fallback
    }

    // Method to handle transitioning to the next activity
    public void animationsFinished() {
        if (!isAnimationFinished) {
            isAnimationFinished = true;

            // Transition to EmotionDetectionActivity
            Intent intent = new Intent(SplashActivity.this, EmotionDetectionActivity.class);
            startActivity(intent);

            // Close SplashActivity
            finish();

            // Optional: Add a transition animation
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
