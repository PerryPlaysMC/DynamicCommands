package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public class DynamicArguments implements Iterable<DynamicArgument<?>> {


 private final CommandSender sender;
 private final String[] inputs;
 private List<DynamicArgument<?>> list;
 private HashMap<String, String> arguments;
 private HashMap<String, DynamicArgument<?>> key;
 private HashMap<String, Class<?>> argumentTypes;

 public DynamicArguments(CommandSender sender, DynamicArgument<?>[] arguments, String[] args) {
	this.sender = sender;
	this.inputs = args;
	this.arguments = new LinkedHashMap<>();
	this.key = new LinkedHashMap<>();
	this.list = Arrays.asList(arguments);
	this.argumentTypes = new LinkedHashMap<>();
	for(DynamicArgument<?> argument : arguments) {
	 if(loadArguments(argument, 0, args)) break;
	}
 }

 private boolean loadArguments(DynamicArgument<?> argument, int depth, String[] args) {
	if(depth >= args.length) return false;
	String arg = String.join(" ", Arrays.copyOfRange(args, depth, Math.min(depth + argument.span(), args.length)));
	if(!argument.isValid(arg) && ((argument.parent() == null && argument.subArguments.size() < 2) || argument.parent().subArguments.size() > 1))
	 return false;
	argument.input(arg);
	if(this.key.containsKey(argument.name().toLowerCase()) ||
		 this.arguments.containsKey(argument.name().toLowerCase()) ||
		 this.argumentTypes.containsKey(argument.name().toLowerCase())) {
	 int sub = 2;
	 while(this.key.containsKey(argument.name().toLowerCase() + sub) ||
			this.arguments.containsKey(argument.name().toLowerCase() + sub) ||
			this.argumentTypes.containsKey(argument.name().toLowerCase() + sub)) {
		sub++;
	 }
	 this.key.put(argument.name().toLowerCase() + sub, argument);
	 this.arguments.put(argument.name().toLowerCase() + sub, arg);
	 this.argumentTypes.put(argument.name().toLowerCase() + sub, argument.type());
	} else {
	 this.key.put(argument.name().toLowerCase(), argument);
	 this.arguments.put(argument.name().toLowerCase(), arg);
	 this.argumentTypes.put(argument.name().toLowerCase(), argument.type());
	}
	for(DynamicArgument<?> dynamicArgument : argument.subArguments()) {
	 if(loadArguments(dynamicArgument, depth + argument.span(), args)) return true;
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

 public boolean hasArgument(String name) {
	return this.arguments.containsKey(name.toLowerCase()) && this.key.containsKey(name.toLowerCase());
 }

 public <T> T get(Class<T> type, String name) throws CommandException {
	if(!argumentTypes.containsKey(name) || !argumentTypes.get(name).getName().equals(type.getName())) return null;
	return type.cast(key.get(name).parse(sender));
 }

 public <T> T get(String name) throws CommandException {
	if(!argumentTypes.containsKey(name.toLowerCase())) return null;
	if(!key.containsKey(name.toLowerCase())) return null;
	return (T) key.get(name.toLowerCase()).parse(sender);
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
	 if(!hasArgument(name))continue;
	 DynamicArgument argument = getArgument(name);
	 argument.parse(sender);
	 if(size < Integer.MAX_VALUE)
		size += argument.span();
	 if(size == -1) {
		size = Integer.MAX_VALUE;
	 }
	}
	if(args.length > size) {
	 throw new CommandException("Too many arguments: " + args[args.length - 1] + ", " + args.length + " max: " + size);
	}
 }
}
