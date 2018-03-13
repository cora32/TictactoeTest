package org.iskopasi.tictactoe

import android.databinding.DataBindingUtil
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import org.iskopasi.tictactoe.databinding.ActivityGridBinding
import org.iskopasi.tictactoe.interfaces.ICallback
import org.iskopasi.tictactoe.utils.GameLogic
import org.iskopasi.tictactoe.utils.Utils


class GridActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityGridBinding = DataBindingUtil.setContentView(this, R.layout.activity_grid)

        val side = intent.getIntExtra(Utils.PLAYER_SIDE, R.string.player_side_cross)
        val logic = GameLogic(3, 3, side)

        val xCallback = object : ICallback<String> {
            override fun call(value: String) {
                binding.xScoreTv.text = value
                (binding.sideCrossIv.drawable as Animatable).start()
            }
        }
        val oCallback = object : ICallback<String> {
            override fun call(value: String) {
                binding.oScoreTv.text = value
                (binding.sideZeroIv.drawable as Animatable).start()
            }
        }

        binding.grid.setLogic(logic, xCallback, oCallback)
        binding.grid.setRestartCallback(object : ICallback<String> {
            override fun call(value: String) {
                val anim = binding.button.animate()
                anim.translationY(0f)
                anim.alpha(1f)
                anim.duration = 400
            }
        })
        binding.button.setOnClickListener({
            binding.grid.restart()

            val anim = binding.button.animate()
            anim.translationY(resources.getDimension(R.dimen.initial_y_translation))
            anim.alpha(0f)
            anim.duration = 300
            anim.interpolator = DecelerateInterpolator()
        })
    }
}