package io.dynamicstudios.commands.util;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SuggestionProvider {

 List<String> suggests();

 <T> List<String> suggests(Class<T> input);

 List<String> suggests(CommandSender sender);


}
