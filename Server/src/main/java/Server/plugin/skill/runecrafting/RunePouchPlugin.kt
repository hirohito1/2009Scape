package plugin.skill.runecrafting

import core.cache.def.impl.ItemDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.node.item.ItemPlugin
import core.game.world.map.Location
import core.plugin.Plugin
import core.plugin.PluginManager.definePlugin
import core.tools.ItemNames
import plugin.dialogue.DestroyItemPlugin
import plugin.stringtools.colorize

/**
 * Handles the rune pouches.
 * @author Ceikry
 */
class RunePouchPlugin : OptionHandler() {
    @Throws(Throwable::class)
    override fun newInstance(arg: Any?): Plugin<Any>? {
        for (i in 5509..5515) {
            ItemDefinition.forId(i).configurations["option:fill"] = this
            ItemDefinition.forId(i).configurations["option:empty"] = this
            ItemDefinition.forId(i).configurations["option:check"] = this
            ItemDefinition.forId(i).configurations["option:drop"] = this
        }
        return this
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        val rEssAmt = player.inventory.getAmount(ItemNames.RUNE_ESSENCE)
        val pEssAmt = player.inventory.getAmount(ItemNames.PURE_ESSENCE_7936)
        var preferenceFlag = 0 //0 -> rune ess, 1 -> pure ess
        if(rEssAmt - pEssAmt == 0 && option == "fill") return true
        if(rEssAmt > pEssAmt) preferenceFlag = 0 else preferenceFlag = 1

        val essence = Item(
                if(preferenceFlag == 0) ItemNames.RUNE_ESSENCE else ItemNames.PURE_ESSENCE_7936,
                if(preferenceFlag == 0) rEssAmt else pEssAmt
        )


        if(player.pouchManager.isDecayedPouch(node.id)){
            player.debug("E2")
            when(option) { //Handling for IF the pouch has already completely decayed
                "drop" -> player.dialogueInterpreter.open(9878,Item(node.id))
                else -> player.sendMessage(colorize("%RThis pouch has completely decayed and needs to be repaired."))
            }
        } else {
            player.debug("E")
            when (option) { //Normal handling
                "fill" -> player.pouchManager.addToPouch(node.id, essence.amount, essence.id)
                "empty" -> player.pouchManager.withdrawFromPouch(node.id)
                "check" -> player.pouchManager.checkAmount(node.id)
                "drop" -> player.dialogueInterpreter.open(9878,Item(node.id))
            }
        }
        return true
    }

    override fun isWalk(): Boolean {
        return false
    }
}