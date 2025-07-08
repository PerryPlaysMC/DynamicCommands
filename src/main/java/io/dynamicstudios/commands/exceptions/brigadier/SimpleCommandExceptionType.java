package io.dynamicstudios.commands.exceptions.brigadier;


public class SimpleCommandExceptionType implements CommandExceptionType {
	private final Message message;

	public SimpleCommandExceptionType(Message message) {
		this.message = message;
	}

	public CommandSyntaxException create() {
		return new CommandSyntaxException(this, this.message);
	}

	public String toString() {
		return this.message.getString();
	}
}
