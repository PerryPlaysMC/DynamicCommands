package io.dynamicstudios.commands.exceptions;

import dev.dynamicstudios.json.data.util.CColor;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC *
 * Any attempts to use these program(s) may result in a penalty of up to $5,000 USD
 **/
public class CommandException extends Exception {

  public CommandException(String text) {
    super(CColor.translateCommon(text));
  }

}
