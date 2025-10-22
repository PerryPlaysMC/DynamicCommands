package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class DynamicLiteral extends DynamicArgument<String> {
 private String[] literals;
 private DynamicLiteral(String name, String description, String... literals) {
	super(name, description);
	this.literals = new String[literals.length + 1];
	if(literals.length > 0)
		System.arraycopy(literals, 0, this.literals, 1, literals.length);
	this.literals[0] = name;
 }

 public static DynamicLiteral of(String name, String description) {
	return new DynamicLiteral(name, description);
 }

 public static DynamicLiteral of(String[] literals, String description) {
	return new DynamicLiteral(literals[0], description, Arrays.copyOfRange(literals, 1, literals.length));
 }


 public static DynamicLiteral of(List<String> literals, String description) {
	return of(literals.toArray(new String[0]), description);
 }

 public String[] getLiterals() {
	return literals;
 }

 @Override
 public List<String> suggestions() {
	return availableNames();
 }

 @Override
 public String parse(CommandSender sender, String input) throws CommandException {
	return name();
 }

 @Override
 public boolean isValid(String input) {
	return name().equalsIgnoreCase(input);
 }
}
