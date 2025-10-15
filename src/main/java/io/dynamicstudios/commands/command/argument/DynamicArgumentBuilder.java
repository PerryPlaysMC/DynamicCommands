package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.command.argument.types.*;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DynamicArgumentBuilder {
 private final DynamicArgument<?> base;
 private DynamicArgumentBuilder parent;

 public DynamicArgumentBuilder(DynamicArgumentBuilder parent, DynamicArgument<?> base) {
	this.parent = parent;
	this.base = base;
 }

 public DynamicArgumentBuilder(DynamicArgument<?> base) {
	this(null, base);
 }


 public DynamicArgumentBuilder begin(DynamicArgumentBuilder builder) {
	add(builder.build());
	return this;
 }

 public <T> DynamicArgumentBuilder begin(DynamicArgument<T> base, Consumer<DynamicArgumentBuilder> builder) {
	DynamicArgumentBuilder other = create(base);
	builder.accept(other);
	add(other.build());
	return this;
 }


 public DynamicArgumentBuilder beginLiteral(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.literal(name, description), consumer);
 }

 public DynamicArgumentBuilder beginLiteral(String name, String description) {
	return begin(DynamicArgument.literal(name, description));
 }


 public DynamicArgumentBuilder beginIntArg(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.intArg(name, description), consumer);
 }

 public DynamicArgumentBuilder beginIntArg(String name, String description) {
	return begin(DynamicArgument.intArg(name, description));
 }

 public DynamicArgumentBuilder beginIntArg(String name, String description, int min, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.intArg(name, description, min), consumer);
 }

 public DynamicArgumentBuilder beginIntArg(String name, String description, int min) {
	return begin(DynamicArgument.intArg(name, description, min));
 }

 public DynamicArgumentBuilder beginIntArg(String name, String description, int min, int max, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.intArg(name, description, min, max), consumer);
 }

 public DynamicArgumentBuilder beginIntArg(String name, String description, int min, int max) {
	return begin(DynamicArgument.intArg(name, description, min, max));
 }


 public DynamicArgumentBuilder beginLongArg(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.longArg(name, description), consumer);
 }

 public DynamicArgumentBuilder beginLongArg(String name, String description) {
	return begin(DynamicArgument.longArg(name, description));
 }

 public DynamicArgumentBuilder beginLongArg(String name, String description, long min, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.longArg(name, description, min), consumer);
 }

 public DynamicArgumentBuilder beginLongArg(String name, String description, long min) {
	return begin(DynamicArgument.longArg(name, description, min));
 }

 public DynamicArgumentBuilder beginLongArg(String name, String description, long min, long max, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.longArg(name, description, min, max), consumer);
 }

 public DynamicArgumentBuilder beginLongArg(String name, String description, long min, long max) {
	return begin(DynamicArgument.longArg(name, description, min, max));
 }


 public DynamicArgumentBuilder beginFloatArg(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.floatArg(name, description), consumer);
 }

 public DynamicArgumentBuilder beginFloatArg(String name, String description) {
	return begin(DynamicArgument.floatArg(name, description));
 }

 public DynamicArgumentBuilder beginFloatArg(String name, String description, float min, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.floatArg(name, description, min), consumer);
 }

 public DynamicArgumentBuilder beginFloatArg(String name, String description, float min) {
	return begin(DynamicArgument.floatArg(name, description, min));
 }

 public DynamicArgumentBuilder beginFloatArg(String name, String description, float min, float max, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.floatArg(name, description, min, max), consumer);
 }

 public DynamicArgumentBuilder beginFloatArg(String name, String description, float min, float max) {
	return begin(DynamicArgument.floatArg(name, description, min, max));
 }


 public DynamicArgumentBuilder beginDoubleArg(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.doubleArg(name, description), consumer);
 }

 public DynamicArgumentBuilder beginDoubleArg(String name, String description) {
	return begin(DynamicArgument.doubleArg(name, description));
 }

 public DynamicArgumentBuilder beginDoubleArg(String name, String description, double min, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.doubleArg(name, description, min), consumer);
 }

 public DynamicArgumentBuilder beginDoubleArg(String name, String description, double min) {
	return begin(DynamicArgument.doubleArg(name, description, min));
 }

 public DynamicArgumentBuilder beginDoubleArg(String name, String description, double min, double max, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.doubleArg(name, description, min, max), consumer);
 }

 public DynamicArgumentBuilder beginDoubleArg(String name, String description, double min, double max) {
	return begin(DynamicArgument.doubleArg(name, description, min, max));
 }

 public DynamicArgumentBuilder beginBool(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.bool(name, description), consumer);
 }

 public DynamicArgumentBuilder beginBool(String name, String description) {
	return begin(DynamicArgument.bool(name, description));
 }

 public DynamicArgumentBuilder beginWord(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.word(name, description), consumer);
 }

 public DynamicArgumentBuilder beginWord(String name, String description) {
	return begin(DynamicArgument.word(name, description));
 }

 public DynamicArgumentBuilder beginGreedy(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.greedy(name, description), consumer);
 }

 public DynamicArgumentBuilder beginGreedy(String name, String description) {
	return begin(DynamicArgument.greedy(name, description));
 }

 public DynamicArgumentBuilder beginSingle(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.single(name, description), consumer);
 }

 public DynamicArgumentBuilder beginSingle(String name, String description) {
	return begin(DynamicArgument.single(name, description));
 }

 public DynamicArgumentBuilder beginLimited(String name, String description, int length, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.limited(name, description, length), consumer);
 }

 public DynamicArgumentBuilder beginLimited(String name, String description, int length) {
	return begin(DynamicArgument.limited(name, description, length));
 }

 public DynamicArgumentBuilder beginObject(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.object(String.class, name, description), consumer);
 }

 public DynamicArgumentBuilder beginObject(String name, String description) {
	return begin(DynamicArgument.object(String.class, name, description));
 }


 public <T> DynamicArgumentBuilder beginObject(Class<T> type, String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.object(type, name, description), consumer);
 }

 public <T> DynamicArgumentBuilder beginObject(Class<T> type, String name, String description) {
	return begin(DynamicArgument.object(type, name, description));
 }


 public DynamicArgumentBuilder beginLocation(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.location(name, description), consumer);
 }

 public DynamicArgumentBuilder beginLocation(String name, String description) {
	return begin(DynamicArgument.location(name, description));
 }


 public DynamicArgumentBuilder beginEntity(String name, String description) {
	return begin(DynamicArgument.entity(name, description));
 }

 public DynamicArgumentBuilder beginEntity(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.entity(name, description), consumer);
 }

 public DynamicArgumentBuilder beginEntities(String name, String description) {
	return begin(DynamicArgument.entities(name, description));
 }

 public DynamicArgumentBuilder beginEntities(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.entities(name, description), consumer);
 }

 public DynamicArgumentBuilder beginPlayers(String name, String description) {
	return begin(DynamicArgument.players(name, description));
 }

 public DynamicArgumentBuilder beginPlayers(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.players(name, description), consumer);
 }

 public DynamicArgumentBuilder beginPlayer(String name, String description) {
	return begin(DynamicArgument.player(name, description));
 }

 public DynamicArgumentBuilder beginPlayer(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return begin(DynamicArgument.player(name, description), consumer);
 }


 public DynamicArgumentBuilder begin(DynamicArgument<?> argument) {
	return new DynamicArgumentBuilder(this, argument);
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

 public DynamicArgumentBuilder add(DynamicArgumentBuilder argument) {
	argument.parent = this;
	add(argument.build());
	return this;
 }

 public DynamicArgumentBuilder literal(String name, String description) {
	return add(DynamicArgument.literal(name, description));
 }

 public DynamicArgumentBuilder multiLiteral(Collection<String> names, String description) {
	names.forEach(name -> DynamicArgument.literal(name, description));
	return this;
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

 public DynamicArgumentBuilder word(String name, String description) {
	return add(DynamicArgument.word(name, description));
 }

 public DynamicArgumentBuilder greedy(String name, String description) {
	return add(DynamicArgument.greedy(name, description));
 }

 public DynamicArgumentBuilder single(String name, String description) {
	return add(DynamicArgument.single(name, description));
 }

 public DynamicArgumentBuilder limited(String name, String description, int length) {
	return add(DynamicArgument.limited(name, description, length));
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

 public DynamicArgumentBuilder entity(String name, String description) {
	return add(DynamicArgument.entity(name, description));
 }

 public DynamicArgumentBuilder entities(String name, String description) {
	return add(DynamicArgument.entities(name, description));
 }

 public DynamicArgumentBuilder players(String name, String description) {
	return add(DynamicArgument.players(name, description));
 }

 public DynamicArgumentBuilder player(String name, String description) {
	return add(DynamicArgument.player(name, description));
 }


 public static DynamicArgumentBuilder createLiteral(String name, String description) {
	return create(DynamicLiteral.of(name, description));
 }

 public static DynamicArgumentBuilder createLiteral(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicLiteral.of(name, description), consumer);
 }

 public static DynamicArgumentBuilder createLimited(String name, String description, int length) {
	return create(DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.LIMITED).span(length));
 }

 public static DynamicArgumentBuilder createLimited(String name, String description, int length, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.LIMITED).span(length), consumer);
 }

 public static DynamicArgumentBuilder createGreedy(String name, String description) {
	return create(DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.GREEDY));
 }

 public static DynamicArgumentBuilder createGreedy(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.GREEDY), consumer);
 }

 public static DynamicArgumentBuilder createWord(String name, String description) {
	return create(DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.WORD));
 }

 public static DynamicArgumentBuilder createWord(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.WORD), consumer);
 }

 public static DynamicArgumentBuilder createSingle(String name, String description) {
	return create(DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.SINGLE));
 }

 public static DynamicArgumentBuilder createSingle(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.SINGLE), consumer);
 }

 public static DynamicArgumentBuilder createLimited(String name, String description) {
	return create(DynamicStringArgument.of(name, description));
 }

 public static DynamicArgumentBuilder createLimited(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicStringArgument.of(name, description), consumer);
 }

 //<editor-fold desc="Integer Arguments">

 public static DynamicArgumentBuilder createInteger(String name, String description) {
	return create(DynamicIntegerArgument.of(name, description));
 }

 public static DynamicArgumentBuilder createInteger(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicIntegerArgument.of(name, description), consumer);
 }

 public static DynamicArgumentBuilder createInteger(String name, String description, int min, int max) {
	return create(DynamicIntegerArgument.of(min, max, name, description));
 }

 public static DynamicArgumentBuilder createInteger(String name, String description, int min, int max, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicIntegerArgument.of(min, max, name, description), consumer);
 }

 public static DynamicArgumentBuilder createInteger(String name, String description, int min) {
	return create(DynamicIntegerArgument.of(min, name, description));
 }

 public static DynamicArgumentBuilder createInteger(String name, String description, int min, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicIntegerArgument.of(min, name, description), consumer);
 }

 //</editor-fold>

 //<editor-fold desc="Long Arguments">

 public static DynamicArgumentBuilder createLong(String name, String description) {
	return create(DynamicLongArgument.of(name, description));
 }

 public static DynamicArgumentBuilder createLong(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicLongArgument.of(name, description), consumer);
 }

 public static DynamicArgumentBuilder createLong(String name, String description, long min, long max) {
	return create(DynamicLongArgument.of(min, max, name, description));
 }

 public static DynamicArgumentBuilder createLong(String name, String description, long min, long max, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicLongArgument.of(min, max, name, description), consumer);
 }

 public static DynamicArgumentBuilder createLong(String name, String description, long min) {
	return create(DynamicLongArgument.of(min, name, description));
 }

 public static DynamicArgumentBuilder createLong(String name, String description, long min, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicLongArgument.of(min, name, description), consumer);
 }

 //</editor-fold>

 //<editor-fold desc="Float Arguments">

 public static DynamicArgumentBuilder createFloat(String name, String description) {
	return create(DynamicFloatArgument.of(name, description));
 }

 public static DynamicArgumentBuilder createFloat(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicFloatArgument.of(name, description), consumer);
 }

 public static DynamicArgumentBuilder createFloat(String name, String description, float min, float max) {
	return create(DynamicFloatArgument.of(min, max, name, description));
 }

 public static DynamicArgumentBuilder createFloat(String name, String description, float min, float max, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicFloatArgument.of(min, max, name, description), consumer);
 }

 public static DynamicArgumentBuilder createFloat(String name, String description, float min) {
	return create(DynamicFloatArgument.of(min, name, description));
 }

 public static DynamicArgumentBuilder createFloat(String name, String description, float min, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicFloatArgument.of(min, name, description), consumer);
 }

 //</editor-fold>

 //<editor-fold desc="Double Arguments">

 public static DynamicArgumentBuilder createDouble(String name, String description) {
	return create(DynamicDoubleArgument.of(name, description));
 }

 public static DynamicArgumentBuilder createDouble(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicDoubleArgument.of(name, description), consumer);
 }

 public static DynamicArgumentBuilder createDouble(String name, String description, double min, double max) {
	return create(DynamicDoubleArgument.of(min, max, name, description));
 }

 public static DynamicArgumentBuilder createDouble(String name, String description, double min, double max, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicDoubleArgument.of(min, max, name, description), consumer);
 }

 public static DynamicArgumentBuilder createDouble(String name, String description, double min) {
	return create(DynamicDoubleArgument.of(min, name, description));
 }

 public static DynamicArgumentBuilder createDouble(String name, String description, double min, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicDoubleArgument.of(min, name, description), consumer);
 }

 //</editor-fold>

 public static DynamicArgumentBuilder createBoolean(String name, String description) {
	return create(DynamicBooleanArgument.of(name, description));
 }

 public static DynamicArgumentBuilder createBoolean(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicBooleanArgument.of(name, description), consumer);
 }

 public static <T> DynamicArgumentBuilder createObj(Class<T> type, String name, String description) {
	return create(DynamicObjectArgument.of(type, name, description));
 }

 public static <T> DynamicArgumentBuilder createObj(Class<T> type, String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicObjectArgument.of(type, name, description), consumer);
 }

 public static DynamicArgumentBuilder createLocation(String name, String description) {
	return create(DynamicLocationArgument.of(name, description));
 }

 public static DynamicArgumentBuilder createLocation(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicLocationArgument.of(name, description), consumer);
 }


 public DynamicArgumentBuilder createEntity(String name, String description) {
	return create(DynamicArgument.entity(name, description));
 }

 public DynamicArgumentBuilder createEntity(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicArgument.entity(name, description), consumer);
 }

 public DynamicArgumentBuilder createEntities(String name, String description) {
	return create(DynamicArgument.entities(name, description));
 }

 public DynamicArgumentBuilder createEntities(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicArgument.entities(name, description), consumer);
 }

 public DynamicArgumentBuilder createPlayers(String name, String description) {
	return create(DynamicArgument.players(name, description));
 }

 public DynamicArgumentBuilder createPlayers(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicArgument.players(name, description), consumer);
 }

 public DynamicArgumentBuilder createPlayer(String name, String description) {
	return create(DynamicArgument.player(name, description));
 }

 public DynamicArgumentBuilder createPlayer(String name, String description, Consumer<DynamicArgumentBuilder> consumer) {
	return create(DynamicArgument.player(name, description), consumer);
 }



 public static DynamicArgumentBuilder create(DynamicArgument<?> argument) {
	return new DynamicArgumentBuilder(argument);
 }

 public static DynamicArgumentBuilder create(DynamicArgument<?> argument, Consumer<DynamicArgumentBuilder> consumer) {
	DynamicArgumentBuilder builder = new DynamicArgumentBuilder(argument);
	consumer.accept(builder);
	return builder;
 }


 public DynamicArgumentBuilder suggestions(Supplier<Collection<String>> suggestions) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.suggestions(suggestions);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).suggestions(suggestions);
	return this;
 }


 public DynamicArgumentBuilder suggestionsAppend(Function<String, Collection<String>> suggestions) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.suggestionsAppend(suggestions);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).suggestionsAppend(suggestions);
	return this;
 }


 public DynamicArgumentBuilder suggestions(Function<CommandSender, Collection<String>> suggestions) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.suggestions(suggestions);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).suggestions(suggestions);
	return this;
 }

 public DynamicArgumentBuilder suggestions(BiFunction<CommandSender, DynamicArguments, Collection<String>> suggestions) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.suggestions(suggestions);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).suggestions(suggestions);
	return this;
 }


 public DynamicArgumentBuilder predicate(ArgumentPredicate predicate) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.predicate(predicate);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).predicate(predicate);
	return this;
 }


 public DynamicArgumentBuilder tabPredicate(TabArgumentPredicate tabPredicate) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.tabPredicate(tabPredicate);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).tabPredicate(tabPredicate);
	return this;
 }


 public DynamicArgumentBuilder executes(ArgumentExecutor executor) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.executes(executor);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).executes(executor);
	return this;
 }

 public DynamicArgumentBuilder retains(boolean retains) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.retains(retains);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).retains(retains);
	return this;
 }

 public DynamicArgumentBuilder retain() {
	return retains(true);
 }


 public DynamicArgumentBuilder optional() {
	return optional(true);
 }


 public DynamicArgumentBuilder optional(boolean optional) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.optional(optional);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).optional(optional);
	return this;
 }

 public DynamicArgumentBuilder helpDescription(String helpDescription) {
	if(this.base.subArguments.isEmpty()) {
	 this.base.helpDescription(helpDescription);
	 return this;
	}
	this.base.subArguments.get(this.base.subArguments.size() - 1).helpDescription(helpDescription);
	return this;
 }

 public DynamicArgument<?> build() {
	if(this.base.subArguments.size() == 1 && (base.parent() == null || base.parent().subArguments.size() > 1) && !(base instanceof DynamicLiteral))
	 this.base.retains(true);
	return base;
 }


}
