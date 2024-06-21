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
@Target({ElementType.TYPE})
public @interface Command {

  String name();

  String[] aliases() default "";

  String description() default "";

  String usage() default "";

  String permission() default "";

  String permissionMessage() default "";

  boolean playerOnly() default false;

  boolean consoleOnly() default false;


}
