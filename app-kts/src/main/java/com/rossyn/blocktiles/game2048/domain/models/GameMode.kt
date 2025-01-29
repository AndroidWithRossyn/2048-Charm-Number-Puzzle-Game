package com.rossyn.blocktiles.game2048.domain.models

import com.rossyn.blocktiles.game2048.R

enum class GameMode(val typeName: Int, val typeDis: Int, val iconRes: Int) {
    Classic(R.string.mode_classic,R.string.mode_exp_classic, R.drawable.classic_mode),
    Blocks(R.string.mode_blocks,R.string.mode_exp_blocks, R.drawable.block_mode),
    Shuffle(R.string.mode_shuffle,R.string.mode_exp_shuffle, R.drawable.anim_mode_suffile)
}