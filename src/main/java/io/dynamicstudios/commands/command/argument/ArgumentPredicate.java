package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

public interface ArgumentPredicate {
 void test(CommandSender sender, String argument) throws CommandException;
}
