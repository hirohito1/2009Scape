package plugin.worldevents.shootingstar

import core.game.system.task.Pulse
import core.game.world.GameWorld
import plugin.worldevents.PluginSet
import plugin.worldevents.WorldEvent
import plugin.worldevents.WorldEvents

/**
 * The world event class for shooting stars. Keeps track of event-related data like the ShootingStar instance.
 * Also handles spawning new shooting stars based on time.
 * @author Ceikry
 */
class ShootingStarEvent : WorldEvent("shooting-stars") {
    val star = ShootingStar()
    val tickDelay = if(GameWorld.getSettings().isDevMode) 200 else 7200


    override fun initialize() {
        plugins = PluginSet(
                ScoreboardHandler(),
                ShootingStarOptionHandler(),
                ShootingStarScoreboard(),
                StarChartPlugin(),
                ShootingStarCommands(),
                StarSpriteDialogue(),
                ShootingStarLogin()
        )
        super.initialize()
        GameWorld.Pulser.submit(StarPulse())
        log("Shooting Star event has been initialized.")
    }

    override fun checkTrigger(): Boolean {
        /**
         * Spawn a new star only if: star's ticks are greater than the tickDelay and star sprite is not spawned OR,
         * neither the star or the sprite are spawned.
         */
        star.ticks += 10
        if ((star.ticks >= tickDelay && !star.spriteSpawned) || (!star.isSpawned && !star.spriteSpawned)) {
            return true
        }

        /**
         * Clear the sprite when we have passed the tickDelay + (1/3) of the tickDelay.
         * This gives players a little extra time before the star respawns to run to the bank and grab
         * stardust or whatever. Once the sprite is cleared a new star will be allowed to spawn.
         */
        val maxDelay = tickDelay + (tickDelay / 3)
        if(star.ticks > maxDelay && star.spriteSpawned){
            star.clearSprite()
        }

        return false
    }

    override fun checkActive(): Boolean {
        return true //this event is always active.
    }

    override fun fireEvent() {
        log("Fired new shooting star event.")
        star.fire()
    }

    /**
     * Handles checking star status and spawning new ones if necessary.
     */
    class StarPulse : Pulse(10){
        override fun pulse(): Boolean {
            val event = WorldEvents.get("shooting-stars")
            event ?: return true

            if(event.checkTrigger()){
                event.fireEvent()
            }

            return false //always returns false because it needs to run forever.
        }
    }
}