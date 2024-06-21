package io.dynamicstudios.commands;

import dev.dynamicstudios.json.data.util.CColor;
import io.dynamicstudios.commands.command.DynamicCommand;
import io.dynamicstudios.commands.command.annotation.Argument;
import io.dynamicstudios.commands.command.annotation.Command;
import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.command.help.DefaultHelpPage;
import io.dynamicstudios.commands.command.help.HelpPage;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.util.ParserFunction;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

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


  //<editor-fold desc="Parser">

  private static final HashMap<Class<?>, ParserFunction<?>> PARSER = new HashMap<>();
	static {
		parser(String.class, (s)->s);
		parser(Player.class, (s) -> {
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
		parser(Integer.class, (s) -> {
			try {
				return Integer.parseInt(s);
			}catch (Exception e) {
				throw new CommandException("Invalid integer '" + s + "'");
			}
		});
		parser(Double.class, (s) -> {
			try {
				return Double.parseDouble(s);
			}catch (Exception e) {
				throw new CommandException("Invalid double '" + s + "'");
			}
		});
		parser(World.class, (s) -> {
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
		parser(Entity.class, (s) -> {
			Entity entity = Bukkit.getWorlds().stream()
				.flatMap(w->w.getEntities().stream())
				.filter(a-> a.getCustomName()!=null)
				.filter(a->CColor.stripColor(a.getCustomName()).equalsIgnoreCase(s))
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
    if(!PARSER.containsKey(clazz)) throw new CommandException(ERROR_MESSAGE);
	  ParserFunction<?> parse = PARSER.get(clazz);
    try {
      return clazz.cast(parse.parse(input));
    }catch (ClassCastException e) {
      throw new CommandException(ERROR_MESSAGE);
    }
  }

  public static <T> boolean canParse(Class<T> clazz, String input) {
    if(!PARSER.containsKey(clazz)) return false;
	  ParserFunction<?> parse = PARSER.get(clazz);
    try {
      return clazz.cast(parse.parse(input)) != null;
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
