package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.LiteralMessage;
import io.dynamicstudios.commands.exceptions.brigadier.SimpleCommandExceptionType;
import org.bukkit.command.CommandSender;

public class DynamicDoubleArgument extends DynamicArgument<Double> {
 public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new LiteralMessage("Invalid boolean"));
 public static final SimpleCommandExceptionType OUT_OF_RANGE = new SimpleCommandExceptionType(new LiteralMessage("Double out of range"));
 private final double min;
 private final double max;

 public DynamicDoubleArgument(double min, double max, String name, String description, DynamicArgument<?>... subArguments) {
	super(Double.class, name, description, subArguments);
	this.min = min;
	this.max = max;
 }

 public double min() {
	return min;
 }

 public double max() {
	return max;
 }

 public static DynamicDoubleArgument of(String name, String description) {
	return of(-Double.MAX_VALUE, name, description);
 }

 public static DynamicDoubleArgument of(double min, String name, String description) {
	return of(min, Double.MAX_VALUE, name, description);
 }

 public static DynamicDoubleArgument of(double min, double max, String name, String description) {
	return new DynamicDoubleArgument(min, max, name, description);
 }

 @Override
 public boolean isValid(String input) {
	return input.replace(",", "").matches("^-?(\\d+)((\\.\\d{1,17})?([eE][-+]?\\d+)?)?$");
 }

 @Override
 public Double parse(CommandSender sender, String input) throws CommandException {
	double value = Double.parseDouble(input);
	if(value < min) throw new CommandException("Invalid double: too low (" + value + ", min: " + min + ")");
	if(value > max) throw new CommandException("Invalid double: too high (" + value + ", max: " + max + ")");
	return value;
 }

// @Override
// public void brigadierValidate(String input) throws CommandSyntaxException {
//	if(input.replace(",", "").matches("^-?(\\d+)((\\.\\d{1,17})?([eE][-+]?\\d+)?)?$")) {
//	 return;
//	}
//	double value = Double.parseDouble(input);
//	if(value < min) throw OUT_OF_RANGE.create();
//	if(value > max) throw OUT_OF_RANGE.create();
//	throw ERROR_INVALID.create();
// }
}
