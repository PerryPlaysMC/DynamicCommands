package io.dynamicstudios.commands.util;

import io.dynamicstudios.commands.exceptions.CommandException;

/**
 * Creator: PerryPlaysMC
 * Created: 06/2024
 **/
public interface ParserFunction<T> {

	T parse(String input) throws CommandException;


}
