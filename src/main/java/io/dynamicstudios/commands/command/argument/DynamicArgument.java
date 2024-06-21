package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public class DynamicArgument<T> {

  private final String name, description;
  private final DynamicArgument<?>[] subArguments;
  private String[] aliases;
  private final Class<T> type;
  private String input;

  public DynamicArgument(String name, String description, DynamicArgument<?>... subArguments) {
    this(null, name, description, subArguments);
  }

  public DynamicArgument(Class<T> type, String name, String description, DynamicArgument<?>... subArguments) {
	  Class<T> clazz = null;
	  try {
		  clazz = (Class<T>) Class.forName(getClass().getGenericSuperclass().getTypeName().replaceAll(".+<([^>]+)>", "$1"));
	  } catch (ClassNotFoundException e) {
	  }
	  this.type = (clazz!=null&&clazz.getName().equalsIgnoreCase(Object.class.getName())) ? (Class<T>) String.class : (type == null ? clazz : type);
    this.name = name;
    this.description = description;
    this.subArguments = subArguments;
    this.aliases = null;
  }

	public boolean execute(CommandSender sender, T self, DynamicArguments args) throws CommandException {
		if(args.inputs().length == 0) return false;
		for(DynamicArgument argument : subArguments) {
			if(argument.name().equalsIgnoreCase(args.inputs()[0]) || checkAliases(args.inputs()[0]) || (argument.name().matches("([\\[<]).+([]>])") && argument.isValid())) {
				argument.input(args.inputs()[0]);
				return argument.execute(sender, argument.parse(), new DynamicArguments(argument.subArguments(), Arrays.copyOfRange(args.inputs(), 1, args.inputs().length)));
			}
		}
		return false;
	}

	private boolean checkAliases(String arg) {
		if(aliases==null)return false;
		for(String alias : aliases) {
			if(alias.equalsIgnoreCase(arg))return true;
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
    return subArguments;
  }

  public DynamicArgument<T> aliases(String... aliases) {
    this.aliases = aliases;
    return this;
  }

	public T parse() throws CommandException {
		return DynamicCommandManager.parse(type,input());
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
			", subArguments=" + Arrays.toString(subArguments) +
			", aliases=" + Arrays.toString(aliases) +
			", type=" + type +
			", input='" + input + '\'' +
			'}';
	}

	public boolean isValid() {
		return isValid(input());
	}

	public boolean isValid(String input) {
		return DynamicCommandManager.canParse(type,input);
	}
}
