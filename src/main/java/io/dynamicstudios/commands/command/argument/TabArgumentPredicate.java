package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

public interface TabArgumentPredicate {
 void test(CommandSender sender, DynamicArguments arg) throws CommandException;
}
