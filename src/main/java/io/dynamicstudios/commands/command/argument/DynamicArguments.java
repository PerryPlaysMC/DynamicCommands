package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.argument.types.DynamicStringArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public class DynamicArguments implements Iterable<DynamicArgument<?>> {


 private static final Logger log = LoggerFactory.getLogger(DynamicArguments.class);
 private final CommandSender sender;
 private final String[] inputs;
 private final List<DynamicArgument<?>> list;
 private final HashMap<String, String> arguments;
 private final HashMap<String, DynamicArgument<?>> key;
 private final HashMap<String, Class<?>> argumentTypes;
 private final HashMap<String, Integer> argumentsSpan;
 private final DynamicArgument<?> startingArgument;

 public DynamicArguments(CommandSender sender, DynamicArgument<?>[] arguments, String[] args) {
	this.sender = sender;
	this.inputs = args;
	this.arguments = new LinkedHashMap<>();
	this.key = new LinkedHashMap<>();
	this.list = Arrays.asList(arguments);
	this.argumentTypes = new LinkedHashMap<>();
	this.argumentsSpan = new LinkedHashMap<>();
	for(DynamicArgument<?> argument : arguments) {
	 if(loadArguments(argument, 0, args)) {
		startingArgument = argument;
		return;
	 }
	}
	startingArgument = null;
//	for(DynamicArgument<?> argument : arguments)
//	 System.out.println(printArgs(argument, 0));
//	System.out.println(sender.getName());
//	System.out.println("a");
//	System.out.println(this.arguments);
//	System.out.println(String.join(" ", args));
 }

 public DynamicArgument<?> startingArgument() {
	return startingArgument;
 }

 public String printArgs(DynamicArgument<?> argument, int indent) {
	StringBuilder builder = new StringBuilder();
//	builder.append("\n");
	for(int i = 0; i < indent; i++) {
	 builder.append(" ");
	}
	builder.append(argument.name()).append(" ").append(argument.type().getSimpleName());
	if(argument.subArguments.isEmpty()) {
	 return builder.append(";").toString();
	}
	builder.append(" {");
	for(DynamicArgument<?> subArgument : argument.subArguments) {
	 builder.append("\n").append(printArgs(subArgument, indent + 1));
	}
	builder.append("\n");
	for(int i = 0; i < indent; i++) {
	 builder.append(" ");
	}
	builder.append("}");
	return builder.toString();
 }

 private boolean loadArguments(DynamicArgument<?> argument, int depth, String[] args) {
	if(depth > args.length) return false;
	String arg = String.join(" ", Arrays.copyOfRange(args, depth, Math.min(Math.max(0, depth + (argument.span() == -1 ? 256 : argument.span())), args.length)));
	int increase = 1;
	if(argument.span() == 1 && argument instanceof DynamicStringArgument) {
	 while(arg.startsWith("\"") && !arg.matches("^\\\"(?<data>(?:(?=\\\\\\\")..|(?!\\\").)*)\\\"$")) {
		arg = String.join(" ", Arrays.copyOfRange(args, depth, Math.min(Math.max(0, depth + increase), args.length)));
		if(arg.matches("^\\\"(?<data>(?:(?=\\\\\\\")..|(?!\\\").)*)\\\"$") || depth + increase > args.length) break;
		increase++;
	 }
	} else increase = arg.split(" ").length;
	if(!argument.isValid(arg))
	 return false;
	if(arg.isEmpty()) return false;
	String lName = argument.name().toLowerCase();
	int argSpan = increase;// arg.split(" ").length;
	if(this.key.containsKey(lName) ||
		 this.arguments.containsKey(lName) ||
		 this.argumentTypes.containsKey(lName) ||
		 this.argumentsSpan.containsKey(lName)) {
	 int sub = 2;
	 while(this.key.containsKey(lName + sub) ||
			this.arguments.containsKey(lName + sub) ||
			this.argumentTypes.containsKey(lName + sub) ||
			this.argumentsSpan.containsKey(lName + sub)) {
		sub++;
	 }
	 this.key.put(lName + sub, argument);
	 this.arguments.put(lName + sub, arg);
	 this.argumentTypes.put(lName + sub, argument.type());
	 this.argumentsSpan.put(lName + sub, argSpan);
	} else {
	 this.key.put(lName, argument);
	 this.arguments.put(lName, arg);
	 this.argumentTypes.put(lName, argument.type());
	 this.argumentsSpan.put(lName, argSpan);
	}
	for(DynamicArgument<?> dynamicArgument : argument.subArguments()) {
	 if(loadArguments(dynamicArgument, depth + (argument.span() == -1 ? 256 : argSpan), args)) return true;
	}
	return true;
 }

 public DynamicArguments from(int index) {
	DynamicArgument[] args = key.values().toArray(new DynamicArgument[0]);
	return new DynamicArguments(this.sender, Arrays.copyOfRange(args, index, args.length), Arrays.copyOfRange(this.inputs, index, this.inputs.length));
 }

 public String[] inputs() {
	return inputs;
 }

 public int getSpan(String name) {
	return argumentsSpan.getOrDefault(name.toLowerCase(), 1);
 }

 public boolean hasArgument(String name) {
	return this.arguments.containsKey(name.toLowerCase()) && this.key.containsKey(name.toLowerCase());
 }

 public <T> T get(Class<T> type, String name) throws CommandException {
	return getOrDefault(type, name, null);
 }


 public <T> T getRequired(Class<T> type, String name) throws CommandException {
	T orDefault = getOrDefault(type, name, null);
	if(orDefault == null) throw new CommandException("Argument '" + name + "' is required.");
	return orDefault;
 }


 public <T> T getOrDefault(Class<T> type, String name, T defaultValue) throws CommandException {
	if(!argumentTypes.containsKey(name) || !argumentTypes.get(name).getName().equals(type.getName())) return defaultValue;
	if(!key.containsKey(name) || (key.get(name).type() != type && key.get(name).type().isAssignableFrom(String.class))) {
	 return DynamicCommandManager.parse(type, arguments.get(name.toLowerCase()));
	}
	return type.cast(key.get(name).parse(sender, arguments.get(name.toLowerCase())));
 }

 public <T> T getRequired(String name) throws CommandException {
	T orDefault = getOrDefault(name, null);
	if(orDefault == null) throw new CommandException("Argument '" + name + "' is required.");
	return orDefault;
 }

 public <T> T get(String name) throws CommandException {
	return getOrDefault(name, null);
 }

 public <T> T getOrDefault(String name, T defaultValue) throws CommandException {
	if(!argumentTypes.containsKey(name.toLowerCase())) return defaultValue;
	if(!key.containsKey(name.toLowerCase())) {
	 return defaultValue;
	}
	return (T) key.get(name.toLowerCase()).parse(sender, arguments.get(name.toLowerCase()));
 }

 public DynamicArgument getArgument(String name) {
	if(!argumentTypes.containsKey(name)) return null;
	if(!key.containsKey(name)) return null;
	return key.get(name);
 }

 public DynamicArgument get(int index) {
	return list.get(index);
 }

 @Override
 public String toString() {
	return "DynamicArguments{" +
		 "args=" + Arrays.toString(inputs) +
		 ", list=" + list +
		 ", arguments=" + arguments +
		 ", key=" + key +
		 ", argumentTypes=" + argumentTypes +
		 '}';
 }

 @Override
 public Iterator<DynamicArgument<?>> iterator() {
	return key.values().iterator();
 }

 public int length() {
	return list.size();
 }

 public String[] names() {
	return key.keySet().toArray(new String[0]);
 }

 public boolean has(String name) {
	return hasArgument(name);
 }

 public void test(CommandSender sender, String[] args) throws CommandException {
	int size = 0;
	for(String name : names()) {
	 if(!hasArgument(name)) continue;
	 DynamicArgument<?> argument = getArgument(name);
	 argument.predicate().test(sender, arguments.get(name.toLowerCase()));
	 argument.parse(sender, arguments.get(name.toLowerCase()));
	 if(size < Integer.MAX_VALUE)
		size += (argument.span() == -1 ? 256 : getSpan(name));
	 if(size == -1) {
		size = Integer.MAX_VALUE;
	 }
	}
	if(args.length > size) {
	 throw new CommandException("Too many arguments: '" + String.join(" ", Arrays.copyOfRange(args, args.length - (args.length - size), args.length)) + "', " + args.length + " max: " + size);
	}
 }

 public String getInput(String name) {
	return arguments.get(name.toLowerCase());
 }

 public Map<String, DynamicArgument<?>> getArguments() {
	return this.key;
 }

 public HashMap<String, DynamicArgument<?>> keys() {
	return key;
 }
}
