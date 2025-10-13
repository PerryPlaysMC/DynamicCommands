package io.dynamicstudios.commands.command;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.command.argument.DynamicArgumentBuilder;
import io.dynamicstudios.commands.command.argument.DynamicArguments;
import io.dynamicstudios.commands.command.argument.types.DynamicLiteral;
import io.dynamicstudios.commands.command.argument.types.DynamicStringArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Creator: PerryPlaysMC
 * Created: 08/2022
 **/
public abstract class DynamicCommand extends Command implements PluginIdentifiableCommand {

 private final List<String> aliases;
 protected final DynamicArgument<?>[] rawArguments;
 private final JavaPlugin plugin;
 private boolean autoGenerateHelp = false;

 public DynamicCommand(JavaPlugin plugin, String name, List<String> aliases) {
	this(plugin, name, aliases, true, new DynamicArgument<?>[0]);
 }

 public DynamicCommand(JavaPlugin plugin, String name, List<String> aliases, DynamicArgument<?>... arguments) {
	this(plugin, name, aliases, true, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, String... aliases) {
	this(plugin, name, aliases, true, new DynamicArgument<?>[0]);
 }

 public DynamicCommand(JavaPlugin plugin, String name, String[] aliases, DynamicArgument<?>... arguments) {
	this(plugin, name, aliases, true, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, List<String> aliases, boolean autoGenerateHelp, DynamicArgument<?>... arguments) {
	this(plugin, name, aliases.toArray(new String[0]), autoGenerateHelp, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, String[] aliases, boolean autoGenerateHelp, DynamicArgumentBuilder... arguments) {
	this(plugin, name, aliases, autoGenerateHelp, Arrays.stream(arguments).map(DynamicArgumentBuilder::build).collect(Collectors.toList()).toArray(new DynamicArgument[0]));
 }

 public DynamicCommand(JavaPlugin plugin, String name, List<String> aliases, DynamicArgumentBuilder... arguments) {
	this(plugin, name, aliases, true, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, String[] aliases, DynamicArgumentBuilder... arguments) {
	this(plugin, name, aliases, true, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, List<String> aliases, boolean autoGenerateHelp, DynamicArgumentBuilder... arguments) {
	this(plugin, name, aliases.toArray(new String[0]), autoGenerateHelp, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, String[] aliases, boolean autoGenerateHelp, Collection<DynamicArgumentBuilder> arguments) {
	this(plugin, name, aliases, autoGenerateHelp, arguments.stream().map(DynamicArgumentBuilder::build).collect(Collectors.toList()).toArray(new DynamicArgument[0]));
 }

 public DynamicCommand(JavaPlugin plugin, String name, List<String> aliases, Collection<DynamicArgumentBuilder> arguments) {
	this(plugin, name, aliases, true, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, String[] aliases, Collection<DynamicArgumentBuilder> arguments) {
	this(plugin, name, aliases, true, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, List<String> aliases, boolean autoGenerateHelp, Collection<DynamicArgumentBuilder> arguments) {
	this(plugin, name, aliases.toArray(new String[0]), autoGenerateHelp, arguments);
 }

 public DynamicCommand(JavaPlugin plugin, String name, String[] aliases, boolean autoGenerateHelp, DynamicArgument<?>... arguments) {
	super(name);
	this.autoGenerateHelp = autoGenerateHelp;
	this.plugin = plugin;
	this.aliases = new ArrayList<>();
	this.aliases.addAll(Arrays.asList(aliases));
	this.aliases.replaceAll(String::toLowerCase);
	this.aliases.removeIf(String::isEmpty);
	boolean hasHelp = false;
	for(DynamicArgument<?> argument : arguments) {
	 if(!argument.name().equalsIgnoreCase("help")) continue;
	 hasHelp = true;
	 break;
	}
	if(arguments.length != 0 && !hasHelp && autoGenerateHelp) {
	 DynamicArgument<?>[] newArgs = new DynamicArgument<?>[arguments.length + 1];
	 System.arraycopy(arguments, 0, newArgs, 0, arguments.length);
	 int size = generateMap(Bukkit.getConsoleSender(), arguments).size();
	 newArgs[arguments.length] = DynamicArgumentBuilder.createLiteral("help", "Shows the help message", (help) -> {
			 help
					.predicate((s, arg) -> {
					 if(!testPermissionSilent(s)) throw new CommandException("invalid-permission");
					})
					.executes((sender, command, cmdLabel, label, arg) -> {
					 int page = arg.getOrDefault("page", 0) - 1;
					 DynamicCommandManager.HELP_PAGE.help(sender, this, page);
					})
					.intArg("page", "Page index for help", 1, DynamicCommandManager.HELP_PAGE.pageSize(size))
					.suggestions(() -> IntStream.range(1, 1 + DynamicCommandManager.HELP_PAGE.pageSize(size)).mapToObj(String::valueOf).collect(Collectors.toList()));
			})
			.build();
	 arguments = newArgs;
	}
	this.rawArguments = arguments;
	setAliases(this.aliases);
 }

 public Map<String, String> generateMap(CommandSender sender, DynamicArgument<?>[] arguments) {
	Map<String, String> args = arguments.length == 0 ? null : new LinkedHashMap<>();
	buildHelpMap(sender, arguments, args, "", "", "");
	return args;
 }

 private void buildHelpMap(CommandSender sender, DynamicArgument<?>[] arguments, Map<String, String> args, String previous, String commandKey, String rawKey) {
	Comparator<DynamicArgument> name = Comparator.comparing(DynamicArgument::length);
	arguments = Arrays.stream(arguments).sorted(name.thenComparing(c -> c.name().length()).thenComparing(DynamicArgument::name).reversed()).collect(Collectors.toList()).toArray(new DynamicArgument<?>[0]);
	for(DynamicArgument<?> argument : arguments) {
	 String argName = argument.name();
	 // Format argument name based on whether it's a literal or not
	 List<String> help = new ArrayList<>();
	 help.add("[i]Info:");
	 help.add("[c]- " + argument.description());
	 help.add("[i]Full Info:\n{fullInfo}");
	 help.add("[i]Click to insert:");
	 boolean optional = !argument.required() && argument.isOptional() && argument.parent(DynamicArgument::required) != null;
	 String formName = (argument instanceof DynamicLiteral) ? (optional ? "[r]" : "[c]") + wrap(argName, optional ? "[]" : "") :
			(argument.executes() == null && optional ?
				 "[o]" + wrap(argName, "[]") : "[r]" + wrap(argName, "<>"));

	 String inputName = (argument instanceof DynamicLiteral) ? wrap(argName, optional ? "[]" : "{}") :
			(argument.executes() == null && optional ?
				 wrap(argName, "[]") : wrap(argName, "<>"));
	 help.add("{cmdContent}");
	 String formattedName = "<hover=\"" + String.join("\n", help).replace("\"", "\\\"") + "\">" + formName + "</hover>";
	 String data = previous.isEmpty() ? formattedName : previous + " " + formattedName;
	 String cmdInput = colorize(getName() + " " + (commandKey.isEmpty() ? "" : commandKey + " ") + inputName,
			"", "", "$1");
	 String key = "<suggest=\"/" + colorize(cmdInput, "", "", "") + "\">" + data + "</suggest>";
	 if(argument.subArguments().length <= 1) {
		if((optional || argument instanceof DynamicLiteral || argument.required() || (argument.executes() == null && argument.parent(c -> c.executes() != null) == null)) && argument.subArguments().length == 0) {
		 String inf = argument.helpDescription();
		 DynamicArgument<?> parent = argument.parent();
		 while(parent != null && inf == null) {
			inf = parent.helpDescription();
			parent = parent.parent();
		 }
		 try {
			argument.predicate().test(sender, "");
			args.put(key.replace("{cmdContent}", "[c]/[i]" + getName() + " " + (rawKey.isEmpty() ? "" : rawKey + " ") + formName)
				 .replace("\n[i]Full Info:\n{fullInfo}", inf == null || inf.equalsIgnoreCase(argument.description()) ? "" : "\n[i]Full Info:\n[c]- " + inf), cmdInput);
		 } catch(Exception e) {
			continue;
		 }
		}
	 }
	 buildHelpMap(sender, argument.subArguments(), args, data, colorize(commandKey.isEmpty() ? argName : commandKey + " " + argName, "", "", ""),
			(rawKey.isEmpty() ? "" : rawKey + " ") + formName);
	}
 }

 private String wrap(String text, String characters) {
	if(characters.isEmpty()) return text;
	return characters.charAt(0) + text + characters.charAt(1);
 }

 public static String colorize(String argument, String replacement1, String replacement2, String replacement3) {
	return argument.replaceAll("\\[([^]]+)]", replacement2)
		 .replaceAll("<([^>]+)>", replacement1)
		 .replaceAll("\\{([^}]+)}", replacement3);
 }

 public static String colorize(String argument) {
	return colorize(argument, "[r]<$1>[a]", "[o][$1][a]", "[c]$1[a]");
 }

 public DynamicArgument<?>[] rawArguments() {
	return rawArguments;
 }

 public List<String> aliases() {
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

 public boolean run(CommandSender sender, String label, DynamicArguments arguments) throws CommandException {
	if(arguments.length() == 0) throw new CommandException("Not implemented");
	int page = 0;
	DynamicCommandManager.HELP_PAGE.help(sender, this, page);
	return true;
 }

 @Override
 public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
	List<String> completions = getCompletions(sender, alias, args);
	return completions == null ? new ArrayList<>() : completions.stream().map(s -> s.replace("§:", "")).collect(Collectors.toList());
 }

 public List<String> getCompletions(CommandSender sender, String alias, String[] args) {
	DynamicArguments arguments = new DynamicArguments(sender, rawArguments.clone(), args);
	List<String> result = new ArrayList<>();
	if(args.length == 1) {
	 for(DynamicArgument<?> argument : rawArguments) {
		collectInputs(sender, args, arguments, 0, result, argument);
	 }
	} else {
	 DynamicArgument<?> argument = arguments.startingArgument();
	 if(argument == null) return result;
	 result.addAll(argumentNames(sender, arguments, Arrays.copyOfRange(args, 1, args.length), argument, 0));
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

 private List<String> argumentNames(CommandSender sender, DynamicArguments arguments, String[] args, DynamicArgument<?> argument, int depth) {
	List<String> result = new ArrayList<>();
	List<String> valid = new ArrayList<>();
	for(DynamicArgument<?> dynamicArgument : argument.subArguments()) {
	 if(depth > args.length) continue;
	 String key = join(Arrays.copyOfRange(args, depth, Math.min(args.length, depth + (dynamicArgument.span() == -1 || dynamicArgument.span() == -2 ? 256 : dynamicArgument.span()))));
	 if(key.isEmpty() || dynamicArgument.isValid(key)) {
		valid.add(dynamicArgument.name());
	 }
	}
	if(argument.subArguments().length == 0) {
	 if((argument instanceof DynamicStringArgument && argument.span() == -1) || argument.span() == -2) {
		collectInputs(sender, args, arguments, 0, result, argument);
		return result;
	 }
	}
	if(valid.isEmpty()) return result;
	for(DynamicArgument<?> dynamicArgument : argument.subArguments()) {
	 if(!valid.contains(dynamicArgument.name())) {
		continue;
	 }
	 if(depth == args.length - 1) {
		if(arguments.hasArgument(dynamicArgument.parent().name())) {
		 collectInputs(sender, args, arguments, 0, result, dynamicArgument);
		}
		if(!result.isEmpty()) continue;
	 }
	 String join = join(Arrays.copyOfRange(args, depth, Math.min(args.length, depth + (dynamicArgument.span() == -1 || dynamicArgument.span() == -2 ? 256 : dynamicArgument.span()))));
	 if(dynamicArgument.isValid(join)) {
		if(testPredicate(sender, dynamicArgument)) continue;
		if(dynamicArgument.span() > 1) {
		 for(int i = 0; i < dynamicArgument.span(); i++) {
			if(depth + i == args.length - 1) {
			 collectInputs(sender, args, arguments, i, result, dynamicArgument);
			}
		 }
		 if(depth + (dynamicArgument.span()) < args.length)
			if(dynamicArgument.subArguments().length > 0)
			 result.addAll(argumentNames(sender, arguments, args, dynamicArgument, depth + dynamicArgument.span()));
		} else {
		 result.addAll(argumentNames(sender, arguments, args, dynamicArgument, depth + 1));
		}
		if(!result.isEmpty()) break;
	 }
	}
	return result;
 }

 private static boolean testPredicate(CommandSender sender, DynamicArgument<?> dynamicArgument) {
	try {
	 dynamicArgument.predicate().test(sender, "");
	} catch(Exception e) {
	 return true;
	}
	return false;
 }

 private void collectInputs(CommandSender sender, String[] args, DynamicArguments arguments, int index, List<String> result, DynamicArgument<?> dynamicArgument) {
	if(testPredicate(sender, dynamicArgument)) return;
	try {
	 dynamicArgument.tabPredicate().test(sender, arguments);
	} catch(Exception e) {
	 return;
	}
	List<String> suggestions = dynamicArgument.suggestions();
	if(suggestions == null) {
	 if(dynamicArgument.suggestions(sender) != null)
		suggestions = dynamicArgument.suggestions(sender);
	 else if(dynamicArgument.suggestions(sender, arguments) != null)
		suggestions = dynamicArgument.suggestions(sender, arguments);
	 else if(DynamicCommandManager.hasSuggestionProviderInput(dynamicArgument.type()))
		suggestions = DynamicCommandManager.suggestionsInput(dynamicArgument.type());
	 else if(DynamicCommandManager.hasSenderSuggestions(dynamicArgument.type()))
		suggestions = DynamicCommandManager.senderSuggestions(dynamicArgument.type()).apply(sender);
	 else if(DynamicCommandManager.hasSuggestionProvider(dynamicArgument.type()))
		suggestions = DynamicCommandManager.suggestions(dynamicArgument.type());
	}
	if(suggestions == null || suggestions.isEmpty()) {
	 List<String> append = dynamicArgument.suggestions(args[args.length - 1]);
	 appendResults(args, result, append);
	 return;
	}
	for(String suggestion : suggestions) {
	 if((dynamicArgument instanceof DynamicStringArgument && dynamicArgument.span() == -1) || dynamicArgument.span() == -2) {
		appendValue(suggestion, args, result);
		continue;
	 }
	 String[] options = suggestion.split(" ");
	 for(int i = index; i <= options.length; i++) {
		String key = join(Arrays.copyOfRange(options, index, i));
		appendValue(key, args, result);
	 }
	}
	List<String> append = dynamicArgument.suggestions(args[args.length - 1]);
	appendResults(args, result, append);
 }

 private static void appendResults(String[] args, List<String> result, List<String> append) {
	if(append != null && !append.isEmpty()) {
	 String arg = args[args.length - 1];
	 List<String> typedMatches = new ArrayList<>();
	 Map<String, String> allMatches = new HashMap<>();
	 for(String s : append) {
		List<String> longestMatch = new ArrayList<>();
		boolean typed = false;
		for(int i = arg.length(); i >= 0; i--) {
		 String sub = arg.substring(i).toLowerCase();
		 if(s.toLowerCase().startsWith(sub)) {
			longestMatch.add(arg.substring(0, i) + "§:" + s);
			if(!sub.isEmpty()) typed = true;
		 }
		}
		if(!typed)
		 longestMatch.stream().min(Comparator.comparingInt(String::length))
				.ifPresent(c -> allMatches.put(s, c));
		else longestMatch.stream().min(Comparator.comparingInt(String::length)).ifPresent(typedMatches::add);
	 }
	 if(!typedMatches.isEmpty())
		result.addAll(typedMatches);
	 else for(String s : append) {
		if(!allMatches.containsKey(s)) continue;
		result.add(allMatches.get(s));
	 }
	 if(result.isEmpty())
		append.forEach(c -> result.add(args[args.length - 1] + "§:" + c));
	}
 }

 private static void appendValue(String key, String[] args, List<String> result) {
	if(!key.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) return;
	if(key.equalsIgnoreCase(args[args.length - 1])) return;
	result.add(key);
 }

 @Override
 public boolean execute(CommandSender sender, String label, String[] args) {
	try {
	 return performCommand(sender, label, args);
	} catch(CommandException | ClassCastException e) {
	 if(e.getMessage().matches(".*cannot be cast to class.*Player.*")) {
		sender.sendMessage("§cOnly players may perform this command");
		return true;
	 }
	 if(e instanceof CommandException) {
		if(((CommandException) e).isCustom()) {
		 sender.sendMessage(e.getMessage());
		 return true;
		}
	 }
	 e.printStackTrace();
	 sender.sendMessage("§4Error while executing command\n§c" + e.getMessage());
	 return true;
	}
 }

 public boolean performCommand(CommandSender sender, String label, String[] args) throws CommandException {
	if(args.length == 0) return run(sender, label, new DynamicArguments(sender, new DynamicArgument[0], args));
	DynamicArguments arguments = new DynamicArguments(sender, rawArguments.clone(), args);
	arguments.test(sender, args);
	int i = arguments.inputs().length;
	int size = 0;
	boolean didExecute = false, retains = false;
	for(DynamicArgument<?> argument : arguments.reversed()) {
	 i--;
	 if(argument.executes() != null && !didExecute) {
		argument.executes().perform(sender, this, label, arguments.inputs()[i], arguments);
		didExecute = true;
	 }
	 size += arguments.getSpan(argument.name());
	 if(argument.retains() || retains) {
		retains = true;
		continue;
	 }
	 size = 0;
	}
	try {
	 if(didExecute) return true;
	 return run(sender, label, arguments);
	} catch(CommandException e) {
	 if(didExecute && e.getMessage().equalsIgnoreCase("Not Implemented")) return true;
	 throw e;
	}
 }

 public JavaPlugin owner() {
	return plugin;
 }

 @Override
 public String toString() {
	return "DynamicCommand{" +
		 "aliases=" + aliases +
		 ", rawArguments=" + Arrays.toString(rawArguments) +
		 ", plugin=" + plugin +
		 '}';
 }

 public JavaPlugin plugin() {
	return plugin;
 }

 @Override
 public Plugin getPlugin() {
	return plugin;
 }

 public DynamicArgument<?>[] getArguments() {
	return rawArguments.clone();
 }
}
