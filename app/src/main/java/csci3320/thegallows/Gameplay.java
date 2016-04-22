package csci3320.thegallows;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Gameplay extends Activity {

    private Button hintKey;

    private Button[] keyboard = new Button[26];

    private int[] keyboardID = { R.id.A, R.id.B, R.id.C, R.id.D, R.id.E,
                                 R.id.F, R.id.G, R.id.H, R.id.I, R.id.J,
                                 R.id.K, R.id.L, R.id.M, R.id.N, R.id.O,
                                 R.id.P, R.id.Q, R.id.R, R.id.S, R.id.T,
                                 R.id.U, R.id.V, R.id.W, R.id.X, R.id.Y, R.id.Z };

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

        GAMETYPE = getIntent().getStringExtra("GameType");
        LEVEL_NUM = getIntent().getIntExtra("LEVEL", -1);
        lifes = getIntent().getIntExtra("LIFE", -1);
        hints = getIntent().getIntExtra("HINTS", -1);

        initKeyboard();
        initGameStatusArea();
        initWordArea();

        play();
    }

    protected void initGameStatusArea() {
        TextView LevelText = (TextView) findViewById(R.id.LevelText);
        TextView CategoryText = (TextView) findViewById(R.id.CategoryText);
        TextView LifesText = (TextView) findViewById(R.id.LifesText);
        TextView HintsText = (TextView) findViewById(R.id.HintsText);

        WordBank level_info = new WordBank(getApplicationContext(), getIntent().getIntExtra("LEVEL", -1));
        String lifes_str = "Lifes: " + Integer.toString(lifes);
        String hints_str = "Hints: " + Integer.toString(hints);

        if (hints == 0) {
            String new_hints_text_str = "No more hints available :(";
            hintKey.setText(new_hints_text_str);
            hintKey.setClickable(false);
        }

        if (GAMETYPE.equals("FREEPLAY")) {
            String level_txt_str = "Freeplay";
            LevelText.setText(level_txt_str);
        }
        else {
            LevelText.setText(level_info.getLevelString());
            CategoryText.setText(level_info.getLevelCategoryString(level_info.getLevel()));
        }

        LifesText.setText(lifes_str);
        HintsText.setText(hints_str);
    }

    protected void initKeyboard() {
        for (int i = 0; i < keyboard.length; i++)
            keyboard[i] = (Button) findViewById(keyboardID[i]);

        hintKey = (Button) findViewById(R.id.HintKey);
    }

    protected void initWordArea() {
        WordBank game_word = new WordBank(getApplicationContext(), getIntent().getIntExtra("LEVEL", -1));
        WORD = game_word.getGameWord();
        HINT = game_word.getGameHint();

        if (GAMETYPE.equals("FREEPLAY")) {
            TextView CategoryText = (TextView) findViewById(R.id.CategoryText);
            CategoryText.setText(game_word.getLevelCategoryString(game_word.getLevel()));
        }

        TableLayout.LayoutParams table_params = new TableLayout.LayoutParams();
        TableLayout wordContainer = new TableLayout(this);

        TableRow.LayoutParams row_params = new TableRow.LayoutParams();
        row_params.weight = 1;

        for (int i = 0; i < 2; i++) {
            TableRow _row = new TableRow(this);

            for (int j = 0; j < WORD.length(); j++) {
                TextView view = new TextView(this);
                view.setBackgroundColor(Color.WHITE);
                view.setTypeface(null, Typeface.BOLD);
                view.setGravity(Gravity.CENTER);

                if (i == 0) {
                    view.setText(Character.toString(WORD.charAt(j)));
                    if (Character.toString(WORD.charAt(j)).equals(" ") ||
                        Character.toString(WORD.charAt(j)).equals("-")) {
                        view.setTextColor(Color.BLACK);
                    }
                    else {
                        view.setTextColor(Color.WHITE);
                        letter_arr.add(view);
                    }
                }
                else {
                    view.setText(R.string.WordUnderline);

                    if (Character.toString(WORD.charAt(j)).equals(" ") ||
                        Character.toString(WORD.charAt(j)).equals("-"))
                        view.setTextColor(Color.WHITE);
                    else
                        view.setTextColor(Color.BLACK);
                }

                _row.addView(view, row_params);
            }

            wordContainer.addView(_row, table_params);
        }

        ScrollView wordAreaContainer = (ScrollView) findViewById(R.id.wordAreaContainer);
        wordAreaContainer.addView(wordContainer);
    }

    protected void play() {
        View.OnClickListener detect_button = new View.OnClickListener() {
            @Override
            public void onClick(View click) {
                Button guess = (Button) click;
                if (guess == hintKey) {
                    if(hints != 0 || hintSelected) {
                        if (!hintSelected) {
                            hints--;
                            hintSelected = true;

                            String curr_hint_str = "Show Hint";
                            guess.setText(curr_hint_str);
                        }

                        TextView HintsText = (TextView) findViewById(R.id.HintsText);
                        String new_hints_text_str = "Hints: " + Integer.toString(hints);
                        HintsText.setText(new_hints_text_str);

                        Toast.makeText(Gameplay.this, HINT, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    char button_letter = guess.getText().toString().charAt(0);

                    if (isValidLetter(button_letter))
                        updateAllOccurrences(button_letter);
                    else
                        attempts--;

                    click.setVisibility(View.INVISIBLE);

                    if (attempts == 0 && letters_correct != letter_arr.size())
                        gameOver(false);
                    else if (letters_correct == letter_arr.size())
                        gameOver(true);
                }
            }
        };

        makeButtonsDetectable(detect_button);
    }

    private boolean isValidLetter(char selection) { return WORD.indexOf(selection) >= 0; }

    private void updateAllOccurrences(char button_letter) {
        for (int i = 0; i < letter_arr.size(); i++) {
            if (button_letter == letter_arr.get(i).getText().toString().charAt(0)) {
                letter_arr.get(i).setTextColor(Color.BLACK);
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
            final Intent nextActivity;

            if (LEVEL_NUM != 6) {
                if (win) {
                    LEVEL_NUM++;

                    nextActivity = Gameplay.this.getIntent();
                    nextActivity.putExtra("LEVEL", LEVEL_NUM);
                    nextActivity.putExtra("LIFE", lifes);
                    nextActivity.putExtra("HINTS", hints);
                    nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    Toast.makeText(Gameplay.this, "LEVEL CLEARED!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Gameplay.this, "YOU DIED!", Toast.LENGTH_SHORT).show();

                    if (lifes != 0) {
                        lifes--;

                        nextActivity = Gameplay.this.getIntent();
                        nextActivity.putExtra("LEVEL", LEVEL_NUM);
                        nextActivity.putExtra("LIFE", lifes);
                        nextActivity.putExtra("HINTS", hints);
                        nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                    else {
                        nextActivity = new Intent(this, Endgame.class);
                        nextActivity.putExtra("Result", win);
                        nextActivity.putExtra("Word", WORD);
                        nextActivity.putExtra("GameType", getIntent().getStringExtra("GameType"));
                    }
                }

                Handler pause = new Handler();
                pause.postDelayed(new Runnable() {
                    public void run() {
                        Gameplay.this.finish();
                        startActivity(nextActivity);
                    }
                }, 3000);
            }
            else {
                if (win)
                    Toast.makeText(Gameplay.this, "LEVEL CLEARED!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Gameplay.this, "YOU DIED!", Toast.LENGTH_SHORT).show();

                nextActivity = new Intent(this, Endgame.class);
                nextActivity.putExtra("Result", win);
                nextActivity.putExtra("Word", WORD);
                nextActivity.putExtra("GameType", getIntent().getStringExtra("GameType"));

                Handler pause = new Handler();
                pause.postDelayed(new Runnable() {
                    public void run() {
                        Gameplay.this.finish();
                        startActivity(nextActivity);
                    }
                }, 3000);
            }
        }
        else if (getIntent().getStringExtra("GameType").equals("FREEPLAY")) {
            if (win)
                Toast.makeText(Gameplay.this, "YOU SURVIVED!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(Gameplay.this, "YOU DIED!", Toast.LENGTH_SHORT).show();

            final Intent _Endgame = new Intent(this, Endgame.class);
            _Endgame.putExtra("Result", win);
            _Endgame.putExtra("Word", WORD);
            _Endgame.putExtra("GameType", getIntent().getStringExtra("GameType"));
            Handler pause = new Handler();
            pause.postDelayed(new Runnable() {
                public void run() {
                    Gameplay.this.finish();
                    startActivity(_Endgame);
                }
            }, 3000);
        }
    }

    private void makeButtonsDetectable(View.OnClickListener detect_button) {
        for (int i = 0; i < keyboard.length; i++)
            keyboard[i].setOnClickListener(detect_button);

        hintKey.setOnClickListener(detect_button);
    }
}
