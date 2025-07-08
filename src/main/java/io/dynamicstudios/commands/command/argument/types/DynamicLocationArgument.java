package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.DynamicArgument;
import io.dynamicstudios.commands.exceptions.CommandException;
import io.dynamicstudios.commands.exceptions.brigadier.CommandSyntaxException;
import io.dynamicstudios.commands.exceptions.brigadier.LiteralMessage;
import io.dynamicstudios.commands.exceptions.brigadier.SimpleCommandExceptionType;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class DynamicLocationArgument extends DynamicArgument<Location> {
	public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new LiteralMessage("Incomplete location"));

	public DynamicLocationArgument(String name, String description, DynamicArgument<?>... subArguments) {
		super(Location.class, name, description, subArguments);
		span(3);
	}

	public static DynamicLocationArgument of(String name, String description) {
		return new DynamicLocationArgument(name, description);
	}

	@Override
	public boolean isValid(String input) {
		String[] inputs = input.split(" ", -1);
		return inputs.length <= 3 &&
			 Arrays.stream(inputs).allMatch(s -> s.isEmpty() || s.matches("[\\^~]|[\\^~]?-?\\d+(\\.\\d+)?"));
	}

	@Override
	public Location parse(CommandSender sender) throws CommandException {
		if (sender instanceof Player) {
			String[] in = input() == null ? new String[0] : input().split(" ");
			if (in.length == 0) return null;
			if (in.length != 3) return null;
			Vector position = new Vector();
			Player player = (Player) sender;
			Location location = player.getLocation();
			if (in[0].startsWith("~")) position.setX(location.getX());
			if (in[1].startsWith("~")) position.setY(location.getY());
			if (in[2].startsWith("~")) position.setZ(location.getZ());

			if (in[0].startsWith("^")) position.setX(location.getX() + location.getDirection().multiply(Double.parseDouble(in[0].substring(1).isEmpty() ? "0" : in[0].substring(1))).getX());
			if (in[1].startsWith("^")) position.setY(location.getY() + location.getDirection().multiply(Double.parseDouble(in[1].substring(1).isEmpty() ? "0" : in[1].substring(1))).getY());
			if (in[2].startsWith("^")) position.setZ(location.getZ() + location.getDirection().multiply(Double.parseDouble(in[2].substring(1).isEmpty() ? "0" : in[2].substring(1))).getZ());

			if (!in[0].equals("~") && !in[0].startsWith("^")) position.setX(position.getX() + Double.parseDouble(in[0].startsWith("~") ? in[0].substring(1) : in[0]));
			if (!in[1].equals("~") && !in[1].startsWith("^")) position.setY(position.getY() + Double.parseDouble(in[1].startsWith("~") ? in[1].substring(1) : in[1]));
			if (!in[2].equals("~") && !in[2].startsWith("^")) position.setZ(position.getZ() + Double.parseDouble(in[2].startsWith("~") ? in[2].substring(1) : in[2]));
			return position.toLocation(((Player) sender).getWorld());
		}
		return null;
	}

	@Override
	public void brigadierValidate(String input) throws CommandSyntaxException {
		String[] inputs = input.split(" ");
		boolean var2 = inputs[0].matches("[\\^~]|[\\^~]?-?\\d+(\\.\\d+)?");
		if (var2 && inputs.length > 1) {
			boolean var3 = inputs[1].matches("[\\^~]|[\\^~]?-?\\d+(\\.\\d+)?");
			if (!var3 || inputs.length < 3)
				throw ERROR_NOT_COMPLETE.create();
		} else
			throw ERROR_NOT_COMPLETE.create();
	}
}
