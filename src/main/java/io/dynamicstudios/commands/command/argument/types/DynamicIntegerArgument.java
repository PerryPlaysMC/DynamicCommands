package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.CommandSyntaxException;
import io.dynamicstudios.commands.exceptions.brigadier.LiteralMessage;
import io.dynamicstudios.commands.exceptions.brigadier.SimpleCommandExceptionType;
import org.bukkit.command.CommandSender;

public class DynamicIntegerArgument extends DynamicArgument<Integer> {
	public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new LiteralMessage("Invalid int"));
	public static final SimpleCommandExceptionType OUT_OF_RANGE = new SimpleCommandExceptionType(new LiteralMessage("Integer out of range"));
	private int min, max;

	public DynamicIntegerArgument(int min, int max, String name, String description, DynamicArgument<?>... subArguments) {
		super(Integer.class, name, description, subArguments);
		this.min = min;
		this.max = max;
	}

	public int min() {
		return min;
	}

	public int max() {
		return max;
	}

	public static DynamicIntegerArgument of(String name, String description) {
		return of(-Integer.MAX_VALUE, name, description);
	}
	public static DynamicIntegerArgument of(int min, String name, String description) {
		return of(min, Integer.MAX_VALUE, name, description);
	}
	public static DynamicIntegerArgument of(int min, int max, String name, String description) {
		return new DynamicIntegerArgument(min, max, name, description);
	}

	@Override
	public boolean isValid(String input) {
	 return input.replace(",", "").matches("^-?(\\d+)((\\.\\d{1,7})?([eE][-+]?\\d+)?)?$");
	}

	@Override
	public Integer parse(CommandSender sender) throws CommandException {
		String input = input();
	 int value = Integer.parseInt(input);
	 if(value < min) throw new CommandException("Invalid integer: too low (" + value + ", min: " + min + ")");
	 if(value > max) throw new CommandException("Invalid integer: too high (" + value + ", max: " + max + ")");
	 return value;
	}

	@Override
	public void brigadierValidate(String input) throws CommandSyntaxException {
		if(input.replace(",", "").matches("^-?(\\d+)((\\.\\d{1,7})?([eE][-+]?\\d+)?)?$"))return;
		int value = Integer.parseInt(input);
		if(value < min) throw OUT_OF_RANGE.create();
		if(value > max) throw OUT_OF_RANGE.create();
		throw ERROR_INVALID.create();
	}
}
