package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.command.argument.types.DynamicLiteral;
import org.bukkit.Location;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DynamicArgumentBuilder {
  private DynamicArgument<?> base;
  private DynamicArgumentBuilder parent;

  private DynamicArgumentBuilder(DynamicArgumentBuilder parent, DynamicArgument<?> base) {
    this.parent = parent;
    this.base = base;
  }

  private DynamicArgumentBuilder(DynamicArgument<?> base) {
    this(null, base);
  }

  public static DynamicArgumentBuilder createLiteral(String name, String description) {
    return new DynamicArgumentBuilder(DynamicLiteral.literal(name, description));
  }

  public static <T> DynamicArgumentBuilder createObject(Class<T> type, String name, String description) {
    return new DynamicArgumentBuilder(DynamicArgument.object(type, name, description));
  }


  public static <T> DynamicArgumentBuilder createLocation(String name, String description) {
    return new DynamicArgumentBuilder(DynamicArgument.location(name, description));
  }

  public DynamicArgumentBuilder begin(DynamicArgumentBuilder builder) {
    add(builder.build());
    return this;
  }

  public DynamicArgumentBuilder beginLiteral(String name, String description, Consumer<DynamicArgumentBuilder> builder) {
    DynamicArgumentBuilder other = createLiteral(name, description);
    builder.accept(other);
    add(other.build());
    return this;
  }

  public <T> DynamicArgumentBuilder beginObject(Class<T> type, String name, String description, Consumer<DynamicArgumentBuilder> builder) {
    DynamicArgumentBuilder other = createObject(type, name, description);
    builder.accept(other);
    add(other.build());
    return this;
  }

  public DynamicArgumentBuilder beginLocation(String name, String description, Consumer<DynamicArgumentBuilder> builder) {
    DynamicArgumentBuilder other = createLocation(name, description);
    builder.accept(other);
    add(other.build());
    return this;
  }

  public DynamicArgumentBuilder begin(DynamicArgument<?> argument) {
    return new DynamicArgumentBuilder(this, argument);
  }

  public DynamicArgumentBuilder beginLiteral(String name, String description) {
    return begin(DynamicLiteral.literal(name, description));
  }

  public <T> DynamicArgumentBuilder beginObject(Class<T> type, String name, String description) {
    if(type.getName().equals(Location.class.getName()))
      return begin(DynamicArgument.location(name,description));
    return begin(DynamicArgument.object(type, name, description));
  }

  public DynamicArgumentBuilder beginLocation(String name, String description) {
      return begin(DynamicArgument.location(name,description));
  }

  public DynamicArgumentBuilder end() {
    if(parent != null) parent.add(this.base);
    return this.parent == null ? this : this.parent;
  }

  public DynamicArgumentBuilder add(DynamicArgument<?> argument) {
    this.base.subArguments.add(argument);
    return this;
  }

  public DynamicArgumentBuilder literal(String name, String description) {
    this.base.subArguments.add(DynamicLiteral.literal(name, description));
    return this;
  }

  public DynamicArgumentBuilder object(String name, String description) {
    return object(String.class, name, description);
  }


  public <T> DynamicArgumentBuilder object(Class<T> type, String name, String description) {
    return add(DynamicArgument.object(type, name, description));
  }


  public DynamicArgumentBuilder location(String name, String description) {
    return add(DynamicArgument.location(name, description));
  }

  public DynamicArgumentBuilder suggestions(Supplier<Collection<String>> suggestions) {
    if(this.base.subArguments.isEmpty()) return this;
    this.base.subArguments.get(this.base.subArguments.size() - 1).suggestions = suggestions;
    return this;
  }

  public DynamicArgument<?> build() {
    return base;
  }
}
