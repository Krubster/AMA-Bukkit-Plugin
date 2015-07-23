package ru.alastar.bukkit.ma;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.matejdro.bukkit.jail.JailAPI;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZone;

public class MainClass extends JavaPlugin implements Listener
{

	public static final Logger _log = Logger.getLogger("Minecraft");

	public static Economy econ = null;
	public static JailAPI jail = null;
	public static Essentials essen = null;

	public static boolean useJail = false;
	public static boolean useEconomy = false;
	public static boolean useTags = false;
	public static boolean useMessages = true;

	public static double jailMultiplier = 0;
	public static int moneyPerKill = 100;
    public static int topLimit = 5;
    
	public static String essentialsJailName = "";
	public static String jailPlugin = "";
	
	public static String killerDeadMessage = "";	
	public static String awardMessage = "";
	public static String murderMessage = "";
	public static String murdersList  = "";
	@Override
	public void onEnable()
	{

		useJail = this.getConfig().getBoolean("useJail");
		useEconomy = this.getConfig().getBoolean("useEconomy");
		useTags = this.getConfig().getBoolean("useTags");

		jailMultiplier = this.getConfig().getDouble("jailMultiplierPerKill");
		moneyPerKill = this.getConfig().getInt("moneyPerKill");
		topLimit = this.getConfig().getInt("topPlayers");
		murdersList = this.getConfig().getString("murderList");
		if (useJail)
		{
			essentialsJailName = this.getConfig().getString("essentialsJailName");
			jailPlugin = this.getConfig().getString("jailPluginUsing");
		}
		
		if (useMessages)
		{
			killerDeadMessage = this.getConfig().getString("killerDeadMessage");
			awardMessage = this.getConfig().getString("awardMessage");
			murderMessage = this.getConfig().getString("murderMessage");

		}
		
		if (useEconomy)
		{
			if (!setupEconomy())
			{
				_log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		
		if (useJail)
		{
			if (jailPlugin.equals("Essentials") && !setupEssentials())
			{
				DisableManually("Disabled due to no Essentials dependency found!");

			}
			if (jailPlugin.equals("Jail") && !setupJailPlugin())
			{
				DisableManually("Disabled due to no Jail dependency found!");
			}
		}
		
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	// Turn off this plugin
	public void DisableManually(String reason)
	{
		_log.severe(String.format("[%s] - " + reason, getDescription()
				.getName()));
		getServer().getPluginManager().disablePlugin(this);
		return;
	}

	// Hooking Vault
	private boolean setupEconomy()
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
		{
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
		{
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	
	// Hooking Essentials
	private boolean setupEssentials()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("Essentials");
		
		if (plugin != null)
		{
			essen = (Essentials) plugin;
			return true;
		} 
		else
		{
			return false;
		}
	}
	
	// Hooking Jail plugin
	private boolean setupJailPlugin()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("Jail");
		
		if (plugin != null)
		{
			jail = ((Jail) plugin).API;
			return true;
		} 
		else
		{
			return false;
		}
	}

	@Override
	public void onDisable()
	{
		_log.info("[Alastars Murder Awards] Saving Players...");
		this.saveConfig();
	}
	
    //Check if player exist in config
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		try 
		{
			if (this.getConfig().getString("players." + event.getPlayer().getName()) == null)
			{
				_log.info("[Alastars Murder Awards] Player not exists! Creating...");
				this.getConfig().set("players.", event.getPlayer().getName());
				this.getConfig().set("players." + event.getPlayer().getName() + ".kills", 0);
			}

		} catch (Exception e) 
		{
			
		}
	}

	// TagAPI code
	@EventHandler
	public void onNameTag(AsyncPlayerReceiveNameTagEvent  event)
	{
		if (useTags)
		{
			if (this.getConfig().getInt("players." + event.getNamedPlayer().getName() + ".kills") > 0) 
			{
				event.setTag(ChatColor.RED + event.getNamedPlayer().getName());
				_log.info("[Alastars Murder Awards] " + event.getNamedPlayer().getName() + " is a crime, paint his nick red!");
			}
			else 
			{
				event.setTag(ChatColor.AQUA + event.getNamedPlayer().getName());
			}
		}
	}

	// Command handling
	public boolean onCommand(CommandSender sender, Command cmd,	String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("ama")) 
		{
			String killers = murdersList;
			Map<String, Object> m = this.getConfig().getConfigurationSection("players").getValues(false);
			ArrayList<Murder> mrdrs = new ArrayList<Murder>();
			
			for (String nick : m.keySet())
			{
				if (this.getConfig().getInt("players." + nick + ".kills") > 0)
				{
					int mkills = this.getConfig().getInt("players." + nick + ".kills");
					mrdrs.add(new Murder(mkills, nick));
				}

			}
             // There's magic over here
			for (int i = 0; i < mrdrs.size(); ++i)
			{
				for (int l = 0; l < mrdrs.size(); ++l)
				{
					if (mrdrs.get(i).kills > mrdrs.get(l).kills)
					{
						if (l < i)
						{
							Murder f = mrdrs.get(i);
							Murder s = mrdrs.get(l);
							mrdrs.set(l, f);
							mrdrs.set(i, s);
						}
					} 
					else
					{
						if (l > i) 
						{
							Murder f = mrdrs.get(i);
							Murder s = mrdrs.get(l);
							mrdrs.set(l, f);
							mrdrs.set(i, s);
						}
					}
				}
			}
            // There's end of magic

			for (int i = 0; i < topLimit; ++i)
			{
				if(i < mrdrs.size())
				{
			    	killers += "\n" + (i + 1) +" Kills: " + mrdrs.get(i).kills + " Nick: " + mrdrs.get(i).nick;
			    }
				else
				{
			    	break;
				}
			}
			sender.sendMessage(killers);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("amaresetall"))
		{
			String nick = args[0];
			this.getConfig().set("players." + nick + ".kills", 0);
			sender.sendMessage("[" + getDescription().getName()	+ "] All kills removed!");
			return true;
		} 
		else if (cmd.getName().equalsIgnoreCase("amareset"))
		{
			String nick = args[0];
			if (this.getConfig().getInt("players." + nick + ".kills") > 0)
			{
				this.getConfig().set("players." + nick + ".kills",(this.getConfig().getInt("players." + nick + ".kills") - 1));
				sender.sendMessage("[" + getDescription().getName()	+ "] One kill removed");
			}
			else 
			{
				sender.sendMessage("[" + getDescription().getName()	+ "] That player doesn't have any kills");
			}
			return true;
		} 
		else if (cmd.getName().equalsIgnoreCase("amadisablejail"))
		{
			useJail = false;
			this.getConfig().set("useJail", false);
			sender.sendMessage("[" + getDescription().getName()	+ "] Jailing turned off");

			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("amaenablejail"))
		{
			useJail = true;
			this.getConfig().set("useJail", true);
			sender.sendMessage("[" + getDescription().getName()	+ "] Jailing turned on");

			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("amadisableeconomy"))
		{
			useEconomy = false;
			this.getConfig().set("useEconomy", false);
			sender.sendMessage("[" + getDescription().getName()	+ "] Economy turned off");

			return true;
		} 
		else if (cmd.getName().equalsIgnoreCase("amaenableeconomy")) 
		{
			useEconomy = true;
			this.getConfig().set("useEconomy", true);
			sender.sendMessage("[" + getDescription().getName()	+ "] Economy turned on");

			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("amasetmultiplier"))
		{
			int m = Integer.parseInt(args[0]);
			jailMultiplier = m;
			this.getConfig().set("jailMultiplierPerKill", m);
			sender.sendMessage("[" + getDescription().getName()	+ "] Multiplier resetted");

			return true;
		} 
		else if (cmd.getName().equalsIgnoreCase("amasetmoney")) 
		{
			int m = Integer.parseInt(args[0]);
			moneyPerKill = m;
			this.getConfig().set("moneyPerKill", m);
			sender.sendMessage("[" + getDescription().getName()	+ "] Money per kill resetted");
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) throws Exception 
	{
	
		Player victim = event.getEntity();
		Player killer = event.getEntity().getKiller();
		
		int kills = this.getConfig().getInt("players." + victim.getName() + ".kills");
		
		if (killer != null && killer instanceof Player)
		{
			if (kills > 0) 
			{
				if (useEconomy)
				{
					EconomyResponse r = econ.depositPlayer(killer.getName(),kills * moneyPerKill);
					if (r.transactionSuccess()) 
					{
						killer.sendMessage(awardMessage.replace("amt", econ.format(r.amount)));
					}
					else 
					{
						killer.sendMessage(String.format("["+ getDescription().getName()+ "] An error occured: %s", r.errorMessage));
					}
				}
				
				this.getConfig().set("players." + victim.getName() + ".kills",	0);
				String reformatMessage = killerDeadMessage.replace("k", killer.getName());
				reformatMessage = reformatMessage.replace("m", victim.getName());
                this.getServer().broadcastMessage(reformatMessage);
				if (useJail)
				{
					if (jailPlugin.equals("Jail"))
					{
						JailZone jailZone = ((JailZone)jail.getAllZones().toArray()[0]);
						jail.jailPlayer(victim.getName(),(int) (kills * jailMultiplier), jailZone.getName(), jailZone.getEmptyCell().getName(), "Murder", "Police");
						victim.sendMessage(String.format("["+ getDescription().getName()+ "] You have been jailed due your murder status!"));
					} 
					else if (jailPlugin.equals("Essentials"))
					{
						User user = essen.getUser(victim);
						user.setJailed(true);
						user.sendMessage(("You have been jailed due your murder status!"));
						user.setJail(null);
						user.setJail(essentialsJailName);
						long timeDiff =  0;
						timeDiff = DateUtil.parseDateDiff(Double.toString((kills * jailMultiplier)), true);
						user.setJailTimeout(timeDiff);
						
					}
				}
			}
			else 
			{
				killer.sendMessage(murderMessage);
				int kkills = this.getConfig().getInt("players." + killer.getName() + ".kills");
				this.getConfig().set("players." + killer.getName() + ".kills",	(kkills + 1));
			}
		}
	}
}

class Murder
{
	public String nick;
	public int kills;

	public Murder(int kills, String nick)
	{
		this.kills = kills;
		this.nick = nick;
	}
}