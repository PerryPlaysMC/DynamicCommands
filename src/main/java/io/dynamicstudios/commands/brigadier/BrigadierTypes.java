package io.dynamicstudios.commands.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import io.dynamicstudios.json.Version;
import io.dynamicstudios.reflectionutils.DynamicClass;
import io.dynamicstudios.reflectionutils.DynamicLookup;
import org.bukkit.NamespacedKey;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BrigadierTypes {

	public static final ArgumentType BOOL;

	public static final ArgumentType FLOAT;
	public static final ArgumentType DOUBLE;
	public static final ArgumentType INTEGER;
	public static final ArgumentType LONG;


	public static final BiFunction<Float,Float,ArgumentType> RANGE2_FLOAT = (min, max) ->  MinecraftArgumentTypes.getByKeyMethodRange(NamespacedKey.fromString("brigadier:float"), min, max);
	public static final BiFunction<Double,Double,ArgumentType> RANGE2_DOUBLE = (min, max) ->  MinecraftArgumentTypes.getByKeyMethodRange(NamespacedKey.fromString("brigadier:double"), min, max);
	public static final BiFunction<Integer,Integer,ArgumentType> RANGE2_INTEGER = (min, max) ->  MinecraftArgumentTypes.getByKeyMethodRange(NamespacedKey.fromString("brigadier:integer"), min, max);
	public static final BiFunction<Long,Long,ArgumentType> RANGE2_LONG = (min, max) ->  MinecraftArgumentTypes.getByKeyMethodRange(NamespacedKey.fromString("brigadier:long"), min, max);

	public static final Function<Float,ArgumentType> RANGE_FLOAT = (max) ->  RANGE2_FLOAT.apply(-Float.MAX_VALUE,max);
	public static final Function<Double,ArgumentType> RANGE_DOUBLE = (max) ->  RANGE2_DOUBLE.apply(-Double.MAX_VALUE,max);
	public static final Function<Integer,ArgumentType> RANGE_INTEGER = (max) ->  RANGE2_INTEGER.apply(-Integer.MAX_VALUE,max);
	public static final Function<Long,ArgumentType> RANGE_LONG = (max) ->  RANGE2_LONG.apply(-Long.MAX_VALUE,max);

	public static final ArgumentType ENTITY;
	public static final ArgumentType GAME_PROFILE;
	public static final ArgumentType BLOCK_POS;
	public static final ArgumentType COLUMN_POS;
	public static final ArgumentType LOCATION;
	public static final ArgumentType LOCATION_2D;
	public static final ArgumentType BLOCK_STATE;
	public static final ArgumentType BLOCK_PREDICATE;
	public static final ArgumentType ITEM_STACK;
	public static final ArgumentType ITEM_PREDICATE;
	public static final ArgumentType COLOR;
	public static final ArgumentType COMPONENT;
	public static final ArgumentType STYLE;
	public static final ArgumentType MESSAGE;
	public static final ArgumentType NBT_COMPOUND_TAG;
	public static final ArgumentType NBT_TAG;
	public static final ArgumentType NBT_PATH;
	public static final ArgumentType OBJECTIVE;
	public static final ArgumentType OBJECTIVE_CRITERIA;
	public static final ArgumentType OPERATION;
	public static final ArgumentType PARTICLE;
	public static final ArgumentType ANGLE;
	public static final ArgumentType ROTATION;
	public static final ArgumentType SCOREBOARD_SLOT;
	public static final ArgumentType SCORE_HOLDER;
	public static final ArgumentType SWIZZLE;
	public static final ArgumentType TEAM;
	public static final ArgumentType ITEM_SLOT;
	public static final ArgumentType ITEM_SLOTS;
	public static final ArgumentType RESOURCE_LOCATION;
	public static final ArgumentType FUNCTION;
	public static final ArgumentType ENTITY_ANCHOR;
	public static final ArgumentType INT_RANGE;
	public static final ArgumentType FLOAT_RANGE;
	public static final ArgumentType DIMENSION;
	public static final ArgumentType GAMEMODE;
	public static final ArgumentType TIME;
	public static final ArgumentType RESOURCE_OR_TAG;
	public static final ArgumentType RESOURCE_OR_TAG_KEY;
	public static final ArgumentType RESOURCE;
	public static final ArgumentType RESOURCE_KEY;
	public static final ArgumentType TEMPLATE_MIRROR;
	public static final ArgumentType TEMPLATE_ROTATION;
	public static final ArgumentType HEIGHTMAP;
	public static final ArgumentType LOOT_TABLE;
	public static final ArgumentType LOOT_PREDICATE;
	public static final ArgumentType LOOT_MODIFIER;

	static {
		BLOCK_STATE = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("block_state"));
		BLOCK_PREDICATE = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("block_predicate"));
		ITEM_STACK = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("item_stack"));
		ITEM_PREDICATE = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("item_predicate"));
		STYLE = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("style"));
		LOOT_MODIFIER = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("loot_modifier"));
		COMPONENT = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("component"));
		PARTICLE = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("particle"));
		RESOURCE_OR_TAG = null; //MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("resource_or_tag"));
		RESOURCE_OR_TAG_KEY = null; //MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("resource_or_tag_key"));
		RESOURCE = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("resource"));
		RESOURCE_KEY = null;//MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("resource_key"));
		LOOT_TABLE = null; //MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("loot_table"));
		LOOT_PREDICATE = null; //MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("loot_predicate"));

		BOOL = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.fromString("brigadier:bool"));
		FLOAT = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.fromString("brigadier:float"));
		DOUBLE = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.fromString("brigadier:double"));
		INTEGER = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.fromString("brigadier:integer"));
		LONG = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.fromString("brigadier:long"));
		ENTITY = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("entity"));
		GAME_PROFILE = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("game_profile"));
		BLOCK_POS = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("block_pos"));
		COLUMN_POS = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("column_pos"));
		LOCATION = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("vec3"));
		LOCATION_2D = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("vec2"));
		COLOR = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("color"));
		MESSAGE = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("message"));
		NBT_COMPOUND_TAG = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("nbt_compound_tag"));
		NBT_TAG = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("nbt_tag"));
		NBT_PATH = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("nbt_path"));
		OBJECTIVE = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("objective"));
		OBJECTIVE_CRITERIA = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("objective_criteria"));
		OPERATION = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("operation"));
		ANGLE = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("angle"));
		ROTATION = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("rotation"));
		SCOREBOARD_SLOT = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("scoreboard_slot"));
		SCORE_HOLDER = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("score_holder"));
		SWIZZLE = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("swizzle"));
		TEAM = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("team"));
		ITEM_SLOT = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("item_slot"));
		ITEM_SLOTS = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("item_slots"));
		RESOURCE_LOCATION = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("resource_location"));
		FUNCTION = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("function"));
		ENTITY_ANCHOR = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("entity_anchor"));
		INT_RANGE = MinecraftArgumentTypes.getByKey(NamespacedKey.minecraft("int_range"));
		FLOAT_RANGE = MinecraftArgumentTypes.getByKey(NamespacedKey.minecraft("float_range"));
		DIMENSION = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("dimension"));
		GAMEMODE = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("gamemode"));
		TIME = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("time"));
		TEMPLATE_MIRROR = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("template_mirror"));
		TEMPLATE_ROTATION = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("template_rotation"));
		HEIGHTMAP = MinecraftArgumentTypes.getByKeyMethod(NamespacedKey.minecraft("heightmap"));
	}

	public static class String {
		private static final DynamicClass<?> ARGUMENT_STRING;

		public static final ArgumentType<java.lang.String> WORD;
		public static final ArgumentType<java.lang.String> GREEDY;
		public static final ArgumentType<java.lang.String> STRING;

		static {
			DynamicClass.addPlaceholder("{version}", Version.currentExact().getVersion());
			ARGUMENT_STRING = DynamicClass.lookup("net.minecraft.commands.arguments.StringArgumentType",
				 "com.mojang.brigadier.arguments.StringArgumentType",
				 "net.minecraft.server.{version}.StringArgumentType");
			if (ARGUMENT_STRING != null) {
				WORD = (ArgumentType) ARGUMENT_STRING
					 .getMethod(DynamicLookup.methodLookupType(ARGUMENT_STRING.getName()).exactName("word").isStatic()).get(null).orElse(null);
				GREEDY = (ArgumentType) ARGUMENT_STRING
					 .getMethod(DynamicLookup.methodLookupType(ARGUMENT_STRING.getName()).exactName("greedyString").isStatic()).get(null).orElse(null);
				STRING = (ArgumentType) ARGUMENT_STRING
					 .getMethod(DynamicLookup.methodLookupType(ARGUMENT_STRING.getName()).exactName("string").isStatic()).get(null).orElse(null);
			} else {
				WORD = null;
				GREEDY = null;
				STRING = null;
			}
		}

	}


}
