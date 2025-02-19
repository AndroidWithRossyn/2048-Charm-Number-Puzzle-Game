package com.rossyn.blocktiles.game2048.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rossyn.blocktiles.game2048.databinding.ScoreLineBinding;
import com.rossyn.blocktiles.game2048.domain.models.ScoreModel;

import java.util.List;

public class ScoreAdapter extends BaseAdapter {
    private final List<ScoreModel> scoreModelList;

    public ScoreAdapter(List<ScoreModel> scoreModelList) {
        this.scoreModelList = scoreModelList;
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
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScoreLineBinding binding;
        if (convertView == null) {
            binding = ScoreLineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ScoreLineBinding) convertView.getTag();
        }


        ScoreModel scoreModel = scoreModelList.get(position);
        binding.adaptScoreTrophy.setImageResource(scoreModel.getIcon());
        binding.adaptTvScore.setText(String.valueOf(scoreModel.getScore()));
        binding.adaptTvBoardType.setText(scoreModel.getBoardType());

        return convertView;
    }
}
