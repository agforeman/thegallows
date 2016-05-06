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
import java.io.IOException;
import java.util.Random;

/**
 * The Gameplay class extends Activity and is the most critical activity of the Gallows app, as it
 * is solely responsible for hangman gameplay. This class both generates the layout for the gameplay,
 * and contains the logic that makes gameplay possible.
 *
 * The Gameplay Activity is a recursive activity that will keep destroying and creating itself until
 * the terminating condition of a game over occurs. The recursive functionality of Gameplay is due to
 * a CYCLIC_INTENT that initializes the the gameplay components during Activity creation, and then
 * loads the updated version of these components back into itself to repeat the process during the
 * next activity. The CYCLIC_INTENT is altered to launch the Endgame Activity when a game over is
 * reached, which terminates the recursive sequence.
 *
 * The recursive sequence of the Gameplay Activity is the actual game, which is the purpose of the app.
 * There can be two different recursive sequences: Regular and Freeplay, which are dependent upon the
 * the initial conditions of CYCLIC_INTENT set by the iterative StartScreen Activity.
 *
 * @author  Justin Shapiro
 * @version 7.0
 * @since   2016-04-10
 *
*/
public class Gameplay extends Activity implements OnClickListener {

    /**********************************************************************************************/
    /*******************************GLOBAL VARIABLES & OBJECTS*************************************/
    /**********************************************************************************************/

    /**
     * Stores a constant containing the number of attempts a user has to get a word right. This
     * can't change unless we make more hangman images. Classically, this number is 6.
     */
    private final int ATTEMPTS_PER_GAME = 6;
    /**
     * Stores the current level number. Used as a counter in Freeplay mode.
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
     * need to name the variable each keyboard Button.
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
     * Sets the transition delay based on whether animations are enabled or not.
     */
    private int CYCLIC_DELAY = 1000;
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
     * moving onto the next, random, level.
     */
    public int FP_currentLevel;
    /**
     * Stores the maximum amount of Gameplay activities that a CYCLIC_INTENT can exist in.
     */
    public int FP_MAX;
    /**
     * Stores the number of lifes and hints the user currently has left in a REGULAR or FREEPLAY mode.
     */
    public int lives, hints;
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
     * activity until the user passes the LEVEL_NUM at which that activity is set.
     */
    public String backgroundIfRestart;
    /**
     * A LayoutInflater object that allows a custom Toast to be generated.
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
    /**
     * Determines if the user has already received a reward for the current level.
     * Used to prevent duplicate rewards.
     */
    private boolean reward_received = false;
    /**
     * Method that returns true if the current game mode is Freeplay, false if Regular Mode.
     * @return Boolean value indicating Freeplay mode
     */
    private boolean isFreeplay() { return GAMETYPE.equals("FREEPLAY"); }
    /**
     * Method that returns true if the selected character from the keyboard is in the WORD being solved for.
     * @param selection The char value of the text contained in the keyboard Button selected by the user.
     * @return Boolean value indicating the validity of the letter
     */
    private boolean isValidLetter(char selection) { return WORD.indexOf(selection) >= 0; }
    /**
     * Method that initializes all critical values that determine just about every aspect of the game.
     * That being said, this method retrieves all Intents passed in. This method is critical to
     * Gameplay's recursive nature.
     */
    private void retrieveIntents() {
        GAMETYPE = getIntent().getStringExtra("GameType");
        win = getIntent().getBooleanExtra("WIN", true);
        backgroundIfRestart = getIntent().getStringExtra("PREVIOUS_BG");
        LEVEL_NUM = getIntent().getIntExtra("LEVEL", -1);
        lives = getIntent().getIntExtra("LIFE", -1);
        hints = getIntent().getIntExtra("HINTS", -1);
        warningShowed = getIntent().getBooleanExtra("LIFE_WARNING", false);
        reward_received = getIntent().getBooleanExtra("REWARD", false);

        // determine whether or not to set the reward_received flag back to false after the user
        // has cleared a level in which they received a reward when starting
        if ((LEVEL_NUM == 11 || LEVEL_NUM == 16) && reward_received)
            reward_received = false;

        // only set the set the variables related to Freeplay gameplay if Freeplay mode is selected
        if (isFreeplay()) {
            FP_MAX = getIntent().getIntExtra("FP_MAX", -1);
            FP_currentLevel = getIntent().getIntExtra("LAST_FP", -1);
        }

        // determine if the recursion of Gameplay will terminate if the user looses this instance
        // and notify the user via a Toast warning
        if ((lives == 0 && !warningShowed && !isFreeplay())) {
            showToast("LAST TRY!");
            warningShowed = true;
        }
    }


    /**********************************************************************************************/
    /************THE FOLLOWING METHODS PERFORM OPERATIONS RELATED TO HANGMAN GAMEPLAY**************/
    /**********************************************************************************************/


    /**
     * Solely responsible for all gameplay after the initialization of the UI.
     * It will call helper methods along the way to reduce complexity.
     * @param click The View object assigned to the to-be-Button.
     */
    public void onClick(final View click) {
        // cast the active View to a Button and store in a variable
        final Button guess = (Button) click;

        // immediately prevent a double-click scenario by disabling the Button
        click.setEnabled(false);

        // determine whether or not the clicked object was assigned to be the hintKey
        // modify gameplay appropriately based on the conditional statement
        if (guess == hintKey) {
            // produce the hint dialog if the user has hints left, they have already used a hint
            // on the current level, or if the game is in Freeplay mode. Either of these three things
            // can be true in order for a hint to display
            if (hints != 0 || hintSelected || isFreeplay()) {
                // create new thread for producing the hint dialog, acknowledging that it will have to be
                // run on the UI thread since it contains View objects.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update the text color of hintKey to indicate that it was selected
                        hintKey.setTextColor(Color.BLUE);

                        // update the TextView in GameStatusArea to show the new number of hints
                        TextView HintsText = (TextView) findViewById(R.id.HintsText);
                        String new_hints_text_str = "Hints: " + Integer.toString(hints);
                        HintsText.setText(new_hints_text_str);

                        // call the helper method that produces and displays the hint dialog
                        createHintDialog();

                        // after the hint dialog has displayed for the first time on a level,
                        // update the gameplay components accordingly
                        if (!hintSelected) {
                            // decrement the hints the user has, assuming we are not in Freeplay mode
                            if (!isFreeplay())
                                hints--;

                            // mark that we have already decremented the hints for the user
                            hintSelected = true;

                            // update the text of hintKey to notify the user that they are allowed to
                            // see the hint again now that they have used a hint
                            String curr_hint_str = "Show Hint Again";
                            guess.setText(curr_hint_str);
                            guess.setTextSize(15);
                        }

                        // only in the case that the noisy View object is the hintKey does the
                        // click functionality get enabled again
                        click.setEnabled(true);
                    }
                });
            }
        }
        // if the noisy View object was not the hintKey, it is a keyboard key and gameplay logic
        // will now start being implemented
        else {
            // retrieve the letter of the selected Button
            final char button_letter = guess.getText().toString().charAt(0);

            // if the letter selected is a subset of the WORD, call the updateAllOccurrences helper
            // method to update the UI notifying the user that they have made the correct guess and
            // set the required components for further advancement in gameplay
            if (isValidLetter(button_letter)) {
                // since UpdateAllOccurrences is an O(n) method, it's best to give it its own UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateAllOccurrences(button_letter);
                    }
                });
            }
            // if the Button selected does not contain text that is a subset of WORD, decrement the user's
            // number of attempts and call the helper method that places the appropriate state of the hangman
            // character in the ImageView
            else {
                attempts--;
                refreshHangmanArea(Math.abs(attempts - 6));
            }

            // disable any function that the View still has
            click.setClickable(false);

            // remove the text from the selected Button and place a drawable of an eraser image
            // in its background to indicate it can not be used again in this level
            ((Button) click).setTextColor(Color.TRANSPARENT);
            click.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.erasermark));

            // finally, call the gameOver helper method with the appropriate boolean value that
            // resulted from this current click event

            // the user cannot continue playing the current level if they are out of attempts with
            // all of the valid letters in WORD not being in letter_arr
            if (attempts == 0 && letters_correct != letter_arr.size())
                gameOver(false);
            // the user can continue playing the current level if the number of letters they
            // got correct is equal to the size of the letter_arr of valid letters
            else if (letters_correct == letter_arr.size()) {
                // call the disableKeyboard helper method to ensure the user cannot select anymore
                // letters after the win/lose logic has been determined
                disableKeyboard();
                gameOver(true);
            }
        }
    }
    /**
     * Helper method to onClick, which create a dialog to display a hint.
     */
    private void createHintDialog() {
        // create and set parameters for the Dialog object
        final Dialog hintWindow = new Dialog(Gameplay.this);
        hintWindow.requestWindowFeature(Window.FEATURE_NO_TITLE); // remove the preset Title of the Dialog object
        hintWindow.setContentView(R.layout.hint_dialog_layout); // set the layout of the Dialog object to the specifications in xml

        // if animations are enabled, make the dialog slide in the screen
        if (prefs.getBoolean("EnableAnimations", true))
            hintWindow.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // create and instantiate a TextView with parameters from xml that acts as the title of the dialog
        TextView hintWindowTitle = (TextView) hintWindow.findViewById(R.id.hintWindowTitle);
        String hint_window_title_str = "Hint";
        hintWindowTitle.setText(hint_window_title_str);
        hintWindowTitle.setTypeface(chalkTypeFace); // give it the game's typeface

        // create and instantiate a TextView with parameters from xml that hold the hint string in the dialog
        TextView hintTextArea = (TextView) hintWindow.findViewById(R.id.hintText);
        hintTextArea.setText(HINT);

        // initialize and set a listener on the Close button of the dialog
        Button closeHintWindow = (Button) hintWindow.findViewById(R.id.close_hint);
        closeHintWindow.setTypeface(chalkTypeFace); // give it the game's typeface
        closeHintWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKey.setTextColor(Color.BLACK); // restore the color of the hintKey to normal
                hintWindow.dismiss();
            }
        });

        // after all initialization has been completed, display the dialog to the user
        hintWindow.show();
    }
    /**
     * Helper method to onClick, which updates the WordArea in the locations of the button_letter param.
     * @param button_letter The valid character corresponding to the user's selection that initiated onClick.
     */
    private void updateAllOccurrences(char button_letter) {
        // iterate through letter_arr and make all instances the value of button_letter visible
        // while updating the letters_correct counter and calling the helper method updateProgress
        // to update the status bar
        for (int i = 0; i < letter_arr.size(); i++) {
            if (button_letter == letter_arr.get(i).getText().toString().charAt(0)) {
                letter_arr.get(i).setTextColor(Color.WHITE);
                if (prefs.getBoolean("EnableAnimations", true))
                    letter_arr.get(i).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate2));

                letters_correct++;
                updateProgress(true);
            }
        }
    }
    /**
     * Helper method to onClick, which disables all keys on the keyboard from being selected.
     */
    private void disableKeyboard() {
        for (Button aKeyboard : keyboard) aKeyboard.setEnabled(false);
        hintKey.setEnabled(false);
    }
    /**
     * Helper method to onClick, and a primary method in Gameplay. Based on the current state of all
     * gameplay components (certain variables), the next stage in the game will be determined.
     * @param _win The win/lose result of an instance of Gameplay determined by onClick.
     */
    protected void gameOver(boolean _win) {
        // set the global win variable with the win/lose result
        win = _win;

        // highlight the WordArea letters green or red for dramatic effect
        for (int i = 0; i < letter_arr.size(); i++) {
            if (!win) {
                letter_arr.get(i).setTextColor(Color.RED);
                // only if a loss has occurred with the updateProgress helper method be called with a false parameter
                updateProgress(false);
            }
            else
                letter_arr.get(i).setTextColor(Color.GREEN);
        }

        // show a Toast to the user with the result of the logic computation
        if (win)
            showToast("Level Cleared!");
        else
            showToast("You Died!");

        // if this current mode is not Freeplay, execute a set of comparisons to determine the
        // boolean value of launchActivity, the next state of the game
        if (!isFreeplay()) {
            // if the current level is not the last, perform this subset of comparisons
            if (LEVEL_NUM != NUM_LEVELS) {
                // if a win has occurred, increment the global level number of this recursive instance
                // and launch Gameplay again with this updated number
                if (win) {
                    LEVEL_NUM++;
                    launchActivity(true);
                }
                // if a loss has occured, only if lifes is zero will the user not be returned to a new
                // Gameplay instance
                else {
                    // if lifes is not equal to zero but a loss has occurred, decrement the user's number of lifes
                    // and launch a new Gameplay instance with the same level number
                    if (lives != 0) {
                        lives--;
                        launchActivity(true);
                    }
                    // if lifes is equal to zero and a loss has occurred, the game is over
                    else
                        launchActivity(false);
                }
            }
            // if the current level is the last, perform this subset of comparisons
            else {
                // if a win has occurred at the last level, the game is over
                if (win)
                    launchActivity(false);
                // if a loss has occurred at the last level, the user the game is not necessarily over
                else {
                    // if lifes is not equal to zero during a loss at the last level, decrement the user's number
                    // of lifes and launch a new Gameplay instance with the same level number
                    if (lives != 0) {
                        lives--;
                        launchActivity(true);
                    }
                    // if lifes is equal to zero and there is a loss at the last level, the game is over
                    else
                        launchActivity(false);
                }
            }
        }
        // if the current mode of the game is Freeplay, a unique set of logic occurs
        else if (isFreeplay()) {
            // if a win has occurred perform the following subset of logic
            if (win) {
                // if the amount of words the user has completed on Freeplay is equal to the number
                // that is specified in SharedPreferences, the game is over
                if (LEVEL_NUM == FP_MAX)
                    launchActivity(false);
                // otherwise, create a new Gameplay activity with a new level
                else
                    launchActivity(true);
            }
            // if a loss has occurred, a new Gameplay activity is created with the same level
            else
                launchActivity(true);
        }
    }
    /**
     * Method that advances the game forward by launching either a Gameplay or an Endgame activity,
     * depending on the value of the resume_cyclic parameter computed by gameOver().
     * @param resume_cyclic The cyclic nature of Gameplay continues if true, recursion is terminated if false.
     */
    private void launchActivity(boolean resume_cyclic) {
        // CYCLIC_INTENT is instantiated here, with a flag to launch the next iteration of the game
        if (resume_cyclic)
            CYCLIC_INTENT = new Intent(this, Gameplay.class);
        else
            CYCLIC_INTENT = new Intent(this, Endgame.class);

        // call the loadIntents method to load CYCLIC_INTENT with the required components that will
        // ensure Gameplay or Endgame correctly perform their function during the next stage of the game
        loadIntents();

        // wait a certain amount of time (CYCLIC_DELAY) before launching the next Activity. This gives
        // the use time to view the result of the UI updates that occurred during the beginning of gameOver
        Handler pause = new Handler();
        pause.postDelayed(new Runnable() {
            public void run() {
                startActivity(CYCLIC_INTENT);
                Gameplay.this.finish();
                if (prefs.getBoolean("EnableAnimations", true)) {
                    if (!win)
                        overridePendingTransition(R.anim.fade_in_alt, R.anim.fade_out_alt);
                    else
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        }, CYCLIC_DELAY);
    }
    /**
     * Method that loads the CYCLIC_INTENT with the required Extras for use in further gameplay.
     */
    private void loadIntents() {
        CYCLIC_INTENT.putExtra("GameType", GAMETYPE);
        CYCLIC_INTENT.putExtra("WIN", win);
        CYCLIC_INTENT.putExtra("PREVIOUS_BG", backgroundIfRestart);
        CYCLIC_INTENT.putExtra("LEVEL", LEVEL_NUM);
        CYCLIC_INTENT.putExtra("LIFE", lives);
        CYCLIC_INTENT.putExtra("HINTS", hints);
        CYCLIC_INTENT.putExtra("LIFE_WARNING", warningShowed);
        CYCLIC_INTENT.putExtra("REWARD", reward_received);

        // like in the retrieveIntents method, only load intents relating to Freeplay if we are in that mode
        if (isFreeplay()) {
            CYCLIC_INTENT.putExtra("FP_MAX", FP_MAX);
            CYCLIC_INTENT.putExtra("LAST_FP", FP_currentLevel);
        }

        // flag CYCLIC_INTENT to destroy the last activity from the activity stack, as it is no longer needed
        CYCLIC_INTENT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    /**
     * Method that generates custom Toast dialogs for the Gameplay activity.
     * @param text The text that is displayed in the custom Toast.
     */
    private void showToast(String text) {
        Toast toast = new Toast(getApplicationContext());

        // initialize the layout inflater to set a predefined xml layout to the Toast dialog in the toast_layout
        toast_inflater = getLayoutInflater();
        toast_layout = toast_inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));

        // initialize the text area inside the toast
        toast_text = (TextView) toast_layout.findViewById(R.id.toast_textview);
        toast_text.setText(text);
        toast_text.setTypeface(chalkTypeFace); // give it the game's typeface

        // set the text to green if the win variable is true, since Toasts are only used in the
        // sense that notifies users of their win/lose status
        if (win)
            toast_text.setTextColor(Color.GREEN);
        // if the win variable is not true, it this Toast can be used to display a dialog indicating loss
        // or used to display a dialog that the user as reached a reward point in the game
        else {
            // if the text passed in is not equal to the reward message, set it equal to red
            if (!text.equals("Level " + LEVEL_NUM + " reached, Congrats!"))
                toast_text.setTextColor(Color.RED);
            // if the text passed in is equal to the reward message, set it equal to green
            else
                toast_text.setTextColor(Color.GREEN);
        }

        // set the parameters of the custom Toast's to display in a particular location on the screen
        toast.setGravity(Gravity.CENTER_VERTICAL, 25, -275);
        // set the length of the custom Toast's duration
        toast.setDuration(Toast.LENGTH_SHORT);
        // apply the inflated layout to the Toast
        toast.setView(toast_layout);
        // show the toast to the user
        toast.show();
    }
    /**
     * Method that uses specialized logic to use the current game parameters to update the ProgressBar object.
     * @param type Boolean value that is true if this is a positive update, false if a negative update
     */
    private void updateProgress(boolean type) {
        // create an Animation object that is used update the ProgressBar in real-time with accurate increments/decrements
        Animation update;

        // create and define a multiplicative constant used in progress calculation equal to the progress
        // a user is at during the very beginning of a particular level before Buttons were pressed
        final float LEVEL_INFLATER = (float) ((LEVEL_NUM - 1) * ATTEMPTS_PER_GAME);

        // create and define a constant that determines which percentage of the overall game one letter counts for
        // this percentage will change for each word
        final float LETTER_PERCENTAGE = (float) ATTEMPTS_PER_GAME / (float) letter_arr.size();

        // used to instantiate parameters of the Animation that shows the progress to the user in real-time
        float _to, _from;

        // if this is a Positive update, define location identifiers for the previous progress and current progress
        if (type) {
            _from = LEVEL_INFLATER + LETTER_PERCENTAGE * ((float)letters_correct - 1);
            _to = LEVEL_INFLATER + LETTER_PERCENTAGE * ((float)letters_correct);
        }
        // if this is a Negative update, define location identifiers for the previous progress and the progress
        // that the game was at before the level occurred
        else {
            _from = LEVEL_INFLATER + LETTER_PERCENTAGE * ((float)letters_correct);
            _to = LEVEL_INFLATER;

            // in the case of a Negative update, set the color of the progress bar to red
            progress_bar.setProgressDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.progress_red));
            // makes sure to reconstrain the size of the ProgressBar after the new Drawable is given to it
            progress_bar.setMinimumHeight(5);
        }

        // initialize Animation parameters
        final float to = _to;
        final float from = _from;

        // define the animation with the calculated locations of previous and current progress
        // this will not look like a smooth Animation, but it is required for real-time updates
        // of overall game progress while pressing keys. Otherwise, the ProgressBar would only update
        // after each level. This is a workaround to such frustrating constraint
        update = new Animation() {
            // ref: http://stackoverflow.com/a/18015071
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                float value = from + (to - from) * interpolatedTime;
                progress_bar.setProgress((int) value);
            }
        };

        // perform the update to the ProgressBar by starting the Animation
        progress_bar.startAnimation(update);
    }
    /**
     * Overridden method from Activity that properly handles the even in which the user presses the BACK
     * button on the Android device. In this case, the user will be warned before the app is forced by the
     * system to return to the previous activity. Since there is no previous activity (they all get terminated),
     * the app will close if this even is not handled properly.
     */
    @Override
    public void onBackPressed(){
        // create an AlertDialog that warns the user that they will loose all their game progress
        // if they let the system action proceed
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("End This Game?")
                .setMessage("You will lose all progress if you continue.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mainMenuIntent = new Intent(Gameplay.this, StartScreen.class);
                        mainMenuIntent.putExtra("firstrun", false);
                        mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Gameplay.this.finish();
                        startActivity(mainMenuIntent);

                        if (prefs.getBoolean("EnableAnimations", true))
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }).create().show();
    }


    /**********************************************************************************************/
    /*******************THE FOLLOWING METHODS SERVE ONLY TO INITIALIZE THE UI**********************/
    /**********************************************************************************************/


    /**
     * Overridden method from Activity that initializes the UI during activity creation.
     * After this method is complete, onClick handles the rest of the activity.
     * @param savedInstanceState Used to store a saved state of the application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* be sure that the screen will remain in portrait mode even when the device's orientation
         * is physically adjusted. Although this was also specified in the Manifest file, we need to
         * be extra sure that this activity will remain in portrait mode or else the game will reset
         */ setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // setup the activity to run and assign it a layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        /************************* THE GOLDEN SEVEN **************************************/
        // Initialize UI with the following function calls. DO NOT REARRANGE THEIR ORDER!!/
        /*******FIRST*************/initPrefsAndAssets();/*******(preliminary)*************/
        /*******SECOND************/retrieveIntents();/**********(preliminary)*************/
        /*******THIRD*************/setBackgroundImg();/***********************************/
        /*******FOURTH************/initKeyboard();/***************************************/
        /*******FIFTH*************/initWordArea();/***************************************/
        /*******SIXTH*************/initGameStatusArea();/*********************************/
        /*******SEVENTH***********/refreshHangmanArea(Math.abs(attempts - 6));/***********/
        /******** After the above function calls take place, onClick handles the rest ****/
        /*********************************************************************************/
    }
    /**
     * Method that defines what the game preferences and fonts are.
     */
    private void initPrefsAndAssets() {
        // load SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // initialize the global gameplay font for later use in initialization and updating the UI
        chalkTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");
    }
    /**
     * Method that retrieves a String containing the key for the background color to be set for
     * that particular activity instance. This key is generated in a previous activtity and passed
     * to the current activity in a CYCLIC_INTENT.
     */
    private void setBackgroundImg(){
        // instantiate the main LinearLayout of the activity defined in xml
        thisLayout = (LinearLayout) findViewById(R.id.MainLayout);

        // create String variable that holds the
        String background;

        // determine if this method should load the previous background color due to a loss
        if (!getIntent().getBooleanExtra("WIN", false))
            background = getIntent().getStringExtra("PREVIOUS_BG");
        else
            background = prefs.getString("BackgroundColor", "");

        // set the current background selected above to the restore variable that will get used
        // in the next activity if there is a loss
        backgroundIfRestart = background;

        // select the case that matches the contents of background its corresponding drawable to
        // the background of the activities main layout
        switch(background){
            case "GREEN":  thisLayout.setBackgroundResource(R.drawable.defaultboard); break;
            case "BLACK":  thisLayout.setBackgroundResource(R.drawable.blackboard);   break;
            case "RED":    thisLayout.setBackgroundResource(R.drawable.redboard);     break;
            case "BLUE":   thisLayout.setBackgroundResource(R.drawable.blueboard);    break;
            case "INDIGO": thisLayout.setBackgroundResource(R.drawable.indigoboard);  break;
            case "CYAN":   thisLayout.setBackgroundResource(R.drawable.cyanboard);    break;
            case "PURPLE": thisLayout.setBackgroundResource(R.drawable.purpleboard);  break;
            case "PINK":   thisLayout.setBackgroundResource(R.drawable.pinkboard);    break;
            case "ORANGE": thisLayout.setBackgroundResource(R.drawable.orangeboard);  break;
            case "VIOLET": thisLayout.setBackgroundResource(R.drawable.violetboard);  break;
            case "YELLOW": thisLayout.setBackgroundResource(R.drawable.yellowboard);  break;
            case "RANDOM": // in this case, a random number generator is used to randomly choose
                           // any of the above resource IDs as the background for this level

                // create an array of background resource IDs so that its index can be randomly generated
                int[] _backgrounds = {R.drawable.defaultboard, R.drawable.blackboard, R.drawable.redboard,
                                      R.drawable.blueboard, R.drawable.cyanboard, R.drawable.indigoboard,
                                      R.drawable.purpleboard, R.drawable.pinkboard, R.drawable.orangeboard,
                                      R.drawable.yellowboard, R.drawable.violetboard};

                // create the random number generator and store a random number in a variable
                Random random_color_generator = new Random();
                int random_drawable = random_color_generator.nextInt(_backgrounds.length);

                // use the random number produced above as the index of the resource ID array and
                // set the main activity layout background as the output resource ID
                thisLayout.setBackgroundResource(_backgrounds[random_drawable]);

                // redefine the background restore variable since we are not sure of what
                // background we have been given
                switch(random_drawable) {
                    case 0:  backgroundIfRestart  =  "GREEN";  break;
                    case 1:  backgroundIfRestart  =  "BLACK";  break;
                    case 2:  backgroundIfRestart  =  "RED";    break;
                    case 3:  backgroundIfRestart  =  "BLUE";   break;
                    case 4:  backgroundIfRestart  =  "CYAN";   break;
                    case 5:  backgroundIfRestart  =  "INDIGO"; break;
                    case 6:  backgroundIfRestart  =  "PURPLE"; break;
                    case 7:  backgroundIfRestart  =  "PINK";   break;
                    case 8:  backgroundIfRestart  =  "ORANGE"; break;
                    case 9:  backgroundIfRestart  =  "YELLOW"; break;
                    default: backgroundIfRestart  =  "GREEN";
                }
                break;
            default: // this case really shouldn't ever be reached
                thisLayout.setBackgroundResource(R.drawable.defaultboard);
                backgroundIfRestart = "GREEN"; // reminds us that in SharedPreferences, Green is default
                break;
        }
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
     * Method that initializes GameStatusArea, which shows the level, topics, and # of lives and hints
     */
    public void initGameStatusArea() {
        // initialize the hangman ImageView
        hangman_img = (ImageView) thisLayout.findViewById(R.id.hangman_area);

        // create and link the four required TextView areas with their associated View IDs
        TextView LevelText =    (TextView) findViewById(R.id.LevelText),
                 CategoryText = (TextView) findViewById(R.id.CategoryText),
                 LivesText =    (TextView) findViewById(R.id.LifesText),
                 HintsText =    (TextView) findViewById(R.id.HintsText);

        // set the typeface for each TextView
        LevelText.setTypeface(chalkTypeFace);
        CategoryText.setTypeface(chalkTypeFace);
        LivesText.setTypeface(chalkTypeFace);
        HintsText.setTypeface(chalkTypeFace);

        // at this point, determine if the current level is eligible for a reward
        if ((LEVEL_NUM == 10 || LEVEL_NUM == 15) && !reward_received && !isFreeplay()) {
            lives += 2; // reward of 2 lives
            hints += 2; // reward of 2 hints
            reward_received = true; // set flag that award was granted so it is not granted again of this level

            // update the user via Toast's that they are being rewarded
            showToast("Level " + LEVEL_NUM + " reached, Congrats!");
            Toast.makeText(getApplicationContext(), "+2 LIVES", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "+2 HINTS", Toast.LENGTH_SHORT).show();
        }

        // if, at the current level, there are no more hints available to the user, disable the
        // hintKey button, make its text transparent,
        if (hints == 0 && !GAMETYPE.equals("FREEPLAY")) {
            hintKey.setTextColor(Color.TRANSPARENT);
            hintKey.setClickable(false);
            hintKey.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.erasermark));
        }

        String lives_str = "", hints_str = "";

        // if GAMETYPE == "FREEPLAY", do not show the Level #. if not, do show the level number
        if (isFreeplay()) {
            String level_txt_str = "Freeplay";
            LevelText.setText(level_txt_str);

            // if the mode is Freeplay, we have unlimited hints and lives, so we can get rid of the
            // right area of the GameStatusArea by updating the layout params of the area
            LinearLayout top_left = (LinearLayout) findViewById(R.id.LevelAndCategory);
            top_left.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                   LinearLayout.LayoutParams.MATCH_PARENT, 0f));
        }
        // if GAMETYPE != "FREEPLAY", initialize all GameStatusArea components like normal
        else {
            // create a WordBank object to get the String values of the current level and associated category
            WordBank level_info = new WordBank(getApplicationContext(), getIntent().getIntExtra("LEVEL", -1));

            LevelText.setText(level_info.getLevelString());
            CategoryText.setText(level_info.getLevelCategoryString(level_info.getLevel()));

            lives_str = "Lives: " + Integer.toString(lives);
            hints_str = "Hints: " + Integer.toString(hints);
        }

        LivesText.setText(lives_str);
        HintsText.setText(hints_str);

        // initialize the progress bar
        progress_bar = (ProgressBar) findViewById(R.id.progressBar);
        // if the current mode is Freeplay, update the style and MAX of the progress bar
        if (isFreeplay()) {
            progress_bar.setProgressDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.progress_white));
            progress_bar.setMinimumHeight(5);
            progress_bar.setMax(FP_MAX * ATTEMPTS_PER_GAME);
            updateProgress(true);
        }

        updateProgress(true); // yes, for some odd reason this needs to be called twice if GAMETYPE == "FREEPLAY"
    }
    /**
     * Method that initializes the WordArea. Dynamically creates a TableLayout that sizes to the length
     * of the word. Row 1 contains the letters, which remain invisible until correctly selected by the user,
     * and Row 2 contains the an underline character for each letter.
     */
    public void initWordArea() {
        // create the variable that will hold the level of the word we want to retrieve from the WordBank
        int _level = 0;

        // if we are not in Freeplay mode, this level is always the current level number
        if (!isFreeplay())
            _level = LEVEL_NUM;
        // if we are in Freeplay mode and we previously won, we want to increment LEVEL_NUM, which is used
        // as a progress counter in Freeplay mode
        else if (isFreeplay() && win)
            LEVEL_NUM++;
        // if we are in Freeplay mode and we previously lost, we want to replay the last level
        else if (isFreeplay() && !win)
            _level = FP_currentLevel;

        WordBank game_word = new WordBank(getApplicationContext(), _level);

        // DO NOT REORDER THE FOLLOWING TWO LINES, IT WILL CRASH THE APP!
        WORD = game_word.getGameWord(); // THIS METHOD NEEDS TO BE CALLED FIRST
        HINT = game_word.getGameHint(); // THIS METHOD NEEDS TO BE CALLED SECOND

        // if we are in Freeplay mode, we will update GameStatusArea with this category now that we have the level
        if (isFreeplay()) {
            TextView CategoryText = (TextView) findViewById(R.id.CategoryText);
            CategoryText.setText(game_word.getLevelCategoryString(game_word.getLevel()));
            CategoryText.setTypeface(chalkTypeFace);

            FP_currentLevel = game_word.getLevel();
        }

        /*** THE REST OF THIS METHOD CREATES A DYNAMICALLY SIZED TABLE CUSTOMIZED FOR HANGMAN GAMEPLAY ***/

        // create the actual TableLayout that the WordArea is composed of
        TableLayout wordContainer = new TableLayout(this);

        // create the layout params for the TableLayout we will use to accomplish the custom TableLayout
        TableLayout.LayoutParams table_params = new TableLayout.LayoutParams(); // we don't actually set any params
        TableRow.LayoutParams row_params = new TableRow.LayoutParams(); // we'll set one param

        // at this time we want to set the layout weight of the rows to be 1
        row_params.weight = 1;

        // the following for-loop will iterate twice, once for each row
        // during each iteration, a nested for-loop will create the individual columns
        for (int i = 0; i < 2; i++) {
            // create the row to put the next set of columns in
            TableRow _row = new TableRow(this);

            // create the columns of the particular row
            for (int j = 0; j < WORD.length(); j++) {
                // create the TextView that we will first format to our liking before adding to the row
                TextView view = new TextView(this);

                // set the stylistic and formatting requirements for the TextView
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setTypeface(chalkTypeFace);
                view.setGravity(Gravity.CENTER);

                // determine how to size the font of the TextView contents based on the length of the word
                if (WORD.length() > 13)
                    view.setTextSize(17);
                else if (WORD.length() > 16)
                    view.setTextSize(15);
                else
                    view.setTextSize(20);

                // if i == 0, the current column will contain a letter of the WORD
                if (i == 0) {
                    // set the text in the TextView to the jth character of the WORD
                    view.setText(Character.toString(WORD.charAt(j)));

                    // if the jth character of the WORD is a number or symbol, display it
                    if (!Character.isLetter(WORD.charAt(j)))
                        view.setTextColor(Color.WHITE);
                    // if the jth character of the WORD is a letter, make it transparent for now and
                    // add it to the letter_arr so we can modify it later
                    else {
                        view.setTextColor(Color.TRANSPARENT);
                        letter_arr.add(view);
                   }
                }
                // if i == 1, the curren column will contain the underline of the letter
                else {
                    // based on the size of the WORD, determine how the underline is composed
                    // we want to be sure that the underline will not break into two seperate lines
                    // within the TextView because it would look like there's glitch creating an extra row
                    if (WORD.length() < 11)
                        view.setText(R.string.WordUnderline);
                    else if (WORD.length() >= 11 && WORD.length() < 14)
                        view.setText(R.string.WordUnderline2);
                    else if (WORD.length() >= 14)
                        view.setText(R.string.WordUnderline3);

                    // if the jth character of the word is a number of symbol, there's no need for an underline
                    if (!Character.isLetter(WORD.charAt(j)))
                        view.setTextColor(Color.TRANSPARENT);
                    // if the jth character of the word is a letter, make the underline visible
                    else
                        view.setTextColor(Color.WHITE);
                }

                // at the end of each column, we add the view and associated row parameters to the row
                _row.addView(view, row_params);
            }

            // after we have two rows with correctly filled cells, we will add it to our main TableLayout
            wordContainer.addView(_row, table_params);
        }

        // after we have a TableLayout with a full table that constitutes the Hangman WordArea,
        // we add it to our ScrollView container
        ScrollView wordAreaContainer = (ScrollView) findViewById(R.id.wordAreaContainer);
        wordAreaContainer.addView(wordContainer);
    }
    /**
     * Method that updates the display of the Hangman character after each loss.
     * @param stage_num An integer value in the range [0, 6] indicating the current stage
     */
    public void refreshHangmanArea(int stage_num) {
        // select the appropriate path containing the correct image for this stage in the game
        switch (stage_num) {
            case 0: hangman_file_path = "gameplay_images/stage0.png"; break;
            case 1: hangman_file_path = "gameplay_images/stage1.png"; break;
            case 2: hangman_file_path = "gameplay_images/stage2.png"; break;
            case 3: hangman_file_path = "gameplay_images/stage3.png"; break;
            case 4: hangman_file_path = "gameplay_images/stage4.png"; break;
            case 5: hangman_file_path = "gameplay_images/stage5.png"; break;
            case 6: hangman_file_path = "gameplay_images/stage6.png";
        }

        try {
            // put an image in the ImageView by creating a Drawable from the InputStream of the desired image via its file path
            hangman_img.setImageDrawable(Drawable.createFromStream(getAssets().open(hangman_file_path), null));
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**********************************************************************************************/
    /***********THE FOLLOWING METHODS ARE UNUSED ARE ONLY EXIST FOR DOCUMENTATION PURPOSES*********/
    /**************************not included in JavaDoc*********************************************/

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
        // Everything is saved in global variables, so there is nothing to reinitialize
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