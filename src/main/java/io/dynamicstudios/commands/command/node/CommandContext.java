package io.dynamicstudios.commands.command.node;

import java.util.Map;

/**
 * Creator: PerryPlaysMC
 * Created: 06/2024
 **/
public class CommandContext<Source> {

	private Map<String, Object> arguments;

	public CommandContext(Source source, NodeArgument node, String arguments) {
		String[] args = arguments.split(" ");

	}


}
