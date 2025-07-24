package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.CommandSyntaxException;
import org.bukkit.command.CommandSender;

public class DynamicStringArgument extends DynamicArgument<String> {

 public DynamicStringArgument(String name, String description, StringType type, DynamicArgument<?>... subArguments) {
	super(String.class, name, description, subArguments);
	span(type == StringType.GREEDY ? -1 : 1);
 }

 public static DynamicStringArgument of(String name, String description) {
	return new DynamicStringArgument(name, description, StringType.GREEDY);
 }

 public static DynamicStringArgument of(String name, String description, StringType type) {
	return new DynamicStringArgument(name, description, type);
 }

 @Override
 public boolean isValid(String input) {
	return (span() == -1 || (input.split(" ").length <= span() && !input.startsWith("\"")) || input.matches("\\\"(?<data>(?:(?=\\\\\\\")..|(?!\\\").)*)\\\""))
		 && !input.isEmpty();
 }

 @Override
 public String parse(CommandSender sender, String input) throws CommandException {
	return input;
 }

 public enum StringType {
	LIMITED, WORD, GREEDY, SINGLE
 }

}
