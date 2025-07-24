package io.dynamicstudios.commands.command.node;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Creator: PerryPlaysMC
 * Created: 06/2024
 **/
public class CommandSourceWrapper<Source extends Entity> {

 private final Source source;

 public CommandSourceWrapper(Source source) {
	this.source = source;
 }

 public boolean isOp() {
	return source.isOp();
 }

 public boolean isPlayer() {
	return source instanceof Player;
 }

}
