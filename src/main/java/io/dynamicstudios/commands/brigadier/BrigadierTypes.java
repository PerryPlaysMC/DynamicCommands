package io.dynamicstudios.commands.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import io.dynamicstudios.json.Version;
import io.dynamicstudios.reflectionutils.DynamicClass;
import io.dynamicstudios.reflectionutils.DynamicLookup;

public class BrigadierTypes {

	private static final DynamicClass<?> ARGUMENT_VEC_3;

	public static final ArgumentType LOCATION;

	static {
		DynamicClass.addPlaceholder("{version}", Version.currentExact().getVersion());
		 ARGUMENT_VEC_3 = DynamicClass.lookup("net.minecraft.commands.arguments.coordinates.ArgumentVec3", "net.minecraft.server.{version}.ArgumentVec3");
		 if(ARGUMENT_VEC_3 != null) {
			 LOCATION = (ArgumentType) ARGUMENT_VEC_3
					.getMethod(DynamicLookup.methodLookupType(ARGUMENT_VEC_3.getName())).get(null).orElse(null);
		 }else {
			 LOCATION = null;
		 }
	}



}
