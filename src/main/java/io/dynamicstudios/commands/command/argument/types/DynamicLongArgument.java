package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.CommandSyntaxException;
import io.dynamicstudios.commands.exceptions.brigadier.LiteralMessage;
import io.dynamicstudios.commands.exceptions.brigadier.SimpleCommandExceptionType;
import org.bukkit.command.CommandSender;

public class DynamicLongArgument extends DynamicArgument<Long> {
	public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new LiteralMessage("Invalid long"));
	public static final SimpleCommandExceptionType OUT_OF_RANGE = new SimpleCommandExceptionType(new LiteralMessage("Long out of range"));
	private long min, max;

	public DynamicLongArgument(long min, long max, String name, String description, DynamicArgument<?>... subArguments) {
		super(Long.class, name, description, subArguments);
		this.min = min;
		this.max = max;
	}

	public long min() {
		return min;
	}

	public long max() {
		return max;
	}

	public static DynamicLongArgument of(String name, String description) {
		return of(-Integer.MAX_VALUE, name, description);
	}
	public static DynamicLongArgument of(long min, String name, String description) {
		return of(min, Integer.MAX_VALUE, name, description);
	}
	public static DynamicLongArgument of(long min, long max, String name, String description) {
		return new DynamicLongArgument(min, max, name, description);
	}

	@Override
	public boolean isValid(String input) {
	 return input.replace(",", "").matches("^-?(\\d+)((\\.\\d{1,7})?([eE][-+]?\\d+)?)?$");
	}

	@Override
	public Long parse(CommandSender sender) throws CommandException {
		String input = input();
	 long value = Long.parseLong(input);
	 if(value < min) throw new CommandException("Invalid long: too low (" + value + ", min: " + min + ")");
	 if(value > max) throw new CommandException("Invalid long: too high (" + value + ", max: " + max + ")");
	 return value;
	}

	@Override
	public void brigadierValidate(String input) throws CommandSyntaxException {
		if(input.replace(",", "").matches("^-?(\\d+)((\\.\\d{1,7})?([eE][-+]?\\d+)?)?$"))return;
		long value = Long.parseLong(input);
		if(value < min) throw OUT_OF_RANGE.create();
		if(value > max) throw OUT_OF_RANGE.create();
		throw ERROR_INVALID.create();
	}
}
