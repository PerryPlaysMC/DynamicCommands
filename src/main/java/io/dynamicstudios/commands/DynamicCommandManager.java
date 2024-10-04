package io.dynamicstudios.commands;

import io.dynamicstudios.commands.command.DynamicCommand;
import io.dynamicstudios.commands.command.annotation.Argument;
import io.dynamicstudios.commands.command.annotation.Command;
import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.command.help.DefaultHelpPage;
import io.dynamicstudios.commands.command.help.HelpPage;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.util.ParserFunction;
import io.dynamicstudios.json.data.util.CColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Creator: PerryPlaysMC
 * Created: 08/2022
 **/
public class DynamicCommandManager {

  public static String ERROR_MESSAGE = "Error: {command} ";
  public static int COMMANDS_PER_PAGE = 5;
  public static HelpPage HELP_PAGE = new DefaultHelpPage();
	private static SimpleCommandMap commandMap;

	public static SimpleCommandMap getCommandMap() {
		if(commandMap == null) {
			SimplePluginManager spm = (SimplePluginManager) Bukkit.getServer().getPluginManager();
			try {
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);
				commandMap = (SimpleCommandMap) f.get(spm);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return commandMap;
	}

  public static void registerCommand(DynamicCommand command) {
		SimpleCommandMap map = getCommandMap();
		if(map == null) return;
		map.register(command.owner().getName(),command);
  }

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
	    byName.put(argument.name(),argument);
			Class<?> firstParam = method.getParameterTypes()[0];
			if(firstParam.getName().equals(Player.class.getName()) && !argument.playerOnly()) continue;
			if(!firstParam.getName().equals(CommandSender.class.getName()) && argument.playerOnly()) continue;
			Argument argF = byName.get(argument.forArg().isEmpty() ? argument.name() : argument.forArg());
			argsFor.putIfAbsent(argF, new ArrayList<>());
			if(!argument.forArg().isEmpty()) argsFor.get(argF).add(argument);
	    methodMap.put(argument,method);
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
		suggestions(Player.class, () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
		suggestions(World.class, () -> Bukkit.getWorlds().stream().map(World::getName).toList());
		suggestions(Enum.class, i -> Arrays.stream(i.getEnumConstants()).map(Enum::name).map(String::toLowerCase).toList());
		senderSuggestions(Location.class, p -> {
			if(p instanceof Player) {
				Location location = ((Player) p).getLocation();
				return List.of(Math.round(location.getX()) + " " + Math.round(location.getY()) + " " + Math.round(location.getZ()));
			}
			return List.of();
		});
	}

	public static void suggestions(Class<?> clazz, Supplier<List<String>> function) {
		SUGGESTIONS.put(clazz, function);
	}

	public static <T> void suggestions(Class<T> clazz, Function<Class<T>, List<String>> function) {
		INPUT_SUGGESTIONS.put(clazz, (c)->function.apply((Class<T>) c));
	}

	public static <T> void senderSuggestions(Class<T> clazz, Function<CommandSender, List<String>> function) {
		SENDER_SUGGESTIONS.put(clazz, function);
	}

	public static List<String> suggestions(Class<?> clazz) {
		if(SUGGESTIONS.containsKey(clazz.getSuperclass()) && !SUGGESTIONS.containsKey(clazz))
			return SUGGESTIONS.get(clazz.getSuperclass()).get();
		if(!SUGGESTIONS.containsKey(clazz))return null;
		return SUGGESTIONS.getOrDefault(clazz, null).get();
	}

	public static List<String> suggestionsInput(Class<?> clazz) {
		if(INPUT_SUGGESTIONS.containsKey(clazz.getSuperclass()) && !INPUT_SUGGESTIONS.containsKey(clazz))
			return INPUT_SUGGESTIONS.get(clazz.getSuperclass()).apply(clazz);
		if(!INPUT_SUGGESTIONS.containsKey(clazz))return null;
		return INPUT_SUGGESTIONS.getOrDefault(clazz,null).apply(clazz);
	}
	public static Function<CommandSender, List<String>> senderSuggestions(Class<?> clazz) {
		if(SENDER_SUGGESTIONS.containsKey(clazz.getSuperclass()) && !SENDER_SUGGESTIONS.containsKey(clazz))
			return SENDER_SUGGESTIONS.get(clazz.getSuperclass());
		if(!SENDER_SUGGESTIONS.containsKey(clazz))return null;
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
		parser(String.class, (i,s)->s);
		parser(Player.class, (i,s) -> {
			Player player = Bukkit.getPlayer(s);
			if(player == null) {
				try {
					UUID id = UUID.fromString(s);
					player = Bukkit.getPlayer(id);
				}catch (Exception e) {
					throw new CommandException("Invalid uuid for player '" + s + "'");
				}
			}
			if(player == null) throw new CommandException("Invalid player '" + s + "'");
			return player;
		});
		parser(Integer.class, (i,s) -> {
			try {
				return Integer.parseInt(s);
			}catch (Exception e) {
				throw new CommandException("Invalid integer '" + s + "'");
			}
		});
		parser(Double.class, (i,s) -> {
			try {
				return Double.parseDouble(s);
			}catch (Exception e) {
				throw new CommandException("Invalid double '" + s + "'");
			}
		});
		parser(Enum.class, (i,s) -> {
			try {
				for(Enum enumConstant : i.getEnumConstants()) {
					if(enumConstant == null) continue;
					if(enumConstant.name().equalsIgnoreCase(s))return enumConstant;
				}
				throw new CommandException("Invalid "+i.getSimpleName()+" '" + s + "'");
			}catch (Exception e) {
				throw new CommandException("Invalid "+i.getSimpleName()+" '" + s + "'");
			}
		});
		parser(World.class, (i,s) -> {
			World world = Bukkit.getWorld(s);
			if(world == null) {
				try {
					UUID id = UUID.fromString(s);
					world = Bukkit.getWorld(id);
				}catch (Exception e) {
					throw new CommandException("Invalid uuid for world '" + s + "'");
				}
			}
			if(world == null) throw new CommandException("Invalid world '" + s + "'");
			return world;
		});
		parser(Entity.class, (i,s) -> {
			Entity entity = Bukkit.getWorlds().stream()
				.flatMap(w->w.getEntities().stream())
				.filter(a-> a.getCustomName()!=null)
				.filter(a-> CColor.stripColor(a.getCustomName()).equalsIgnoreCase(s))
				.findFirst().orElse(null);
			if(entity == null) {
				try {
					UUID id = UUID.fromString(s);
					entity = Bukkit.getEntity(id);
				}catch (Exception e) {
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
		if(clazz.getName().equalsIgnoreCase(String.class.getName()))return (T) input;
		if(!PARSER.containsKey(clazz) && !PARSER.containsKey(clazz.getSuperclass()))throw new CommandException(ERROR_MESSAGE);
		ParserFunction<T> parse = (ParserFunction<T>) PARSER.getOrDefault(clazz, PARSER.get(clazz.getSuperclass()));
    try {
      return clazz.cast(parse.parse(clazz,input));
    }catch (ClassCastException e) {
      throw new CommandException(ERROR_MESSAGE);
    }
  }

  public static <T> boolean canParse(Class<T> clazz, String input) {
    if(!PARSER.containsKey(clazz) && !PARSER.containsKey(clazz.getSuperclass())) return false;
	  ParserFunction<T> parse = (ParserFunction<T>) PARSER.getOrDefault(clazz, PARSER.get(clazz.getSuperclass()));
    try {
      return clazz.cast(parse.parse(clazz,input)) != null;
    }catch (ClassCastException | CommandException e) {
      return false;
    }
  }

  //</editor-fold>

  public static String translateDefault(String msg, String[] texts, Object... objects) {
    for(int i = 0; i < objects.length; i++) {
      Object o = objects[i];
      if(msg.contains("{"+i+"}"))
        msg=msg.replace("{"+i+"}", o.toString());
      if(texts!=null&&texts.length == objects.length && msg.contains(!texts[i].matches("\\{.+}") ? "{"+texts[i]+"}": texts[i]))
        msg=msg.replace(!texts[i].matches("\\{.+}") ? "{"+texts[i]+"}": texts[i], o.toString());
    }
    return(CColor.translateCommon(msg));
  }


}
