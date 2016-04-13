package csci3320.thegallows;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class StartScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscreen);

        final Button buttonEasy = (Button) findViewById(R.id.easy_button);
        final Button buttonMedium = (Button) findViewById(R.id.medium_button);
        final Button buttonHard = (Button) findViewById(R.id.hard_button);
        final Button buttonFreeplay = (Button) findViewById(R.id.freeplay_button);

        final Intent launchGame = new Intent(this, Gameplay.class);

        buttonEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame.putExtra("Difficulty","EASY");
                startActivity(launchGame);
            }
        });

        buttonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame.putExtra("Difficulty","MEDIUM");
                startActivity(launchGame);
            }
        });

        buttonHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame.putExtra("Difficulty","HARD");
                startActivity(launchGame);
            }
        });

        buttonFreeplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                launchGame.putExtra("Difficulty","FREEPLAY");
                startActivity(launchGame);
            }
        });
    }
}
