package com.utsavsoft.mergetiles.game2048.adapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.utsavsoft.mergetiles.game2048.maingamecode.ScoreModel;
import com.utsavsoft.mergetiles.game2048.R;

import java.util.List;

public class ScoreAdapter extends BaseAdapter {
    private List<ScoreModel> scoreModelList;
    private Context context;

    public ScoreAdapter(List<ScoreModel> scoreModelList, Context context) {
        this.scoreModelList = scoreModelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return scoreModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return scoreModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.score_line, parent, false);
        }
        ScoreModel scoreModel = scoreModelList.get(position);
        ImageView icon = convertView.findViewById(R.id.adapt_score_trophy);
        TextView tvBoardType = convertView.findViewById(R.id.adapt_tv_board_type);
        TextView tvScore = convertView.findViewById(R.id.adapt_tv_score);

        icon.setImageResource(scoreModel.getIcon());
        tvScore.setText(String.valueOf(scoreModel.getScore()));

        tvBoardType.setText(scoreModel.getBoardType());

        return convertView;

    }
}