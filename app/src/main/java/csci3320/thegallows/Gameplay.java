package csci3320.thegallows;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;

public class Gameplay extends Activity {

    private Button keyA, keyB, keyC, keyD, keyE,
                   keyF, keyG, keyH, keyI, keyJ,
                   keyK, keyL, keyM, keyN, keyO,
                   keyP, keyQ, keyR, keyS, keyT,
                   keyU, keyV, keyW, keyX, keyY, keyZ;

    private Button[] keyboard = { keyA, keyB, keyC, keyD, keyE,
                                  keyF, keyG, keyH, keyI, keyJ,
                                  keyK, keyL, keyM, keyN, keyO,
                                  keyP, keyQ, keyR, keyS, keyT,
                                  keyU, keyV, keyW, keyX, keyY, keyZ};

    private int[] keyboardID = { R.id.A, R.id.B, R.id.C, R.id.D, R.id.E,
                                 R.id.F, R.id.G, R.id.H, R.id.I, R.id.J,
                                 R.id.K, R.id.L, R.id.M, R.id.N, R.id.O,
                                 R.id.P, R.id.Q, R.id.R, R.id.S, R.id.T,
                                 R.id.U, R.id.V, R.id.W, R.id.X, R.id.Y, R.id.Z };

    protected ArrayList<TextView> letter_arr = new ArrayList<TextView>();
    public String WORD = "";
    protected int attempts = 6;
    protected int letters_correct = 0;
    public boolean win = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        initKeyboard();
        initWordArea();

        play();
    }

    protected void initKeyboard() {
        for (int i = 0; i < keyboard.length; i++)
            keyboard[i] = (Button) findViewById(keyboardID[i]);
    }

    protected void initWordArea() {
        WordBank game_word = new WordBank(getApplicationContext(), getIntent().getStringExtra("Difficulty"));
        WORD = game_word.getGameWord();

        // 1. Create a tableLayout and its params
        TableLayout.LayoutParams table_params = new TableLayout.LayoutParams();
        TableLayout wordContainer = new TableLayout(this);

        // 2. create tableRow params
        TableRow.LayoutParams row_params = new TableRow.LayoutParams();
        row_params.weight = 1;

        // 3. populate table
        for (int i = 0; i < 2; i++) {
            TableRow _row = new TableRow(this);

            for (int j = 0; j < WORD.length(); j++) {
                TextView view = new TextView(this);
                view.setBackgroundColor(Color.WHITE);
                view.setTypeface(null, Typeface.BOLD);
                view.setGravity(Gravity.CENTER);

                if (i == 0) {
                    view.setText(Character.toString(WORD.charAt(j)));
                    view.setTextColor(Color.WHITE);

                    letter_arr.add(view);
                }
                else
                    view.setText(R.string.WordUnderline);

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
                char button_letter = guess.getText().toString().charAt(0);

                if (isValidLetter(button_letter))
                    updateAllOccurrences(button_letter);
                else
                    attempts--;

                click.setVisibility(View.INVISIBLE);

                if (attempts == 0 && letters_correct != WORD.length())
                    gameOver(false);
                else if (letters_correct == WORD.length())
                    gameOver(true);
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

        Intent _Endgame = new Intent(this, Endgame.class);
        _Endgame.putExtra("Result", win);
        _Endgame.putExtra("Word", WORD);
        _Endgame.putExtra("Difficulty", getIntent().getStringExtra("Difficulty"));
        startActivity(_Endgame);

        finish();
    }

    private void makeButtonsDetectable(View.OnClickListener detect_button) {
        for (int i = 0; i < keyboard.length; i++)
            keyboard[i].setOnClickListener(detect_button);
    }
}
