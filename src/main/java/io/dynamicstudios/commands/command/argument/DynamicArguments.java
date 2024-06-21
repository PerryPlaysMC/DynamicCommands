package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.exceptions.CommandException;

import java.util.*;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public class DynamicArguments implements Iterable<DynamicArgument<?>>{


	private final String[] inputs;
	private List<DynamicArgument<?>> list;
	private HashMap<String, String> arguments;
  private HashMap<String, DynamicArgument<?>> key;
  private HashMap<String, Class<?>> argumentTypes;

  public DynamicArguments(DynamicArgument<?>[] arguments, String[] args) {
	  this.inputs = args;
	  this.arguments = new LinkedHashMap<>();
    this.key = new LinkedHashMap<>();
		this.list = Arrays.asList(arguments);
    this.argumentTypes = new LinkedHashMap<>();
	  for(int i = 0; i < Math.min(args.length,arguments.length); i++) {
		  String arg = args[i];
			DynamicArgument<?> argument = arguments[i];
			argument.input(arg);
			key.put(argument.name(), argument);
      this.arguments.put(argument.name().toLowerCase(), arg);
      this.argumentTypes.put(argument.name().toLowerCase(),argument.type());
    }
  }

	public DynamicArguments from(int index) {
		DynamicArgument[] args = key.values().toArray(new DynamicArgument[0]);
		return new DynamicArguments(Arrays.copyOfRange(args,index,args.length), Arrays.copyOfRange(this.inputs,index,this.inputs.length));
	}

	public String[] inputs() {
		return inputs;
	}

	public boolean hasArgument(String name) {
    return this.arguments.containsKey(name.toLowerCase());
  }

	public <T> T get(Class<T> type, String name) throws CommandException {
		if(!argumentTypes.containsKey(name)||!argumentTypes.get(name).getName().equals(type.getName()))return null;
		return type.cast(key.get(name).parse());
	}
	public <T> T get(String name) throws CommandException {
		if(!argumentTypes.containsKey(name))return null;
		if(!key.containsKey(name))return null;
		return (T) key.get(name).parse();
	}
	public DynamicArgument getArgument(String name) {
		if(!argumentTypes.containsKey(name))return null;
		if(!key.containsKey(name))return null;
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
}
