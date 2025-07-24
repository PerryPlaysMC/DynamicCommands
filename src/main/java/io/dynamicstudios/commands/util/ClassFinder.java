package io.dynamicstudios.commands.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public class ClassFinder {

 public static File findPluginFile(JavaPlugin plugin) {
	File file;
	try {
	 Method method = JavaPlugin.class.getDeclaredMethod("getFile");
	 boolean access = method.isAccessible();
	 method.setAccessible(true);
	 file = (File) method.invoke(plugin);
	 method.setAccessible(access);
	} catch(Exception e) {
	 System.out.println("An error occurred while getting the jar file of plugin: " + plugin.getName());
	 e.printStackTrace();
	 return null;
	}


	return file;
 }

 public static <T> Set<Class<? extends T>> classesOf(JavaPlugin plugin, Class<T> castTo, String packageName) {
	return classesOf(findPluginFile(plugin), castTo, packageName);
 }

 public static <T> Set<Class<? extends T>> classesOf(File jarFile, Class<T> castTo, String packageName) {
	Set<Class<? extends T>> classes = new HashSet<>();
	List<String> names = new ArrayList<>();
	String pkg = (packageName.length() > 0 ? (packageName.endsWith(".") ? packageName : packageName + ".") : "");
	String pkgR = pkg.replace(".", "\\.");
	try {
	 JarFile file = new JarFile(jarFile);
	 for(Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements(); ) {
		JarEntry jarEntry = entry.nextElement();
		String name = jarEntry.getName().replace("/", ".");
		if(name.startsWith(pkg)
			 && !name.contains("$") && name.endsWith(".class")
			 && (pkg.isEmpty() || !names.contains(name.split(pkgR)[1].replace(".class", "")))) {
		 String newName = (!pkg.isEmpty() ? name.split(pkgR)[1] : name).replace(".class", "");
		 try {
			Class<?> s = Class.forName((pkg) + newName);
			List<String> nameClasses = new ArrayList<>();
			for(Class<?> c : s.getInterfaces())
			 if(c != null) nameClasses.add(c.getSimpleName());
			if(s.getSuperclass() != null) nameClasses.add(s.getSuperclass().getSimpleName());
			if(nameClasses.contains(castTo.getSimpleName()))
			 names.add((!pkg.isEmpty() ? name.split(pkgR)[1] : name).replace(".class", ""));
		 } catch(Exception ignored) {
		 }
		}
	 }
	 Collections.sort(names);
	 for(String name : names) {
		try {
		 Class<?> clazz = Class.forName(pkg + name);
		 classes.add(clazz.asSubclass(castTo));
		} catch(Exception e) {
		 System.out.println("Error " + ((pkg) + name) + "\nIs not an instanceof " + castTo.getSimpleName());
		}
	 }
	 file.close();
	} catch(Exception e) {
	 e.printStackTrace();
	 System.out.println("Error");
	}
	return classes;
 }
}
