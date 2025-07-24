package io.dynamicstudios.commands.command.help;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.DynamicCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public abstract class HelpPage {

 public abstract void help(CommandSender sender, DynamicCommand command, int page);

 public boolean isPaginated() {
	return true;
 }


 public int pageSize(int arguments) {
	double maxPerPage = DynamicCommandManager.COMMANDS_PER_PAGE;
	return (int) Math.ceil(arguments / maxPerPage);
 }

 public <T> List<List<T>> splitList(List<T> list, int n) {
	List<List<T>> result = new ArrayList<>();
	for(int i = 0; i < list.size(); i += n) {
	 result.add(list.subList(i, Math.min(i + n, list.size())));
	}
	return result;
 }

}
