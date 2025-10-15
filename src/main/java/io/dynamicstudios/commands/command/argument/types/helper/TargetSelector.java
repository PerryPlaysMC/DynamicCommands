package io.dynamicstudios.commands.command.argument.types.helper;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TargetSelector {
 private enum SelectorType {
	ALL_PLAYERS, NEAREST_PLAYER, RANDOM_PLAYER, ALL_ENTITIES, SELF, NEAREST_ENTITY
 }

 private enum SortType {
	NEAREST, FURTHEST, RANDOM, ARBITRARY
 }

 private static class Range {
	Double min;
	Double max;
	boolean negated = false;

	boolean matches(double value) {
	 double low = min != null ? min : Double.NEGATIVE_INFINITY;
	 double high = max != null ? max : Double.POSITIVE_INFINITY;
	 boolean inRange = value >= low && value <= high;
	 return negated ? !inRange : inRange;
	}
 }

 private static class StringCriterion {
	final String value;
	final boolean negated;

	StringCriterion(String value, boolean negated) {
	 this.value = value;
	 this.negated = negated;
	}
 }

 private static class NBTFilter {
	final String nbt;
	final boolean negated;

	NBTFilter(String nbt, boolean negated) {
	 this.nbt = nbt;
	 this.negated = negated;
	}
 }

 private static class AdvancementCriterion {
	final String advancement;
	final Object value; // Boolean or Map<String, Boolean>

	AdvancementCriterion(String advancement, Object value) {
	 this.advancement = advancement;
	 this.value = value;
	}
 }

 private final SelectorType type;
 private final CommandSender sender;
 private final Location origin;
 private final World world;

 // Position
 private Double posX, posY, posZ;
 private Double dx, dy, dz;
 private transient Double minX, maxX, minY, maxY, minZ, maxZ; // Computed

 // Filters
 private Range distance;
 private List<StringCriterion> gamemodes = new ArrayList<>();
 private Integer limit;
 private SortType sortType;
 private List<StringCriterion> types = new ArrayList<>();
 private List<StringCriterion> names = new ArrayList<>();
 private Map<String, Range> scores = new HashMap<>(); // Removed negated for JE
 private List<StringCriterion> tags = new ArrayList<>();
 private List<StringCriterion> teams = new ArrayList<>();
 private Range level;
 private Range xRotation;
 private Range yRotation;
 private List<AdvancementCriterion> advancements = new ArrayList<>();
 private List<StringCriterion> predicates = new ArrayList<>();
 private List<NBTFilter> nbtFilters = new ArrayList<>();

 public TargetSelector(String selectorStr, CommandSender sender, Location origin, World world) {
	this.sender = sender;
	this.origin = origin != null ? origin : new Location(world, 0, 0, 0);
	this.world = world != null ? world : Bukkit.getWorlds().get(0);
	if(!selectorStr.startsWith("@") || selectorStr.length() < 2) {
	 throw new IllegalArgumentException("Invalid selector: must start with @");
	}
	String var = selectorStr.substring(1, 2).toLowerCase();
	if("a".equals(var)) {
	 this.type = SelectorType.ALL_PLAYERS;
	} else if("p".equals(var)) {
	 this.type = SelectorType.NEAREST_PLAYER;
	} else if("r".equals(var)) {
	 this.type = SelectorType.RANDOM_PLAYER;
	} else if("e".equals(var)) {
	 this.type = SelectorType.ALL_ENTITIES;
	} else if("s".equals(var)) {
	 this.type = SelectorType.SELF;
	} else if("n".equals(var)) {
	 this.type = SelectorType.NEAREST_ENTITY;
	} else {
	 throw new IllegalArgumentException("Invalid selector variable: " + var);
	}
	int bracketStart = selectorStr.indexOf('[');
	if(bracketStart == -1) {
	 return;
	}
	int bracketEnd = selectorStr.lastIndexOf(']');
	if(bracketEnd <= bracketStart) {
	 throw new IllegalArgumentException("Mismatched brackets");
	}
	String argsStr = selectorStr.substring(bracketStart + 1, bracketEnd);
	if(argsStr.trim().isEmpty()) {
	 return;
	}
	List<String> argPairs = parseArgPairs(argsStr);
	Map<String, List<Object>> rawCriteria = new HashMap<>();
	for(String pair : argPairs) {
	 String trimmed = pair.trim();
	 if(trimmed.isEmpty()) continue;
	 int eqIndex = trimmed.indexOf('=');
	 if(eqIndex == -1) {
		throw new IllegalArgumentException("Invalid argument: " + trimmed);
	 }
	 String key = trimmed.substring(0, eqIndex).trim().toLowerCase();
	 String valStr = trimmed.substring(eqIndex + 1).trim();
	 boolean negated = valStr.startsWith("!");
	 if(negated) {
		valStr = valStr.substring(1);
	 }
	 String unquoted = unquote(valStr);
	 if(isRangeArgument(key) && negated) {
		throw new IllegalArgumentException("Negation not supported for range argument: " + key);
	 }
	 Object crit;
	 try {
		crit = parseValue(key, unquoted, negated);
	 } catch(IllegalArgumentException ex) {
		throw new IllegalArgumentException("Invalid value for " + key + ": " + valStr);
	 }
	 rawCriteria.computeIfAbsent(key, k -> new ArrayList<>()).add(crit);
	}
	// Validate duplicates and set fields
	for(Map.Entry<String, List<Object>> entry : rawCriteria.entrySet()) {
	 String key = entry.getKey();
	 List<Object> crits = entry.getValue();
	 if(crits.size() > 1) {
		boolean allNegated = false;
		if(!crits.isEmpty()) {
		 allNegated = crits.stream().allMatch(c -> {
			if(c instanceof StringCriterion) {
			 return ((StringCriterion) c).negated;
			}
			return false;
		 });
		}
		if(!"tag".equals(key) && !"predicate".equals(key) && !"nbt".equals(key) && !"type".equals(key) && !"name".equals(key) && !"team".equals(key) && !allNegated) {
		 throw new IllegalArgumentException("Duplicate equality argument: " + key);
		}
	 }
	 setCriterion(key, crits);
	}
	computeVolumeBounds();
 }

 private boolean isRangeArgument(String key) {
	return "distance".equals(key) || "level".equals(key) || "x_rotation".equals(key) || "y_rotation".equals(key);
 }

 private List<String> parseArgPairs(String argsStr) {
	List<String> pairs = new ArrayList<>();
	StringBuilder sb = new StringBuilder();
	boolean inQuote = false;
	int braceLevel = 0;
	for(int i = 0; i < argsStr.length(); i++) {
	 char c = argsStr.charAt(i);
	 if(c == '"') {
		inQuote = !inQuote;
	 } else if(c == '{') {
		braceLevel++;
	 } else if(c == '}') {
		braceLevel--;
	 } else if(c == ',' && !inQuote && braceLevel == 0) {
		pairs.add(sb.toString());
		sb = new StringBuilder();
		continue;
	 }
	 sb.append(c);
	}
	pairs.add(sb.toString());
	return pairs;
 }

 private Object parseValue(String key, String unquoted, boolean negated) {
	if("x".equals(key) || "y".equals(key) || "z".equals(key) || "dx".equals(key) || "dy".equals(key) || "dz".equals(key)) {
	 double val = Double.parseDouble(unquoted);
	 if((key.equals("x") || key.equals("y") || key.equals("z")) && unquoted.startsWith("~")) {
		throw new IllegalArgumentException("Relative coordinates (~) not supported in Java Edition selectors");
	 }
	 return val;
	} else if("distance".equals(key)) {
	 return parseRange(unquoted, Double::parseDouble);
	} else if("gamemode".equals(key)) {
	 return new StringCriterion(unquoted.toLowerCase(Locale.ROOT), negated);
	} else if("limit".equals(key)) {
	 return Integer.parseInt(unquoted);
	} else if("sort".equals(key)) {
	 String sortStr = unquoted.toUpperCase(Locale.ROOT);
	 if("NEAREST".equals(sortStr)) {
		return SortType.NEAREST;
	 } else if("FURTHEST".equals(sortStr)) {
		return SortType.FURTHEST;
	 } else if("RANDOM".equals(sortStr)) {
		return SortType.RANDOM;
	 } else if("ARBITRARY".equals(sortStr)) {
		return SortType.ARBITRARY;
	 } else {
		throw new IllegalArgumentException("Invalid sort: " + unquoted);
	 }
	} else if("type".equals(key) || "name".equals(key) || "tag".equals(key) || "team".equals(key)) {
	 return new StringCriterion(unquoted, negated);
	} else if("level".equals(key)) {
	 return parseRange(unquoted, Integer::parseInt);
	} else if("x_rotation".equals(key)) {
	 return parseRange(unquoted, Double::parseDouble);
	} else if("y_rotation".equals(key)) {
	 return parseRange(unquoted, Double::parseDouble);
	} else if("scores".equals(key)) {
	 if(!unquoted.startsWith("{") || !unquoted.endsWith("}")) {
		throw new IllegalArgumentException("Scores must be in {}");
	 }
	 String inner = unquoted.substring(1, unquoted.length() - 1);
	 List<String> subPairs = parseArgPairs(inner);
	 Map<String, Range> map = new HashMap<>();
	 for(String subPair : subPairs) {
		String subTrim = subPair.trim();
		if(subTrim.isEmpty()) continue;
		int subEq = subTrim.indexOf('=');
		if(subEq == -1) throw new IllegalArgumentException("Invalid scores subpair: " + subPair);
		String obj = subTrim.substring(0, subEq).trim();
		String oValStr = subTrim.substring(subEq + 1).trim();
		// No negation for JE
		String oUnq = unquote(oValStr);
		Range oR = parseRange(oUnq, Integer::parseInt);
		map.put(obj, oR);
	 }
	 return map;
	} else if("advancements".equals(key)) {
	 if(!unquoted.startsWith("{") || !unquoted.endsWith("}")) {
		throw new IllegalArgumentException("Advancements must be in {}");
	 }
	 String inner = unquoted.substring(1, unquoted.length() - 1);
	 List<String> subPairs = parseArgPairs(inner);
	 List<AdvancementCriterion> list = new ArrayList<>();
	 for(String subPair : subPairs) {
		String subTrim = subPair.trim();
		if(subTrim.isEmpty()) continue;
		int subEq = subTrim.indexOf('=');
		if(subEq == -1) throw new IllegalArgumentException("Invalid advancements subpair: " + subPair);
		String advKey = subTrim.substring(0, subEq).trim();
		String valS = subTrim.substring(subEq + 1).trim();
		Object valObj;
		if("true".equals(valS) || "false".equals(valS)) {
		 valObj = Boolean.valueOf(valS);
		} else if(valS.startsWith("{") && valS.endsWith("}")) {
		 String cInner = valS.substring(1, valS.length() - 1);
		 List<String> cPairs = parseArgPairs(cInner);
		 Map<String, Boolean> critMap = new HashMap<>();
		 for(String cSub : cPairs) {
			String cTrim = cSub.trim();
			if(cTrim.isEmpty()) continue;
			int cEq = cTrim.indexOf('=');
			if(cEq == -1) throw new IllegalArgumentException("Invalid criterion subpair: " + cSub);
			String cKey = cTrim.substring(0, cEq).trim();
			String cVal = cTrim.substring(cEq + 1).trim();
			critMap.put(cKey, Boolean.valueOf(cVal));
		 }
		 valObj = critMap;
		} else {
		 throw new IllegalArgumentException("Invalid advancements value: " + valS);
		}
		list.add(new AdvancementCriterion(advKey, valObj));
	 }
	 return list;
	} else if("predicate".equals(key)) {
	 return new StringCriterion(unquoted, negated);
	} else if("nbt".equals(key)) {
	 return new NBTFilter(unquoted, negated);
	} else {
	 throw new IllegalArgumentException("Unknown argument: " + key);
	}
 }

 private void setCriterion(String key, List<Object> crits) {
	if(crits.isEmpty()) return;
	Object crit = crits.get(0);
	if("x".equals(key)) {
	 posX = (Double) crit;
	} else if("y".equals(key)) {
	 posY = (Double) crit;
	} else if("z".equals(key)) {
	 posZ = (Double) crit;
	} else if("dx".equals(key)) {
	 dx = (Double) crit;
	} else if("dy".equals(key)) {
	 dy = (Double) crit;
	} else if("dz".equals(key)) {
	 dz = (Double) crit;
	} else if("distance".equals(key)) {
	 distance = (Range) crit;
	} else if("gamemode".equals(key)) {
	 for(Object c : crits) gamemodes.add((StringCriterion) c);
	} else if("limit".equals(key)) {
	 limit = (Integer) crit;
	} else if("sort".equals(key)) {
	 sortType = (SortType) crit;
	} else if("type".equals(key)) {
	 for(Object c : crits) types.add((StringCriterion) c);
	} else if("name".equals(key)) {
	 for(Object c : crits) names.add((StringCriterion) c);
	} else if("scores".equals(key)) {
	 scores = (Map<String, Range>) crit;
	} else if("tag".equals(key)) {
	 for(Object c : crits) tags.add((StringCriterion) c);
	} else if("team".equals(key)) {
	 for(Object c : crits) teams.add((StringCriterion) c);
	} else if("level".equals(key)) {
	 level = (Range) crit;
	} else if("x_rotation".equals(key)) {
	 xRotation = (Range) crit;
	} else if("y_rotation".equals(key)) {
	 yRotation = (Range) crit;
	} else if("advancements".equals(key)) {
	 for(Object c : crits) advancements.add((AdvancementCriterion) c);
	} else if("predicate".equals(key)) {
	 for(Object c : crits) predicates.add((StringCriterion) c);
	} else if("nbt".equals(key)) {
	 for(Object c : crits) nbtFilters.add((NBTFilter) c);
	}
 }

 private String unquote(String s) {
	if(s.startsWith("\"") && s.endsWith("\"")) {
	 return s.substring(1, s.length() - 1);
	}
	return s;
 }

 private Range parseRange(String v, Function<String, Number> parser) {
	Range r = new Range();
	if(v.contains("..")) {
	 String[] parts = v.split("\\.\\.", -1);
	 if(!parts[0].isEmpty()) {
		r.min = parser.apply(parts[0]).doubleValue();
	 }
	 if(parts.length > 1 && !parts[1].isEmpty()) {
		r.max = parser.apply(parts[1]).doubleValue();
	 }
	} else {
	 r.min = r.max = parser.apply(v).doubleValue();
	}
	return r;
 }

 private void computeVolumeBounds() {
	double x = posX != null ? posX : origin.getX();
	double y = posY != null ? posY : origin.getY();
	double z = posZ != null ? posZ : origin.getZ();
	double ddx = dx != null ? dx : 0.0;
	double ddy = dy != null ? dy : 0.0;
	double ddz = dz != null ? dz : 0.0;
	if(ddx == 0 && ddy == 0 && ddz == 0 && posX == null && posY == null && posZ == null) {
	 return; // No volume filter
	}
	minX = Math.min(x, x + ddx);
	maxX = Math.max(x, x + ddx);
	minY = Math.min(y, y + ddy);
	maxY = Math.max(y, y + ddy);
	minZ = Math.min(z, z + ddz);
	maxZ = Math.max(z, z + ddz);
 }

 private boolean hasVolumeFilter() {
	return minX != null;
 }

 private Location getFilterLocation() {
	double x = posX != null ? posX : origin.getX();
	double y = posY != null ? posY : origin.getY();
	double z = posZ != null ? posZ : origin.getZ();
	return new Location(world, x, y, z);
 }

 private Location getSortLocation() {
	return getFilterLocation();
 }

 private SortType getDefaultSort(SelectorType t) {
	if(t == SelectorType.NEAREST_PLAYER || t == SelectorType.NEAREST_ENTITY) {
	 return SortType.NEAREST;
	} else if(t == SelectorType.RANDOM_PLAYER) {
	 return SortType.RANDOM;
	} else {
	 return SortType.ARBITRARY;
	}
 }

 private int getDefaultLimit(SelectorType t) {
	if(t == SelectorType.ALL_PLAYERS || t == SelectorType.ALL_ENTITIES) {
	 return Integer.MAX_VALUE;
	} else {
	 return 1;
	}
 }

 public List<Entity> select() {
	List<Entity> candidates = getCandidates();
	if(candidates.isEmpty()) {
	 return Collections.emptyList();
	}
	List<Entity> filtered = new ArrayList<>();
	for(Entity e : candidates) {
	 if(matches(e)) {
		filtered.add(e);
	 }
	}
	SortType st = sortType != null ? sortType : getDefaultSort(type);
	if(st == SortType.NEAREST || st == SortType.FURTHEST) {
	 Location sortLoc = getSortLocation();
	 Collections.sort(filtered, new Comparator<Entity>() {
		@Override
		public int compare(Entity a, Entity b) {
		 double da = a.getLocation().distanceSquared(sortLoc);
		 double db = b.getLocation().distanceSquared(sortLoc);
		 if(st == SortType.NEAREST) {
			return Double.compare(da, db);
		 } else {
			return Double.compare(db, da);
		 }
		}
	 });
	} else if(st == SortType.RANDOM) {
	 Collections.shuffle(filtered, new Random());
	}
	// arbitrary: keep insertion order
	int lmt = limit != null ? limit : getDefaultLimit(type);
	if(lmt <= 0 || filtered.isEmpty()) {
	 return Collections.emptyList();
	}
	return filtered.subList(0, Math.min(lmt, filtered.size()));
 }

 private List<Entity> getCandidates() {
	switch(type) {
	 case ALL_PLAYERS:
	 case NEAREST_PLAYER:
	 case RANDOM_PLAYER:
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	 case ALL_ENTITIES:
	 case NEAREST_ENTITY:
		return new ArrayList<>(Bukkit.getWorlds().stream().flatMap(c -> c.getEntities().stream()).collect(Collectors.toList()));
	 case SELF:
		if(sender instanceof Player) {
		 return Collections.singletonList((Player) sender);
		} else {
		 return Collections.emptyList();
		}
	 default:
		return Collections.emptyList();
	}
 }

 private boolean matches(Entity e) {
	Location eloc = e.getLocation();
	// Volume filter
	if(hasVolumeFilter()) {
	 if(eloc.getX() < minX || eloc.getX() > maxX ||
			eloc.getY() < minY || eloc.getY() > maxY ||
			eloc.getZ() < minZ || eloc.getZ() > maxZ) {
		return false;
	 }
	}
	// Distance filter
	if(distance != null) {
	 double d = eloc.distance(getFilterLocation());
	 if(!distance.matches(d)) {
		return false;
	 }
	}
	// Rotation filters
	if(xRotation != null) {
	 double pitch = eloc.getPitch();
	 if(!xRotation.matches(pitch)) {
		return false;
	 }
	}
	if(yRotation != null) {
	 double yaw = eloc.getYaw();
	 if(!yRotation.matches(yaw)) {
		return false;
	 }
	}
	// Type filter
	if(!types.isEmpty()) {
	 String etName = e.getType().getKey().getKey().toLowerCase(Locale.ROOT);
	 for(StringCriterion tc : types) {
		String tval = tc.value.toLowerCase(Locale.ROOT);
		if(tc.value.startsWith("#")) {
		 // TODO: Check if type is in tag (requires registry data)
		 continue;
		}
		boolean eq = etName.equals(tval);
		if(tc.negated ? eq : !eq) {
		 return false;
		}
	 }
	}
	// Name filter
	if(!names.isEmpty()) {
	 String ename = e instanceof Player ? ((Player) e).getName() : (e.getCustomName() != null ? e.getCustomName() : "");
	 for(StringCriterion nc : names) {
		boolean eq = ename.equals(nc.value);
		if(nc.negated ? eq : !eq) {
		 return false;
		}
	 }
	}
	// Player-only filters
	boolean isPlayerOnly = !gamemodes.isEmpty() || level != null || !advancements.isEmpty();
	if(isPlayerOnly && !(e instanceof Player)) {
	 return false;
	}
	if(e instanceof Player) {
	 Player p = (Player) e;
	 // Gamemode filter
	 if(!gamemodes.isEmpty()) {
		String gmName = p.getGameMode().name().toLowerCase(Locale.ROOT);
		for(StringCriterion gc : gamemodes) {
		 boolean eq = gmName.equals(gc.value);
		 if(gc.negated ? eq : !eq) {
			return false;
		 }
		}
	 }
	 // Level filter
	 if(level != null) {
		int lvl = p.getLevel();
		if(!level.matches(lvl)) {
		 return false;
		}
	 }
	 // Advancements filter
	 if(!advancements.isEmpty()) {
		for(AdvancementCriterion ac : advancements) {
		 NamespacedKey advKey = new NamespacedKey("minecraft", ac.advancement);
		 Advancement adv = Bukkit.getAdvancement(advKey);
		 if(adv == null) {
			return false; // Invalid advancement
		 }
		 AdvancementProgress progress = p.getAdvancementProgress(adv);
		 Object val = ac.value;
		 boolean required = (Boolean) val;
		 boolean done = progress.isDone();
		 if(required != done) {
			return false;
		 }
		}
	 }
	}
	// Tags filter
	if(!tags.isEmpty()) {
	 Set<String> etags = e.getScoreboardTags();
	 for(StringCriterion tagc : tags) {
		if(tagc.value.isEmpty()) {
		 boolean hasAny = !etags.isEmpty();
		 boolean match = tagc.negated ? hasAny : !hasAny;
		 if(!match) return false;
		} else {
		 boolean has = etags.contains(tagc.value);
		 if(tagc.negated ? has : !has) {
			return false;
		 }
		}
	 }
	}
	// Teams filter
	if(!teams.isEmpty()) {
	 Scoreboard sb = e instanceof Player ? ((Player) e).getScoreboard() : Bukkit.getScoreboardManager().getMainScoreboard();
	 String entry = e instanceof Player ? ((Player) e).getName() : e.getUniqueId().toString();
	 boolean hasAnyTeam = false;
	 for(Team team : sb.getTeams()) {
		if(team.hasEntry(entry)) {
		 hasAnyTeam = true;
		 break;
		}
	 }
	 for(StringCriterion tc : teams) {
		if(tc.value.isEmpty()) {
		 boolean match = tc.negated ? hasAnyTeam : !hasAnyTeam;
		 if(!match) return false;
		} else {
		 Team tm = sb.getTeam(tc.value);
		 boolean has = tm != null && tm.hasEntry(entry);
		 if(tc.negated ? has : !has) {
			return false;
		 }
		}
	 }
	}
	// Scores filter
	if(!scores.isEmpty()) {
	 Scoreboard sb = e instanceof Player ? ((Player) e).getScoreboard() : Bukkit.getScoreboardManager().getMainScoreboard();
	 String entry = e instanceof Player ? ((Player) e).getName() : e.getUniqueId().toString();
	 for(Map.Entry<String, Range> scEntry : scores.entrySet()) {
		String objName = scEntry.getKey();
		Objective obj = sb.getObjective(objName);
		if(obj == null) {
		 return false;
		}
		Score score = obj.getScore(entry);
		int scoreVal = score.getScore();
		Range crit = scEntry.getValue();
		if(!crit.matches(scoreVal)) {
		 return false;
		}
	 }
	}
	// Predicate filter
	if(!predicates.isEmpty()) {
	 // TODO: Evaluate predicates on entity (requires NMS or custom implementation)
	 // For now, assume all match
	}
	// NBT filter
	if(!nbtFilters.isEmpty()) {
	 // TODO: Check if entity NBT matches the specified compound (requires NMS)
	 // For now, assume all match
	}
	return true;
 }

 // Tab completion: basic support for suggesting selectors and argument names/values
 public static List<String> getTabCompletions(String input) {
	List<String> suggestions = new ArrayList<>();
	if(input == null || input.trim().isEmpty()) {
	 suggestions.addAll(Arrays.asList("@a", "@p", "@r", "@e", "@s", "@n"));
	 return suggestions;
	}
	input = input.trim();
	if(!input.startsWith("@")) {
	 return Collections.emptyList();
	}
	if(!input.contains("[")) {
	 String partial = input.substring(1).toLowerCase(Locale.ROOT);
	 if(partial.length() < 2) {
		suggestions.addAll(Arrays.asList("@a", "@p", "@r", "@e", "@s", "@n"));
	 } else {
		suggestions.add(input + "[");
	 }
	 return suggestions;
	}
	int bracketIdx = input.indexOf('[');
	String after = input.substring(bracketIdx + 1);
	if(after.trim().isEmpty() || after.endsWith(",") || after.endsWith("[")) {
	 suggestions.addAll(Arrays.asList(
			"x=", "y=", "z=", "dx=", "dy=", "dz=", "distance=", "gamemode=", "limit=",
			"sort=", "type=", "name=", "scores={", "tag=", "team=", "level=", "x_rotation=",
			"y_rotation=", "advancements={", "predicate=", "nbt={"
	 ));
	 return suggestions;
	}
	int lastEq = after.lastIndexOf('=');
	if(lastEq != -1) {
	 String lastKeyPart = after.substring(0, lastEq);
	 int lastComma = lastKeyPart.lastIndexOf(',');
	 String lastKey = lastKeyPart.substring(lastComma + 1).trim().toLowerCase(Locale.ROOT);
	 String partialVal = after.substring(lastEq + 1).trim();
	 if("gamemode".equals(lastKey)) {
		List<String> gms = new ArrayList<>();
		for(GameMode gm : GameMode.values()) {
		 String s = gm.name().toLowerCase(Locale.ROOT);
		 if(s.startsWith(partialVal.toLowerCase(Locale.ROOT))) {
			gms.add(s);
		 }
		 String negS = "!" + s;
		 if(negS.startsWith(partialVal)) {
			gms.add(negS);
		 }
		}
		suggestions.addAll(gms);
	 } else if("sort".equals(lastKey)) {
		for(SortType st : SortType.values()) {
		 String s = st.name().toLowerCase(Locale.ROOT);
		 if(s.startsWith(partialVal.toLowerCase(Locale.ROOT))) {
			suggestions.add(s);
		 }
		}
	 } else if("limit".equals(lastKey)) {
		for(int i = 1; i <= 10; i++) {
		 String numStr = Integer.toString(i);
		 if(numStr.startsWith(partialVal)) {
			suggestions.add(numStr);
		 }
		}
	 } else if("type".equals(lastKey)) {
		List<String> names = new ArrayList<>();
		for(EntityType value : EntityType.values()) {
		 String name = value.getName();
		 if(name==null)continue;
		 names.add(name);
		}
		for(String ct : names) {
		 if(ct.toLowerCase().startsWith(partialVal.toLowerCase())) {
			suggestions.add(ct);
		 }
		}
	 } else if("distance".equals(lastKey)) {
		List<String> ranges = Arrays.asList("..10", "10..", "5", "10");
		for(String r : ranges) {
		 if(r.startsWith(partialVal)) {
			suggestions.add(r);
		 }
		}
	 } else if("level".equals(lastKey)) {
		for(int i = 0; i <= 30; i++) {
		 String numStr = Integer.toString(i);
		 if(numStr.startsWith(partialVal)) {
			suggestions.add(numStr);
		 }
		}
	 } else if("scores".equals(lastKey)) {
		Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();
		for(Objective obj : main.getObjectives()) {
		 String s = obj.getName();
		 if(s.startsWith(partialVal)) {
			suggestions.add(s + "=");
		 }
		}
	 }
	 // Add more for other args if needed
	}
	return suggestions;
 }
}