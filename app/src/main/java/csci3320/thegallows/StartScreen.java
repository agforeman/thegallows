package csci3320.thegallows;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartScreen extends Activity {
    private Typeface chalkTypeFace;

    private Button buttonRegularPlay = null;
    private Button buttonFreeplay = null;
    private ImageButton buttonSettings = null;
    private Intent launchIntent = null;
    private Intent settingsIntent = null;
    LinearLayout thisLayout = null;
    private LinearLayout navigation_buttons = null;
    private ImageView logo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getResources().getBoolean(R.bool.portrait_only))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscreen);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        chalkTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");

        thisLayout = (LinearLayout) findViewById(R.id.view_layout);
        thisLayout.setBackgroundResource(R.drawable.startboard);
        navigation_buttons = (LinearLayout) findViewById(R.id.navigation_buttons);

        logo = (ImageView) findViewById(R.id.SplashImage);

        buildButtons();

        logo.setImageBitmap(ReduceImageOverhead.decodeSampledBitmapFromResource(getResources(), R.drawable.splashscreen, 500, 500));

        new Handler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logo.startAnimation(AnimationUtils.loadAnimation(StartScreen.this, R.anim.rotate3));
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonSettings.startAnimation(AnimationUtils.loadAnimation(StartScreen.this, R.anim.rotate1));
                    }
                });
            }
        });

        // Get an intent to launch the game
        launchIntent = new Intent(this, Gameplay.class);
        settingsIntent = new Intent(this, Settings.class);

        // Set the Regular Play Button logic
        buttonRegularPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFreeplay.setClickable(false);
                buttonRegularPlay.setClickable(false);
                buttonSettings.setClickable(false);
                launchIntent.putExtra("GameType", "REGULAR");
                launchIntent.putExtra("LEVEL", 1);
                launchIntent.putExtra("LIFE", 3);
                launchIntent.putExtra("HINTS", 3);
                launchIntent.putExtra("WIN", true);
                launchIntent.putExtra("PREVIOUS_BG", 0);
                launchIntent.putExtra("LIFE_WARNING", false);
                launchIntent.putExtra("REWARD", false);
                launchActivity(launchIntent, "REGULAR PLAY SELECTED");
            }
        });

        // Set the Free Play Button logic
        buttonFreeplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFreeplay.setClickable(false);
                buttonRegularPlay.setClickable(false);
                buttonSettings.setClickable(false);
                launchIntent.putExtra("GameType", "FREEPLAY");
                launchIntent.putExtra("LEVEL", 0);
                launchIntent.putExtra("FP_MAX", Integer.parseInt(prefs.getString("FreeplayRounds", "")));
                launchIntent.putExtra("LIFE", 0);
                launchIntent.putExtra("HINTS", 0);
                launchIntent.putExtra("WIN", true);
                launchIntent.putExtra("PREVIOUS_BG", 0);
                launchActivity(launchIntent, "FREEPLAY SELECTED");
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    private void buildButtons() {
        // Get the two buttons
        buttonRegularPlay = (Button) findViewById(R.id.regular_play_button);
        buttonFreeplay = (Button) findViewById(R.id.freeplay_button);
        buttonSettings = (ImageButton) findViewById(R.id.settings_button);

        // Set the button font
        buttonRegularPlay.setTypeface(chalkTypeFace);
        buttonFreeplay.setTypeface(chalkTypeFace);
    }

    private void launchActivity(final Intent activity, String toast_text) {
        buttonFreeplay.setTextColor(Color.TRANSPARENT);
        buttonRegularPlay.setTextColor(Color.TRANSPARENT);
        navigation_buttons.setBackgroundColor(Color.TRANSPARENT);
        Toast.makeText(this, toast_text, Toast.LENGTH_SHORT).show();
        makeToast("GOOD LUCK!").show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                StartScreen.this.finish();
                startActivity(activity);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 4000);
    }

    private Toast makeToast(String text) {
        Toast toast = new Toast(getApplicationContext());
        Typeface chalkTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");

        LayoutInflater toast_inflater = getLayoutInflater();
        View toast_layout = toast_inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView toast_text = (TextView) toast_layout.findViewById(R.id.toast_textview);
        toast_text.setText(text);
        toast_text.setTextColor(Color.WHITE);
        toast_text.setTypeface(chalkTypeFace);

        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toast_layout);

        return toast;
    }
}
