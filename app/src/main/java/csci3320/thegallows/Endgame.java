package csci3320.thegallows;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class Endgame extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);

        final Button buttonPlayAgain = (Button) findViewById(R.id.play_again_button);
        final Button buttonChangeDifficulty = (Button) findViewById(R.id.change_difficulty_button);

        final Intent incomingIntent = new Intent(getIntent());
        final String difficulty = incomingIntent.getStringExtra("Difficulty");

        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replayIntent = new Intent(v.getContext(), Gameplay.class);
                replayIntent.putExtra("Difficulty", difficulty);
                startActivity(replayIntent);
            }
        });

        buttonChangeDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeDifficultyIntent = new Intent(v.getContext(), StartScreen.class);
                startActivity(changeDifficultyIntent);
            }
        });

    }
}
