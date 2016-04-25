package csci3320.thegallows;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;

public class Endgame extends Activity {

    //final Intent incomingIntent = new Intent(getIntent());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determine which layout file to use.
        Configuration currentConfig = getResources().getConfiguration();
        if (currentConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_endgame);
        } else if(currentConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_endgame_horizontal);
        }

        // Get the Font Object
        final Typeface chalkTypeFace = Typeface.createFromAsset(getAssets(), "fonts/SqueakyChalkSound.ttf");

        // Get the two buttons
        final Button buttonPlayAgain = (Button) findViewById(R.id.play_again_button);
        final Button buttonMainMenu = (Button) findViewById(R.id.main_menu_button);

        // Set the font for each button
        buttonPlayAgain.setTypeface(chalkTypeFace);
        buttonMainMenu.setTypeface(chalkTypeFace);

        // Get the incoming intent
        final Intent incomingIntent = new Intent(getIntent());
        // Set the intent to call the StartScreen activity currently on the activity stack
        //incomingIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        // Get info from the incoming intent
        final boolean result = incomingIntent.getBooleanExtra("Result", false);
        final String gameWord = incomingIntent.getStringExtra("Word");
        final String game_type = incomingIntent.getStringExtra("GameType");

        // Set Win/Lose Image
        final ImageView resultImage = (ImageView) findViewById(R.id.ResultImage);
        if(result){
            resultImage.setImageResource(R.drawable.winimage);
        } else {
            resultImage.setImageResource(R.drawable.loseimage);
        }

        // Set Play Again button logic
        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent mainMenuIntent = new Intent(v.getContext(), StartScreen.class);
                // Set this intent to call the StartScreen currently on the Activity stack.
                mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                // Kill this activity and start main menu
                finish();
                startActivity(mainMenuIntent);
            }
        });
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
}
