package io.dynamicstudios.commands.command;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.brigadier.registration.ReflectionBrigadier;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginReloadListener implements Listener {


 private final Plugin plugin;

 public PluginReloadListener(Plugin plugin) {
	this.plugin = plugin;
	PluginManager pm = Bukkit.getPluginManager();
	for(RegisteredListener reg : HandlerList.getRegisteredListeners(plugin)) {
	 if(reg.getListener().getClass().getName().equals(getClass().getName())) {
//		HandlerList.unregisterAll(reg.getListener());
		return;
	 }
	}
	pm.registerEvents(this, plugin);
 }


 @EventHandler(priority = EventPriority.LOW)
 void onLoad(PluginDisableEvent event) {
	SimpleCommandMap map = DynamicCommandManager.getCommandMap();
	for(Map.Entry<String, Set<DynamicCommand>> entry : DynamicCommandManager.commands().entrySet()) {
	 for(DynamicCommand command : entry.getValue()) {
		if(!event.getPlugin().getName().equals(command.owner().getName())) continue;
		List<String> aliases = command.aliases();
		for(String alias : aliases) {
		 DynamicCommandManager.unregisterCommand(map, entry.getKey() + ":" + alias);
		 DynamicCommandManager.unregisterCommand(map, alias);
		}
		DynamicCommandManager.unregisterCommand(map, command.getName());
		DynamicCommandManager.unregisterCommand(map, entry.getKey() + ":" + command.getName());
		DynamicCommandManager.unregisterCommand(map, entry.getKey() + ":");
		ReflectionBrigadier brigadier = DynamicCommandManager.getBrigadier(event.getPlugin(), entry.getKey());
		brigadier.removeChild(brigadier.getDispatcher().getRoot(), command.getName());
		brigadier.removeChild(brigadier.getDispatcher().getRoot(), entry.getKey() + ":" + command.getName());
		for(String alias : command.aliases()) {
		 brigadier.removeChild(brigadier.getDispatcher().getRoot(), alias);
		 brigadier.removeChild(brigadier.getDispatcher().getRoot(), entry.getKey() + ":" + alias);
		}
	 }
	}
	Bukkit.getScheduler().runTaskLater(event.getPlugin(), () -> {
	 try {
		Bukkit.getServer().getClass().getMethod("syncCommands")
			 .invoke(Bukkit.getServer());
	 } catch(Exception ex) {
	 }
	 Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
	}, 1);
 }
//
// @EventHandler
// void onLoad(PluginEnableEvent event) {
//	SimpleCommandMap map = DynamicCommandManager.getCommandMap();
//	for(Map.Entry<String, Set<DynamicCommand>> entry : DynamicCommandManager.commands().entrySet()) {
//	 for(DynamicCommand command : entry.getValue()) {
//		if(!event.getPlugin().getName().equals(command.owner().getName())) continue;
//		for(String alias : command.aliases()) {
//		 DynamicCommandManager.unregisterCommand(map, alias);
//		 DynamicCommandManager.unregisterCommand(map, entry.getKey() + ":" + alias);
//		}
//		DynamicCommandManager.unregisterCommand(map, command.getName());
//		DynamicCommandManager.unregisterCommand(map, entry.getKey() + ":" + command.getName());
//	 }
//	}
// }

}
