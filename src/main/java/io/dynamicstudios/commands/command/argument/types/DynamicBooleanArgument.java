package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.CommandSyntaxException;
import io.dynamicstudios.commands.exceptions.brigadier.LiteralMessage;
import io.dynamicstudios.commands.exceptions.brigadier.SimpleCommandExceptionType;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class DynamicBooleanArgument extends DynamicArgument<Boolean> {
 public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new LiteralMessage("Invalid boolean"));

 public DynamicBooleanArgument(String name, String description, DynamicArgument<?>... subArguments) {
	super(Boolean.class, name, description, subArguments);
 }

 public static DynamicBooleanArgument of(String name, String description) {
	return new DynamicBooleanArgument(name, description);
 }

 @Override
 public boolean isValid(String input) {
	return input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")
		 || input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no");
 }

 @Override
 public Boolean parse(CommandSender sender, String input) throws CommandException {
	if(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("true")) return true;
	if(input.equalsIgnoreCase("no") || input.equalsIgnoreCase("false")) return false;
	return null;
 }

// @Override
// public void brigadierValidate(String input) throws CommandSyntaxException {
//	if(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("true")) return;
//	if(input.equalsIgnoreCase("no") || input.equalsIgnoreCase("false")) return;
//	throw ERROR_INVALID.create();
// }

 @Override
 public List<String> suggestions() {
	return Arrays.asList("yes", "no", "true", "false");
 }
}
