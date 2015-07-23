# AMA-Bukkit-Plugin
Alastar Murder Awards - justice economy system for you server!

Version: 6.1

Bukkit API Version: 1.7.5-R0.1

# Building
 If you are eclipse user - just import this project.
 
 IntelliJIDEA users can also import this project as "Eclipse project"

# Commands

/ama - murders list (Permission: - ma.ama)

/amareset Nick - remove one kill from player (Permission: - ma.amareset)

/amaresetall Nick - remove all kils from player (Permission: - ma.amaresetall)

/amadisablejail - disable jailing (Permission: - ma.amadisablejail)

/amaenablejail - enable jailing (Permission: - ma.amaenablejail)

/amadisableeconomy - Disable justice pay (Permission: - ma.amadisableeconomy)

/amaenableeconomy - Enable justice pay (Permission: - ma.amaenableeconomy)

/amasetmultiplier - Set jail time for one kill (Permission: - ma.amasetmultiplier)

/amasetmoney - Set justice pay value for one kill (Permission: - ma.moneyPerKill)

#Config

 
useJail: true //on\off jailing
useEconomy: true //on\off justice pay
useTags: true // color murders nicks
useMessages: true // Use messages
moneyPerKill: 150 // justice pay value per kill
jailMultiplierPerKill: 10.5 // jail time per kill
topPlayers: 20 // Max player to be displayed in top
killerDeadMessage: k поймал убийцу m! // Name of catcher - k, Name of murder - m
awardMessage: За убийство преступника вы получили amt //Amount of pay - amt
murderMessage: Вы убили невиновного! // Innocent killed
murderList: Список преступников // Muirders list
essentialsJailName: jail // If you are using EssentialsJail, enter jail name here
jailPluginUsing: Essentials // Essentials/Jail
players: // Murders list
  Alastar:
    kills: 0
  AlastarI:
    kills: 1
 