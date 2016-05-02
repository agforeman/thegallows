package csci3320.thegallows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import java.util.Random;

/**
 * The Gameplay class extends Activity and is the most critical activity of the Gallows app, as it
 * is solely responsible for hangman gameplay. This class both generates the layout for the gameplay,
 * and contains the logic that makes gameplay possible.
 *
 * @author  Justin Shapiro
 * @version 7.0
 * @since   2016-04-10
 *
*/
public class Gameplay extends Activity implements OnClickListener {
    /**
     * Stores a constant containing the number of attemps a user has to get a word right. This
     * can't change unless we make more hangman images. Classically, this number is 6.
     */
    private final int ATTEMPTS_PER_GAME = 6;
    /**
     * Stores the current level number. Used as a counter in Freeplay mode
     */
    private int LEVEL_NUM;

    /**
     * Stores the number of levels in the game. Used only in REGULAR gameplay.
     */
    public final int NUM_LEVELS = 17;
    /**
     * A LinearLayout object that holds the main activity container. This is only used to update
     * the background color of the activity.
     */
    private LinearLayout thisLayout;
    /**
     * An ImageView object that will hold the images of the classic hangman figure during gameplay.
     */
    private ImageView hangman_img;
    /**
     * The Button object for the key that, when pressed, launches the hint dialog.
     */
    private Button hintKey;
    /**
     * An array of Button objects that act as the letter selection keyboard for the game.
     * Using an array is optimal for such bulk initialization of Buttons since we do not explicitly
     * need to name the variable each keyboard Button
     */
    private final Button[] keyboard = new Button[26];
    /**
     * keyboardID[] is a helper array to the keyboard[] array. It contains, sequentially, the IDs
     * of each keyboard variable A-Z. This simplifies the bulk keyboard Button initialization in
     * the initKeyboard() method.
     */
    private final int[] keyboardID = {R.id.A, R.id.B, R.id.C, R.id.D, R.id.E,
            R.id.F, R.id.G, R.id.H, R.id.I, R.id.J,
            R.id.K, R.id.L, R.id.M, R.id.N, R.id.O,
            R.id.P, R.id.Q, R.id.R, R.id.S, R.id.T,
            R.id.U, R.id.V, R.id.W, R.id.X, R.id.Y, R.id.Z};
    /**
     * A ProgressBar object shows the user's progress during regular mode. This is updated throughout.
     */
    private ProgressBar progress_bar;
    /**
     * Logically, this Intent can be thought of as a recycled intent that launches and starts
     * n Gameplay activities according to user Freeplay preferences and Regular Mode implementation.
     */
    public Intent CYCLIC_INTENT;
    /**
     * This is a SharedPreferences object that contains the methods need to retrieve user
     * preferences for the game.
     */
    public SharedPreferences prefs;
    /**
     * The font for most of the text for the app. The text in the hint dialog and Toasts
     * are the only exception.
     */
    private Typeface chalkTypeFace;
    /**
     * This String indicates the location of the image in assets that will go into the ImageView that
     * displays the hangman character.
     */
    public String hangman_file_path = "";
    /**
     * An ArrayList is used to keep track of the letters (A-Z), and not any other character, that
     * the user eventually needs to select. This ArrayList is populated in initWordArea() based on
     * the characters in the return of WordBank.getGameWord(). Once populated initially, the ArrayList
     * does not change and is used later in the updateAllOccurrences() method to update the text
     * color of all occurrences of a character that the user selected.
     */
    protected ArrayList<TextView> letter_arr = new ArrayList<>();
    /**
     * Stores the current level that is being done on freeplay mode.
     * Used in the event that the user dies during freeplay mode. They have to clear the level before
     * moving onto the next, random, level
     */
    public int FP_currentLevel;
    /**
     * Stores the maximum amount of Gameplay activities that a CYCLIC_INTENT can exist in
     */
    public int FP_MAX;
    /**
     * Stores the number of lifes and hints the user currently has left in a REGULAR or FREEPLAY mode.
     */
    public int lifes, hints;
    /**
     * Stores the String of the word used for gameplay and its associated hint, respectively.
     */
    public String WORD, HINT;
    /**
     * Stores the String that indicates the game mode.
     */
    public String GAMETYPE;
    /**
     * Stores the number of attempts the user will initially have. This number corresponds to the
     * body parts of the hangman character (head, arms, legs, and torso). This variable is decremented
     * with each incorrect guess.
     */
    private int attempts = ATTEMPTS_PER_GAME;
    /**
     * Keeps track of the number of correct letters the user has selected. When this variable is equal
     * to the size of the letter_arr, the user has cleared the current level.
     */
    protected int letters_correct = 0;
    /**
     * Initially set to false, this variable indicates if the user has generated a letters_correct
     * that is equal to the size of the letter_arr before attempts == 6.
     */
    public boolean win = false;
    /**
     * Keeps track of whether or not a hint has been selected by the user during the current activity.
     * This is required in order for the user to display the hint dialog multiple times without using
     * a new hint.
     */
    public boolean hintSelected = false;
    /**
     * Keeps track of whether or not a user on their last life has been shown a warning message regarding
     * thier low life. This exists to prevent the message from repeating more than once.
     */
    public boolean warningShowed = false;
    /**
     * Used if the user has set Random as their preference for color scheme in SharedPreferences. If
     * the user dies, the activity will be regenerated with the same, random color from the previous
     * activity until the user passes the LEVEL_NUM at which that activty is set.
     */
    public String backgroundIfRestart;
    /**
     * A LayoutInflater object that allows a custom Toast to be generated
     */
    public LayoutInflater toast_inflater;
    /**
     * A generic View object in which we store our custom Toast in.
     */
    public View toast_layout;
    /**
     * A TextView object that allows us to manage our own text within custom Toasts.
     */
    public TextView toast_text;

    private boolean reward_recieved = false;

    private boolean isFreeplay() { return GAMETYPE.equals("FREEPLAY"); }

    private boolean isValidLetter(char selection) { return WORD.indexOf(selection) >= 0; }

    private void retrieveIntents() {
        GAMETYPE = getIntent().getStringExtra("GameType");
        win = getIntent().getBooleanExtra("WIN", true);
        backgroundIfRestart = getIntent().getStringExtra("PREVIOUS_BG");
        LEVEL_NUM = getIntent().getIntExtra("LEVEL", -1);
        lifes = getIntent().getIntExtra("LIFE", -1);
        hints = getIntent().getIntExtra("HINTS", -1);
        warningShowed = getIntent().getBooleanExtra("LIFE_WARNING", false);
        reward_recieved = getIntent().getBooleanExtra("REWARD", false);

        if ((LEVEL_NUM == 11 || LEVEL_NUM == 16) && reward_recieved)
            reward_recieved = false;


        if (isFreeplay()) {
            FP_MAX = getIntent().getIntExtra("FP_MAX", -1);
            FP_currentLevel = getIntent().getIntExtra("LAST_FP", -1);
        }

        if ((lifes == 0 && !warningShowed && !isFreeplay())) {
            showToast("LAST TRY!");
            warningShowed = true;
        }
    }

    /**
     * Initializes the UI. After this method is complete, onClick handles the rest of the activity
     * @param savedInstanceState Used to store a saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        /************************* THE GOLDEN SEVEN **************************************/
        // Initialize UI with the following function calls. DO NOT REARRANGE THEIR ORDER!!/
        /*******FIRST*************/initAssets();/*****************************************/
        /*******SECOND************/retrieveIntents();/************************************/
        /*******THIRD*************/setBackgroundImg();/***********************************/
        /*******FOURTH************/initKeyboard();/***************************************/
        /*******FIFTH*************/initWordArea();/***************************************/
        /*******SIXTH*************/initGameStatusArea();/*********************************/
        /*******SEVENTH***********/refreshHangmanArea(Math.abs(attempts - 6));/***********/
        /******** After the above function calls take place, onClick handles the rest ****/
        /*********************************************************************************/
    }

    public void onClick(final View click) {
        final Button guess = (Button) click;
        click.setEnabled(false);

        if (guess == hintKey) {
            if (hints != 0 || hintSelected || isFreeplay()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createHintDialog();

                        if (!hintSelected) {
                            if (!isFreeplay())
                                hints--;
                            hintSelected = true;

                            String curr_hint_str = "Show Hint Again";
                            guess.setText(curr_hint_str);
                            guess.setTextSize(15);
                        }

                        click.setEnabled(true);
                    }
                });
            }
        }
        else {
            final char button_letter = guess.getText().toString().charAt(0);

            if (isValidLetter(button_letter)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateAllOccurrences(button_letter);
                    }
                });
            }
            else {
                attempts--;
                refreshHangmanArea(Math.abs(attempts - 6));
            }

            click.setClickable(false);
            ((Button) click).setTextColor(Color.TRANSPARENT);
            click.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.erasermark));

            if (attempts == 0 && letters_correct != letter_arr.size())
                gameOver(false);
            else if (letters_correct == letter_arr.size()) {
                disableKeyboard();
                gameOver(true);
            }
        }
    }

    private void updateAllOccurrences(char button_letter) {
        for (int i = 0; i < letter_arr.size(); i++) {
            if (button_letter == letter_arr.get(i).getText().toString().charAt(0)) {
                letter_arr.get(i).setTextColor(Color.WHITE);
                letter_arr.get(i).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate2));
                letters_correct++;

                updateProgress(true);
            }
        }
    }

    private void disableKeyboard() {
        for (Button aKeyboard : keyboard) aKeyboard.setClickable(false);
        hintKey.setClickable(false);
    }

    protected void gameOver(boolean _win) {
        win = _win;

        for (int i = 0; i < letter_arr.size(); i++) {
            if (!win) {
                letter_arr.get(i).setTextColor(Color.RED);
                updateProgress(false);
            }
            else {
                letter_arr.get(i).setTextColor(Color.GREEN);
            }
        }

        if (win)
            showToast("Level Cleared!");
        else
            showToast("You Died!");

        if (!isFreeplay()) {
            if (LEVEL_NUM != NUM_LEVELS) {
                if (win) {
                    LEVEL_NUM++;
                    launchActivity(true);
                }
                else {
                    if (lifes != 0) {
                        lifes--;
                        launchActivity(true);
                    }
                    else
                        launchActivity(false);
                }
            }
            else {
                if (win)
                    launchActivity(false);
                else {
                    if (lifes != 0) {
                        lifes--;
                        launchActivity(true);
                    }
                    else
                        launchActivity(false);

                }
            }
        }
        else if (isFreeplay()) {
            if (win) {
                if (LEVEL_NUM == FP_MAX)
                    launchActivity(false);
                else
                    launchActivity(true);
            }
            else
                launchActivity(true);
        }
    }

    private void launchActivity(boolean resume_cyclic) {
        if (resume_cyclic)
            CYCLIC_INTENT = new Intent(this, Gameplay.class);
        else
            CYCLIC_INTENT = new Intent(this, Endgame.class);

        loadIntents();

        Handler pause = new Handler();
        pause.postDelayed(new Runnable() {
            public void run() {
                startActivity(CYCLIC_INTENT);
                Gameplay.this.finish();
                if (!win)
                    overridePendingTransition(R.anim.fade_in_alt, R.anim.fade_out_alt);
                else
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }, 500);
    }

    private void loadIntents() {
        CYCLIC_INTENT.putExtra("GameType", GAMETYPE);
        CYCLIC_INTENT.putExtra("WIN", win);
        CYCLIC_INTENT.putExtra("PREVIOUS_BG", backgroundIfRestart);
        CYCLIC_INTENT.putExtra("LEVEL", LEVEL_NUM);
        CYCLIC_INTENT.putExtra("LIFE", lifes);
        CYCLIC_INTENT.putExtra("HINTS", hints);
        CYCLIC_INTENT.putExtra("LIFE_WARNING", warningShowed);
        CYCLIC_INTENT.putExtra("REWARD", reward_recieved);

        if (isFreeplay()) {
            CYCLIC_INTENT.putExtra("FP_MAX", FP_MAX);
            CYCLIC_INTENT.putExtra("LAST_FP", FP_currentLevel);
        }

        CYCLIC_INTENT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    private void createHintDialog() {
        hintKey.setTextColor(Color.BLUE);

        TextView HintsText = (TextView) findViewById(R.id.HintsText);
        String new_hints_text_str = "Hints: " + Integer.toString(hints);
        HintsText.setText(new_hints_text_str);

        final Dialog hintWindow = new Dialog(Gameplay.this);
        hintWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        hintWindow.setContentView(R.layout.hint_dialog_layout);

        TextView hintWindowTitle = (TextView) hintWindow.findViewById(R.id.hintWindowTitle);
        String hint_window_title_str = "Hint";
        hintWindowTitle.setText(hint_window_title_str);
        hintWindowTitle.setTypeface(chalkTypeFace);

        TextView hintTextArea = (TextView) hintWindow.findViewById(R.id.hintText);
        hintTextArea.setText(HINT);

        hintWindow.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        Button closeHintWindow = (Button) hintWindow.findViewById(R.id.close_hint);
        closeHintWindow.setTypeface(chalkTypeFace);
        closeHintWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKey.setTextColor(Color.BLACK);
                hintWindow.dismiss();
            }
        });

        hintWindow.show();
    }

    private void showToast(String text) {
        Toast toast = new Toast(getApplicationContext());

        toast_inflater = getLayoutInflater();
        toast_layout = toast_inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
        toast_text = (TextView) toast_layout.findViewById(R.id.toast_textview);
        toast_text.setText(text);
        toast_text.setTypeface(chalkTypeFace);

        if (win)
            toast_text.setTextColor(Color.GREEN);
        else {
            if (!text.equals("Level " + LEVEL_NUM + " reached, Congrats!"))
                toast_text.setTextColor(Color.RED);
            else
                toast_text.setTextColor(Color.GREEN);
        }

        toast.setGravity(Gravity.CENTER_VERTICAL, 25, -275);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toast_layout);
        toast.show();
    }

    private void updateProgress(boolean type) {
        Animation update;
        final float LEVEL_INFLATER = (float) ((LEVEL_NUM - 1) * ATTEMPTS_PER_GAME);
        final float LETTER_PERCENTAGE = (float) ATTEMPTS_PER_GAME / (float) letter_arr.size();
        float _to, _from;

        if (type) {
            _from = LEVEL_INFLATER + LETTER_PERCENTAGE * ((float)letters_correct - 1);
            _to = LEVEL_INFLATER + LETTER_PERCENTAGE * ((float)letters_correct);
        }
        else {
            _from = LEVEL_INFLATER + LETTER_PERCENTAGE * ((float)letters_correct);
            _to = LEVEL_INFLATER;

            progress_bar.setProgressDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.progress_red));
            progress_bar.setMinimumHeight(5);
        }

        final float to = _to;
        final float from = _from;
        update = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                float value = from + (to - from) * interpolatedTime;
                progress_bar.setProgress((int) value);
            }
        };

        progress_bar.startAnimation(update);
    }
    /**
     * Method that retrieves a String containing the key for the background color to be set for
     * that particular activity instance. This key is generated in a previous activtity and passed
     * to the current activty in a CYCLIC_INTENT.
     */
    private void setBackgroundImg(){
        thisLayout = (LinearLayout) findViewById(R.id.MainLayout);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String background = "";

        if (!getIntent().getBooleanExtra("WIN", false))
            background = getIntent().getStringExtra("PREVIOUS_BG");
        else
            background = prefs.getString("BackgroundColor", "");

        switch(background){
            case "GREEN":
                thisLayout.setBackgroundResource(R.drawable.defaultboard);
                backgroundIfRestart = "GREEN";
                break;
            case "BLACK":
                thisLayout.setBackgroundResource(R.drawable.blackboard);
                backgroundIfRestart = "BLACK";
                break;
            case "RED":
                thisLayout.setBackgroundResource(R.drawable.redboard);
                backgroundIfRestart = "RED";
                break;
            case "BLUE":
                thisLayout.setBackgroundResource(R.drawable.blueboard);
                backgroundIfRestart = "BLUE";
                break;
            case "INDIGO":
                thisLayout.setBackgroundResource(R.drawable.indigoboard);
                backgroundIfRestart = "INDIGO";
                break;
            case "CYAN":
                thisLayout.setBackgroundResource(R.drawable.cyanboard);
                backgroundIfRestart = "CYAN";
                break;
            case "PURPLE":
                thisLayout.setBackgroundResource(R.drawable.purpleboard);
                backgroundIfRestart = "PURPLE";
                break;
            case "PINK":
                thisLayout.setBackgroundResource(R.drawable.pinkboard);
                backgroundIfRestart = "PINK";
                break;
            case "ORANGE":
                thisLayout.setBackgroundResource(R.drawable.orangeboard);
                backgroundIfRestart = "ORANGE";
                break;
            case "YELLOW":
                thisLayout.setBackgroundResource(R.drawable.yellowboard);
                backgroundIfRestart = "YELLOW";
                break;
            case "RANDOM":
                int[] _backgrounds = {R.drawable.defaultboard, R.drawable.blackboard, R.drawable.redboard,
                                      R.drawable.blueboard, R.drawable.cyanboard, R.drawable.indigoboard,
                                      R.drawable.purpleboard, R.drawable.pinkboard, R.drawable.orangeboard,
                                      R.drawable.yellowboard};

                Random random_color_generator = new Random();
                int random_drawable = random_color_generator.nextInt(_backgrounds.length);

                thisLayout.setBackgroundResource(_backgrounds[random_drawable]);

                switch(random_drawable) {
                    case 0: backgroundIfRestart = "GREEN";
                        break;
                    case 1: backgroundIfRestart = "BLACK";
                        break;
                    case 2: backgroundIfRestart = "RED";
                        break;
                    case 3: backgroundIfRestart = "BLUE";
                        break;
                    case 4: backgroundIfRestart = "CYAN";
                        break;
                    case 5: backgroundIfRestart = "INDIGO";
                        break;
                    case 6: backgroundIfRestart = "PURPLE";
                        break;
                    case 7: backgroundIfRestart = "PINK";
                        break;
                    case 8: backgroundIfRestart = "ORANGE";
                        break;
                    case 9: backgroundIfRestart = "YELLOW";
                        break;
                    default: backgroundIfRestart = "GREEN";
                }
                break;
            default:
                thisLayout.setBackgroundResource(R.drawable.defaultboard);
                backgroundIfRestart = "GREEN";
                break;
        }
    }

    private void initAssets() {
        // Initialize the global gameplay font for later use in initialization and updating
        chalkTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");
    }
    /**
     * Method that initializes the Button objects for the keyboard and hintKey
     */
    public void initKeyboard() {
        // link each keyboard Button object to the associated View ID and set the typeface
        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = (Button) findViewById(keyboardID[i]);
            keyboard[i].setTypeface(chalkTypeFace);
        }

        // link the hintKey Button object with its associated View ID and set the typeface
        hintKey = (Button) findViewById(R.id.HintKey);
        hintKey.setTypeface(chalkTypeFace);

        // set the OnClickListener for each keyboard Button
        for (Button aKeyboard : keyboard)
            aKeyboard.setOnClickListener(this);

        // set the OnClickListener for the hintKey Button
        hintKey.setOnClickListener(this);
    }
    /**
     * Method that initializes GameStatusArea, which shows the level, topics, and # of lifes and hints
     */
    public void initGameStatusArea() {
        // initialize the hangman ImageView
        hangman_img = (ImageView) findViewById(R.id.hangman_area);

        // create and link the four required TextView areas with their associated View IDs
        TextView LevelText =    (TextView) findViewById(R.id.LevelText),
                 CategoryText = (TextView) findViewById(R.id.CategoryText),
                 LifesText =    (TextView) findViewById(R.id.LifesText),
                 HintsText =    (TextView) findViewById(R.id.HintsText);

        // set the typeface for each TextView
        LevelText.setTypeface(chalkTypeFace);
        CategoryText.setTypeface(chalkTypeFace);
        LifesText.setTypeface(chalkTypeFace);
        HintsText.setTypeface(chalkTypeFace);

        if ((LEVEL_NUM == 10 || LEVEL_NUM == 15) && !reward_recieved && !isFreeplay()) {
            lifes += 2;
            hints += 2;
            reward_recieved = true;

            Toast.makeText(getApplicationContext(), "+2 LIFES", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "+2 HINTS", Toast.LENGTH_SHORT).show();
            showToast("Level " + LEVEL_NUM + " reached, Congrats!");
        }

        // if, at the current level, there are no more hints available to the user, disable the
        // hintKey button, make its text transparent,
        if (hints == 0 && !GAMETYPE.equals("FREEPLAY")) {
            hintKey.setTextColor(Color.TRANSPARENT);
            hintKey.setClickable(false);
            hintKey.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.erasermark));
        }

        String lifes_str = "", hints_str = "";

        // if GAMETYPE == "FREEPLAY", do not show the Level #. if not, do show the level number
        if (isFreeplay()) {
            String level_txt_str = "Freeplay";
            LevelText.setText(level_txt_str);

            LinearLayout top_left = (LinearLayout) findViewById(R.id.LevelAndCategory);
            top_left.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                   LinearLayout.LayoutParams.MATCH_PARENT, 0f));
        }
        else {
            // create a WordBank object to get the String values of the current level and associated category
            WordBank level_info = new WordBank(getApplicationContext(), getIntent().getIntExtra("LEVEL", -1));

            LevelText.setText(level_info.getLevelString());
            CategoryText.setText(level_info.getLevelCategoryString(level_info.getLevel()));

            lifes_str = "Lifes: " + Integer.toString(lifes);
            hints_str = "Hints: " + Integer.toString(hints);
        }

        LifesText.setText(lifes_str);
        HintsText.setText(hints_str);

        //initialize the progress bar
        progress_bar = (ProgressBar) findViewById(R.id.progressBar);
        if (isFreeplay()) {
            progress_bar.setProgressDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.progress_white));
            progress_bar.setMinimumHeight(5);
            progress_bar.setMax(FP_MAX * ATTEMPTS_PER_GAME);
            updateProgress(true);
        }

        updateProgress(true);
    }
    /**
     * Method that initializes the WordArea. Dynamically creates a TableLayout that sizes to the length
     * of the word. Row 1 contains the letters, which remain invisible until correctly selected by the user,
     * and Row 2 contains the an underline character for each letter.
     */
    public void initWordArea() {
        int _level = 0;

        if (!isFreeplay())
            _level = LEVEL_NUM;
        else if (isFreeplay() && win)
            LEVEL_NUM++;
        else if (isFreeplay() && !win)
            _level = FP_currentLevel;

        WordBank game_word = new WordBank(getApplicationContext(), _level);
        WORD = game_word.getGameWord();
        HINT = game_word.getGameHint();

        if (isFreeplay()) {
            TextView CategoryText = (TextView) findViewById(R.id.CategoryText);
            CategoryText.setText(game_word.getLevelCategoryString(game_word.getLevel()));
            CategoryText.setTypeface(chalkTypeFace);

            FP_currentLevel = game_word.getLevel();
        }

        TableLayout.LayoutParams table_params = new TableLayout.LayoutParams();
        TableLayout wordContainer = new TableLayout(this);

        TableRow.LayoutParams row_params = new TableRow.LayoutParams();
        row_params.weight = 1;

        for (int i = 0; i < 2; i++) {
            TableRow _row = new TableRow(this);

            for (int j = 0; j < WORD.length(); j++) {
                TextView view = new TextView(this);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setTypeface(chalkTypeFace);
                view.setGravity(Gravity.CENTER);

                if (WORD.length() > 13)
                    view.setTextSize(17);
                else if (WORD.length() > 16)
                    view.setTextSize(15);
                else
                    view.setTextSize(20);

                if (i == 0) {
                    view.setText(Character.toString(WORD.charAt(j)));

                    if (!Character.isLetter(WORD.charAt(j)))
                        view.setTextColor(Color.WHITE);
                    else {
                        view.setTextColor(Color.TRANSPARENT);
                        letter_arr.add(view);
                   }
                }
                else {
                    if (WORD.length() < 13)
                        view.setText(R.string.WordUnderline);
                    else if (WORD.length() >= 13 && WORD.length() < 16)
                        view.setText(R.string.WordUnderline2);
                    else if (WORD.length() >= 16)
                        view.setText(R.string.WordUnderline3);

                    if (!Character.isLetter(WORD.charAt(j)))
                        view.setTextColor(Color.TRANSPARENT);
                    else
                        view.setTextColor(Color.WHITE);
                }

                _row.addView(view, row_params);
            }

            wordContainer.addView(_row, table_params);
        }

        ScrollView wordAreaContainer = (ScrollView) findViewById(R.id.wordAreaContainer);
        wordAreaContainer.addView(wordContainer);
    }

    public void refreshHangmanArea(int stage_num) {
        switch (stage_num) {
            case 0:
                hangman_file_path = "gameplay_images/stage0.png";
                break;
            case 1:
                hangman_file_path = "gameplay_images/stage1.png";
                break;
            case 2:
                hangman_file_path = "gameplay_images/stage2.png";
                break;
            case 3:
                hangman_file_path = "gameplay_images/stage3.png";
                break;
            case 4:
                hangman_file_path = "gameplay_images/stage4.png";
                break;
            case 5:
                hangman_file_path = "gameplay_images/stage5.png";
                break;
            case 6:
                hangman_file_path = "gameplay_images/stage6.png";
        }


        hangman_img = (ImageView) findViewById(R.id.hangman_area);

        try {
            InputStream imgStream = getAssets().open(hangman_file_path);
            Drawable imgDrawable = Drawable.createFromStream(imgStream, null);
            hangman_img.setImageDrawable(imgDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("End This Game?")
                .setMessage("You will loose all progress if you continue.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mainMenuIntent = new Intent(Gameplay.this, StartScreen.class);
                        mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        Gameplay.this.finish();
                        startActivity(mainMenuIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }).create().show();
    }

    @Override
    protected void onStart() {
        // Once the UI is initialized, no additional processing needs to be done in onStart() since
        // Gameplay extends OnClickListener, and in onClick() all the gameplay processing is done.
        super.onStart();
    }

    @Override
    protected void onRestart() {
        // Just as no additional processing needs to be done in onStart(), nothing needs to be done
        // in onRestart()
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // Everything is saved in gloabal variables, so there is nothing to reinitialize
        super.onResume();
    }

    @Override
    protected void onPause() {
        // There is no ongoing processes that run throughout the activity, so nothing needs to be
        // stopped or saved for a later resume
        super.onPause();
    }

    @Override
    protected void onStop() {
        // Since there is no ongoing processes that make gameplay possible, nothing needs to be
        // stopped here
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // No ongoing threads to be stopping here
        super.onDestroy();
    }
}