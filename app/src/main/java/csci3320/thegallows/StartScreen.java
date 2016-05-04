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
    public SharedPreferences prefs = null;
    private Typeface chalkTypeFace;
    private Button buttonRegularPlay = null;
    private Button buttonFreeplay = null;
    private ImageButton buttonSettings = null;
    private Intent launchIntent = null;
    private Intent settingsIntent = null;
    private LinearLayout thisLayout = null;
    private ImageView logo = null;
    private Toast main_toast = null;
    private Toast selection_toast = null;
    private Runnable back_runnable = null;
    private Handler back_handler = null;
    private int START_DELAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* be sure that the screen will remain in portrait mode even when the device's orientation
         * is physically adjusted. Although this was also specified in the Manifest file, we need to
         * be extra sure that this activity will remain in portrait mode or else the game will reset
         */ setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscreen);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean("EnableAnimations", false) && getIntent().getBooleanExtra("firstrun", true))
            Toast.makeText(StartScreen.this, "Animations Disabled.", Toast.LENGTH_LONG).show();

        chalkTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");

        thisLayout = (LinearLayout) findViewById(R.id.view_layout);
        thisLayout.setBackgroundResource(R.drawable.startboard);

        logo = (ImageView) findViewById(R.id.SplashImage);

        buildButtons();

        logo.setImageBitmap(ReduceImageOverhead.decodeSampledBitmapFromResource(getResources(), R.drawable.splashscreen, 500, 500));

        if (prefs.getBoolean("EnableAnimations", true)) {
            animate(true);
            START_DELAY = 4000;
        }
        else
        START_DELAY = 1000;

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
        selection_toast = Toast.makeText(this, toast_text, Toast.LENGTH_SHORT);
        main_toast = makeToast("GOOD LUCK!");
        if (prefs.getBoolean("EnableAnimations", true))
            selection_toast.show();
        main_toast.show();


        back_handler = new Handler();
        back_runnable = new Runnable() {
            @Override
            public void run() {
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                StartScreen.this.finish();
                startActivity(activity);

                if (prefs.getBoolean("EnableAnimations", true))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        back_handler.postDelayed(back_runnable, START_DELAY);
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

    private void animate(boolean on) {
        if (on) {
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
        }
        else {
            buttonSettings.clearAnimation();
            logo.clearAnimation();
        }
    }

    // Handle back button presses manually here
    @Override
    public void onBackPressed(){
        if (back_runnable != null) {
            back_handler.removeCallbacks(back_runnable);
            if (prefs.getBoolean("EnableAnimations", true))
                selection_toast.cancel();
            main_toast.cancel();
            buttonFreeplay.setTextColor(Color.WHITE);
            buttonRegularPlay.setTextColor(Color.WHITE);
            buttonFreeplay.setClickable(true);
            buttonRegularPlay.setClickable(true);
            buttonSettings.setClickable(true);
            back_runnable = null;
            back_handler = null;
            makeToast("Game launch canceled");
        }
        else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit App?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).create().show();
        }
    }
}