package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.LiteralMessage;
import io.dynamicstudios.commands.exceptions.brigadier.SimpleCommandExceptionType;
import org.bukkit.command.CommandSender;

public class DynamicFloatArgument extends DynamicArgument<Float> {
 public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new LiteralMessage("Invalid float"));
 public static final SimpleCommandExceptionType OUT_OF_RANGE = new SimpleCommandExceptionType(new LiteralMessage("Float out of range"));
 private final float min;
 private final float max;

 public DynamicFloatArgument(float min, float max, String name, String description, DynamicArgument<?>... subArguments) {
	super(Float.class, name, description, subArguments);
	this.min = min;
	this.max = max;
 }

 public float min() {
	return min;
 }

 public float max() {
	return max;
 }

 public static DynamicFloatArgument of(String name, String description) {
	return of(-Float.MAX_VALUE, name, description);
 }

 public static DynamicFloatArgument of(float min, String name, String description) {
	return of(min, Float.MAX_VALUE, name, description);
 }

 public static DynamicFloatArgument of(float min, float max, String name, String description) {
	return new DynamicFloatArgument(min, max, name, description);
 }

 @Override
 public boolean isValid(String input) {
	return input.replace(",", "").matches("^-?(\\d+)((\\.\\d{1,7})?([eE][-+]?\\d+)?)?$");
 }


 @Override
 public Float parse(CommandSender sender, String input) throws CommandException {
	float value = Float.parseFloat(input);
	if(value < min) throw new CommandException("Invalid float: too low (" + value + ", min: " + min + ")");
	if(value > max) throw new CommandException("Invalid float: too high (" + value + ", max: " + max + ")");
	return value;
 }

// @Override
// public void brigadierValidate(String input) throws CommandSyntaxException {
//	if(input.replace(",", "").matches("^-?(\\d+)((\\.\\d{1,7})?([eE][-+]?\\d+)?)?$")) return;
//	float value = Float.parseFloat(input);
//	if(value < min) throw OUT_OF_RANGE.create();
//	if(value > max) throw OUT_OF_RANGE.create();
//	throw ERROR_INVALID.create();
// }
}
