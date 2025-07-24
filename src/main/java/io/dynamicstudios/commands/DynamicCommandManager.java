package io.dynamicstudios.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.dynamicstudios.commands.brigadier.BrigadierTypes;
import io.dynamicstudios.commands.brigadier.registration.ReflectionBrigadier;
import io.dynamicstudios.commands.command.DynamicCommand;
import io.dynamicstudios.commands.command.PluginReloadListener;
import io.dynamicstudios.commands.command.annotation.Argument;
import io.dynamicstudios.commands.command.annotation.Command;
import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.command.argument.types.*;
import io.dynamicstudios.commands.command.help.DefaultHelpPage;
import io.dynamicstudios.commands.command.help.HelpPage;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.util.ParserFunction;
import io.dynamicstudios.json.data.util.CColor;
import io.dynamicstudios.json.data.util.packet.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creator: PerryPlaysMC
 * Created: 08/2022
 **/
public class DynamicCommandManager {
 private static Map<String, Set<DynamicCommand>> REGISTERED_COMMANDS = new HashMap<>();

 private static Set<Plugin> PLUGINS = new HashSet<>();

 static {
	CColor.registerColorTranslator((input) -> input
		 .replace("[c]", "&7")
		 .replace("[i]", "&5")
		 .replace("[r]", "&6")
		 .replace("[o]", "&e")
		 .replace("[a]", "&d")
	);
 }

 public static String ERROR_MESSAGE = "Error: {command}";
 public static int COMMANDS_PER_PAGE = 10;
 public static HelpPage HELP_PAGE = new DefaultHelpPage();
 private static final Map<String, ReflectionBrigadier> BRIGADIERS = new HashMap<>();
 private static SimpleCommandMap commandMap;
 private static Field knownCommandsField;

 public static SimpleCommandMap getCommandMap() {
	if(commandMap == null) {
	 SimplePluginManager spm = (SimplePluginManager) Bukkit.getServer().getPluginManager();
	 try {
		Field f = SimplePluginManager.class.getDeclaredField("commandMap");
		f.setAccessible(true);
		commandMap = (SimpleCommandMap) f.get(spm);
		knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
		knownCommandsField.setAccessible(true);
	 } catch(Exception e) {
		e.printStackTrace();
	 }
	}
	return commandMap;
 }

 public static Map<String, Set<DynamicCommand>> commands() {
	return REGISTERED_COMMANDS;
 }

 public static void registerCommand(DynamicCommand command) {
	registerCommand(command, true);
 }

 public static void registerCommand(DynamicCommand command, boolean useBrigadier) {
	registerCommand(command.owner().getName(), command, useBrigadier);
 }

 public static void registerCommand(String fallback, DynamicCommand command) {
	registerCommand(fallback, command, true);
 }

 public static void registerCommand(String fallbackPrefix, DynamicCommand command, boolean useBrigadier) {
	SimpleCommandMap map = getCommandMap();
	if(map == null) return;
	PLUGINS.removeIf(c -> c.getName().equalsIgnoreCase(command.owner().getName()) && !c.equals(command.owner()));
	REGISTERED_COMMANDS.getOrDefault(fallbackPrefix, new HashSet<>()).removeIf(c -> c.getName().equalsIgnoreCase(command.getName()));
	List<String> aliases = command.aliases();
	for(String alias : aliases) {
	 unregisterCommand(map, fallbackPrefix + ":" + alias);
	 unregisterCommand(map, alias);
	}
	unregisterCommand(map, command.getName());
	unregisterCommand(map, fallbackPrefix + ":" + command.getName());
	unregisterCommand(map, fallbackPrefix + ":");
	if(command.getName().isEmpty() || fallbackPrefix.isEmpty()) return;
	String prefix = fallbackPrefix.toLowerCase().isEmpty() ? "minecraft" : fallbackPrefix.toLowerCase();
	map.register(prefix, command);
	REGISTERED_COMMANDS.computeIfAbsent(fallbackPrefix, k -> new HashSet<>()).add(command);
	if(PLUGINS.add(command.owner())) {
	 new PluginReloadListener(command.owner());
	}
	if(!useBrigadier) return;
	Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
	Bukkit.getScheduler().runTaskLater(command.owner(), () -> {
	registerBrigadierCommand(prefix, command, aliases);
	registerBrigadierCommand("", command, aliases);
	 Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
	}, 20);
 }

 @SuppressWarnings("unchecked")
 public static void unregisterCommand(SimpleCommandMap map, String command) {
	org.bukkit.command.Command oldCMD = map.getCommand(command.toLowerCase());
	if(oldCMD != null) {
	 try {
		Map<String, org.bukkit.command.Command> known = getKnownCommands();
		known.remove(command.toLowerCase());
		if(command.contains(":"))
		 known.remove(command.split(":")[command.split(":").length - 1].toLowerCase());
	 } catch(Exception e) {
		throw new RuntimeException(e);
	 }
	}
 }

 private static Map<String, org.bukkit.command.Command> getKnownCommands() {
	try {
	 return (Map<String, org.bukkit.command.Command>) knownCommandsField.get(getCommandMap());
	} catch(IllegalAccessException e) {
	 return new HashMap<>();
	}
 }

 private static void registerBrigadierCommand(String fallback, DynamicCommand command, List<String> aliases) {
	try {
	 Class.forName("com.mojang.brigadier.builder.LiteralArgumentBuilder");
	 Predicate<Object> basePermission = (ctx) -> {
		Method getBukkit = ReflectionUtils.getMethod(ctx.getClass(), CommandSender.class);
		if(getBukkit == null) return true;
		try {
		 return command.testPermissionSilent((CommandSender) getBukkit.invoke(ctx));
		} catch(IllegalAccessException | InvocationTargetException e) {
		 return true;
		}
	 };
	 String prefix = fallback.isEmpty() ? "" : fallback + ":";
	 LiteralArgumentBuilder brigadierCommand = LiteralArgumentBuilder.literal(prefix + command.getName())
			.requires(basePermission);
	 buildBrigadier(command, brigadierCommand);
	 ReflectionBrigadier brigadier = getBrigadier(command.owner(), fallback);
	 LiteralCommandNode<?> node = brigadierCommand.build();
	 brigadier.register(node);
	 for(String alias : aliases) {
		LiteralArgumentBuilder<Object> node2 = LiteralArgumentBuilder.literal(prefix + alias)
			 .requires(basePermission);
		buildBrigadier(command, node2);
		brigadier.register(node2.build());
	 }
	} catch(Exception e) {
	 e.printStackTrace();
	}
 }

 private static void buildBrigadier(DynamicCommand command, LiteralArgumentBuilder brigadierCommand) {
	executes(command, brigadierCommand);
	boolean previousHasProvider = false;
	for(DynamicArgument<?> dynamicArgument : command.getArguments()) {
	 boolean has = buildCommand(command, dynamicArgument, brigadierCommand, null, previousHasProvider);
	 if(has) previousHasProvider = true;
	}
 }
// public static void register(CommandDispatcher<CommandSourceStack> commanddispatcher) {
//	LiteralCommandNode<CommandSourceStack> literalcommandnode = commanddispatcher
//		 .register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)
//				Commands.literal("teleport").requires((commandlistenerwrapper) -> commandlistenerwrapper.hasPermission(2))).then(Commands.argument("location", Vec3Argument.vec3()).executes((commandcontext) -> teleportToPos((CommandSourceStack)commandcontext.getSource(), Collections.singleton(((CommandSourceStack)commandcontext.getSource()).getEntityOrException()), ((CommandSourceStack)commandcontext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandcontext, "location"), (Coordinates)null, (LookAt)null)))).then(Commands.argument("destination", EntityArgument.entity()).executes((commandcontext) -> teleportToEntity((CommandSourceStack)commandcontext.getSource(), Collections.singleton(((CommandSourceStack)commandcontext.getSource()).getEntityOrException()), EntityArgument.getEntity(commandcontext, "destination"))))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", Vec3Argument.vec3()).executes((commandcontext) -> teleportToPos((CommandSourceStack)commandcontext.getSource(), EntityArgument.getEntities(commandcontext, "targets"), ((CommandSourceStack)commandcontext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandcontext, "location"), (Coordinates)null, (LookAt)null))).then(Commands.argument("rotation", RotationArgument.rotation()).executes((commandcontext) -> teleportToPos((CommandSourceStack)commandcontext.getSource(), EntityArgument.getEntities(commandcontext, "targets"), ((CommandSourceStack)commandcontext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandcontext, "location"), RotationArgument.getRotation(commandcontext, "rotation"), (LookAt)null)))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity()).executes((commandcontext) -> teleportToPos((CommandSourceStack)commandcontext.getSource(), EntityArgument.getEntities(commandcontext, "targets"), ((CommandSourceStack)commandcontext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandcontext, "location"), (Coordinates)null, new LookAt.LookAtEntity(EntityArgument.getEntity(commandcontext, "facingEntity"), EntityAnchorArgument.Anchor.FEET)))).then(Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes((commandcontext) -> teleportToPos((CommandSourceStack)commandcontext.getSource(), EntityArgument.getEntities(commandcontext, "targets"), ((CommandSourceStack)commandcontext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandcontext, "location"), (Coordinates)null, new LookAt.LookAtEntity(EntityArgument.getEntity(commandcontext, "facingEntity"), EntityAnchorArgument.getAnchor(commandcontext, "facingAnchor")))))))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((commandcontext) -> teleportToPos((CommandSourceStack)commandcontext.getSource(), EntityArgument.getEntities(commandcontext, "targets"), ((CommandSourceStack)commandcontext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandcontext, "location"), (Coordinates)null, new LookAt.LookAtPosition(Vec3Argument.getVec3(commandcontext, "facingLocation")))))))).then(Commands.argument("destination", EntityArgument.entity()).executes((commandcontext) -> teleportToEntity((CommandSourceStack)commandcontext.getSource(), EntityArgument.getEntities(commandcontext, "targets"), EntityArgument.getEntity(commandcontext, "destination"))))));
//	commanddispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires((commandlistenerwrapper) -> commandlistenerwrapper.hasPermission(2))).redirect(literalcommandnode));
// }

 public static <T> void registerCommand(T command) {
	Class<?> type = command.getClass();
	Command annotatedCommand = type.getAnnotation(Command.class);
	if(annotatedCommand == null) return;
	Map<Argument, List<Argument>> argsFor = new LinkedHashMap<>();
	Map<String, Argument> byName = new LinkedHashMap<>();
	Map<Argument, Method> methodMap = new LinkedHashMap<>();
	for(Method method : type.getMethods()) {
	 Argument argument = method.getAnnotation(Argument.class);
	 if(argument == null) continue;
	 byName.put(argument.name(), argument);
	 Class<?> firstParam = method.getParameterTypes()[0];
	 if(firstParam.getName().equals(Player.class.getName()) && !argument.playerOnly()) continue;
	 if(!firstParam.getName().equals(CommandSender.class.getName()) && argument.playerOnly()) continue;
	 Argument argF = byName.get(argument.forArg().isEmpty() ? argument.name() : argument.forArg());
	 argsFor.putIfAbsent(argF, new ArrayList<>());
	 if(!argument.forArg().isEmpty()) argsFor.get(argF).add(argument);
	 methodMap.put(argument, method);
	}
	List<DynamicArgument<?>> args = new ArrayList<>();
	for(Argument argument : argsFor.keySet()) {

	}

 }

 //<editor-fold desc="Suggestion Providers">

 private static final Map<Class<?>, Supplier<List<String>>> SUGGESTIONS = new HashMap<>();
 private static final Map<Class<?>, Function<Class<?>, List<String>>> INPUT_SUGGESTIONS = new HashMap<>();
 private static final Map<Class<?>, Function<CommandSender, List<String>>> SENDER_SUGGESTIONS = new HashMap<>();

 static {
	suggestions(Player.class, () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).distinct().collect(Collectors.toList()));

	suggestions(OfflinePlayer.class, () -> Stream.concat(Bukkit.getOnlinePlayers().stream().map(Player::getName),
		 Stream.of(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName)).distinct().collect(Collectors.toList()));
	suggestions(World.class, () -> Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
	suggestions(Enum.class, i -> Arrays.stream(i.getEnumConstants()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList()));
	senderSuggestions(Location.class, p -> {
	 if(p instanceof Player) {
		Location location = ((Player) p).getLocation();
		return Collections.singletonList(Math.round(location.getX()) + " " + Math.round(location.getY()) + " " + Math.round(location.getZ()));
	 }
	 return Collections.emptyList();
	});
 }

 public static void suggestions(Class<?> clazz, Supplier<List<String>> function) {
	SUGGESTIONS.put(clazz, function);
 }

 public static <T> void suggestions(Class<T> clazz, Function<Class<T>, List<String>> function) {
	INPUT_SUGGESTIONS.put(clazz, (c) -> function.apply((Class<T>) c));
 }

 public static <T> void senderSuggestions(Class<T> clazz, Function<CommandSender, List<String>> function) {
	SENDER_SUGGESTIONS.put(clazz, function);
 }

 public static List<String> suggestions(Class<?> clazz) {
	if(SUGGESTIONS.containsKey(clazz.getSuperclass()) && !SUGGESTIONS.containsKey(clazz))
	 return SUGGESTIONS.get(clazz.getSuperclass()).get();
	if(!SUGGESTIONS.containsKey(clazz)) return null;
	return SUGGESTIONS.getOrDefault(clazz, null).get();
 }

 public static List<String> suggestionsInput(Class<?> clazz) {
	if(INPUT_SUGGESTIONS.containsKey(clazz.getSuperclass()) && !INPUT_SUGGESTIONS.containsKey(clazz))
	 return INPUT_SUGGESTIONS.get(clazz.getSuperclass()).apply(clazz);
	if(!INPUT_SUGGESTIONS.containsKey(clazz)) return null;
	return INPUT_SUGGESTIONS.getOrDefault(clazz, null).apply(clazz);
 }

 public static Function<CommandSender, List<String>> senderSuggestions(Class<?> clazz) {
	if(SENDER_SUGGESTIONS.containsKey(clazz.getSuperclass()) && !SENDER_SUGGESTIONS.containsKey(clazz))
	 return SENDER_SUGGESTIONS.get(clazz.getSuperclass());
	if(!SENDER_SUGGESTIONS.containsKey(clazz)) return null;
	return SENDER_SUGGESTIONS.getOrDefault(clazz, null);
 }

 public static boolean hasSuggestionProvider(Class<?> clazz) {
	return SUGGESTIONS.containsKey(clazz) || SUGGESTIONS.containsKey(clazz.getSuperclass());
 }

 public static boolean hasSuggestionProviderInput(Class<?> clazz) {
	return INPUT_SUGGESTIONS.containsKey(clazz) || INPUT_SUGGESTIONS.containsKey(clazz.getSuperclass());
 }

 public static boolean hasSenderSuggestions(Class<?> clazz) {
	return SENDER_SUGGESTIONS.containsKey(clazz) || SENDER_SUGGESTIONS.containsKey(clazz.getSuperclass());
 }

 //</editor-fold>

 //<editor-fold desc="Parser">

 private static final HashMap<Class<?>, ParserFunction<?>> PARSER = new HashMap<>();

 static {
	parser(String.class, (i, s) -> s);


	parser(Player.class, (i, s) -> {
	 Player player = Bukkit.getOnlinePlayers().stream()
			.filter(p -> p.getName().equalsIgnoreCase(s)
				 || p.getDisplayName().equalsIgnoreCase(s)
				 || p.getPlayerListName().equalsIgnoreCase(s)
				 || (p.getCustomName() != null && p.getCustomName().equalsIgnoreCase(s))).findFirst().orElse(null);
	 if(player == null) {
		try {
		 UUID id = UUID.fromString(s);
		 player = Bukkit.getOnlinePlayers().stream()
				.filter(p -> p.getUniqueId().equals(id)).findFirst().orElse(null);
		} catch(Exception ignored) {
		}
	 }
	 if(player == null) throw new CommandException("Invalid player '" + s + "'");
	 return player;
	});


	parser(OfflinePlayer.class, (i, s) -> {
	 OfflinePlayer player = Stream.concat(Bukkit.getOnlinePlayers().stream(), Arrays.stream(Bukkit.getOfflinePlayers()))
			.filter(p -> p.getName() != null)
			.filter(p -> p.getName().equalsIgnoreCase(s)).findFirst().orElse(null);
	 if(player == null) {
		try {
		 UUID id = UUID.fromString(s);
		 player = Stream.concat(Bukkit.getOnlinePlayers().stream(), Arrays.stream(Bukkit.getOfflinePlayers()))
				.filter(p -> p.getUniqueId().equals(id)).findFirst().orElse(null);
		} catch(Exception ignored) {
		}
	 }
	 if(player == null) throw new CommandException("Invalid player '" + s + "'");
	 return player;
	});
	parser(Integer.class, (i, s) -> {
	 try {
		return Integer.parseInt(s);
	 } catch(Exception e) {
		throw new CommandException("Invalid integer '" + s + "'");
	 }
	});
	parser(Double.class, (i, s) -> {
	 try {
		return Double.parseDouble(s);
	 } catch(Exception e) {
		throw new CommandException("Invalid double '" + s + "'");
	 }
	});
	parser(Enum.class, (i, s) -> {
	 for(Enum enumConstant : i.getEnumConstants()) {
		if(enumConstant == null) continue;
		if(enumConstant.name().equalsIgnoreCase(s)) return enumConstant;
	 }
	 throw new CommandException("Invalid " + i.getSimpleName() + " '" + s + "'");
//	 try {
//	 } catch(Exception e) {
//		throw new CommandException("Invalid " + i.getSimpleName() + " '" + s + "'");
//	 }
	});
	parser(World.class, (i, s) -> {
	 World world = Bukkit.getWorld(s);
	 if(world == null) {
		try {
		 UUID id = UUID.fromString(s);
		 world = Bukkit.getWorld(id);
		} catch(Exception e) {
		 throw new CommandException("Invalid uuid for world '" + s + "'");
		}
	 }
	 if(world == null) throw new CommandException("Invalid world '" + s + "'");
	 return world;
	});
	parser(Entity.class, (i, s) -> {
	 Entity entity = Bukkit.getWorlds().stream()
			.flatMap(w -> w.getEntities().stream())
			.filter(a -> a.getCustomName() != null)
			.filter(a -> CColor.stripColor(a.getCustomName()).equalsIgnoreCase(s))
			.findFirst().orElse(null);
	 if(entity == null) {
		try {
		 UUID id = UUID.fromString(s);
		 entity = Bukkit.getWorlds().stream()
				.flatMap(w -> w.getEntities().stream()).filter(e -> e.getUniqueId().equals(id)).findFirst().orElse(null);
		} catch(Exception e) {
		 throw new CommandException("Invalid uuid for entity '" + s + "'");
		}
	 }
	 if(entity == null) throw new CommandException("Invalid entity '" + s + "'");
	 return entity;
	});
 }

 public static <T> void parser(Class<T> clazz, ParserFunction<T> function) {
	PARSER.put(clazz, function);
 }

 public static <T> T parse(Class<T> clazz, String input) throws CommandException {
	if(clazz.getName().equalsIgnoreCase(String.class.getName())) return (T) input;
	if(!PARSER.containsKey(clazz) && !PARSER.containsKey(clazz.getSuperclass()))
	 throw new CommandException(ERROR_MESSAGE);
	ParserFunction<T> parse = (ParserFunction<T>) PARSER.getOrDefault(clazz, PARSER.get(clazz.getSuperclass()));
	try {
	 return clazz.cast(parse.parse(clazz, input));
	} catch(ClassCastException e) {
	 throw new CommandException(ERROR_MESSAGE);
	}
 }

 public static <T> boolean canParse(Class<T> clazz, String input) {
	if(!PARSER.containsKey(clazz) && !PARSER.containsKey(clazz.getSuperclass())) return false;
	try {
	 return parse(clazz, input) != null;
	} catch(ClassCastException | CommandException e) {
	 return false;
	}
 }

 //</editor-fold>

 public static String translateDefault(String msg, String[] texts, Object... objects) {
	for(int i = 0; i < objects.length; i++) {
	 Object o = objects[i];
	 if(msg.contains("{" + i + "}"))
		msg = msg.replace("{" + i + "}", o.toString());
	 if(texts != null && texts.length == objects.length && msg.contains(!texts[i].matches("\\{.+}") ? "{" + texts[i] + "}" : texts[i]))
		msg = msg.replace(!texts[i].matches("\\{.+}") ? "{" + texts[i] + "}" : texts[i], o.toString());
	}
	return (CColor.translateCommon(msg));
 }

 @SuppressWarnings("unchecked")
 private static <T> void buildCommand(DynamicCommand executor, DynamicArgument<?> arg, LiteralArgumentBuilder<T> command, ArgumentBuilder<T, ?> stack) {
	buildCommand(executor, arg, command, stack, false);
 }

 private static <T> boolean buildCommand(DynamicCommand executor, DynamicArgument<?> arg, LiteralArgumentBuilder<T> command, ArgumentBuilder<T, ?> stack, boolean previousHasProvider) {
	ArgumentBuilder<T, ?> argCommand;
	boolean hasProvider = true;
	if(arg instanceof DynamicLiteral) {
	 argCommand = LiteralArgumentBuilder.literal(arg.name());
	} else if(arg instanceof DynamicLocationArgument) {
	 argCommand = RequiredArgumentBuilder.argument(arg.name(), BrigadierTypes.LOCATION);
	 hasProvider = false;
	} else if(arg instanceof DynamicDoubleArgument) {
	 DynamicDoubleArgument dArg = (DynamicDoubleArgument) arg;
	 argCommand = RequiredArgumentBuilder.argument(arg.name(), BrigadierTypes.RANGE2_DOUBLE.apply(dArg.min(), dArg.max()));
	} else if(arg instanceof DynamicFloatArgument) {
	 DynamicFloatArgument fArg = (DynamicFloatArgument) arg;
	 argCommand = RequiredArgumentBuilder.argument(arg.name(), BrigadierTypes.RANGE2_FLOAT.apply(fArg.min(), fArg.max()));
	} else if(arg instanceof DynamicLongArgument) {
	 DynamicLongArgument lArg = (DynamicLongArgument) arg;
	 argCommand = RequiredArgumentBuilder.argument(arg.name(), BrigadierTypes.RANGE2_LONG.apply(lArg.min(), lArg.max()));
	} else if(arg instanceof DynamicIntegerArgument) {
	 DynamicIntegerArgument iArg = (DynamicIntegerArgument) arg;
	 argCommand = RequiredArgumentBuilder.argument(arg.name(), BrigadierTypes.RANGE2_INTEGER.apply(iArg.min(), iArg.max()));
	} else if(arg instanceof DynamicBooleanArgument) {
	 argCommand = RequiredArgumentBuilder.argument(arg.name(), BrigadierTypes.BOOL);
	} else argCommand = RequiredArgumentBuilder.argument(arg.name(), BrigadierTypes.String.STRING);
	if(arg instanceof DynamicStringArgument) {
	 if(arg.span() == -1 || arg.span() == 1) {
		argCommand = RequiredArgumentBuilder.argument(arg.name(), arg.span() == 1 ? BrigadierTypes.String.STRING : BrigadierTypes.String.GREEDY);
		argCommand = requires(arg, argCommand);
	 } else {
		List<ArgumentBuilder<T, ?>> list = new ArrayList<>();
		for(int i = 0; i < arg.span(); i++) {
		 argCommand = RequiredArgumentBuilder.argument(arg.name() + "-" + (i + 1), BrigadierTypes.String.STRING);
//		 argCommand = requires(arg, argCommand);
		 if(argCommand instanceof RequiredArgumentBuilder && (!previousHasProvider || i > 0)) {// && hasProvider && isLast){
			argCommand = ((RequiredArgumentBuilder<T, ?>) argCommand).suggests((ctx, suggestionsBuilder) ->
				 provideSuggestions(executor, ctx, suggestionsBuilder));
		 }
		 executes(executor, argCommand);
		 list.add(0, argCommand);
		}
		for(int i = 0; i < list.size() - 1; i++) {
		 ArgumentBuilder<T, ?> argument = list.get(i + 1);
		 ArgumentBuilder<T, ?> nextArg = list.get(i);
		 applyExecutor(executor, arg, command, argument, nextArg);
		 argument.then(nextArg);
		}
		applyExecutor(executor, arg, command, stack, list.get(list.size() - 1));
		return true;
	 }
	}
	argCommand = requires(arg, argCommand);
	if(argCommand instanceof RequiredArgumentBuilder && !previousHasProvider && hasProvider) {// && hasProvider && isLast){
	 argCommand = ((RequiredArgumentBuilder<T, ?>) argCommand).suggests((ctx, suggestionsBuilder) -> provideSuggestions(executor, ctx, suggestionsBuilder));
	}
	applyExecutor(executor, arg, command, stack, argCommand);
	return hasProvider;
 }

 private static <T> CompletableFuture<Suggestions> provideSuggestions(DynamicCommand executor, CommandContext<T> ctx, SuggestionsBuilder suggestionsBuilder) {
	String[] args = suggestionsBuilder.getInput().substring(1).split(" ", -1);
	suggestionsBuilder = suggestionsBuilder.createOffset(suggestionsBuilder.getInput().lastIndexOf(' ') + 1);
	Method getBukkit = ReflectionUtils.getMethod(ctx.getSource().getClass(), CommandSender.class);
	if(getBukkit == null) return suggestionsBuilder.buildFuture();
	try {
	 CommandSender sender = (CommandSender) getBukkit.invoke(ctx.getSource());
	 String remainingLowerCase = suggestionsBuilder.getRemainingLowerCase();
	 List<String> suggestions = new ArrayList<>();
	 for(String s : (executor.tabComplete(sender, args[0], Arrays.copyOfRange(args, 1, args.length))))
		if(!s.isEmpty() && s.toLowerCase().startsWith(remainingLowerCase) && suggestions.stream().noneMatch(s::equalsIgnoreCase))
		 suggestions.add(s);
	 for(String suggestion : suggestions) {
		suggestionsBuilder.suggest(suggestion);
	 }
	 return suggestionsBuilder.buildFuture();
	} catch(IllegalAccessException | InvocationTargetException e) {
	 e.printStackTrace();
	 return suggestionsBuilder.buildFuture();
	}
 }

 private static <T> ArgumentBuilder<T, ?> requires(DynamicArgument<?> arg, ArgumentBuilder<T, ?> argCommand) {
	return argCommand.requires((ctx) -> {
	 try {
		Method getBukkit = ReflectionUtils.getMethod(ctx.getClass(), CommandSender.class);
		if(getBukkit == null) return true;
		arg.predicate().test((CommandSender) getBukkit.invoke(ctx), arg.name());
		return true;
	 } catch(Exception e) {
		return false;
	 }
	});
 }


 private static <T> void applyExecutor(DynamicCommand executor, DynamicArgument<?> arg, LiteralArgumentBuilder<T> command, ArgumentBuilder<T, ?> stack, ArgumentBuilder<T, ?> argCommand) {
	if(arg.required() || (arg.isOptional() && arg.parent(DynamicArgument::required) != null))
	 executes(executor, argCommand);
	boolean previousHasProvider = false;
	for(DynamicArgument<?> argument : arg.subArguments()) {
	 boolean has = buildCommand(executor, argument, command, argCommand, previousHasProvider);
	 if(has) previousHasProvider = true;
	}
	if(stack != null) stack.then(argCommand);
	else command.then(argCommand);
 }

 private static <T> void executes(DynamicCommand executor, ArgumentBuilder<T, ?> argCommand) {
	argCommand.executes((ctx) -> {
	 Method getBukkit = ReflectionUtils.getMethod(ctx.getSource().getClass(), CommandSender.class);
	 if(getBukkit == null) return 0;
	 try {
		CommandSender sender = (CommandSender) getBukkit.invoke(ctx.getSource());
		String[] args = ctx.getInput().split(" ");
		if(executor.execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length))) return 1;
		else return 0;
	 } catch(IllegalAccessException | InvocationTargetException e) {
		e.printStackTrace();
		return 0;
	 }
	});
 }

 public static ReflectionBrigadier getBrigadier(Plugin plugin, String fallback) {
	fallback = fallback == null ? plugin.getName().toLowerCase() : fallback;
	return BRIGADIERS.computeIfAbsent(fallback, c -> {
	 ReflectionBrigadier brigadier = new ReflectionBrigadier(plugin);
	 brigadier.ensureSetup();
	 for(Map.Entry<String, org.bukkit.command.Command> entry : new HashMap<>(getKnownCommands()).entrySet()) {
		Class<?> isDynamic = entry.getValue().getClass();
		while(isDynamic != null && !isDynamic.getName().equalsIgnoreCase(DynamicCommand.class.getName())) {
		 isDynamic = isDynamic.getSuperclass();
		}
		if(isDynamic != null) {
		 if(!entry.getKey().startsWith(c + ":")) continue;
		 entry.getValue().unregister(getCommandMap());
		 getKnownCommands().remove(entry.getKey());
		 brigadier.removeChild(brigadier.getDispatcher().getRoot(), entry.getKey().toLowerCase());
		 brigadier.removeChild(brigadier.getDispatcher().getRoot(), entry.getValue().getName().toLowerCase());
		 for(String alias : entry.getValue().getAliases()) {
		 brigadier.removeChild(brigadier.getDispatcher().getRoot(), alias.toLowerCase());
		 }
		}
	 }
	 return brigadier;
	});
 }
}
