package io.dynamicstudios.commands.command.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Creator: PerryPlaysMC
 * Created: 08/2022
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Argument {

	String name();

	String forArg() default "";

	String[] aliases() default "";

	String description() default "";

	String usage() default "";

	String permission() default "";

	boolean playerOnly() default false;

}
