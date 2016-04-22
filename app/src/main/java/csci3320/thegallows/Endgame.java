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
        final Button buttonMainMenu = (Button) findViewById(R.id.main_menu_button);

        final Intent incomingIntent = new Intent(getIntent());
        final String game_type = incomingIntent.getStringExtra("GameType");

        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replayIntent = new Intent(v.getContext(), Gameplay.class);
                replayIntent.putExtra("GameType",game_type);

                if (game_type.equals("FREEPLAY")) {
                    replayIntent.putExtra("LEVEL", 0);
                    replayIntent.putExtra("LIFE", 0);
                    replayIntent.putExtra("HINTS", 1);
                }
                else if (game_type.equals("REGULAR")) {
                    replayIntent.putExtra("LEVEL", 1);
                    replayIntent.putExtra("LIFE", 3);
                    replayIntent.putExtra("HINTS", 3);
                }

                startActivity(replayIntent);
            }
        });

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuIntent = new Intent(v.getContext(), StartScreen.class);
                startActivity(mainMenuIntent);
            }
        });

    }
}
