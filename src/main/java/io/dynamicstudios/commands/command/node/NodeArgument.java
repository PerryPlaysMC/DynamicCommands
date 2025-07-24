package io.dynamicstudios.commands.command.node;

import io.dynamicstudios.commands.command.node.arguments.Literal;

/**
 * Creator: PerryPlaysMC
 * Created: 06/2024
 **/
public interface NodeArgument<Source> {

 NodeArgument then(NodeArgument argument);

 int executes(CommandContext<Source> context);

 static <Source> Literal<Source> literal(String name) {
	return new Literal<>(name);
 }

 static <Source> Literal<Source> required(String name) {
	return new Literal<>(name);
 }

}
