package csci3320.thegallows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Endgame extends Activity {

    SharedPreferences prefs = null;
    Intent replayIntent = null;
    private Button buttonPlayAgain = null;
    private Button buttonMainMenu = null;
    private LinearLayout thisLayout = null;
    private ImageView resultImage = null;
    private String game_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* be sure that the screen will remain in portrait mode even when the device's orientation
         * is physically adjusted. Although this was also specified in the Manifest file, we need to
         * be extra sure that this activity will remain in portrait mode or else the game will reset
         */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

        if (prefs.getBoolean("EnableAnimations", true)) {
            new Handler().postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    resultImage.startAnimation(AnimationUtils.loadAnimation(Endgame.this, R.anim.rotate3));
                }
            });
        }

        // Get info from the incoming intent
        game_type = getIntent().getStringExtra("GameType");

        // Set Play Again button logic
        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayIntent = new Intent(Endgame.this, Gameplay.class);
                replayIntent.putExtra("GameType", game_type);
                launchActivity(replayIntent);
            }
        });

        // Set the Main Menu button logic
        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuIntent = new Intent(Endgame.this, StartScreen.class);
                mainMenuIntent.putExtra("firstrun", false);
                mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                launchActivity(mainMenuIntent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        buttonMainMenu.setBackgroundColor(0x00000000);
        buttonPlayAgain.setBackgroundColor(0x00000000);
        buttonPlayAgain.setTextColor(Color.WHITE);
        buttonMainMenu.setTextColor(Color.WHITE);
    }

    // Handle back button presses manually here
    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Replay in " + game_type + " mode?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Endgame.this, Gameplay.class);
                        launchActivity(i);
                    }
                }).create().show();
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

        if (game_type.equals("FREEPLAY")) {
            activity.putExtra("GameType", "FREEPLAY");
            activity.putExtra("LEVEL", 0);
            activity.putExtra("FP_MAX", getIntent().getIntExtra("FP_MAX", -1));
            activity.putExtra("LIFE", 0);
            activity.putExtra("HINTS", 0);
            activity.putExtra("WIN", true);
        }
        else if (game_type.equals("REGULAR")) {
            activity.putExtra("LEVEL", 1);
            activity.putExtra("LIFE", 3);
            activity.putExtra("HINTS", 3);
            activity.putExtra("WIN", true);
            activity.putExtra("LIFE_WARNING", false);
            activity.putExtra("REWARD", false);
        }

        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Endgame.this.finish();
        startActivity(activity);

            if (prefs.getBoolean("EnableAnimations", true))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}