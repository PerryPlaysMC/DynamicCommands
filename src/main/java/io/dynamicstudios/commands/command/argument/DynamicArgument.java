package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.argument.types.*;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.CommandSyntaxException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public abstract class DynamicArgument<T> {

 private final String name, description;
 private DynamicArgument parent;
 protected final List<DynamicArgument<?>> subArguments;
 private String[] aliases;
 private List<String> availableNames;
 private final Class<T> type;
 private String input;
 private int span = 1;
 Supplier<Collection<String>> suggestions;

 public DynamicArgument(String name, String description, DynamicArgument<?>... subArguments) {
	this(null, name, description, subArguments);
 }

 public DynamicArgument(Class<T> type, String name, String description, DynamicArgument<?>... subArguments) {
	Class<T> clazz = null;
	try {
	 clazz = (Class<T>) Class.forName(getClass().getGenericSuperclass().getTypeName().replaceAll(".+<([^>]+)>", "$1"));
	} catch(ClassNotFoundException e) {
	}
	this.type = (clazz != null && clazz.getName().equalsIgnoreCase(Object.class.getName())) ? (Class<T>) String.class : (type == null ? clazz : type);
	this.name = name;
	this.description = description;
	this.subArguments = new ArrayList<>(Arrays.asList(subArguments));
	this.aliases = null;
	this.availableNames = new ArrayList<>();
	availableNames.add(name);
 }

 public DynamicArgument parent() {
	return parent;
 }

 public void parent(DynamicArgument parent) {
	this.parent = parent;
 }

 public int span() {
	return span;
 }

 protected DynamicArgument<T> span(int span) {
	this.span = span;
	return this;
 }

 public List<String> availableNames() {
	return availableNames;
 }

 public static DynamicLiteral literal(String name, String description) {
	return DynamicLiteral.of(name, description);
 }

 //<editor-fold desc="Integer Arguments">

 public static DynamicIntegerArgument intArg(String name, String description) {
	return DynamicIntegerArgument.of(name, description);
 }

 public static DynamicIntegerArgument intArg(String name, String description, int min, int max) {
	return DynamicIntegerArgument.of(min, max, name, description);
 }

 public static DynamicIntegerArgument intArg(String name, String description, int min) {
	return DynamicIntegerArgument.of(min, name, description);
 }

 //</editor-fold>

 //<editor-fold desc="Long Arguments">

 public static DynamicLongArgument longArg(String name, String description) {
	return DynamicLongArgument.of(name, description);
 }

 public static DynamicLongArgument longArg(String name, String description, long min, long max) {
	return DynamicLongArgument.of(min, max, name, description);
 }

 public static DynamicLongArgument longArg(String name, String description, long min) {
	return DynamicLongArgument.of(min, name, description);
 }

 //</editor-fold>

 //<editor-fold desc="Float Arguments">

 public static DynamicFloatArgument floatArg(String name, String description) {
	return DynamicFloatArgument.of(name, description);
 }

 public static DynamicFloatArgument floatArg(String name, String description, float min, float max) {
	return DynamicFloatArgument.of(min, max, name, description);
 }

 public static DynamicFloatArgument floatArg(String name, String description, float min) {
	return DynamicFloatArgument.of(min, name, description);
 }

 //</editor-fold>

 //<editor-fold desc="Double Arguments">

 public static DynamicDoubleArgument doubleArg(String name, String description) {
	return DynamicDoubleArgument.of(name, description);
 }

 public static DynamicDoubleArgument doubleArg(String name, String description, double min, double max) {
	return DynamicDoubleArgument.of(min, max, name, description);
 }

 public static DynamicDoubleArgument doubleArg(String name, String description, double min) {
	return DynamicDoubleArgument.of(min, name, description);
 }

 //</editor-fold>

 public static DynamicBooleanArgument bool(String name, String description) {
	return DynamicBooleanArgument.of(name, description);
 }

 public static <T> DynamicObjectArgument<T> object(Class<T> type, String name, String description) {
	return DynamicObjectArgument.of(type, name, description);
 }

 public static DynamicObjectArgument<String> object(String name, String description) {
	return DynamicObjectArgument.of(String.class, name, description);
 }

 public static DynamicLocationArgument location(String name, String description) {
	return DynamicLocationArgument.of(name, description);
 }

 public List<String> suggestions() {
	return suggestions == null ? null : new ArrayList<>(suggestions.get());
 }

 private boolean checkAliases(String arg) {
	if(aliases == null) return false;
	for(String alias : aliases) {
	 if(alias.equalsIgnoreCase(arg)) return true;
	}
	return false;
 }

 public String input() {
	return input == null ? "" : input;
 }

 protected void input(String text) {
	this.input = text;
 }

 public DynamicArgument<?>[] subArguments() {
	return subArguments.toArray(new DynamicArgument<?>[0]);
 }

 public DynamicArgument<T> aliases(String... aliases) {
	this.aliases = aliases;
	availableNames.clear();
	availableNames.add(name);
	availableNames.addAll(Arrays.asList(aliases));
	return this;
 }

 public void brigadierValidate(String reader) throws CommandSyntaxException {
 }

 public T parse(CommandSender sender) throws CommandException {
	return DynamicCommandManager.parse(type, input());
 }

 public Class<?> type() {
	return type;
 }

 public String[] aliases() {
	return aliases;
 }

 public String name() {
	return name;
 }

 public String description() {
	return description;
 }


 @Override
 public String toString() {
	return "DynamicArgument{" +
		 "name='" + name + '\'' +
		 ", description='" + description + '\'' +
		 ", subArguments=" + subArguments +
		 ", aliases=" + Arrays.toString(aliases) +
		 ", type=" + type +
		 ", input='" + input + '\'' +
		 '}';
 }

 public boolean isValid() {
	return isValid(input());
 }

 public abstract boolean isValid(String input);
}
