package io.dynamicstudios.commands.exceptions.brigadier;

import java.util.function.Function;

public class DynamicCommandExceptionType implements CommandExceptionType {
	private final Function<Object, Message> function;

	public DynamicCommandExceptionType(Function<Object, Message> function) {
		this.function = function;
	}

	public CommandSyntaxException create(Object arg) {
		return new CommandSyntaxException(this, (Message) this.function.apply(arg));
	}

}