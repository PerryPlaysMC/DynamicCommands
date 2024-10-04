package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.argument.DynamicArgument;

public class DynamicObjectArgument<T> extends DynamicArgument<T> {
  public DynamicObjectArgument(String name, String description, DynamicArgument<?>... subArguments) {
    super(name, description, subArguments);
  }

  public DynamicObjectArgument(Class<T> type, String name, String description, DynamicArgument<?>... subArguments) {
    super(type, name, description, subArguments);
  }

  public static <T> DynamicObjectArgument<T> of(Class<T> type, String name, String description) {
    return new DynamicObjectArgument<>(type, name, description);
  }


  @Override
  public boolean isValid(String input) {
    return DynamicCommandManager.canParse(type(),input);
  }
}
