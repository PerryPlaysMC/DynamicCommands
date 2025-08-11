package io.dynamicstudios.commands.exceptions;


/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC *
 * Any attempts to use these program(s) may result in a penalty of up to $5,000 USD
 **/
public class CommandException extends Exception {
 private boolean isCustom = false;

 public CommandException(String text) {
	super(text);
 }

 public CommandException(boolean isCustom, String text) {
	super(text);
	this.isCustom = isCustom;
 }

 public boolean isCustom() {
	return isCustom;
 }

}
