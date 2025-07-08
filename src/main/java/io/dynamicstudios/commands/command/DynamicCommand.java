package io.dynamicstudios.commands.command;

import com.google.common.collect.ImmutableMap;
import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.command.argument.DynamicArgumentBuilder;
import io.dynamicstudios.commands.command.argument.DynamicArguments;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.dynamicstudios.commands.DynamicCommandManager.HELP_PAGE;

/**
 * Creator: PerryPlaysMC
 * Created: 08/2022
 **/
public abstract class DynamicCommand extends Command {

 private final List<String> aliases;
 private final Set<DynamicSubCommand<?>> subCommands;
 protected final DynamicArgument<?>[] rawArguments;
 private final Map<String, String> arguments;
 private final JavaPlugin plugin;

 public DynamicCommand(JavaPlugin plugin, String name, String[] aliases, DynamicArgument<?>... arguments) {
	super(name);
	this.plugin = plugin;
	this.subCommands = new HashSet<>();
	this.aliases = new ArrayList<>();
	this.aliases.addAll(Arrays.asList(aliases));
	this.aliases.replaceAll(String::toLowerCase);
	this.rawArguments = arguments;
	Map<String, String> args = arguments.length == 0 ? null : new HashMap<>();
	for(DynamicArgument<?> argument : arguments) args.put(argument.name(), argument.description());
	this.arguments = args == null ? null : ImmutableMap.copyOf(args);
 }

 public void registerSubCommand(DynamicSubCommand<?> sub) {
	if(sub.arguments() != null && !(sub.arguments().size() == 1 && !sub.canUse(new String[0])) && !sub.arguments().isEmpty() && !sub.arguments().containsKey(sub.name() + " " +
		 "help")) {
	 DynamicArgument<?>[] args = new DynamicArgument[sub.rawArguments().length + 1];
	 for(int i = 0; i < sub.rawArguments().length; i++) args[i] = sub.rawArguments()[i];
	 args[args.length - 1] = DynamicArgumentBuilder.createLiteral("help", "Shows information about this command")
			.add(DynamicArgument.object(Integer.class, "page", "The page to view")).build().aliases("?");
	 DynamicSubCommand<?> cmd = new DynamicSubCommand(this, args) {
		@Override
		public String name() {
		 return sub.name();
		}

		@Override
		public List<String> aliases() {
		 return sub.aliases();
		}

		@Override
		public boolean canUse(String[] args) {
		 return sub.canUse(args) || (args.length == 1 && isHelp(args[0]))
				|| (HELP_PAGE.isPaginated() && args.length == 2 && isHelp(args[0]) &&
				DynamicCommandManager.canParse(Integer.class, args[1])) || args.length == 0;
		}

		@Override
		public String usage() {
		 return sub.usage();
		}

		@Override
		public boolean isPlayerCommand() {
		 return sub.isPlayerCommand();
		}

		@Override
		public String[] permissions() {
		 return sub.permissions();
		}

		@Override
		public List<String> executeTab(CommandSender sender, String label, DynamicArguments args) {
		 if(args.length() == 2 && HELP_PAGE.isPaginated()) {
			if(isHelp(args.get(0).input()) && getPages() > 1) {
			 return StringUtil.copyPartialMatches(args.get(1).input(),
					IntStream.rangeClosed(1, getPages()).mapToObj(String::valueOf).collect(Collectors.toList()), new ArrayList<>());
			}
		 }
		 return sub.executeTab(sender, label, args);
		}

		@Override
		public String helpMessage() {
		 return sub.helpMessage();
		}

		private boolean isHelp(String text) {
		 return text.equalsIgnoreCase("help") || text.equalsIgnoreCase("?");
		}

		@Override
		public boolean execute(CommandSender s, String label, DynamicArguments args) throws CommandException {
		 if(args.length() == 0) {
			if(sub.canUse(args) && sub.execute(s, label, args)) return true;
			HELP_PAGE.help(this, 0).send(s);
			return true;
		 }
//					boolean help = args.hasArgument("help");
//					if(help)
//						return args.getArgument("help").execute(s, args.get("help"), args.from(1));

		 return sub.execute(s, label, args);
		}

		private int getPages() {
		 return (arguments().size() + (arguments().size() % 5)) / 5;
		}
	 };
	 subCommands.add(cmd);
	} else
	 subCommands.add(sub);
	if(sub instanceof Listener) Bukkit.getPluginManager().registerEvents((Listener) sub, owner());
 }

 public static String colorize(String argument, String replacement1, String replacement2) {
	return argument.replaceAll("<(.+)>", replacement1).replaceAll("\\[(.+)\\]", replacement2);
 }

 public static String colorize(String argument) {
	return colorize(argument, "[r]$1[a]", "[o]$1[a]");
 }

 public List<String> aliases() {
	return aliases;
 }

 @Override
 public List<String> getAliases() {
	return aliases;
 }

 public DynamicCommand addAlias(String alias) {
	if(!aliases.contains(alias.toLowerCase())) aliases.add(alias.toLowerCase());
	return this;
 }

 public DynamicCommand removeAlias(String alias) {
	aliases.remove(alias.toLowerCase());
	return this;
 }

 private DynamicArgument argument(CommandSender sender, DynamicArguments arguments) {
	for(String name : arguments.inputs()) {
	 for(DynamicArgument<?> rawArgument : rawArguments) {
		if(rawArgument.name().equalsIgnoreCase(name) || rawArgument.isValid(name)) {
		 return rawArgument;
		}
	 }
	}
	return null;
 }

 public DynamicSubCommand subCommand(String name) {
	for(DynamicSubCommand<?> subCommand : subCommands) {
	 if(subCommand.name().equalsIgnoreCase(name)) {
		return subCommand;
	 }
	}
	return null;
 }

 public boolean run(CommandSender sender, String label, DynamicArguments arguments) throws CommandException {
	System.out.println("Failed");
	throw new CommandException("Invalid usage");
 }

 @Override
 public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
	DynamicArguments arguments = new DynamicArguments(sender, rawArguments.clone(), args);
	List<String> result = new ArrayList<>();
	if(args.length == 1) {
	 for(DynamicSubCommand<?> subCommand : subCommands) {
		if(subCommand.name().toLowerCase().startsWith(arguments.get(0).input().toLowerCase()))
		 result.add(subCommand.name());
	 }
	 for(DynamicArgument<?> argument : rawArguments) {
		collectInputs(sender, args, 0, result, argument);
	 }
	} else {
	 DynamicSubCommand<?> subCommand = subCommand(arguments.get(0).name());
	 if(subCommand != null)
		return subCommand.executeTab(sender, alias, new DynamicArguments(sender, subCommand.rawArguments, Arrays.copyOfRange(args, 1, args.length)));
	 DynamicArgument<?> argument = argument(sender, arguments);
	 if(argument == null) return result;
	 result.addAll(argumentNames(sender, Arrays.copyOfRange(args, 1, args.length), argument, 0));
	 if(!result.isEmpty()) return result;
	}
	return result;
 }

 public static String join(String... arguments) {
	String text = "";
	int len = 0;
	for(String argument : arguments) {
	 len++;
	 text += (argument == null ? "" : argument) + (len < arguments.length ? " " : "");
	}
	return text;
 }

 private List<String> argumentNames(CommandSender sender, String[] args, DynamicArgument<?> argument, int depth) {
	List<String> result = new ArrayList<>();
	List<String> valid = new ArrayList<>();
	for(DynamicArgument<?> dynamicArgument : argument.subArguments()) {
	 if(depth > args.length) continue;
	 String key = join(Arrays.copyOfRange(args, depth, Math.min(args.length, depth + dynamicArgument.span())));
	 if(key.isEmpty() || dynamicArgument.isValid(key)) valid.add(dynamicArgument.name());
	}
	for(DynamicArgument<?> dynamicArgument : argument.subArguments()) {
	 if(depth > args.length) continue;
	 if(!valid.isEmpty() && !valid.contains(dynamicArgument.name())) {
		continue;
	 }
	 if(depth == args.length - 1) {
		collectInputs(sender, args, 0, result, dynamicArgument);
		continue;
	 }
	 if(dynamicArgument.isValid(join(Arrays.copyOfRange(args, depth, Math.min(args.length, depth + dynamicArgument.span()))))) {
		if(dynamicArgument.span() > 1) {
		 for(int i = 0; i < dynamicArgument.span(); i++) {
			if(depth + i == args.length - 1) {
			 collectInputs(sender, args, i, result, dynamicArgument);
			}
		 }
		 if(depth + (dynamicArgument.span()) < args.length)
			if(dynamicArgument.subArguments().length > 0)
			 result.addAll(argumentNames(sender, args, dynamicArgument, depth + dynamicArgument.span()));
		} else result.addAll(argumentNames(sender, args, dynamicArgument, depth + 1));
		break;
	 } else collectInputs(sender, args, 0, result, dynamicArgument);
	}
	return result;
 }

 private void collectInputs(CommandSender sender, String[] args, int index, List<String> result, DynamicArgument<?> dynamicArgument) {
	List<String> suggestions = dynamicArgument.suggestions();
	if(suggestions == null) {
	 if(DynamicCommandManager.hasSuggestionProviderInput(dynamicArgument.type()))
		suggestions = DynamicCommandManager.suggestionsInput(dynamicArgument.type());
	 else if(DynamicCommandManager.hasSenderSuggestions(dynamicArgument.type()))
		suggestions = DynamicCommandManager.senderSuggestions(dynamicArgument.type()).apply(sender);
	 else if(DynamicCommandManager.hasSuggestionProvider(dynamicArgument.type()))
		suggestions = DynamicCommandManager.suggestions(dynamicArgument.type());
	}
	if(suggestions != null) {
	 for(String suggestion : suggestions) {
		String[] options = suggestion.split(" ");
		for(int i = index; i <= options.length; i++) {
		 String key = join(Arrays.copyOfRange(options, index, i));
		 if(!key.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) continue;
		 result.add(key);
		}
	 }
	}
 }

 @Override
 public boolean execute(CommandSender sender, String label, String[] args) {
	try {
	 return performCommand(sender, label, args);
	} catch(CommandException e) {
	 sender.sendMessage("§4Error while executing command: §c" + e.getMessage());
	 return true;
	}
 }

 public boolean performCommand(CommandSender sender, String label, String[] args) throws CommandException {
	if(args.length == 0) return run(sender, label, new DynamicArguments(sender, new DynamicArgument[0], args));
	DynamicArguments arguments = new DynamicArguments(sender, rawArguments.clone(), args);
	for(DynamicSubCommand<?> subCommand : subCommands) {
	 if(subCommand.name().equalsIgnoreCase(arguments.get(0).input()))
		return subCommand.execute(sender, label, arguments.from(1));
	}
	arguments.test(sender, args);
	return run(sender, label, arguments);
 }

 public JavaPlugin owner() {
	return plugin;
 }

 @Override
 public String toString() {
	return "DynamicCommand{" +
		 "aliases=" + aliases +
		 ", subCommands=" + subCommands +
		 ", rawArguments=" + Arrays.toString(rawArguments) +
		 ", arguments=" + arguments +
		 ", plugin=" + plugin +
		 '}';
 }

 public DynamicArgument<?>[] getArguments() {
	return rawArguments.clone();
 }
}
