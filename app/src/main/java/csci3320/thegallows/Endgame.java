package csci3320.thegallows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Endgame extends Activity {

    private Button buttonPlayAgain = null;
    private Button buttonMainMenu = null;
    private ImageButton buttonSettings = null;
    private Intent settingsIntent = null;
    private LinearLayout thisLayout = null;
    //private LinearLayout navigation_buttons = null;
    private ImageView resultImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        thisLayout = (LinearLayout) findViewById(R.id.view_layout2);
        thisLayout.setBackgroundResource(R.drawable.defaultboard);
        //navigation_buttons = (LinearLayout) findViewById(R.id.difficulty_buttons);

        resultImage = (ImageView) findViewById(R.id.ResultImage);

        buildButtons();

        if(getIntent().getBooleanExtra("WIN", false)){
            resultImage.setImageBitmap(ReduceImageOverhead.decodeSampledBitmapFromResource(getResources(),
                    R.drawable.winimage, 500, 500));
        } else {
            resultImage.setImageBitmap(ReduceImageOverhead.decodeSampledBitmapFromResource(getResources(),
                    R.drawable.loseimage, 500, 500));
        }

        new Handler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                resultImage.startAnimation(AnimationUtils.loadAnimation(Endgame.this, R.anim.rotate3));
            }
        });

        // Get info from the incoming intent
        final String game_type = getIntent().getStringExtra("GameType");

        // Set Play Again button logic
        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                buttonMainMenu.setBackgroundResource(R.drawable.erasermark);
                buttonMainMenu.setTextColor(0x00000000);
                buttonSettings.setBackgroundResource(R.drawable.erasermark);*/
                Intent replayIntent = new Intent(Endgame.this, Gameplay.class);
                replayIntent.putExtra("GameType", game_type);

                if (game_type.equals("FREEPLAY")) {
                    replayIntent.putExtra("GameType", "FREEPLAY");
                    replayIntent.putExtra("LEVEL", 0);
                    replayIntent.putExtra("FP_MAX", getIntent().getIntExtra("FP_MAX", -1));
                    replayIntent.putExtra("LIFE", 0);
                    replayIntent.putExtra("HINTS", 0);
                    replayIntent.putExtra("WIN", true);
                }
                else if (game_type.equals("REGULAR")) {
                    replayIntent.putExtra("LEVEL", 1);
                    replayIntent.putExtra("LIFE", 3);
                    replayIntent.putExtra("HINTS", 3);
                    replayIntent.putExtra("WIN", true);
                    replayIntent.putExtra("LIFE_WARNING", false);
                    replayIntent.putExtra("REWARD", false);
                }
                launchActivity(replayIntent);
            }
        });

        // Set the Main Menu button logic
        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                buttonPlayAgain.setBackgroundResource(R.drawable.erasermark);
                buttonPlayAgain.setTextColor(0x00000000);
                buttonSettings.setBackgroundResource(R.drawable.erasermark);*/
                Intent mainMenuIntent = new Intent(Endgame.this, StartScreen.class);
                mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                launchActivity(mainMenuIntent);
            }
        });
/*
        settingsIntent = new Intent(this, Settings.class);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(settingsIntent);
            }
        });*/
    }

    @Override
    protected void onResume(){
        super.onResume();

       // buttonSettings.setBackgroundColor(0x00000000);
        buttonMainMenu.setBackgroundColor(0x00000000);
        buttonPlayAgain.setBackgroundColor(0x00000000);
        buttonPlayAgain.setTextColor(Color.WHITE);
        buttonMainMenu.setTextColor(Color.WHITE);
    }

    // Handle back button presses manually here
    @Override
    public void onBackPressed(){
        Intent mainMenuIntent = new Intent(Endgame.this, StartScreen.class);
        mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Endgame.this.finish();
        startActivity(mainMenuIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void buildButtons() {
        final Typeface styleTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");

        // Get the two buttons
        buttonPlayAgain = (Button) findViewById(R.id.play_again_button);
        buttonMainMenu = (Button) findViewById(R.id.main_menu_button);
        //buttonSettings = (ImageButton) findViewById(R.id.settings_button2);

        // Set the button font
        buttonPlayAgain.setTypeface(styleTypeFace);
        buttonMainMenu.setTypeface(styleTypeFace);
    }

    private void launchActivity(final Intent activity) {
        buttonMainMenu.setTextColor(Color.TRANSPARENT);
        buttonMainMenu.setClickable(false);
        buttonPlayAgain.setTextColor(Color.TRANSPARENT);
        buttonPlayAgain.setClickable(false);

        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Endgame.this.finish();
        startActivity(activity);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


}
