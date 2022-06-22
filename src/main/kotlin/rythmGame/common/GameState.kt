package rythmGame.common

import util.Vector2

class GameState : engine.simulation.GameState {
    var timeStamp = 0L
    val player = Player()

    fun lerp(t: Float, past: GameState, future: GameState) {
        player.pos copyFrom Vector2.lerp(t, past.player.pos, future.player.pos)
        player.vel copyFrom Vector2.lerp(t, past.player.vel, future.player.vel)
        player.mouse copyFrom Vector2.lerp(t, past.player.mouse, future.player.mouse)

        //Log.debug("${past.player.vel}, ${player.vel}")
    }

    infix fun copyFrom(gs: GameState) {
        timeStamp = gs.timeStamp

        player.pos copyFrom gs.player.pos
        player.vel copyFrom gs.player.vel
        player.mouse copyFrom gs.player.mouse
    }

    override fun toString(): String {
        return "$timeStamp: {$player}"
    }
}