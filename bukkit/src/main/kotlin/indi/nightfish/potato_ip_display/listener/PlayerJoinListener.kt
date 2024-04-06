package indi.nightfish.potato_ip_display.listener

import indi.nightfish.potato_ip_display.PotatoIpDisplay
import indi.nightfish.potato_ip_display.parser.IpParseFactory
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.scheduler.BukkitRunnable


class PlayerJoinListener : Listener {
    private val plugin = Bukkit.getPluginManager().getPlugin("PotatoIpDisplay") as PotatoIpDisplay
    private val conf = plugin.conf

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {

        object : BukkitRunnable() {
                override fun run() {
                    val playerAddress = event.realAddress.hostAddress
                    val playerName = event.player.name
                    val ipParse = IpParseFactory.getIpParse(playerAddress)
                    var result = ipParse.getProvince()

                    if (result == "未知" || result == "") {
                        result = ipParse.getCity()
                        if (result == "未知" || result == "") {
                            result = ipParse.getCountry()
                        }
                    }

                    IpAttributeMap.playerIpAttributeMap[playerName] = result
                    plugin.log("Player named $playerName connect to proxy from ${ipParse.getProvince()}${ipParse.getCity()} ${ipParse.getISP()}")
                }
            }.run()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val ipAttr = IpAttributeMap.playerIpAttributeMap[event.player.name] ?: "未知"
        if (conf.message.playerLogin.enabled) {
            event.player.sendMessage(conf.message.playerLogin.string
                .replace("%ipAttr%", ipAttr))
        }
    }
}
