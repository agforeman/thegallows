package csci3320.thegallows;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Endgame extends Activity {

    Button buttonPlayAgain = null;
    Button buttonMainMenu = null;
    Button buttonSettings = null;
    Intent incomingIntent = null;
    Intent settingsIntent = null;
    ImageView resultImage = null;
    String background = null;
    LinearLayout thisLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chooseLayout();

        setBackgroundImg();

        buildButtons();

        // Get the incoming intent
        incomingIntent = new Intent(getIntent());
        // Set the intent to call the StartScreen activity currently on the activity stack
        //incomingIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        // Get info from the incoming intent
        final boolean result = incomingIntent.getBooleanExtra("Result", false);
        final String gameWord = incomingIntent.getStringExtra("Word");
        final String game_type = incomingIntent.getStringExtra("GameType");

        // Set Win/Lose Image
        resultImage = (ImageView) findViewById(R.id.ResultImage);
        if(result){
            resultImage.setImageResource(R.drawable.winimage);
        } else {
            resultImage.setImageResource(R.drawable.loseimage);
        }

        settingsIntent = new Intent(this, Settings.class);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buttonSettings.setBackgroundColor(0x3c000000);
                buttonPlayAgain.setBackgroundResource(R.drawable.erasermark);
                buttonPlayAgain.setTextColor(0x00000000);
                buttonMainMenu.setBackgroundResource(R.drawable.erasermark);
                buttonMainMenu.setTextColor(0x00000000);
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });

        // Set Play Again button logic
        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buttonPlayAgain.setBackgroundColor(0x3C000000);
                buttonMainMenu.setBackgroundResource(R.drawable.erasermark);
                buttonMainMenu.setTextColor(0x00000000);
                buttonSettings.setBackgroundResource(R.drawable.erasermark);
                buttonSettings.setTextColor(0x00000000);
                Intent replayIntent = new Intent(v.getContext(), Gameplay.class);
                replayIntent.putExtra("GameType", game_type);

                if (game_type.equals("FREEPLAY")) {
                    replayIntent.putExtra("LEVEL", 0);
                    replayIntent.putExtra("LIFE", 0);
                    replayIntent.putExtra("HINTS", 1);
                }
                else if (game_type.equals("REGULAR")) {
                    replayIntent.putExtra("LEVEL", 1);
                    replayIntent.putExtra("LIFE", 3);
                    replayIntent.putExtra("HINTS", 3);
                }
                // Kill this activity and launch a new game.
                finish();
                startActivity(replayIntent);
            }
        });

        // Set the Main Menu button logic
        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buttonMainMenu.setBackgroundColor(0x3c000000);
                buttonPlayAgain.setBackgroundResource(R.drawable.erasermark);
                buttonPlayAgain.setTextColor(0x00000000);
                buttonSettings.setBackgroundResource(R.drawable.erasermark);
                buttonSettings.setTextColor(0x00000000);
                Intent mainMenuIntent = new Intent(v.getContext(), StartScreen.class);
                // Set this intent to call the StartScreen currently on the Activity stack.
                mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                // Kill this activity and start main menu
                finish();
                startActivity(mainMenuIntent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        buttonSettings.setBackgroundColor(0x00000000);
        buttonMainMenu.setBackgroundColor(0x00000000);
        buttonPlayAgain.setBackgroundColor(0x00000000);
        buttonPlayAgain.setTextColor(Color.WHITE);
        buttonMainMenu.setTextColor(Color.WHITE);
        setBackgroundImg();
    }

    // Handle orientation changes with this code
    @Override
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);
        // Essentially relaunch this activity with the same intent that came in in the first place
        //  This allows you to save the original intents Extras (IE result, word...). Doing this
        //  Lets the Endgame.java save state information.
        Intent incomingIntent = new Intent(getIntent());
        finish();
        startActivity(incomingIntent);
    }

    // Handle back button presses manually here
    @Override
    public void onBackPressed(){
        // Launch the StartScreen activity currently on the activity stack.
        Intent mainMenuIntent = new Intent(this, StartScreen.class);
        mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        finish();
        startActivity(mainMenuIntent);
    }

    private void chooseLayout(){
        // Determine which layout file to use
        Configuration currentConfig = getResources().getConfiguration();
        if (currentConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_endgame);
        } else if(currentConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_endgame_horizontal);
        }
    }

    private void setBackgroundImg(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        background = prefs.getString("BackgroundColor", "");
        thisLayout = (LinearLayout) findViewById(R.id.view_layout);

        switch(background){
            case "BLACK":
                thisLayout.setBackgroundResource(R.drawable.blackboard);
                break;
            case "BLUE":
                thisLayout.setBackgroundResource(R.drawable.blueboard);
                break;
            case "CYAN":
                thisLayout.setBackgroundResource(R.drawable.cyanboard);
                break;
            case "GREEN":
                thisLayout.setBackgroundResource(R.drawable.greenboard);
                break;
            case "INDIGO":
                thisLayout.setBackgroundResource(R.drawable.indigoboard);
                break;
            case "ORANGE":
                thisLayout.setBackgroundResource(R.drawable.orangeboard);
                break;
            case "PINK":
                thisLayout.setBackgroundResource(R.drawable.pinkboard);
                break;
            case "PURPLE":
                thisLayout.setBackgroundResource(R.drawable.purpleboard);
                break;
            case "RED":
                thisLayout.setBackgroundResource(R.drawable.redboard);
                break;
            case "VIOLET":
                thisLayout.setBackgroundResource(R.drawable.violetboard);
                break;
            case "YELLOW":
                thisLayout.setBackgroundResource(R.drawable.yellowboard);
                break;
            default:
                thisLayout.setBackgroundResource(R.drawable.defaultboard);
        }
    }

    private void buildButtons() {
        final Typeface styleTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");

        // Get the two buttons
        buttonPlayAgain = (Button) findViewById(R.id.play_again_button);
        buttonMainMenu = (Button) findViewById(R.id.main_menu_button);
        buttonSettings = (Button) findViewById(R.id.settings_button);

        // Set the button font
        buttonPlayAgain.setTypeface(styleTypeFace);
        buttonMainMenu.setTypeface(styleTypeFace);
        buttonSettings.setTypeface(styleTypeFace);
    }
}
