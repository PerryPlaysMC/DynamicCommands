package io.dynamicstudios.commands.command.node;

import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 06/2024
 **/
public class CommandNode<Source> implements NodeArgument<Source> {


	private Source source;
	private String name;

	private List<NodeArgument> arguments;


	@Override
	public NodeArgument then(NodeArgument argument) {
		arguments.add(argument);
		return this;
	}

	@Override
	public int executes(CommandContext<Source> context) {
		return 0;
	}
}
