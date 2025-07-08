package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.exceptions.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SelectorParser {
	private static final Pattern SELECTOR_PATTERN = Pattern.compile("^@([pares])(?:\\[([^]]*)\\])?$");

	public static List<Entity> parse(String input, CommandSender sender) throws CommandException {
		Matcher matcher = SELECTOR_PATTERN.matcher(input);
		if (!matcher.matches()) {
			// Handle UUID or player name
			return Collections.singletonList(getEntityByNameOrUUID(input));
		}
		Bukkit.selectEntities(sender, input);
		String selectorType = matcher.group(1);
		Map<String, String> arguments = parseArguments(matcher.group(2));
		Location senderLocation = sender instanceof Player ? ((Player) sender).getLocation() : null;

		switch (selectorType) {
			case "p":
				return Collections.singletonList(findNearestPlayer(arguments, senderLocation));
			case "r":
				return Collections.singletonList(findRandomPlayer(arguments));
			case "a":
				return new ArrayList<>(getAllPlayers(arguments));
			case "e":
				return getEntities(arguments, senderLocation);
			case "s":
				return sender instanceof Entity ? Collections.singletonList((Entity) sender) : Collections.emptyList();
			default:
				throw new CommandException("Unknown selector type: @" + selectorType);
		}
	}

	private static Map<String, String> parseArguments(String args) {
		Map<String, String> arguments = new HashMap<>();
		if (args != null && !args.isEmpty()) {
			String[] pairs = args.split(",");
			for (String pair : pairs) {
				String[] kv = pair.split("=", 2);
				if (kv.length == 2) {
					arguments.put(kv[0], kv[1]);
				} else {
					arguments.put(kv[0], "");
				}
			}
		}
		return arguments;
	}

	private static Entity getEntityByNameOrUUID(String input) throws CommandException {
		try {
			UUID uuid = UUID.fromString(input);
			return Bukkit.getEntity(uuid);
		} catch (IllegalArgumentException e) {
			Player player = Bukkit.getPlayer(input);
			if (player != null) {
				return player;
			}
		}
		throw new CommandException("No entity found for: " + input);
	}

	private static Player findNearestPlayer(Map<String, String> arguments, Location origin) throws CommandException {
		return Bukkit.getOnlinePlayers().stream()
			 .filter(player -> matchesArguments(player, arguments))
			 .min(Comparator.comparingDouble(player -> player.getLocation().distanceSquared(origin)))
			 .orElseThrow(() -> new CommandException("No matching players found."));
	}

	private static Player findRandomPlayer(Map<String, String> arguments) throws CommandException {
		List<Player> players = Bukkit.getOnlinePlayers().stream()
			 .filter(player -> matchesArguments(player, arguments))
			 .collect(Collectors.toList());
		if (players.isEmpty()) {
			throw new CommandException("No matching players found.");
		}
		return players.get(new Random().nextInt(players.size()));
	}

	private static List<Player> getAllPlayers(Map<String, String> arguments) {
		return Bukkit.getOnlinePlayers().stream()
			 .filter(player -> matchesArguments(player, arguments))
			 .collect(Collectors.toList());
	}

	private static List<Entity> getEntities(Map<String, String> arguments, Location origin) {
		List<Entity> entities = Bukkit.getWorlds().stream()
			 .flatMap(world -> world.getEntities().stream())
			 .filter(entity -> matchesArguments(entity, arguments))
			 .collect(Collectors.toList());

		if (arguments.containsKey("sort") && origin != null) {
			String sort = arguments.get("sort");
			Comparator<Entity> comparator;
			switch (sort) {
				case "nearest":
					comparator = Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(origin));
					break;
				case "furthest":
					comparator = Comparator.comparingDouble(entity -> -entity.getLocation().distanceSquared(origin));
					break;
				default:
					comparator = (e1, e2) -> 0; // Arbitrary
					break;
			}
			;
			entities.sort(comparator);
		}

		int limit = arguments.containsKey("limit") ? Integer.parseInt(arguments.get("limit")) : Integer.MAX_VALUE;
		return entities.stream().limit(limit).collect(Collectors.toList());
	}

	private static boolean matchesArguments(Entity entity, Map<String, String> arguments) {
		if (arguments.containsKey("type")) {
			String type = arguments.get("type");
			if (!entity.getType().name().equalsIgnoreCase(type)) {
				return false;
			}
		}

		if (arguments.containsKey("distance")) {
			String range = arguments.get("distance");
			double distance = entity.getLocation().distanceSquared(entity.getWorld().getSpawnLocation());
			if (!matchesRange(distance, range)) {
				return false;
			}
		}

		if (arguments.containsKey("tag")) {
			String tag = arguments.get("tag");
			if (!entity.getScoreboardTags().contains(tag)) {
				return false;
			}
		}

		return true;
	}

	private static boolean matchesRange(double value, String range) {
		if (range.startsWith("..")) {
			double max = Double.parseDouble(range.substring(2));
			return value <= max * max;
		} else if (range.endsWith("..")) {
			double min = Double.parseDouble(range.substring(0, range.length() - 2));
			return value >= min * min;
		} else if (range.contains("..")) {
			String[] parts = range.split("\\.\\.");
			double min = Double.parseDouble(parts[0]);
			double max = Double.parseDouble(parts[1]);
			return value >= min * min && value <= max * max;
		} else {
			double exact = Double.parseDouble(range);
			return value == exact * exact;
		}
	}
}