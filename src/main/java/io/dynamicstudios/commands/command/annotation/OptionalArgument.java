package io.dynamicstudios.commands.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Creator: PerryPlaysMC
 * Created: 06/2024
 **/


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface OptionalArgument {


}
