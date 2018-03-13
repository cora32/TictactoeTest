package org.iskopasi.tictactoe

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.widget.ImageView
import org.iskopasi.tictactoe.databinding.ActivityMainBinding
import org.iskopasi.tictactoe.utils.Utils.PLAYER_SIDE


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        (binding.sideCrossIv.drawable as Animatable).start()
        (binding.sideZeroIv.drawable as Animatable).start()

        binding.sideCrossIv.setOnClickListener({
            animateActivity(R.string.player_side_cross, binding.sideCrossIv, getString(R.string.iv_transition_x))
        })
        binding.sideZeroIv.setOnClickListener({
            animateActivity(R.string.player_side_cirle, binding.sideZeroIv, getString(R.string.iv_transition_o))
        })
    }

    private fun animateActivity(side: Int, imageView: ImageView, tag: String) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, tag)
        val intent = Intent(this, GridActivity::class.java)
        intent.putExtra(PLAYER_SIDE, side)
        startActivity(intent, options.toBundle())
    }
}
