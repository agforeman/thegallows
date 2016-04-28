package csci3320.thegallows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * @version 4.0
 * @since   2016-04-10
 *
*/
public class Gameplay extends Activity implements OnClickListener {
    /**
     * The font for most of the text for the app. The text in the hint dialog and Toasts
     * are the only exception
     */
    private Typeface chalkTypeFace;
    /**
     * The Button object for the key that, when pressed, launches the hint dialog
     */
    private Button hintKey;
    /**
     * An array of Button objects that act as the letter selection keyboard for the game.
     * Using an array is optimal for such bulk initialization of Buttons since we do not explicitly
     * need to name the variable each keyboard Button
     */
    private Button[] keyboard = new Button[26];
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
     * An ImageView object that will hold the images of the classic hangman figure during gameplay.
     */
    protected ImageView hangman_img;
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
     * Stores the current level number. Used mainly in REGULAR gameplay.
     */
    public int LEVEL_NUM;
    /**
     * Stores the number of levels in the game. Used only in REGULAR gameplay.
     */
    public final int NUM_LEVELS = 9;
    /**
     * Stores the number of lifes and hints the user currently has left in a REGULAR or FREEPLAY mode.
     */
    public int lifes, hints;
    /**
     * Stores the String of the word used for gameplay and its associated hint, respectively.
     */
    public String WORD = "", HINT = "";
    /**
     * Stores the String that indicates the game mode.
     */
    public String GAMETYPE = "";
    /**
     * Stores the number of attempts the user will initially have. This number corresponds to the
     * body parts of the hangman character (head, arms, legs, and torso). This variable is decremented
     * with each incorrect guess.
     */
    protected int attempts = 6;
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
     * Initializes the UI. After this method is complete, onClick handles the rest of the activity
     * @param savedInstanceState Used to store a saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        // Initialize the global gameplay font for later use in initialization and updating
        chalkTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");

        // Retrieve all required Intents passed in from StartScreen for proper initialization of gameplay
        GAMETYPE = getIntent().getStringExtra("GameType");
        LEVEL_NUM = getIntent().getIntExtra("LEVEL", -1);
        lifes = getIntent().getIntExtra("LIFE", -1);
        hints = getIntent().getIntExtra("HINTS", -1);

        // Initialize UI with the following function calls
        setBackgroundImg();
        initKeyboard();
        initGameStatusArea();
        initWordArea();
        refreshHangmanArea(Math.abs(attempts - 6));

        // After the above function calls take place, onClick handles the rest of the gameplay processing
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

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("End This Game?")
                .setMessage("Hitting the back button will end this game and you will loose all progress. Continue?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Launch the StartScreen activity currently on the activity stack.
                        Intent mainMenuIntent = new Intent(Gameplay.this, StartScreen.class);
                        mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        Gameplay.this.finish();
                        startActivity(mainMenuIntent);
                    }
                }).create().show();
    }

    private void setBackgroundImg(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String background = prefs.getString("BackgroundColor", "");
        LinearLayout thisLayout = (LinearLayout) findViewById(R.id.MainLayout);

        switch(background){
            case "GREEN":
                thisLayout.setBackgroundResource(R.drawable.greenboard);
                break;
            case "BLACK":
                thisLayout.setBackgroundResource(R.drawable.blackboard);
                break;
            case "RED":
                thisLayout.setBackgroundResource(R.drawable.redboard);
                break;
            case "BLUE":
                thisLayout.setBackgroundResource(R.drawable.blueboard);
                break;
            case "INDIGO":
                thisLayout.setBackgroundResource(R.drawable.indigoboard);
                break;
            case "CYAN":
                thisLayout.setBackgroundResource(R.drawable.cyanboard);
                break;
            case "PURPLE":
                thisLayout.setBackgroundResource(R.drawable.purpleboard);
                break;
            case "PINK":
                thisLayout.setBackgroundResource(R.drawable.pinkboard);
                break;
            case "ORANGE":
                thisLayout.setBackgroundResource(R.drawable.orangeboard);
                break;
            case "YELLOW":
                thisLayout.setBackgroundResource(R.drawable.yellowboard);
                break;
            case "RANDOM":
                int[] _backgrounds = {R.drawable.greenboard, R.drawable.blackboard, R.drawable.redboard,
                                      R.drawable.blueboard, R.drawable.cyanboard, R.drawable.indigoboard,
                                      R.drawable.purpleboard, R.drawable.pinkboard, R.drawable.orangeboard,
                                      R.drawable.yellowboard};

                Random random_color_generator = new Random();
                int random_drawable = random_color_generator.nextInt(_backgrounds.length);

                thisLayout.setBackgroundResource(_backgrounds[random_drawable]);

                break;

            default:
                thisLayout.setBackgroundResource(R.drawable.greenboard);
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
     * Method that initializes GameStatusArea, which shows the level, topics, and # of lifes and hints
     */
    public void initGameStatusArea() {
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

        // if, at the current level, there are no more hints available to the user, disable the
        // hintKey button, make its text transparent,
        if (hints == 0) {
            hintKey.setTextColor(Color.TRANSPARENT);
            hintKey.setClickable(false);
            hintKey.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.erasermark));
        }

        // if GAMETYPE == "FREEPLAY", do not show the Level #. if not, do show the level number
        if (GAMETYPE.equals("FREEPLAY")) {
            String level_txt_str = "Freeplay";
            LevelText.setText(level_txt_str);
        }
        else {
            // create a WordBank object to get the String values of the current level and associated category
            WordBank level_info = new WordBank(getApplicationContext(), getIntent().getIntExtra("LEVEL", -1));

            LevelText.setText(level_info.getLevelString());
            CategoryText.setText(level_info.getLevelCategoryString(level_info.getLevel()));
        }

        String lifes_str = "Lifes: " + Integer.toString(lifes),
               hints_str = "Hints: " + Integer.toString(hints);

        LifesText.setText(lifes_str);
        HintsText.setText(hints_str);
    }
    /**
     * Method that initializes the WordArea. Dynamically creates a TableLayout that sizes to the length
     * of the word. Row 1 contains the letters, which remain invisible until correctly selected by the user,
     * and Row 2 contains the an underline character for each letter.
     */
    public void initWordArea() {
        WordBank game_word = new WordBank(getApplicationContext(), getIntent().getIntExtra("LEVEL", -1));
        WORD = game_word.getGameWord();
        HINT = game_word.getGameHint();

        if (GAMETYPE.equals("FREEPLAY")) {
            TextView CategoryText = (TextView) findViewById(R.id.CategoryText);
            CategoryText.setText(game_word.getLevelCategoryString(game_word.getLevel()));
            CategoryText.setTypeface(chalkTypeFace);
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
                view.setTextSize(20);
                view.setGravity(Gravity.CENTER);

                if (i == 0) {
                    view.setText(Character.toString(WORD.charAt(j)));

                    if (Character.toString(WORD.charAt(j)).equals(" ") ||
                            Character.toString(WORD.charAt(j)).equals("-")) {
                        view.setTextColor(Color.WHITE);
                    }
                    else {
                        view.setTextColor(Color.TRANSPARENT);
                        letter_arr.add(view);
                    }
                }
                else if (i == 1){
                    view.setText(R.string.WordUnderline);

                    if (Character.toString(WORD.charAt(j)).equals(" ") ||
                            Character.toString(WORD.charAt(j)).equals("-"))
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

    public void onClick(View click) {
        Button guess = (Button) click;
        click.setEnabled(false);

        if (guess == hintKey) {
            if (hints != 0 || hintSelected) {
                if (!hintSelected) {
                    hints--;
                    hintSelected = true;

                    String curr_hint_str = "Show Hint";
                    guess.setText(curr_hint_str);
                }

                createHintDialog();
            }
        } else {
            char button_letter = guess.getText().toString().charAt(0);

            if (isValidLetter(button_letter))
                updateAllOccurrences(button_letter);
            else {
                attempts--;
                refreshHangmanArea(Math.abs(attempts - 6));
            }

            click.setClickable(false);
            ((Button) click).setTextColor(Color.TRANSPARENT);
            click.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.erasermark));

            if (attempts == 0 && letters_correct != letter_arr.size())
                gameOver(false);
            else if (letters_correct == letter_arr.size())
                gameOver(true);
        }
    }

    protected void createHintDialog() {
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

        Button closeHintWindow = (Button) hintWindow.findViewById(R.id.close_hint);
        closeHintWindow.setTypeface(chalkTypeFace);
        closeHintWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintWindow.dismiss();
            }
        });

        hintWindow.show();
    }

    private boolean isValidLetter(char selection) { return WORD.indexOf(selection) >= 0; }

    private void updateAllOccurrences(char button_letter) {
        for (int i = 0; i < letter_arr.size(); i++) {
            if (button_letter == letter_arr.get(i).getText().toString().charAt(0)) {
                letter_arr.get(i).setTextColor(Color.WHITE);
                letters_correct++;
            }
        }
    }

    protected void gameOver(boolean _win) {
        win = _win;

        for (int i = 0; i < letter_arr.size(); i++) {
            if (!win)
                letter_arr.get(i).setTextColor(Color.RED);
            else
                letter_arr.get(i).setTextColor(Color.GREEN);
        }

        if (GAMETYPE.equals("REGULAR")) {
            if (LEVEL_NUM != NUM_LEVELS) {
                if (win) {
                    LEVEL_NUM++;
                    Toast.makeText(Gameplay.this, "LEVEL CLEARED!", Toast.LENGTH_LONG).show();

                    nextLevel();
                }
                else {
                    Toast.makeText(Gameplay.this, "YOU DIED!", Toast.LENGTH_LONG).show();

                    if (lifes != 0) {
                        lifes--;
                        nextLevel();
                    }
                    else
                        endGame();
                }
            }
            else {
                if (win) {
                    Toast.makeText(Gameplay.this, "LEVEL CLEARED!", Toast.LENGTH_LONG).show();
                    endGame();
                }
                else {
                    Toast.makeText(Gameplay.this, "YOU DIED!", Toast.LENGTH_LONG).show();

                    if (lifes != 0)
                        nextLevel();
                    else
                        endGame();
                }
            }
        }
        else if (getIntent().getStringExtra("GameType").equals("FREEPLAY")) {
            if (win)
                Toast.makeText(Gameplay.this, "YOU SURVIVED!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(Gameplay.this, "YOU DIED!", Toast.LENGTH_LONG).show();

            endGame();
        }
    }

    private void nextLevel() {
        Intent nextActivity = Gameplay.this.getIntent();
        nextActivity.putExtra("LEVEL", LEVEL_NUM);
        nextActivity.putExtra("LIFE", lifes);
        nextActivity.putExtra("HINTS", hints);
        nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        launchActivity(nextActivity);
    }

    private void endGame() {
        Intent nextActivity = new Intent(this, Endgame.class);
        nextActivity.putExtra("Result", win);
        nextActivity.putExtra("Word", WORD);
        nextActivity.putExtra("GameType", getIntent().getStringExtra("GameType"));
        nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        launchActivity(nextActivity);
    }

    private void launchActivity(final Intent activity) {
        Handler pause = new Handler();
        pause.postDelayed(new Runnable() {
            public void run() {
                Gameplay.this.finish();
                startActivity(activity);
            }
        }, 3000);
    }
}