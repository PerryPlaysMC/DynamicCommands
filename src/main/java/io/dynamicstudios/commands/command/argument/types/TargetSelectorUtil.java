package io.dynamicstudios.commands.command.argument.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TargetSelectorUtil {
    // Parse a target selector string and return matching entities
    public static List<Entity> parseSelector(CommandSender sender, String selector, Location origin) throws IllegalArgumentException {
        // Parse selector: @<variable>[<arguments>]
        Pattern pattern = Pattern.compile("^@([praesv])(?:\\[(.*?)\\])?$");
        Matcher matcher = pattern.matcher(selector);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid selector: " + selector);
        }

        String variable = matcher.group(1);
        String argsString = matcher.group(2) != null ? matcher.group(2) : "";
        Map<String, String> args = parseArguments(argsString);

        // Validate sender for @s
        if (variable.equals("s") && !(sender instanceof Entity)) {
            throw new IllegalArgumentException("@s requires an entity sender");
        }

        // Apply arguments
        double x = args.containsKey("x") ? parseDouble(args.get("x"), origin != null ? origin.getX() : 0) : origin != null ? origin.getX() : 0;
        double y = args.containsKey("y") ? parseDouble(args.get("y"), origin != null ? origin.getY() : 0) : origin != null ? origin.getY() : 0;
        double z = args.containsKey("z") ? parseDouble(args.get("z"), origin != null ? origin.getZ() : 0) : origin != null ? origin.getZ() : 0;
        Location searchOrigin = new Location(origin != null ? origin.getWorld() : Bukkit.getWorlds().get(0), x, y, z);

        double dx = args.containsKey("dx") ? parseDouble(args.get("dx"), 0) : 0;
        double dy = args.containsKey("dy") ? parseDouble(args.get("dy"), 0) : 0;
        double dz = args.containsKey("dz") ? parseDouble(args.get("dz"), 0) : 0;
        BoundingBox aabb = dx != 0 || dy != 0 || dz != 0 ? BoundingBox.of(searchOrigin, searchOrigin.clone().add(dx, dy, dz)) : null;

        String distance = args.getOrDefault("distance", args.getOrDefault("r", ""));
        String distanceMin = args.getOrDefault("rm", "");
        Range distanceRange = parseRange(distance, distanceMin);

        String xRotation = args.getOrDefault("x_rotation", args.getOrDefault("rx", ""));
        String xRotationMin = args.getOrDefault("rxm", "");
        Range xRotationRange = parseRange(xRotation, xRotationMin);

        String yRotation = args.getOrDefault("y_rotation", args.getOrDefault("ry", ""));
        String yRotationMin = args.getOrDefault("rym", "");
        Range yRotationRange = parseRange(yRotation, yRotationMin);

        String level = args.getOrDefault("level", args.getOrDefault("l", ""));
        String levelMin = args.getOrDefault("lm", "");
        Range levelRange = parseRange(level, levelMin);

        String gamemode = args.get("gamemode");
        String notGamemode = args.get("m") != null ? args.get("m").startsWith("!") ? args.get("m").substring(1) : null : null;
        if (gamemode != null && gamemode.startsWith("!")) {
            notGamemode = gamemode.substring(1);
            gamemode = null;
        }

        String name = args.get("name");
        boolean notName = name != null && name.startsWith("!");
        if (notName) name = name.substring(1);

        String type = args.get("type");
        boolean notType = type != null && type.startsWith("!");
        if (notType) type = type.substring(1);

        List<String> tags = args.entrySet().stream()
                .filter(e -> e.getKey().startsWith("tag"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        String team = args.get("team");
        boolean notTeam = team != null && team.startsWith("!");
        if (notTeam) team = team.substring(1);

        String scores = args.get("scores");
        Map<String, Range> scoreRanges = scores != null ? parseScores(scores) : new HashMap<>();

//        String nbt = args.get("nbt");
//        boolean notNbt = nbt != null && nbt.startsWith("!");
//        if (notNbt) nbt = nbt.substring(1);

        String advancements = args.get("advancements");
        Map<Advancement, Boolean> advancementCriteria = advancements != null ? parseAdvancements(advancements) : new HashMap<>();

        String predicate = args.get("predicate");
        boolean notPredicate = predicate != null && predicate.startsWith("!");
        if (notPredicate) predicate = predicate.substring(1);

        String family = args.get("family");
        boolean notFamily = family != null && family.startsWith("!");
        if (notFamily) family = family.substring(1);

        String hasitem = args.get("hasitem");
        List<Map<String, String>> itemConditions = hasitem != null ? parseHasItem(hasitem) : new ArrayList<>();

        String haspermission = args.get("haspermission");
        Map<String, String> permissions = haspermission != null ? parsePermissions(haspermission) : new HashMap<>();

        int limit = args.containsKey("limit") ? parseInt(args.get("limit"), Integer.MAX_VALUE) : variable.equals("p") || variable.equals("r") ? 1 : Integer.MAX_VALUE;
        String sort = args.getOrDefault("sort", args.getOrDefault("c", variable.equals("p") ? "nearest" : variable.equals("r") ? "random" : "arbitrary"));

        // Build predicate
        List<Predicate<Entity>> predicates = new ArrayList<>();
        if (distanceRange != null) {
            predicates.add(entity -> distanceRange.matches(entity.getLocation().distanceSquared(searchOrigin)));
        }
        if (aabb != null) {
            predicates.add(entity -> entity.getBoundingBox().overlaps(aabb));
        }
        if (xRotationRange != null) {
            predicates.add(entity -> xRotationRange.matches(entity.getLocation().getPitch()));
        }
        if (yRotationRange != null) {
            predicates.add(entity -> yRotationRange.matches(entity.getLocation().getYaw()));
        }
        if (levelRange != null) {
            predicates.add(entity -> entity instanceof Player && levelRange.matches(((Player) entity).getLevel()));
        }
        if (gamemode != null) {
            String finalGamemode = gamemode;
            predicates.add(entity -> entity instanceof Player && ((Player) entity).getGameMode().name().equalsIgnoreCase(finalGamemode));
        }
        if (notGamemode != null) {
            String finalNotGamemode = notGamemode;
            predicates.add(entity -> entity instanceof Player && !((Player) entity).getGameMode().name().equalsIgnoreCase(finalNotGamemode));
        }
        if (name != null) {
            String finalName = name;
            predicates.add(notName ? entity -> !entity.getName().equals(finalName) && !(entity.getCustomName() == null ? "" : entity.getCustomName()).equals(finalName) : entity ->
               entity.getName().equals(finalName) || (entity.getCustomName() == null ? "" : entity.getCustomName()).equals(finalName));
        }
        if (type != null) {
            try {
                EntityType entityType = EntityType.valueOf(type.toUpperCase());
                predicates.add(notType ? entity -> entity.getType() != entityType : entity -> entity.getType() == entityType);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid entity type: " + type);
            }
        }
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                boolean notTag = tag.startsWith("!");
                String tagValue = notTag ? tag.substring(1) : tag;
                predicates.add(notTag ? entity -> !entity.getScoreboardTags().contains(tagValue) : entity -> entity.getScoreboardTags().contains(tagValue));
            }
        }
        if (team != null) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team sbTeam = scoreboard.getTeam(team);
            predicates.add(notTeam ? entity -> !(entity instanceof Player && sbTeam != null
               && sbTeam.hasPlayer((Player) entity)) : e -> e instanceof Player && sbTeam != null && sbTeam.hasPlayer((Player) e));
        }
        if (!scoreRanges.isEmpty()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            for (Map.Entry<String, Range> entry : scoreRanges.entrySet()) {
                String objective = entry.getKey();
                Range range = entry.getValue();
                predicates.add(entity -> {
                    if (!(entity instanceof Player)) return false;
                    try {
                        return range.matches(scoreboard.getObjective(objective).getScore((Player) entity).getScore());
                    } catch (NullPointerException ex) {
                        return false;
                    }
                });
            }
        }
        if (!advancementCriteria.isEmpty()) {
            predicates.add(entity -> {
                if(!(entity instanceof Player))return false;
                Player player = (Player) entity;
                boolean allMatch = true;
                for(Map.Entry<Advancement, Boolean> entry : advancementCriteria.entrySet()) {
                    if(player.getAdvancementProgress(entry.getKey()).isDone() != entry.getValue()) {
                        allMatch = false;
                        break;
                    }
                }
                return allMatch;
            });
        }
        if (!itemConditions.isEmpty()) {
            predicates.add(entity -> {
                if (!(entity instanceof Player)) return false;
                Player p = (Player) entity;
                for (Map<String, String> condition : itemConditions) {
                    String item = condition.get("item");
                    int quantity = condition.containsKey("quantity") ? parseInt(condition.get("quantity"), 1) : 1;
                    // Simplified item check
                    boolean hasItem = Arrays.stream(p.getInventory().getContents())
                            .filter(Objects::nonNull)
                            .anyMatch(i -> i.getType().name().equalsIgnoreCase(item) && i.getAmount() >= quantity);
                    if (!hasItem) return false;
                }
                return true;
            });
        }
        if (!permissions.isEmpty()) {
            predicates.add(entity -> entity instanceof Player && permissions.entrySet().stream()
                    .allMatch(entry -> entity.hasPermission(entry.getKey()) == entry.getValue().equals("enabled")));
        }

        // Combine predicates
        Predicate<Entity> finalPredicate = predicates.stream().reduce(Predicate::and).orElse(e -> true);

        // Select entities
        List<Entity> results = new ArrayList<>();
        switch (variable) {
            case "p":
                results = selectNearest(sender, searchOrigin, finalPredicate, limit, aabb);
                break;
            case "r":
                results = selectRandom(sender, finalPredicate, limit, aabb);
                break;
            case "a":
                results = selectAllPlayers(sender, finalPredicate, limit, aabb);
                break;
            case "e":
                results = selectAllEntities(sender, finalPredicate, limit, aabb);
                break;
            case "s":
                results = selectSelf(sender, finalPredicate);
                break;
        }

        // Apply sorting
        switch (sort.toLowerCase()) {
            case "nearest":
                results.sort(Comparator.comparing(e -> e.getLocation().distanceSquared(searchOrigin)));
                break;
            case "furthest":
                results.sort((e1, e2) -> Double.compare(e2.getLocation().distanceSquared(searchOrigin), e1.getLocation().distanceSquared(searchOrigin)));
                break;
            case "random":
                Collections.shuffle(results);
                break;
            case "arbitrary":
            default:
                break;
        }

        // Apply limit
        return results.stream().limit(limit).collect(Collectors.toList());
    }

    private static List<Entity> selectNearest(CommandSender sender, Location origin, Predicate<Entity> predicate, int limit, BoundingBox aabb) {
        List<Entity> results = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            List<Entity> entities = aabb != null
                    ? new ArrayList<>(world.getNearbyEntities(aabb).stream().filter(predicate).collect(Collectors.toList()))
                    : world.getEntities().stream().filter(predicate).collect(Collectors.toList());
            results.addAll(entities);
        }
        results.sort(Comparator.comparing(e -> e.getLocation().distanceSquared(origin)));
        return results.stream().limit(limit).collect(Collectors.toList());
    }

    private static List<Entity> selectRandom(CommandSender sender, Predicate<Entity> predicate, int limit, BoundingBox aabb) {
        List<Entity> results = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            List<Entity> entities = aabb != null
                    ? new ArrayList<>(world.getNearbyEntities(aabb).stream().filter(predicate).collect(Collectors.toList()))
                    : world.getEntities().stream().filter(predicate).collect(Collectors.toList());
            results.addAll(entities);
        }
        Collections.shuffle(results);
        return results.stream().limit(limit).collect(Collectors.toList());
    }

    private static List<Entity> selectAllPlayers(CommandSender sender, Predicate<Entity> predicate, int limit, BoundingBox aabb) {
        List<Entity> results = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (predicate.test(player) && (aabb == null || aabb.overlaps(player.getBoundingBox()))) {
                results.add(player);
            }
        }
        return results.stream().limit(limit).collect(Collectors.toList());
    }

    private static List<Entity> selectAllEntities(CommandSender sender, Predicate<Entity> predicate, int limit, BoundingBox aabb) {
        List<Entity> results = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            List<Entity> entities = aabb != null
                    ? world.getNearbyEntities(aabb).stream().filter(predicate).collect(Collectors.toList())
                    : world.getEntities().stream().filter(predicate).collect(Collectors.toList());
            results.addAll(entities);
        }
        return results.stream().limit(limit).collect(Collectors.toList());
    }

    private static List<Entity> selectSelf(CommandSender sender, Predicate<Entity> predicate) {
        if (sender instanceof Entity && predicate.test((Entity) sender)) {
            return Collections.singletonList((Entity) sender);
        }
        return new ArrayList<>();
    }

    private static Map<String, String> parseArguments(String argsString) {
        Map<String, String> args = new HashMap<>();
        if (argsString.isEmpty()) return args;

        Pattern argPattern = Pattern.compile("([^,=]+)=([^,]+)(?:,|$)");
        Matcher matcher = argPattern.matcher(argsString);
        while (matcher.find()) {
            args.put(matcher.group(1).trim(), matcher.group(2).trim());
        }
        return args;
    }

    private static double parseDouble(String s, double defaultValue) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int parseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static Range parseRange(String max, String min) {
        if(max.isEmpty() && min.isEmpty()) return null;
        try {
            if(!min.isEmpty()) {
                double minValue = Double.parseDouble(min);
                double maxValue = max.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(max);
                return new Range(minValue, maxValue);
            } else {
                if(max.contains("..")) {
                    String[] parts = max.split("\\.\\.");
                    double minValue = parts[0].isEmpty() ? -Double.MAX_VALUE : Double.parseDouble(parts[0]);
                    double maxValue = parts[1].isEmpty() ? Double.MAX_VALUE : Double.parseDouble(parts[1]);
                    return new Range(minValue, maxValue);
                } else {
                    double value = Double.parseDouble(max);
                    return new Range(value, value);
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid range: " + max + "," + min);
        }
    }

    private static Map<String, Range> parseScores(String scores) {
        Map<String, Range> result = new HashMap<>();
        if (scores.isEmpty()) return result;

        Pattern scorePattern = Pattern.compile("\\{([^}]*)\\}");
        Matcher matcher = scorePattern.matcher(scores);
        if (!matcher.find()) return result;

        String scoreString = matcher.group(1);
        Pattern scoreArgPattern = Pattern.compile("([^,=]+)=([^,]+)");
        Matcher argMatcher = scoreArgPattern.matcher(scoreString);
        while (argMatcher.find()) {
            String objective = argMatcher.group(1).trim();
            String value = argMatcher.group(2).trim();
            Range range = parseRange(value, "");
            if (range != null) {
                result.put(objective, range);
            }
        }
        return result;
    }

    private static Map<Advancement, Boolean> parseAdvancements(String advancements) {
        Map<Advancement, Boolean> result = new HashMap<>();
        for(String s : advancements.split(",")) {
        Bukkit.getAdvancement(new NamespacedKey("minecraft",
           s));
        }
        return result;
    }

    private static List<Map<String, String>> parseHasItem(String hasitem) {
        List<Map<String, String>> result = new ArrayList<>();
        if (hasitem.isEmpty()) return result;

        Pattern itemPattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = itemPattern.matcher(hasitem);
        while (matcher.find()) {
            Map<String, String> condition = new HashMap<>();
            String itemString = matcher.group(1);
            Pattern argPattern = Pattern.compile("([^,=]+)=([^,]+)");
            Matcher argMatcher = argPattern.matcher(itemString);
            while (argMatcher.find()) {
                condition.put(argMatcher.group(1).trim(), argMatcher.group(2).trim());
            }
            result.add(condition);
        }
        return result;
    }

    private static Map<String, String> parsePermissions(String permissions) {
        Map<String, String> result = new HashMap<>();
        if (permissions.isEmpty()) return result;

        Pattern permPattern = Pattern.compile("\\{([^}]*)\\}");
        Matcher matcher = permPattern.matcher(permissions);
        if (!matcher.find()) return result;

        String permString = matcher.group(1);
        Pattern argPattern = Pattern.compile("([^,=]+)=([^,]+)");
        Matcher argMatcher = argPattern.matcher(permString);
        while (argMatcher.find()) {
            result.put(argMatcher.group(1).trim(), argMatcher.group(2).trim());
        }
        return result;
    }

    private static class Range {
        double min, max;

        Range(double min, double max) {
            this.min = min;
            this.max = max;
        }

        boolean matches(double value) {
            return value >= min && value <= max;
        }
    }
}