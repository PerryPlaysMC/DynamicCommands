package io.dynamicstudios.commands.command.help;

import dev.dynamicstudios.json.DynamicJText;
import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.DynamicCommand;
import io.dynamicstudios.commands.command.DynamicSubCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public class DefaultHelpPage extends HelpPage {


  public DynamicJText help(DynamicSubCommand<?> command, int page) {
    DynamicCommand parent = command.parent();
    Map<String,String> args = command.arguments();
    List<String> subCommand = new ArrayList<>(args.keySet());
    List<List<String>> pages = splitList(subCommand, DynamicCommandManager.COMMANDS_PER_PAGE);
    Collections.sort(subCommand);
    page = page >= pages.size() ? pages.size()-1 : (Math.max(page, 0));
    String commandName = parent.getName();
    DynamicJText msg = new DynamicJText("\n\n\n\n\n")
      .add("[c]Showing help for /[i]" + commandName + " [r]" + command.name()).suggest("/" + commandName + " " + command.name());
    msg.add("\n[c]Page [i]" + (page+1) + "[c]/[i]" + pages.size());
    msg.add("\n[c]&l&m---------------------&r");
    String aliases = "none";
    if(!command.aliases().isEmpty())aliases = String.join("[c], [i]", command.aliases());
    msg.add("\n[c]Aliases: [i]" + aliases);
    List<String> commandsToDisplay = subCommand.subList(page*5,Math.min((page*5)+5, subCommand.size()));
    for(String name : commandsToDisplay) {
      String info = args.get(name);
      msg.add("\n  [c]-/[i]")
        .add(parent.getName()+"[r] " + command.name() + " " + DynamicCommand.colorize(name))
        .suggest("/" + commandName + " " + command.name() + " " + DynamicCommand.colorize(name,"", ""))
        .hover("[c]Info:",
          "[c] - [i]" + info,
          "[c]Click to insert:",
          "/[i]"+commandName+" [r]" + command.name() + " "+ DynamicCommand.colorize(name));
    }
    msg.add("\n[c]&l&m---------------------");
    return msg;
  }

}
