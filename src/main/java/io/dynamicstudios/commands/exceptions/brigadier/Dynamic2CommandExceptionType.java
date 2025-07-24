package io.dynamicstudios.commands.exceptions.brigadier;


public class Dynamic2CommandExceptionType implements CommandExceptionType {
 private final Function function;

 public Dynamic2CommandExceptionType(Function function) {
	this.function = function;
 }

 public CommandSyntaxException create(Object a, Object b) {
	return new CommandSyntaxException(this, this.function.apply(a, b));
 }

 public interface Function {
	Message apply(Object var1, Object var2);
 }
}
