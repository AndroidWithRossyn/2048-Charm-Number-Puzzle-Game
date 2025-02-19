package com.rossyn.blocktiles.game2048.data.prefs;

import static com.rossyn.blocktiles.game2048.data.prefs.SharedPref.TOP_SCORE;

import android.content.SharedPreferences;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.domain.models.ScoreModel;

import java.util.ArrayList;
import java.util.Collections;

public class ScoreBoardBuilder {

    public static ArrayList<ScoreModel> createClassicArrayList(SharedPreferences sharedPreferences, String gameMode) {

        ArrayList<ScoreModel> scoreModels = new ArrayList<>();

        scoreModels.add(new ScoreModel("6x5", sharedPreferences.getLong(TOP_SCORE + gameMode + 6 + 5, 0), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("5x4", sharedPreferences.getLong(TOP_SCORE + gameMode + 5 + 4, 0), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("5x3", sharedPreferences.getLong(TOP_SCORE + gameMode + 5 + 3, 0), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("4x3", sharedPreferences.getLong(TOP_SCORE + gameMode + 4 + 3, 0), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("8x8", sharedPreferences.getLong(TOP_SCORE + gameMode + 8 + 8, 0), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("6x6", sharedPreferences.getLong(TOP_SCORE + gameMode + 6 + 6, 0), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("5x5", sharedPreferences.getLong(TOP_SCORE + gameMode + 5 + 5, 0), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("4x4", sharedPreferences.getLong(TOP_SCORE + gameMode + 4 + 4, 0), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("3x3", sharedPreferences.getLong(TOP_SCORE + gameMode + 3 + 3, 0), R.drawable.icon_trophy_empty));


        scoreModels.sort((p1, p2) -> (int) (p1.getScore() - p2.getScore()));

        Collections.reverse(scoreModels);

        if (scoreModels.get(0).getScore() != 0) {
            scoreModels.get(0).setIcon(R.drawable.icon_trophy_gold);
        }
        if (scoreModels.get(1).getScore() != 0) {
            scoreModels.get(1).setIcon(R.drawable.icon_trophy_silver);
        }
        if (scoreModels.get(2).getScore() != 0) {
            scoreModels.get(2).setIcon(R.drawable.icon_trophy_bronze);
        }

        return scoreModels;
    }


}
