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
	return new DynamicArgumentBuilder(DynamicArgument.literal(name, description));
 }

 public static DynamicArgumentBuilder createInteger(String name, String description) {
	return new DynamicArgumentBuilder(DynamicArgument.intArg(name, description));
 }


 public static DynamicArgumentBuilder createInteger(String name, String description, int min) {
	return new DynamicArgumentBuilder(DynamicArgument.intArg(name, description, min));
 }


 public static DynamicArgumentBuilder createInteger(String name, String description, int min, int max) {
	return new DynamicArgumentBuilder(DynamicArgument.intArg(name, description, min, max));
 }


 public static DynamicArgumentBuilder createLong(String name, String description) {
	return new DynamicArgumentBuilder(DynamicArgument.longArg(name, description));
 }


 public static DynamicArgumentBuilder createLong(String name, String description, long min) {
	return new DynamicArgumentBuilder(DynamicArgument.longArg(name, description, min));
 }


 public static DynamicArgumentBuilder createLong(String name, String description, long min, long max) {
	return new DynamicArgumentBuilder(DynamicArgument.longArg(name, description, min, max));
 }


 public static DynamicArgumentBuilder createFloat(String name, String description) {
	return new DynamicArgumentBuilder(DynamicArgument.floatArg(name, description));
 }


 public static DynamicArgumentBuilder createFloat(String name, String description, float min) {
	return new DynamicArgumentBuilder(DynamicArgument.floatArg(name, description, min));
 }


 public static DynamicArgumentBuilder createFloat(String name, String description, float min, float max) {
	return new DynamicArgumentBuilder(DynamicArgument.floatArg(name, description, min, max));
 }


 public static DynamicArgumentBuilder createDouble(String name, String description) {
	return new DynamicArgumentBuilder(DynamicArgument.doubleArg(name, description));
 }


 public static DynamicArgumentBuilder createDouble(String name, String description, double min) {
	return new DynamicArgumentBuilder(DynamicArgument.doubleArg(name, description, min));
 }


 public static DynamicArgumentBuilder createDouble(String name, String description, double min, double max) {
	return new DynamicArgumentBuilder(DynamicArgument.doubleArg(name, description, min, max));
 }


 public static DynamicArgumentBuilder createBoolean(String name, String description) {
	return new DynamicArgumentBuilder(DynamicArgument.bool(name, description));
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

 public DynamicArgumentBuilder beginLong(String name, String description, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createLong(name, description);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginLong(String name, String description, long min, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createLong(name, description, min);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginLong(String name, String description, long min, long max, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createLong(name, description, min, max);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginInteger(String name, String description, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createInteger(name, description);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginInteger(String name, String description, int min, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createInteger(name, description, min);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginInteger(String name, String description, int min, int max, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createInteger(name, description, min, max);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginDouble(String name, String description, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createDouble(name, description);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginDouble(String name, String description, double min, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createDouble(name, description, min);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginDouble(String name, String description, double min, double max, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createDouble(name, description, min, max);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginFloat(String name, String description, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createFloat(name, description);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginFloat(String name, String description, float min, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createFloat(name, description, min);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginFloat(String name, String description, float min, float max, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createFloat(name, description, min, max);
	builder.accept(other);
	add(other.build());
	return this;
 }

 public DynamicArgumentBuilder beginBool(String name, String description, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = createBoolean(name, description);
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
	 return begin(DynamicArgument.location(name, description));
	return begin(DynamicArgument.object(type, name, description));
 }

 public DynamicArgumentBuilder beginLocation(String name, String description) {
	return begin(DynamicArgument.location(name, description));
 }

 public DynamicArgumentBuilder end() {
	if(parent != null) parent.add(this.base);
	return this.parent == null ? this : this.parent;
 }

 public DynamicArgumentBuilder add(DynamicArgument<?> argument) {
	argument.parent(this.base);
	this.base.subArguments.add(argument);
	return this;
 }

 public DynamicArgumentBuilder literal(String name, String description) {
	return add(DynamicArgument.literal(name, description));
 }


 public DynamicArgumentBuilder intArg(String name, String description) {
	return add(DynamicArgument.intArg(name, description));
 }

 public DynamicArgumentBuilder intArg(String name, String description, int min) {
	return add(DynamicArgument.intArg(name, description, min));
 }

 public DynamicArgumentBuilder intArg(String name, String description, int min, int max) {
	return add(DynamicArgument.intArg(name, description, min, max));
 }


 public DynamicArgumentBuilder longArg(String name, String description) {
	return add(DynamicArgument.longArg(name, description));
 }

 public DynamicArgumentBuilder longArg(String name, String description, long min) {
	return add(DynamicArgument.longArg(name, description, min));
 }

 public DynamicArgumentBuilder longArg(String name, String description, long min, long max) {
	return add(DynamicArgument.longArg(name, description, min, max));
 }


 public DynamicArgumentBuilder floatArg(String name, String description) {
	return add(DynamicArgument.floatArg(name, description));
 }

 public DynamicArgumentBuilder floatArg(String name, String description, float min) {
	return add(DynamicArgument.floatArg(name, description, min));
 }

 public DynamicArgumentBuilder floatArg(String name, String description, float min, float max) {
	return add(DynamicArgument.floatArg(name, description, min, max));
 }


 public DynamicArgumentBuilder doubleArg(String name, String description) {
	return add(DynamicArgument.doubleArg(name, description));
 }

 public DynamicArgumentBuilder doubleArg(String name, String description, double min) {
	return add(DynamicArgument.doubleArg(name, description, min));
 }

 public DynamicArgumentBuilder doubleArg(String name, String description, double min, double max) {
	return add(DynamicArgument.doubleArg(name, description, min, max));
 }

 public DynamicArgumentBuilder bool(String name, String description) {
	return add(DynamicArgument.bool(name, description));
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
