package io.dynamicstudios.commands.command.node.arguments;

import io.dynamicstudios.commands.command.node.CommandContext;
import io.dynamicstudios.commands.command.node.NodeArgument;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 06/2024
 **/
public class Literal<Source> implements NodeArgument<Source> {


 private final String name;

 private final List<NodeArgument> arguments;

 public Literal(String name) {
	this.name = name;
	this.arguments = new ArrayList<>();
 }


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
