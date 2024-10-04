package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DynamicLiteral extends DynamicArgument<String> {
  private DynamicLiteral(String name, String description, DynamicArgument... subArguments) {
    super(name, description, subArguments);
  }

  public static DynamicLiteral of(String name, String description) {
    return new DynamicLiteral(name, description);
  }

  @Override
  public List<String> suggestions() {
    return availableNames();
  }

  @Override
  public String parse(CommandSender sender) throws CommandException {
    return name();
  }

  @Override
  public boolean isValid(String input) {
    return name().equalsIgnoreCase(input);
  }
}
