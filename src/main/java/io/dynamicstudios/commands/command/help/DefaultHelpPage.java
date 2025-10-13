package io.dynamicstudios.commands.command.help;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.DynamicCommand;
import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.command.argument.DynamicArguments;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.json.DynamicJText;
import io.dynamicstudios.json.JsonBuilder;
import io.dynamicstudios.json.data.component.IComponent;
import io.dynamicstudios.json.data.util.CColor;
import io.dynamicstudios.json.data.util.DynamicStyle;
import org.bukkit.command.CommandSender;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public class DefaultHelpPage extends HelpPage {

 public void help(CommandSender sender, DynamicCommand command, int page) {
	List<Map.Entry<String, String>> subCommand = new ArrayList<>();
	DynamicArgument<?>[] arr = Arrays.stream(command.rawArguments()).filter(c -> {
	 try {
		c.predicate().test(sender, "");
		return true;
	 } catch(CommandException e) {
		return false;
	 }
	}).collect(Collectors.toList()).toArray(new DynamicArgument[0]);
	LinkedHashSet<Map.Entry<String, String>> entries = new LinkedHashSet<>(command.generateMap(sender, arr).entrySet());
	Map.Entry<String, String> prev = null;
	for(Map.Entry<String, String> argument : entries) {
	 int addIndex = prev == null ? 0 : (argument.getValue().startsWith(prev.getValue()) && prev.getValue().trim().split(" ").length < argument.getValue().trim().split(" ").length ? 1 : 0);
	 subCommand.add(addIndex, argument);
	 prev = argument;
	}
	int pageSize = DynamicCommandManager.COMMANDS_PER_PAGE;
	List<List<Map.Entry<String, String>>> pages = splitList(subCommand, pageSize);
	page = page >= pages.size() ? pages.size() - 1 : (Math.max(page, 0));
	String commandName = command.getName();
	String aliases = "none";
	if(!command.aliases().isEmpty()) aliases = String.join("[c], [i]", command.aliases());

	DynamicJText msg = new DynamicJText("\n\n\n\n\n")
		 .add("[c]Showing help for /[i]" + commandName).suggest("/" + commandName);
	msg.add("\n[c]Aliases: [i]" + aliases);
 	msg.add("\n[c]Page [i]" + (page + 1) + "[c]/[i]" + pages.size());
	msg.add("\n[c]&l&m----------");
	if(page == 0) msg.add("&r&l&8&m«&r");
	else msg.add("&r&l[i]«").hover("[c]Click to go to previous page").command(command.getName() + " help " + (page));
	msg.add(" ").disableStyles(DynamicStyle.STRIKETHROUGH);
	if(page == pages.size()-1) msg.add("&r&l&8&m»&r");
	else msg.add("&r&l[i]»").hover("[c]Click to go to next page").command(command.getName() + " help " + (page + 2));
	msg.add("[c]&l&m----------&r");
	List<Map.Entry<String, String>> commandsToDisplay = subCommand.subList(page * pageSize, Math.min((page * pageSize) + pageSize, subCommand.size()));
	for(Map.Entry<String, String> name : commandsToDisplay) {
	 DynamicJText text = DynamicJText.parseText(name.getKey());
	 msg.add("\n [c]/[i]")
			.add(IComponent.textComponent(CColor.translateCommon(commandName + "[c] ")).add(text))
	 ;
	}
	msg.add("\n[c]&l&m----------");
	if(page == 0) msg.add("&r&l&8&m«&r");
	else msg.add("&r&l[i]«").hover("[c]Click to go to previous page").command(command.getName() + " help " + (page));
	msg.add(" ").disableStyles(DynamicStyle.STRIKETHROUGH);
	if(page == pages.size()-1) msg.add("&r&l&8&m»&r");
	else msg.add("&r&l[i]»").hover("[c]Click to go to next page").command(command.getName() + " help " + (page + 2));
	msg.add("[c]&l&m----------");
	msg.sendChat(sender);
 }

}
