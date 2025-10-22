package io.dynamicstudios.commands.command.argument;

import io.dynamicstudios.commands.DynamicCommandManager;
import io.dynamicstudios.commands.command.argument.types.*;
import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public abstract class DynamicArgument<T> {

 private final String name, description;
 private String helpDescription;
 private DynamicArgument parent;
 protected final List<DynamicArgument<?>> subArguments;
 private String[] aliases;
 private final List<String> availableNames;
 private final Class<T> type;
 private int span = 1;
 private Supplier<Collection<String>> suggestions;
 private Function<CommandSender, Collection<String>> senderSuggestions;
 private ArgumentExecutor executor = null;
 public boolean retains = false;
 public boolean required = false;
 public boolean optional = true;
 private ArgumentPredicate predicate = (s, arg) -> {
 };
 private TabArgumentPredicate tabPredicate = (s, arg) -> {
 };
 private Function<String, Collection<String>> appendSuggestions;
 private BiFunction<CommandSender, DynamicArguments, Collection<String>> dynamicArguments;

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

 public DynamicArgument parent(Predicate<DynamicArgument<?>> predicate) {
	DynamicArgument parent = this.parent;
	if(parent == null || predicate.test(parent)) return parent;
	while(parent != null) {
	 if(parent.parent == null || predicate.test(parent.parent)) return parent.parent;
	 parent = parent.parent;
	}
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
 public static DynamicLiteral literal(String[] literals, String description) {
	return DynamicLiteral.of(literals, description);
 }
 public static DynamicLiteral literal(List<String> literals, String description) {
	return DynamicLiteral.of(literals, description);
 }

 public static DynamicStringArgument limited(String name, String description, int length) {
	return (DynamicStringArgument) DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.LIMITED).span(length);
 }

 public static DynamicStringArgument greedy(String name, String description) {
	return DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.GREEDY);
 }

 public static DynamicStringArgument word(String name, String description) {
	return DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.WORD);
 }

 public static DynamicStringArgument single(String name, String description) {
	return DynamicStringArgument.of(name, description, DynamicStringArgument.StringType.SINGLE);
 }

 public static DynamicStringArgument limited(String name, String description) {
	return DynamicStringArgument.of(name, description);
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

 //<editor-fold desc="Entity Arguments">

 public static DynamicEntityArgument entity(String name, String description) {
	return DynamicEntityArgument.entity(name, description);
 }

 public static DynamicEntityArgument entities(String name, String description) {
	return DynamicEntityArgument.entities(name, description);
 }

 public static DynamicEntityArgument player(String name, String description) {
	return DynamicEntityArgument.player(name, description);
 }

 public static DynamicEntityArgument players(String name, String description) {
	return DynamicEntityArgument.players(name, description);
 }

 //</editor-fold>

 public ArgumentPredicate predicate() {
	return predicate == null ? (s, arg) -> {
	} : predicate;
 }

 public TabArgumentPredicate tabPredicate() {
	return tabPredicate == null ? (s, arg) -> {
	} : tabPredicate;
 }

 public List<String> suggestions() {
	return suggestions == null ? null : new ArrayList<>(suggestions.get());
 }

 public List<String> suggestions(CommandSender sender) {
	return senderSuggestions == null ? null : new ArrayList<>(senderSuggestions.apply(sender));
 }

 public List<String> suggestions(String input) {
	return appendSuggestions == null || appendSuggestions.apply(input) == null ? null : new ArrayList<>(appendSuggestions.apply(input));
 }

 public List<String> suggestions(CommandSender sender, DynamicArguments input) {
	return dynamicArguments == null || dynamicArguments.apply(sender, input) == null ? null : new ArrayList<>(dynamicArguments.apply(sender, input));
 }

 public ArgumentExecutor executes() {
	return executor;
 }

 public boolean retains() {
	return retains;
 }

 public boolean required() {
	return required;
 }

 public boolean isOptional() {
	return optional;
 }

 public DynamicArgument<T> suggestions(Supplier<Collection<String>> suggestions) {
	this.suggestions = suggestions;
	return this;
 }

 public DynamicArgument<T> suggestions(Function<CommandSender, Collection<String>> senderSuggestions) {
	this.senderSuggestions = senderSuggestions;
	return this;
 }


 public DynamicArgument<T> suggestions(BiFunction<CommandSender, DynamicArguments, Collection<String>> dynamicArguments) {
	this.dynamicArguments = dynamicArguments;
	return this;
 }

 public DynamicArgument<T> suggestionsAppend(Function<String, Collection<String>> appendSuggestions) {
	this.appendSuggestions = appendSuggestions;
	return this;
 }

 public DynamicArgument<T> predicate(ArgumentPredicate predicate) {
	this.predicate = predicate;
	return this;
 }

 public DynamicArgument<T> tabPredicate(TabArgumentPredicate tabPredicate) {
	this.tabPredicate = tabPredicate;
	return this;
 }

 public DynamicArgument<T> executes(ArgumentExecutor executor) {
	this.executor = executor;
	required = true;
	return this;
 }

 public DynamicArgument<T> retains(boolean retains) {
	this.retains = retains;
	return this;
 }

 public DynamicArgument<T> required(boolean required) {
	this.required = required;
	return this;
 }

 public DynamicArgument<T> optional(boolean optional) {
	this.optional = optional;
	return this;
 }

 private boolean checkAliases(String arg) {
	if(aliases == null) return false;
	for(String alias : aliases) {
	 if(alias.equalsIgnoreCase(arg)) return true;
	}
	return false;
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

 public String helpDescription() {
	return helpDescription;
 }

 public DynamicArgument<T> helpDescription(String helpDescription) {
	this.helpDescription = helpDescription;
	return this;
 }

 public T parse(CommandSender sender, String input) throws CommandException {
	return DynamicCommandManager.parse(type, input);
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
	return print(0);
 }

 public String print(int indent) {
	StringBuilder indentStr = new StringBuilder();
	for(int i = 0; i < indent; i++) {
	 indentStr.append(" ");
	}
	StringBuilder key = new StringBuilder(indentStr + "{\n");
	key.append(indentStr).append(" \"name\":\"").append(name).append("\",\n");
	key.append(indentStr).append(" \"description\":\"").append(description).append("\",\n");
	key.append(indentStr).append(" \"subArguments\":[");
	int index = 0;
	for(DynamicArgument<?> subArgument : subArguments) {
	 key.append(index > 0 ? "," : "").append("\n").append(subArgument.print(indent + 1));
	 index++;
	}
	if(subArguments.isEmpty()) key.append("]");
	else key.append("\n").append(indentStr).append(" ]");
	key.append("\n").append(indentStr).append("}");
	return key.toString();
 }

 public abstract boolean isValid(String input);

 public ValidationResult validationResult(String input) {
	return null;
 }

 public int length() {
	int length = subArguments.size();
	for(DynamicArgument<?> subArgument : subArguments) {
	 length += subArgument.length();
	}
	return length;
 }
}
