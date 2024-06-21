package io.dynamicstudios.commands.command;

import com.google.common.collect.ImmutableMap;
import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.argument.DynamicArgument;
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
		for(DynamicArgument<?> argument : arguments) args.put(argument.name(),argument.description());
		this.arguments = args == null ? null : ImmutableMap.copyOf(args);
	}

	public void registerSubCommand(DynamicSubCommand<?> sub) {
		if(sub.arguments()!=null&&!(sub.arguments().size()==1&&!sub.canUse(new String[0]))&&!sub.arguments().isEmpty()&&!sub.arguments().containsKey(sub.name()+" " +
			"help")) {
			DynamicArgument<?>[] args = new DynamicArgument[sub.rawArguments().length+1];
			for(int i = 0; i < sub.rawArguments().length; i++) args[i] = sub.rawArguments()[i];
			args[args.length-1] = new DynamicArgument<String>("help","Shows information about this command",
				new DynamicArgument<Integer>("[page]","The page to view")) {
				@Override
				public boolean execute(CommandSender sender, String self, DynamicArguments args) throws CommandException {
					if(args.hasArgument("[page]")&&HELP_PAGE.isPaginated()) {
						HELP_PAGE.help(sub, args.get("[page]")).send(sender);
						return true;
					}
					HELP_PAGE.help(sub, 0).send(sender);
					return true;
				}
			}.aliases("?");
			DynamicSubCommand<?> cmd = new DynamicSubCommand<DynamicCommand>(this, args) {
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
						if(sub.canUse(args)&&sub.execute(s, label, args))return true;
						HELP_PAGE.help(this, 0).send(s);
						return true;
					}
					boolean help = args.hasArgument("help");
					if(help)
						return args.getArgument("help").execute(s, args.get("help"), args.from(1));

					return sub.execute(s, label, args);
				}

				private int getPages() {
					return (arguments().size() + (arguments().size()%5)) / 5;
				}
			};
			subCommands.add(cmd);
		}else
			subCommands.add(sub);
		if(sub instanceof Listener) Bukkit.getPluginManager().registerEvents((Listener) sub, owner());
	}

	public static String colorize(String argument, String replacement1, String replacement2) {
		return argument.replaceAll("<(.+)>", replacement1).replaceAll("\\[(.+)\\]",replacement2);
	}
	public static String colorize(String argument) {
		return colorize(argument,"[r]$1[a]","[o]$1[a]");
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

	public DynamicArgument argument(DynamicArguments arguments) {
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

	public boolean run(CommandSender sender, String label) throws CommandException{
		throw new CommandException("Invalid usage");
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		DynamicArguments arguments = new DynamicArguments(rawArguments.clone(), args);
		List<String> result = new ArrayList<>();
		if(args.length == 1) {
			for(DynamicSubCommand<?> subCommand : subCommands) {
				if(subCommand.name().toLowerCase().startsWith(arguments.get(0).input().toLowerCase()))
					result.add(subCommand.name());
			}

			for(DynamicArgument argument : arguments) {
				if(!argument.name().matches("([\\[<]).+([\\]>])") && argument.name().toLowerCase().startsWith(arguments.get(0).input().toLowerCase()))
					result.add(argument.name());
			}
		}else {
			DynamicSubCommand subCommand = subCommand(arguments.get(0).name());
			if(subCommand!=null)
				return subCommand.executeTab(sender,alias,new DynamicArguments(subCommand.rawArguments,Arrays.copyOfRange(args,1,args.length)));
			DynamicArgument argument = argument(arguments);
			if(argument!=null) {
//				for(DynamicArgument dynamicArgument : argument.subArguments()) {
					result.addAll(argumentNames(Arrays.copyOfRange(args,1,args.length),argument,0));
//				}
					if(!result.isEmpty())return result;
			}
		}
		return result;
	}
	private List<String> argumentNames(String[] args, DynamicArgument argument, int depth) {
		List<String> result = new ArrayList<>();
		for(DynamicArgument dynamicArgument : argument.subArguments()) {
			if(dynamicArgument.name().equalsIgnoreCase(args[depth]) || dynamicArgument.isValid(args[depth]))
				if(depth != args.length-1)
					result.addAll(argumentNames(args,dynamicArgument,depth+1));
				else if(!dynamicArgument.name().matches("[<\\[].+[>\\]]") && dynamicArgument.name().toLowerCase().startsWith(args[depth])) result.add(dynamicArgument.name());
		}
		return result;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		try {
			if(args.length == 0) return run(sender,label);
			DynamicArguments arguments = new DynamicArguments(rawArguments.clone(), args);
			for(DynamicSubCommand<?> subCommand : subCommands) {
				if(subCommand.name().equalsIgnoreCase(arguments.get(0).input())) return subCommand.execute(sender,label, arguments.from(1));
			}
			for(DynamicArgument argument : arguments) {
				if(argument.name().equalsIgnoreCase(args[0]) || (argument.name().matches("([\\[<]).+([\\]>])") && argument.isValid())) {
					return argument.execute(sender, argument.parse(), new DynamicArguments(argument.subArguments(), Arrays.copyOfRange(args, 1, args.length)));
				}
			}
		} catch (CommandException e) {
			sender.sendMessage(e.getMessage());
			return true;
		}
		return false;
	}

	public JavaPlugin owner() {
		return plugin;
	}
}
