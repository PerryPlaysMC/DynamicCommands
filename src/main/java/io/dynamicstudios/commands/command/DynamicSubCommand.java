package io.dynamicstudios.commands.command;

import com.google.common.collect.ImmutableMap;
import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.command.argument.DynamicArguments;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public abstract class DynamicSubCommand<C extends DynamicCommand> {

	protected final C command;
	protected final DynamicArgument[] rawArguments;
	private final Map<String, String> arguments;

	protected DynamicSubCommand(C command, DynamicArgument... arguments) {
		this.command = command;
		this.rawArguments = arguments;
		Map<String, String> args = arguments.length == 0 ? null : new HashMap<>();
		for (DynamicArgument argument : arguments) args.put(argument.name(), argument.description());
		this.arguments = args == null ? null : ImmutableMap.copyOf(args);
	}

	public C parent() {
		return command;
	}

	public boolean isPlayerCommand() {
		return false;
	}

	public boolean isEnabled() {
		return true;
	}

	public abstract String name();

	public String usage() {
		return name();
	}

	public abstract String helpMessage();

	public DynamicArgument<?>[] rawArguments() {
		return rawArguments;
	}

	public Map<String, String> arguments() {
		return arguments;
	}

	public String[] permissions() {
		return new String[]{command.owner().getName().toLowerCase() + ".command." + command.getName() + "." + name()};
	}


	public List<String> aliases() {
		return new ArrayList<>();
	}

	private boolean compare(String[] args, String[] args2) {
		if (args.length != args2.length) return false;
		for (int i = 0; i < args.length; i++) {
			if (args2[i].matches("<.*>") || args2[i].matches("\\[.*]")) continue;
			if (!args[i].equals(args2[i])) return false;
		}
		return true;
	}

	private boolean compare(DynamicArguments args, DynamicArguments args2) {
		if (args.length() != args2.length()) return false;
		for (int i = 0; i < args.length(); i++) {
			if (args2.get(i).input().matches("<.*>") || args2.get(i).input().matches("\\[.*]")) continue;
			if (!args.get(i).input().equals(args2.get(i).input())) return false;
		}
		return true;
	}

	public boolean canUse(String[] args) {
		return arguments.isEmpty() || arguments.keySet().stream()
			 .anyMatch(key -> compare(args, key.split(" ")));
	}

	public boolean canUse(DynamicArguments args) {
		return arguments.isEmpty() || arguments.keySet().stream()
			 .anyMatch(key -> compare(args.names(), key.split(" ")));
	}

	public abstract boolean execute(CommandSender s, String label, DynamicArguments args) throws CommandException;

	public List<String> executeTab(CommandSender sender, String label, DynamicArguments args) {
		return new ArrayList<>();
	}

	@Override
	public String toString() {
		return "DynamicSubCommand{" +
			 "command=" + command +
			 ", rawArguments=" + Arrays.toString(rawArguments) +
			 ", arguments=" + arguments +
			 '}';
	}
}
