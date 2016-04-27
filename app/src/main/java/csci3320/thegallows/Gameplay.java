package csci3320.thegallows;

import android.view.View.OnClickListener;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@TargetApi(16)
public class Gameplay extends Activity implements OnClickListener {

    private Typeface chalkTypeFace;

    private Button hintKey;

    private Button[] keyboard = new Button[26];

    private final int[] keyboardID = {R.id.A, R.id.B, R.id.C, R.id.D, R.id.E,
            R.id.F, R.id.G, R.id.H, R.id.I, R.id.J,
            R.id.K, R.id.L, R.id.M, R.id.N, R.id.O,
            R.id.P, R.id.Q, R.id.R, R.id.S, R.id.T,
            R.id.U, R.id.V, R.id.W, R.id.X, R.id.Y, R.id.Z};

    protected ImageView hangman_img;

    public String hangman_file_path = "";

    protected ArrayList<TextView> letter_arr = new ArrayList<>();

    public int LEVEL_NUM;
    public int lifes, hints;
    public String WORD = "";
    public String HINT = "";
    public String GAMETYPE = "";

    protected int attempts = 6;
    protected int letters_correct = 0;
    public boolean win = false;
    public boolean hintSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        chalkTypeFace = Typeface.createFromAsset(getAssets(), "fonts/squeakychalksound.ttf");

        GAMETYPE = getIntent().getStringExtra("GameType");
        LEVEL_NUM = getIntent().getIntExtra("LEVEL", -1);
        lifes = getIntent().getIntExtra("LIFE", -1);
        hints = getIntent().getIntExtra("HINTS", -1);

        initKeyboard();
        initGameStatusArea();
        initWordArea();
        refreshHangmanArea(Math.abs(attempts - 6));
    }

    public void initKeyboard() {
        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = (Button) findViewById(keyboardID[i]);
            keyboard[i].setTypeface(chalkTypeFace);
        }

        hintKey = (Button) findViewById(R.id.HintKey);
        hintKey.setTypeface(chalkTypeFace);

        for (Button aKeyboard : keyboard)
            aKeyboard.setOnClickListener(Gameplay.this);

        hintKey.setOnClickListener(Gameplay.this);
    }

    public void initGameStatusArea() {
        TextView LevelText = (TextView) findViewById(R.id.LevelText);
        LevelText.setTypeface(chalkTypeFace);

        TextView CategoryText = (TextView) findViewById(R.id.CategoryText);
        CategoryText.setTypeface(chalkTypeFace);

        TextView LifesText = (TextView) findViewById(R.id.LifesText);
        LifesText.setTypeface(chalkTypeFace);

        TextView HintsText = (TextView) findViewById(R.id.HintsText);
        HintsText.setTypeface(chalkTypeFace);

        WordBank level_info = new WordBank(getApplicationContext(), getIntent().getIntExtra("LEVEL", -1));
        String lifes_str = "Lifes: " + Integer.toString(lifes);
        String hints_str = "Hints: " + Integer.toString(hints);

        if (hints == 0) {
            hintKey.setTextColor(Color.TRANSPARENT);
            hintKey.setClickable(false);
            hintKey.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.erasermark));
        }

        if (GAMETYPE.equals("FREEPLAY")) {
            String level_txt_str = "Freeplay";
            LevelText.setText(level_txt_str);
        } else {
            LevelText.setText(level_info.getLevelString());
            CategoryText.setText(level_info.getLevelCategoryString(level_info.getLevel()));
        }

        LifesText.setText(lifes_str);
        HintsText.setText(hints_str);
    }

    public void initWordArea() {
        final WordBank game_word = new WordBank(getApplicationContext(), getIntent().getIntExtra("LEVEL", -1));
        WORD = game_word.getGameWord();
        HINT = game_word.getGameHint();

        if (GAMETYPE.equals("FREEPLAY")) {
            TextView CategoryText = (TextView) findViewById(R.id.CategoryText);
            CategoryText.setText(game_word.getLevelCategoryString(game_word.getLevel()));
            CategoryText.setTypeface(chalkTypeFace);
        }

        TableLayout.LayoutParams table_params = new TableLayout.LayoutParams();
        TableLayout wordContainer = new TableLayout(Gameplay.this);

        TableRow.LayoutParams row_params = new TableRow.LayoutParams();
        row_params.weight = 1;

        for (int i = 0; i < 2; i++) {
            TableRow _row = new TableRow(Gameplay.this);

            for (int j = 0; j < WORD.length(); j++) {
                TextView view = new TextView(Gameplay.this);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setTypeface(chalkTypeFace);
                view.setTextSize(25);
                view.setGravity(Gravity.CENTER);

                if (i == 0) {
                    view.setText(Character.toString(WORD.charAt(j)));
                    if (Character.toString(WORD.charAt(j)).equals(" ") ||
                            Character.toString(WORD.charAt(j)).equals("-")) {
                        view.setTextColor(Color.WHITE);
                    } else {
                        view.setTextColor(Color.TRANSPARENT);
                        letter_arr.add(view);
                    }
                } else {
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

    public void refreshHangmanArea(final int stage_num) {
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


    public void onClick(final View click) {
        Button guess = (Button) click;
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
        hintWindow.setContentView(R.layout.dialog_layout);

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

    private void updateAllOccurrences(final char button_letter) {
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
            if (LEVEL_NUM != 9) {
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
                if (win)
                    Toast.makeText(Gameplay.this, "LEVEL CLEARED!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Gameplay.this, "YOU DIED!", Toast.LENGTH_SHORT).show();

                if (lifes != 0) {
                    lifes--;
                    nextLevel();
                }
                else
                    endGame();

            }
        }
        else if (getIntent().getStringExtra("GameType").equals("FREEPLAY")) {
            if (win)
                Toast.makeText(Gameplay.this, "YOU SURVIVED!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(Gameplay.this, "YOU DIED!", Toast.LENGTH_SHORT).show();

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