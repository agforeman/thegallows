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

        final Button buttonRegularPlay = (Button) findViewById(R.id.regular_play_button);
        final Button buttonFreeplay = (Button) findViewById(R.id.freeplay_button);

        final Intent launchGame = new Intent(this, Gameplay.class);

        buttonRegularPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                launchGame.putExtra("GameType","REGULAR");
                launchGame.putExtra("LEVEL", 1);
                launchGame.putExtra("LIFE", 3);
                launchGame.putExtra("HINTS", 3);
                startActivity(launchGame);
            }
        });

        buttonFreeplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                launchGame.putExtra("GameType","FREEPLAY");
                launchGame.putExtra("LEVEL", 0);
                launchGame.putExtra("LIFE", 0);
                launchGame.putExtra("HINTS", 1);
                startActivity(launchGame);
            }
        });
    }
}
