package csci3320.thegallows;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.LinearLayout;


public class StartScreen extends Activity {

    Button buttonRegularPlay = null;
    Button buttonFreeplay = null;
    Button buttonSettings = null;
    Intent launchIntent = null;
    Intent settingsIntent = null;
    String background = null;
    LinearLayout thisLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        chooseLayout();

        setBackgroundImg();

        buildButtons();

        // Get an intent to launch the game
        launchIntent = new Intent(this, Gameplay.class);
        settingsIntent = new Intent(this, Settings.class);

        // Set the Regular Play Button logic
        buttonRegularPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buttonRegularPlay.setBackgroundColor(0x3C000000);
                buttonFreeplay.setBackgroundResource(R.drawable.erasermark);
                buttonFreeplay.setTextColor(0x00000000);
                buttonSettings.setBackgroundResource(R.drawable.erasermark);
                buttonSettings.setTextColor(0x00000000);
                launchIntent.putExtra("GameType", "REGULAR");
                launchIntent.putExtra("LEVEL", 1);
                launchIntent.putExtra("LIFE", 3);
                launchIntent.putExtra("HINTS", 3);
                startActivity(launchIntent);
            }
        });

        // Set the Free Play Button logic
        buttonFreeplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buttonFreeplay.setBackgroundColor(0x3C000000);
                buttonRegularPlay.setBackgroundResource(R.drawable.erasermark);
                buttonRegularPlay.setTextColor(0x00000000);
                buttonSettings.setBackgroundResource(R.drawable.erasermark);
                buttonSettings.setTextColor(0x00000000);
                launchIntent.putExtra("GameType", "FREEPLAY");
                launchIntent.putExtra("LEVEL", 0);
                launchIntent.putExtra("LIFE", 0);
                launchIntent.putExtra("HINTS", 1);
                startActivity(launchIntent);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buttonSettings.setBackgroundColor(0x3c000000);
                buttonRegularPlay.setBackgroundResource(R.drawable.erasermark);
                buttonRegularPlay.setTextColor(0x00000000);
                buttonFreeplay.setBackgroundResource(R.drawable.erasermark);
                buttonFreeplay.setTextColor(0x00000000);
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        buttonFreeplay.setBackgroundColor(0x00000000);
        buttonFreeplay.setTextColor(Color.WHITE);
        buttonRegularPlay.setBackgroundColor(0x00000000);
        buttonRegularPlay.setTextColor(Color.WHITE);
        buttonSettings.setBackgroundColor(0x00000000);
        buttonSettings.setTextColor(Color.WHITE);

        setBackgroundImg();
    }

    // Handle orientation changes Manually here.
    @Override
    public void onConfigurationChanged(Configuration config){
        // Just reload this activity
        super.onConfigurationChanged(config);
        launchIntent = new Intent(this, StartScreen.class);
        finish();
        startActivity(launchIntent);
    }

    private void chooseLayout(){
        // Determine which layout file to use
        Configuration currentConfig = getResources().getConfiguration();
        if (currentConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_startscreen);
        } else if(currentConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_startscreen_horizontal);
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
        buttonRegularPlay = (Button) findViewById(R.id.regular_play_button);
        buttonFreeplay = (Button) findViewById(R.id.freeplay_button);
        buttonSettings = (Button) findViewById(R.id.settings_button);

        // Set the button font
        buttonRegularPlay.setTypeface(styleTypeFace);
        buttonFreeplay.setTypeface(styleTypeFace);
        buttonSettings.setTypeface(styleTypeFace);
    }

}
