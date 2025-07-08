package io.dynamicstudios.commands.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.dynamicstudios.json.Version;
import io.dynamicstudios.reflectionutils.DynamicClass;
import io.dynamicstudios.reflectionutils.DynamicField;
import io.dynamicstudios.reflectionutils.DynamicLookup;
import io.dynamicstudios.reflectionutils.DynamicMethod;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MinecraftArgumentTypes {
	private MinecraftArgumentTypes() {}

	// MinecraftKey constructor
	private static final Constructor<?> MINECRAFT_KEY_CONSTRUCTOR;

	// ArgumentRegistry#getByKey (obfuscated) method
	private static final Method ARGUMENT_REGISTRY_GET_BY_KEY_METHOD;

	// ArgumentRegistry.Entry#clazz (obfuscated) field
	private static final Field ARGUMENT_REGISTRY_ENTRY_CLASS_FIELD;

	private static final Map<Object, Class<?>> ARGUMENT_LOOKUP = new HashMap<>();

	private static final Map<NamespacedKey, Class<?>> NAMED_ARGUMENT_LOOKUP = new HashMap<>();
	private static final Map<Class<?>, Method> METHOD_LOOKUP = new HashMap<>();
	private static final Map<Class<?>, Method> _PARAM_METHOD_LOOKUP = new HashMap<>();
	private static final Map<Class<?>, Method> _2PARAM_METHOD_LOOKUP = new HashMap<>();

	private static final Object ARGUMENT_CLASS;

	static {
		Method argumentRegistryGetByKeyMethod = null;
		Field argumentRegistryEntryClassField = null;
		Constructor<?> keyConstructor = null;
		Object argumentClass = null;
		try {

			DynamicClass.addPlaceholder("{version}", Version.currentExact().getVersion());
			Class<?> minecraftKey = DynamicClass.lookup(
				"net.minecraft.server.ResourceLocation",
				"net.minecraft.resources.ResourceLocation",
				"net.minecraft.resource.ResourceLocation",
				"net.minecraft.server.MinecraftKey",
				"net.minecraft.resources.MinecraftKey",
				"net.minecraft.server.{version}.MinecraftKey",
				"net.minecraft.server.{version}.ResourceLocation"
			).getJavaClass();
			for(Constructor<?> constructor : Stream.concat(Arrays.stream(minecraftKey.getDeclaredConstructors()), Arrays.stream(minecraftKey.getConstructors())).collect(Collectors.toList())) {
				if(constructor.getParameterCount() != 2) continue;
				if(constructor.getParameterTypes()[0].getName().equalsIgnoreCase("java.lang.String") && constructor.getParameterTypes()[1].getName().equalsIgnoreCase("java.lang.String")) {
					keyConstructor = constructor;
					keyConstructor.setAccessible(true);
					break;
				}
			}

			DynamicClass<?> argumentRegistry = DynamicClass.lookup(
				"net.minecraft.server.ArgumentRegistry", "net.minecraft.commands.synchronization.ArgumentTypeInfos",
				"net.minecraft.server.{version}.ArgumentTypeInfos", "net.minecraft.server.{version}.ArgumentRegistry"
			);

			DynamicClass<?> argumentInfo = DynamicClass.lookup(
				"net.minecraft.server.ArgumentInfo", "net.minecraft.commands.synchronization.ArgumentTypeInfo",
				"net.minecraft.server.{version}.ArgumentInfo", "net.minecraft.server.{version}.ArgumentTypeInfo"
			);
			DynamicClass<?> registries = DynamicClass.lookup(
				"net.minecraft.server.BuiltInRegistries",
				"net.minecraft.core.BuiltInRegistries",
				"net.minecraft.core.registries.BuiltInRegistries",
				"net.minecraft.core.Registry",
				"net.minecraft.server.{version}.Registry",
				"net.minecraft.server.{version}.Registries",
				"net.minecraft.server.{version}.BuiltInRegistries"
			);
			DynamicField registry = null;
			for(DynamicField allField : registries.getAllFields()) {
				if(allField.getGenericType().toString().replace(" ", "").contains((argumentInfo.getName() + "<?,?>").replace(" ", ""))) {
					registry = allField;
					break;
				}
			}
			if(registry != null) {
				DynamicClass<?> registryClass = new DynamicClass<>(registry.getType());
//				Registry;
				//a(IRegistry<ArgumentTypeInfo<?, ?>> var0, String var1, Class<? extends A> var2, ArgumentTypeInfo<A, T> var3) {
				{
					DynamicMethod<?> m = argumentRegistry.getMethod(DynamicLookup.methodLookupParams(new Class[]{
						 registryClass.getJavaClass(), String.class, Class.class, argumentInfo.getJavaClass()
					}).isStatic());
					DynamicClass c = DynamicClass.lookup("net.minecraft.commands.synchronization.SingletonArgumentInfo");
					DynamicMethod x = c.getMethod(DynamicLookup.methodLookupParams(new Class[] {
						 Supplier.class
					}));
//					m.get(null, registry.get(null), "test", TestArgument.class, x.get(null, (Supplier) () -> TestArgument.test()));
				}
				DynamicMethod<?> getMethod = registryClass.lookupMethods(DynamicLookup.methodLookupType(Object.class.getName())
						.parameters(new Class<?>[]{minecraftKey}))
					.stream()
					.filter(m->m.getParameters().size() == 1)
					.filter(m->m.getGenericType().toString().equalsIgnoreCase("T"))
					.findFirst().orElse(null)
					;
				if(getMethod != null) argumentRegistryGetByKeyMethod = getMethod.getMethod();
				argumentClass = registry.get(null).orElse(null);
				argumentRegistryEntryClassField = argumentRegistry.getField(DynamicLookup.fieldLookupType(Map.class.getName())).getField();
				argumentRegistryEntryClassField.setAccessible(true);
				Map<?,?> data = (Map<?, ?>) argumentRegistryEntryClassField.get(null);
				for(Object o : data.keySet()) {
					ARGUMENT_LOOKUP.put(data.get(o), (Class<?>) o);
				}
			}

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		ARGUMENT_REGISTRY_GET_BY_KEY_METHOD = argumentRegistryGetByKeyMethod;
		ARGUMENT_REGISTRY_ENTRY_CLASS_FIELD = argumentRegistryEntryClassField;
		ARGUMENT_CLASS = argumentClass;
		MINECRAFT_KEY_CONSTRUCTOR = keyConstructor;
		if (MINECRAFT_KEY_CONSTRUCTOR == null || (ARGUMENT_REGISTRY_GET_BY_KEY_METHOD == null) && ARGUMENT_CLASS == null) {
			throw new RuntimeException("Failed to initialize MinecraftArgumentTypes");
		}
	}

	public static class TestArgument implements ArgumentType<Boolean> {
		private static final Collection<String> EXAMPLES = Arrays.asList("true", "false");

		private TestArgument() {
		}

		public static TestArgument test() {
			return new TestArgument();
		}

		public Boolean parse(StringReader reader) throws CommandSyntaxException {
			return reader.readBoolean();
		}

		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			if ("true".startsWith(builder.getRemainingLowerCase())) {
				builder.suggest("true");
			}
			if ("yes".startsWith(builder.getRemainingLowerCase())) {
				builder.suggest("yes");
			}

			if ("false".startsWith(builder.getRemainingLowerCase())) {
				builder.suggest("false");
			}
			if ("no".startsWith(builder.getRemainingLowerCase())) {
				builder.suggest("no");
			}

			return builder.buildFuture();
		}

		public Collection<String> getExamples() {
			return EXAMPLES;
		}
	}

	/**
	 * Gets a registered argument type class by key.
	 *
	 * @param key the key
	 * @return the returned argument type class
	 * @throws IllegalArgumentException if no such argument is registered
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends ArgumentType<?>> getClassByKey(NamespacedKey key) throws IllegalArgumentException {
		try {
			if(NAMED_ARGUMENT_LOOKUP.containsKey(key)) {
				return (Class<? extends ArgumentType<?>>) NAMED_ARGUMENT_LOOKUP.get(key);
			}
			Object minecraftKey = MINECRAFT_KEY_CONSTRUCTOR.newInstance(key.getNamespace(), key.getKey());
			Object entry = ARGUMENT_REGISTRY_GET_BY_KEY_METHOD.invoke(ARGUMENT_CLASS, minecraftKey);
			if (entry == null) {
				throw new IllegalArgumentException(key.toString());
			}
			NAMED_ARGUMENT_LOOKUP.put(key, ARGUMENT_LOOKUP.get(entry));
			return (Class<? extends ArgumentType<?>>) ARGUMENT_LOOKUP.get(entry);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets a registered argument type by key.
	 *
	 * @param key the key
	 * @return the returned argument
	 * @throws IllegalArgumentException if no such argument is registered
	 */
	public static ArgumentType<?> getByKey(NamespacedKey key) throws IllegalArgumentException {
		try {
			final Constructor<? extends ArgumentType<?>> constructor = getClassByKey(key).getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets a registered argument type by key.
	 *
	 * @param key the key
	 * @return the returned argument
	 * @throws IllegalArgumentException if no such argument is registered
	 */
	public static ArgumentType<?> getByKeyMethod(NamespacedKey key) throws IllegalArgumentException {
		try {
			Class<? extends ArgumentType<?>> clazz = getClassByKey(key);
			Method m = null;
			for(Method declaredMethod : clazz.getDeclaredMethods()) {
				if(Modifier.isStatic(declaredMethod.getModifiers()) && declaredMethod.getParameterCount() == 0) {
					if(!clazz.getName().equalsIgnoreCase(declaredMethod.getReturnType().getName()) &&
						!clazz.getSuperclass().getName().equalsIgnoreCase(declaredMethod.getReturnType().getName())) {
						continue;
					}
					m = declaredMethod;
					m.setAccessible(true);
					break;
				}
			}
			return (ArgumentType<?>) m.invoke(null);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets a registered argument type by key.
	 *
	 * @param key the key
	 * @return the returned argument
	 * @throws IllegalArgumentException if no such argument is registered
	 */
	public static <T extends Number> ArgumentType getByKeyMethodRange(NamespacedKey key, T min, T max) throws IllegalArgumentException {
		try {
			Class<? extends ArgumentType<?>> clazz = getClassByKey(key);
			if(_2PARAM_METHOD_LOOKUP.containsKey(clazz)) {
				return (ArgumentType) _2PARAM_METHOD_LOOKUP.get(clazz).invoke(null, min, max);
			}
			if(METHOD_LOOKUP.containsKey(clazz)) {
				return (ArgumentType) METHOD_LOOKUP.get(clazz).invoke(null, min, max);
			}
			Method m = null;
			for(Method declaredMethod : clazz.getDeclaredMethods()) {
				if(Modifier.isStatic(declaredMethod.getModifiers()) && declaredMethod.getParameterCount() == 2) {
					if(!clazz.getName().equalsIgnoreCase(declaredMethod.getReturnType().getName()) &&
						!clazz.getSuperclass().getName().equalsIgnoreCase(declaredMethod.getReturnType().getName())) {
						continue;
					}
					m = declaredMethod;
					m.setAccessible(true);
					break;
				}
			}
			_2PARAM_METHOD_LOOKUP.put(clazz, m);
			return (ArgumentType) m.invoke(null, min, max);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	static void ensureSetup() {
		// do nothing - this is only called to trigger the static initializer
	}
}