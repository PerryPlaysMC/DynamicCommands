package io.dynamicstudios.commands.command.argument.types;

import io.dynamicstudios.commands.command.argument.types.helper.AxisAlignedBB;
import io.dynamicstudios.commands.command.argument.types.helper.Vec3D;
import io.dynamicstudios.commands.exceptions.brigadier.DynamicCommandExceptionType;
import io.dynamicstudios.commands.exceptions.brigadier.SimpleCommandExceptionType;
import io.dynamicstudios.json.data.component.DynamicComponent;
import io.dynamicstudios.json.data.component.DynamicTranslatableComponent;
import io.dynamicstudios.json.data.component.IComponent;
import io.dynamicstudios.json.data.util.Translation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class DynamicEntityParserSelector {
//  public static final char SYNTAX_SELECTOR_START = '@';
//  private static final char SYNTAX_OPTIONS_START = '[';
//  private static final char SYNTAX_OPTIONS_END = ']';
//  public static final char SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR = '=';
//  private static final char SYNTAX_OPTIONS_SEPARATOR = ',';
//  public static final char SYNTAX_NOT = '!';
//  public static final char SYNTAX_TAG = '#';
//  private static final char SELECTOR_NEAREST_PLAYER = 'p';
//  private static final char SELECTOR_ALL_PLAYERS = 'a';
//  private static final char SELECTOR_RANDOM_PLAYERS = 'r';
//  private static final char SELECTOR_CURRENT_ENTITY = 's';
//  private static final char SELECTOR_ALL_ENTITIES = 'e';
//  private static final char SELECTOR_NEAREST_ENTITY = 'n';
//  public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType(() -> IComponent.translationComponent(Translation.byId("argument.entity.invalid")).json());
//  public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(() -> IComponent.translationComponent(Translation.byId("argument.entity.selector.not_allowed")).json());
//  public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(() -> IComponent.translationComponent(Translation.byId("argument.entity.selector.missing")).json());
//  public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(() -> IComponent.translationComponent(Translation.byId("argument.entity.options.unterminated")).json());
//  public static final BiConsumer<Vec3D, List<? extends Entity>> ORDER_NEAREST = (var0, var1) -> {
//    var1.sort((var1x, var2) -> {
//      return Double.compare(var1x.getLocation().distanceSquared(new Location(var1x.getWorld(), var0.x, var0.y, var0.z)),
//         var2.distanceToSqr(new Location(var1x.getWorld(), var0.x, var0.y, var0.z)));
//    });
//  };
//  public static final BiConsumer<Vec3D, List<? extends Entity>> ORDER_FURTHEST = (var0, var1) -> {
//    var1.sort((var1x, var2) -> {
//      return Double.compare(var2.distanceToSqr(new Location(var1x.getWorld(), var0.x, var0.y, var0.z)),
//         var1x.getLocation().distanceSquared(new Location(var1x.getWorld(), var0.x, var0.y, var0.z)));
//    });
//  };
//  public static final BiConsumer<Vec3D, List<? extends Entity>> ORDER_RANDOM = (var0, var1) -> {
//    Collections.shuffle(var1);
//  };
//  private final boolean allowSelectors;
//  private int maxResults;
//  private boolean includesEntities;
//  private boolean worldLimited;
//  private DoubleRange distance;
//  private IntegerRange level;
//  @Nullable
//  private Double x;
//  @Nullable
//  private Double y;
//  @Nullable
//  private Double z;
//  @Nullable
//  private Double deltaX;
//  @Nullable
//  private Double deltaY;
//  @Nullable
//  private Double deltaZ;
//  private CriterionConditionRange rotX;
//  private CriterionConditionRange rotY;
//  private final List<Predicate<Entity>> predicates;
//  private BiConsumer<Vec3D, List<? extends Entity>> order;
//  private boolean currentEntity;
//  @Nullable
//  private String playerName;
//  private int startPosition;
//  @Nullable
//  private UUID entityUUID;
//  private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;
//  private boolean hasNameEquals;
//  private boolean hasNameNotEquals;
//  private boolean isLimited;
//  private boolean isSorted;
//  private boolean hasGamemodeEquals;
//  private boolean hasGamemodeNotEquals;
//  private boolean hasTeamEquals;
//  private boolean hasTeamNotEquals;
//  @Nullable
//  private EntityType type;
//  private boolean typeInverse;
//  private boolean hasScores;
//  private boolean hasAdvancements;
//  private boolean usesSelectors;
//
//  public DynamicEntityParserSelector(StringReader var0) {
//    this(var0, true);
//  }
//
//  public DynamicEntityParserSelector(StringReader var0, boolean var1) {
//    this.distance = DoubleRange.ANY;
//    this.level = IntegerRange.ANY;
//    this.rotX = CriterionConditionRange.ANY;
//    this.rotY = CriterionConditionRange.ANY;
//    this.predicates = new ArrayList();
//    this.order = EntitySelector.ORDER_ARBITRARY;
//    this.suggestions = SUGGEST_NOTHING;
//    this.reader = var0;
//    this.allowSelectors = var1;
//  }
//
//  public EntitySelector getSelector() {
//    AxisAlignedBB var0;
//    if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
//      if (this.distance.max().isPresent()) {
//        double var1 = (Double)this.distance.max().get();
//        var0 = new AxisAlignedBB(-var1, -var1, -var1, var1 + 1.0, var1 + 1.0, var1 + 1.0);
//      } else {
//        var0 = null;
//      }
//    } else {
//      var0 = this.createAabb(this.deltaX == null ? 0.0 : this.deltaX, this.deltaY == null ? 0.0 : this.deltaY, this.deltaZ == null ? 0.0 : this.deltaZ);
//    }
//
//    Function var1;
//    if (this.x == null && this.y == null && this.z == null) {
//      var1 = (var0x) -> {
//        return var0x;
//      };
//    } else {
//      var1 = (var0x) -> {
//        return new Vec3D(this.x == null ? var0x.x : this.x, this.y == null ? var0x.y : this.y, this.z == null ? var0x.z : this.z);
//      };
//    }
//
//    return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, List.copyOf(this.predicates), this.distance, var1, var0, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
//  }
//
//  private AxisAlignedBB createAabb(double var0, double var2, double var4) {
//    boolean var6 = var0 < 0.0;
//    boolean var7 = var2 < 0.0;
//    boolean var8 = var4 < 0.0;
//    double var9 = var6 ? var0 : 0.0;
//    double var11 = var7 ? var2 : 0.0;
//    double var13 = var8 ? var4 : 0.0;
//    double var15 = (var6 ? 0.0 : var0) + 1.0;
//    double var17 = (var7 ? 0.0 : var2) + 1.0;
//    double var19 = (var8 ? 0.0 : var4) + 1.0;
//    return new AxisAlignedBB(var9, var11, var13, var15, var17, var19);
//  }
//
//  private void finalizePredicates() {
//    if (this.rotX != CriterionConditionRange.ANY) {
//      this.predicates.add(this.createRotationPredicate(this.rotX, Entity::getXRot));
//    }
//
//    if (this.rotY != CriterionConditionRange.ANY) {
//      this.predicates.add(this.createRotationPredicate(this.rotY, Entity::getYRot));
//    }
//
//    if (!this.level.isAny()) {
//      this.predicates.add((var0) -> {
//        return !(var0 instanceof EntityPlayer) ? false : this.level.matches(((EntityPlayer)var0).experienceLevel);
//      });
//    }
//
//  }
//
//  private Predicate<Entity> createRotationPredicate(CriterionConditionRange var0, ToDoubleFunction<Entity> var1) {
//    double var2 = (double)MathHelper.wrapDegrees(var0.min() == null ? 0.0F : var0.min());
//    double var4 = (double)MathHelper.wrapDegrees(var0.max() == null ? 359.0F : var0.max());
//    return (var5) -> {
//      double var6 = MathHelper.wrapDegrees(var1.applyAsDouble(var5));
//      if (var2 > var4) {
//        return var6 >= var2 || var6 <= var4;
//      } else {
//        return var6 >= var2 && var6 <= var4;
//      }
//    };
//  }
//
//  protected void parseSelector() throws CommandSyntaxException {
//    this.usesSelectors = true;
//    this.suggestions = this::suggestSelector;
//    if (!this.reader.canRead()) {
//      throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
//    } else {
//      int var0 = this.reader.getCursor();
//      char var1 = this.reader.read();
//      boolean var2;
//      switch (var1) {
//        case 'a':
//          this.maxResults = Integer.MAX_VALUE;
//          this.includesEntities = false;
//          this.order = EntitySelector.ORDER_ARBITRARY;
//          this.limitToType(EntityType.PLAYER);
//          var2 = false;
//          break;
//        case 'b':
//        case 'c':
//        case 'd':
//        case 'f':
//        case 'g':
//        case 'h':
//        case 'i':
//        case 'j':
//        case 'k':
//        case 'l':
//        case 'm':
//        case 'o':
//        case 'q':
//        default:
//          this.reader.setCursor(var0);
//          throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, "@" + String.valueOf(var1));
//        case 'e':
//          this.maxResults = Integer.MAX_VALUE;
//          this.includesEntities = true;
//          this.order = EntitySelector.ORDER_ARBITRARY;
//          var2 = true;
//          break;
//        case 'n':
//          this.maxResults = 1;
//          this.includesEntities = true;
//          this.order = ORDER_NEAREST;
//          var2 = true;
//          break;
//        case 'p':
//          this.maxResults = 1;
//          this.includesEntities = false;
//          this.order = ORDER_NEAREST;
//          this.limitToType(EntityType.PLAYER);
//          var2 = false;
//          break;
//        case 'r':
//          this.maxResults = 1;
//          this.includesEntities = false;
//          this.order = ORDER_RANDOM;
//          this.limitToType(EntityType.PLAYER);
//          var2 = false;
//          break;
//        case 's':
//          this.maxResults = 1;
//          this.includesEntities = true;
//          this.currentEntity = true;
//          var2 = false;
//      }
//
//      if (var2) {
//        this.predicates.add(Entity::isAlive);
//      }
//
//      this.suggestions = this::suggestOpenOptions;
//      if (this.reader.canRead() && this.reader.peek() == '[') {
//        this.reader.skip();
//        this.suggestions = this::suggestOptionsKeyOrClose;
//        this.parseOptions();
//      }
//
//    }
//  }
//
//  protected void parseNameOrUUID() throws CommandSyntaxException {
//    if (this.reader.canRead()) {
//      this.suggestions = this::suggestName;
//    }
//
//    int var0 = this.reader.getCursor();
//    String var1 = this.reader.readString();
//
//    try {
//      this.entityUUID = UUID.fromString(var1);
//      this.includesEntities = true;
//    } catch (IllegalArgumentException var4) {
//      if (var1.isEmpty() || var1.length() > 16) {
//        this.reader.setCursor(var0);
//        throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
//      }
//
//      this.includesEntities = false;
//      this.playerName = var1;
//    }
//
//    this.maxResults = 1;
//  }
//
//  protected void parseOptions() throws CommandSyntaxException {
//    this.suggestions = this::suggestOptionsKey;
//    this.reader.skipWhitespace();
//
//    while(this.reader.canRead() && this.reader.peek() != ']') {
//      this.reader.skipWhitespace();
//      int var0 = this.reader.getCursor();
//      String var1 = this.reader.readString();
//      PlayerSelector.a var2 = PlayerSelector.get(this, var1, var0);
//      this.reader.skipWhitespace();
//      if (this.reader.canRead() && this.reader.peek() == '=') {
//        this.reader.skip();
//        this.reader.skipWhitespace();
//        this.suggestions = SUGGEST_NOTHING;
//        var2.handle(this);
//        this.reader.skipWhitespace();
//        this.suggestions = this::suggestOptionsNextOrClose;
//        if (!this.reader.canRead()) {
//          continue;
//        }
//
//        if (this.reader.peek() == ',') {
//          this.reader.skip();
//          this.suggestions = this::suggestOptionsKey;
//          continue;
//        }
//
//        if (this.reader.peek() != ']') {
//          throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
//        }
//        break;
//      }
//
//      this.reader.setCursor(var0);
//      throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, var1);
//    }
//
//    if (this.reader.canRead()) {
//      this.reader.skip();
//      this.suggestions = SUGGEST_NOTHING;
//    } else {
//      throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
//    }
//  }
//
//  public boolean shouldInvertValue() {
//    this.reader.skipWhitespace();
//    if (this.reader.canRead() && this.reader.peek() == '!') {
//      this.reader.skip();
//      this.reader.skipWhitespace();
//      return true;
//    } else {
//      return false;
//    }
//  }
//
//  public boolean isTag() {
//    this.reader.skipWhitespace();
//    if (this.reader.canRead() && this.reader.peek() == '#') {
//      this.reader.skip();
//      this.reader.skipWhitespace();
//      return true;
//    } else {
//      return false;
//    }
//  }
//
//  public StringReader getReader() {
//    return this.reader;
//  }
//
//  public void addPredicate(Predicate<Entity> var0) {
//    this.predicates.add(var0);
//  }
//
//  public void setWorldLimited() {
//    this.worldLimited = true;
//  }
//
//  public CriterionConditionValue.DoubleRange getDistance() {
//    return this.distance;
//  }
//
//  public void setDistance(CriterionConditionValue.DoubleRange var0) {
//    this.distance = var0;
//  }
//
//  public CriterionConditionValue.IntegerRange getLevel() {
//    return this.level;
//  }
//
//  public void setLevel(CriterionConditionValue.IntegerRange var0) {
//    this.level = var0;
//  }
//
//  public CriterionConditionRange getRotX() {
//    return this.rotX;
//  }
//
//  public void setRotX(CriterionConditionRange var0) {
//    this.rotX = var0;
//  }
//
//  public CriterionConditionRange getRotY() {
//    return this.rotY;
//  }
//
//  public void setRotY(CriterionConditionRange var0) {
//    this.rotY = var0;
//  }
//
//  @Nullable
//  public Double getX() {
//    return this.x;
//  }
//
//  @Nullable
//  public Double getY() {
//    return this.y;
//  }
//
//  @Nullable
//  public Double getZ() {
//    return this.z;
//  }
//
//  public void setX(double var0) {
//    this.x = var0;
//  }
//
//  public void setY(double var0) {
//    this.y = var0;
//  }
//
//  public void setZ(double var0) {
//    this.z = var0;
//  }
//
//  public void setDeltaX(double var0) {
//    this.deltaX = var0;
//  }
//
//  public void setDeltaY(double var0) {
//    this.deltaY = var0;
//  }
//
//  public void setDeltaZ(double var0) {
//    this.deltaZ = var0;
//  }
//
//  @Nullable
//  public Double getDeltaX() {
//    return this.deltaX;
//  }
//
//  @Nullable
//  public Double getDeltaY() {
//    return this.deltaY;
//  }
//
//  @Nullable
//  public Double getDeltaZ() {
//    return this.deltaZ;
//  }
//
//  public void setMaxResults(int var0) {
//    this.maxResults = var0;
//  }
//
//  public void setIncludesEntities(boolean var0) {
//    this.includesEntities = var0;
//  }
//
//  public BiConsumer<Vec3D, List<? extends Entity>> getOrder() {
//    return this.order;
//  }
//
//  public void setOrder(BiConsumer<Vec3D, List<? extends Entity>> var0) {
//    this.order = var0;
//  }
//
//  public EntitySelector parse() throws CommandSyntaxException {
//    this.startPosition = this.reader.getCursor();
//    this.suggestions = this::suggestNameOrSelector;
//    if (this.reader.canRead() && this.reader.peek() == '@') {
//      if (!this.allowSelectors) {
//        throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
//      }
//
//      this.reader.skip();
//      this.parseSelector();
//    } else {
//      this.parseNameOrUUID();
//    }
//
//    this.finalizePredicates();
//    return this.getSelector();
//  }
//
//  private static void fillSelectorSuggestions(SuggestionsBuilder var0) {
//    var0.suggest("@p", IChatBaseComponent.translatable("argument.entity.selector.nearestPlayer"));
//    var0.suggest("@a", IChatBaseComponent.translatable("argument.entity.selector.allPlayers"));
//    var0.suggest("@r", IChatBaseComponent.translatable("argument.entity.selector.randomPlayer"));
//    var0.suggest("@s", IChatBaseComponent.translatable("argument.entity.selector.self"));
//    var0.suggest("@e", IChatBaseComponent.translatable("argument.entity.selector.allEntities"));
//    var0.suggest("@n", IChatBaseComponent.translatable("argument.entity.selector.nearestEntity"));
//  }
//
//  private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    var1.accept(var0);
//    if (this.allowSelectors) {
//      fillSelectorSuggestions(var0);
//    }
//
//    return var0.buildFuture();
//  }
//
//  private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    SuggestionsBuilder var2 = var0.createOffset(this.startPosition);
//    var1.accept(var2);
//    return var0.add(var2).buildFuture();
//  }
//
//  private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    SuggestionsBuilder var2 = var0.createOffset(var0.getStart() - 1);
//    fillSelectorSuggestions(var2);
//    var0.add(var2);
//    return var0.buildFuture();
//  }
//
//  private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    var0.suggest(String.valueOf('['));
//    return var0.buildFuture();
//  }
//
//  private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    var0.suggest(String.valueOf(']'));
//    PlayerSelector.suggestNames(this, var0);
//    return var0.buildFuture();
//  }
//
//  private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    PlayerSelector.suggestNames(this, var0);
//    return var0.buildFuture();
//  }
//
//  private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    var0.suggest(String.valueOf(','));
//    var0.suggest(String.valueOf(']'));
//    return var0.buildFuture();
//  }
//
//  private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    var0.suggest(String.valueOf('='));
//    return var0.buildFuture();
//  }
//
//  public boolean isCurrentEntity() {
//    return this.currentEntity;
//  }
//
//  public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> var0) {
//    this.suggestions = var0;
//  }
//
//  public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder var0, Consumer<SuggestionsBuilder> var1) {
//    return (CompletableFuture)this.suggestions.apply(var0.createOffset(this.reader.getCursor()), var1);
//  }
//
//  public boolean hasNameEquals() {
//    return this.hasNameEquals;
//  }
//
//  public void setHasNameEquals(boolean var0) {
//    this.hasNameEquals = var0;
//  }
//
//  public boolean hasNameNotEquals() {
//    return this.hasNameNotEquals;
//  }
//
//  public void setHasNameNotEquals(boolean var0) {
//    this.hasNameNotEquals = var0;
//  }
//
//  public boolean isLimited() {
//    return this.isLimited;
//  }
//
//  public void setLimited(boolean var0) {
//    this.isLimited = var0;
//  }
//
//  public boolean isSorted() {
//    return this.isSorted;
//  }
//
//  public void setSorted(boolean var0) {
//    this.isSorted = var0;
//  }
//
//  public boolean hasGamemodeEquals() {
//    return this.hasGamemodeEquals;
//  }
//
//  public void setHasGamemodeEquals(boolean var0) {
//    this.hasGamemodeEquals = var0;
//  }
//
//  public boolean hasGamemodeNotEquals() {
//    return this.hasGamemodeNotEquals;
//  }
//
//  public void setHasGamemodeNotEquals(boolean var0) {
//    this.hasGamemodeNotEquals = var0;
//  }
//
//  public boolean hasTeamEquals() {
//    return this.hasTeamEquals;
//  }
//
//  public void setHasTeamEquals(boolean var0) {
//    this.hasTeamEquals = var0;
//  }
//
//  public boolean hasTeamNotEquals() {
//    return this.hasTeamNotEquals;
//  }
//
//  public void setHasTeamNotEquals(boolean var0) {
//    this.hasTeamNotEquals = var0;
//  }
//
//  public void limitToType(EntityType var0) {
//    this.type = var0;
//  }
//
//  public void setTypeLimitedInversely() {
//    this.typeInverse = true;
//  }
//
//  public boolean isTypeLimited() {
//    return this.type != null;
//  }
//
//  public boolean isTypeLimitedInversely() {
//    return this.typeInverse;
//  }
//
//  public boolean hasScores() {
//    return this.hasScores;
//  }
//
//  public void setHasScores(boolean var0) {
//    this.hasScores = var0;
//  }
//
//  public boolean hasAdvancements() {
//    return this.hasAdvancements;
//  }
//
//  public void setHasAdvancements(boolean var0) {
//    this.hasAdvancements = var0;
//  }
}
