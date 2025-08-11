package io.dynamicstudios.commands.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.dynamicstudios.json.Version;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 11/25/19-2023
 * Package: me.perryplaysmc.titancells.utils
 * Path: me.perryplaysmc.titancells.utils.PlayerData
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/

@SuppressWarnings("all")
public class PlayerData implements Listener {

 private static boolean didRegister = false;

 public static void register(Plugin plugin) {
	if(didRegister) return;
	didRegister = true;
	Bukkit.getPluginManager().registerEvents(new PlayerData(), plugin);
	Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
	 for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
		PlayerData.getOfflinePlayersName().put(player.getName(), player);
		PlayerData.getOfflinePlayersUUID().put(player.getUniqueId(), player);
	 }
	 for(Player player : Bukkit.getOnlinePlayers()) {
		PlayerData.getOfflinePlayersName().remove(player.getName());
		PlayerData.getOfflinePlayersUUID().remove(player.getUniqueId());
	 }
	}, 0, 20 * 10);
 }


 @EventHandler
 void onQuit(PlayerQuitEvent e) {
	OfflinePlayer p = Bukkit.getOfflinePlayer(e.getPlayer().getName());
	PlayerData.getOfflinePlayersName().put(e.getPlayer().getName(), p);
	PlayerData.getOfflinePlayersUUID().put(e.getPlayer().getUniqueId(), p);
 }

 @EventHandler
 void onJoin(PlayerJoinEvent e) {
	PlayerData.getOfflinePlayersName().remove(e.getPlayer().getName());
	PlayerData.getOfflinePlayersUUID().remove(e.getPlayer().getUniqueId());
 }

 private static Map<UUID, OfflinePlayer> offlinePlayersUUID = new HashMap<>();
 private static Map<String, OfflinePlayer> offlinePlayersName = new HashMap<>();

 public static Map<UUID, OfflinePlayer> getOfflinePlayersUUID() {
	return offlinePlayersUUID;
 }

 public static Map<String, OfflinePlayer> getOfflinePlayersName() {
	return offlinePlayersName;
 }

 public static List<String> getAllNames() {
	Set<String> names = offlinePlayersName.keySet();
	names.addAll(Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toSet()));
	return new ArrayList<>(names);
 }

 public static List<String> getAllNames(Predicate<OfflinePlayer> test) {
	Set<String> names = offlinePlayersName.values().stream().filter(test).map(OfflinePlayer::getName).collect(Collectors.toSet());
	names.addAll(Bukkit.getOnlinePlayers().stream().filter(test).map(OfflinePlayer::getName).collect(Collectors.toSet()));
	return new ArrayList<>(names);
 }

 public static List<String> getOnlineNames(Predicate<Player> test) {
	return new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(test).map(OfflinePlayer::getName).collect(Collectors.toSet()));
 }

 public static List<String> getOfflineNames(Predicate<OfflinePlayer> test) {
	return new ArrayList<>(offlinePlayersName.values().stream().filter(test).map(OfflinePlayer::getName).collect(Collectors.toSet()));
 }

 public static List<String> getOnlineNames() {
	return new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toSet()));
 }

 public static List<String> getOfflineNames() {
	return new ArrayList<>(offlinePlayersName.values().stream().map(OfflinePlayer::getName).collect(Collectors.toSet()));
 }

 public static OfflinePlayer getOfflinePlayer(String name) {
	for(OfflinePlayer offlinePlayer : Bukkit.getOnlinePlayers()) {
	 if(offlinePlayer.getName().equalsIgnoreCase(name)) return offlinePlayer;
	}
	UUID uuid = null;
	try {
	 uuid = UUID.fromString(name);
	} catch(Exception e) {
	}
	return offlinePlayersName.getOrDefault(name, offlinePlayersUUID.get(uuid));
 }


 public static OfflinePlayer getOfflinePlayer(UUID uuid) {
	if(!offlinePlayersUUID.containsKey(uuid)) {
	 for(OfflinePlayer offlinePlayer : Stream.concat(offlinePlayersUUID.values().stream(), offlinePlayersName.values().stream()).collect(Collectors.toSet())) {
		if(offlinePlayer.getUniqueId().equals(uuid)) return offlinePlayer;
	 }
	 for(OfflinePlayer offlinePlayer : Bukkit.getOnlinePlayers()) {
		if(offlinePlayer.getUniqueId().equals(uuid)) return offlinePlayer;
	 }
	 if(Bukkit.getPlayer(uuid) != null) {
		return Bukkit.getPlayer(uuid);
	 }
	 OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
	 if(player.hasPlayedBefore()) return player;
	}
	return offlinePlayersUUID.get(uuid);
 }


 public static Player getPlayer(String name) {
	for(Player offlinePlayer : Bukkit.getOnlinePlayers()) {
	 if(offlinePlayer.getName().equalsIgnoreCase(name)) return offlinePlayer;
	}
	return null;
 }


 public static Player getPlayer(UUID uuid) {
	for(Player offlinePlayer : Bukkit.getOnlinePlayers()) {
	 if(offlinePlayer.getUniqueId().equals(uuid)) return offlinePlayer;
	}
	return null;
 }

 public static UUID getUUID(Player player) throws Exception {

	OfflinePlayer op = Bukkit.getOfflinePlayer(player.getName());
	if(op.hasPlayedBefore()) {
	 return op.getUniqueId();
	} else {
	 return getUUIDFromString(player.getName());
	}
 }

 public static UUID getUUID(String playerName) throws Exception {
	OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
	if(op.hasPlayedBefore() || op.isOnline()) {
	 return op.getUniqueId();
	} else {
	 return getUUIDFromString(playerName);
	}
 }

 public static UUID getUUIDFromString(String playerName) throws Exception {
	byte[] data = null;
	int length = getWebData("https://api.mojang.com/players/profiles/minecraft/" + playerName, "id").length();
	data = Hex.decodeHex(getWebData("https://api.mojang.com/players/profiles/minecraft/" + playerName, "id")
		 .substring(1, length - 1).toCharArray());
	return new UUID(ByteBuffer.wrap(data, 0, 8).getLong(), ByteBuffer.wrap(data, 8, 8).getLong());
 }

 public static List<String> getLocation(Player player) throws Exception {
	return getLocation(player.getAddress());
 }

 public static List<String> getLocation(InetSocketAddress inetSocketAddress) throws Exception {
	List<String> localIPs = Arrays.asList("192.168.1.1", "127.0.0.1");
	List<String> dataToGet = Arrays.asList("country", "regionName", "city", "zip");
	List<String> data;
	if(localIPs.contains(inetSocketAddress.getAddress().toString().replace("/", ""))) {
	 data = getWebData("http://ip-api.com/json/" + Bukkit.getServer().getIp().replace("/", ""), dataToGet);
	} else {
	 data = getWebData("http://ip-api.com/json/" + inetSocketAddress.getAddress().toString().replace("/", ""), dataToGet);
	}
	data.set(0, "Country: " + data.get(0));
	data.set(1, "State: " + data.get(1));
	data.set(2, "City: " + data.get(2));
	data.set(3, "Zip Code: " + data.get(3));
	return data;
 }


 public static String getName(Player player) throws Exception {
	if(player.isOnline()) {
	 return getName(player.getUniqueId());
	} else {
	 return getName(getUUID(player));
	}
 }

 public static UUID stringToUUID(String uuid) throws Exception {
	byte[] data;
	if(Version.isCurrentHigher(Version.v1_12)) {
	 data = Hex.decodeHex(uuid.replace("-", "").toCharArray());
	} else data = org.apache.commons.codec.binary.Hex.decodeHex(uuid.replace("-", "").toCharArray());
	return new UUID(ByteBuffer.wrap(data, 0, 8).getLong(), ByteBuffer.wrap(data, 8, 8).getLong());
 }

 public static String getName(UUID uuid) throws Exception {
	return getWebData("https://sessionserver.mojang.com/session/minecraft/profile/"
		 + stringToUUID(uuid.toString()).toString().replace("-", ""), "name").replace("\"", "");
 }

 public static String getSkin(UUID uuid) throws Exception {
	return getWebData("https://sessionserver.mojang.com/session/minecraft/profile/" + stringToUUID(uuid.toString()).toString().replace("-", ""), "textures");
 }


 public static String getWebData(String URL, String JSONDataToGet) throws Exception {
	java.net.URL url = null;
	JsonObject rootObj = null;
	url = new URL(URL);
	HttpURLConnection request = (HttpURLConnection) url.openConnection();
	request.connect();

	JsonParser jp = new JsonParser();
	JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
	rootObj = root.getAsJsonObject();

	return rootObj.get(JSONDataToGet).toString();//.getAsString();
 }

 static List<String> getWebData(String URL, List<String> JSONDataToGet) throws Exception {
	URL url = new URL(URL);
	HttpURLConnection request = (HttpURLConnection) url.openConnection();
	request.connect();

	JsonParser jp = new JsonParser();
	JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
	JsonObject rootObj = root.getAsJsonObject();

	List<String> dataToReturn = new ArrayList<>();

	for(String JSONData : JSONDataToGet) {
	 dataToReturn.add(rootObj.get(JSONData).getAsString());
	}
	return dataToReturn;
 }

}
