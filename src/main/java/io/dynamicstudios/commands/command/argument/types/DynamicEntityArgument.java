package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.command.argument.ValidationResult;
import io.dynamicstudios.commands.command.argument.types.helper.TargetSelector;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.LiteralMessage;
import io.dynamicstudios.commands.exceptions.brigadier.SimpleCommandExceptionType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class DynamicEntityArgument extends DynamicArgument<TargetSelector> {
 public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new LiteralMessage("Invalid boolean"));
 public static final SimpleCommandExceptionType OUT_OF_RANGE = new SimpleCommandExceptionType(new LiteralMessage("Double out of range"));

 private boolean single;
 private boolean player;

 public DynamicEntityArgument(boolean single, boolean player, String name, String description, DynamicArgument<?>... subArguments) {
	super(TargetSelector.class, name, description, subArguments);
	this.single = single;
	this.player = player;
	suggestionsAppend(TargetSelector::getTabCompletions);
	span(-2);
 }

 public boolean isSingle() {
	return single;
 }

 public boolean isPlayer() {
	return player;
 }

 public static DynamicEntityArgument entity(String name, String description) {
	return of(true, false, name, description);
 }

 public static DynamicEntityArgument entities(String name, String description) {
	return of(false, false, name, description);
 }

 public static DynamicEntityArgument player(String name, String description) {
	return of(true, true, name, description);
 }

 public static DynamicEntityArgument players(String name, String description) {
	return of(false, true, name, description);
 }

 public static DynamicEntityArgument of(boolean single, boolean player, String name, String description) {
	return new DynamicEntityArgument(single, player, name, description);
 }

 @Override
 public boolean isValid(String input) {
	if(TargetSelector.getTabCompletions(String.join(" ", input)).isEmpty() && input.endsWith("]"))return true;
	return !TargetSelector.getTabCompletions(String.join(" ", input)).isEmpty();
 }
 @Override
 public ValidationResult validationResult(String input) {
	if(TargetSelector.getTabCompletions(String.join(" ", input)).isEmpty() && input.endsWith("]"))return ValidationResult.NEXT;
	return TargetSelector.getTabCompletions(String.join(" ", input)).isEmpty() ? ValidationResult.CONTINUE : ValidationResult.FAIL;
 }

 @Override
 public TargetSelector parse(CommandSender sender, String input) throws CommandException {
	return new TargetSelector(input, sender
		 , sender instanceof Player ? ((Player) sender).getLocation() : Bukkit.getWorlds().get(0).getSpawnLocation()
		 , sender instanceof Player ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0));
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
