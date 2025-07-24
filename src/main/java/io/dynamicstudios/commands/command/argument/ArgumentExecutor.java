package io.dynamicstudios.commands.command.argument;

import com.mojang.serialization.Dynamic;
import io.dynamicstudios.commands.command.DynamicCommand;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

public interface ArgumentExecutor {

 void perform(CommandSender sender, DynamicCommand executor, String cmdLabel, String label, DynamicArguments args) throws CommandException;

}
