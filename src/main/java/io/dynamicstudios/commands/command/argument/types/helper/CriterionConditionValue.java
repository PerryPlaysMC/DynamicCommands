package io.dynamicstudios.commands.command.argument.types.helper;

public interface CriterionConditionValue<T extends Number> {
//  SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(() -> IComponent.translationComponent(Translation.byId("argument.range.empty")).json());
//  SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(() -> IComponent.translationComponent(Translation.byId("argument.range.swapped")).json());
//
//  Optional<T> min();
//
//  Optional<T> max();
//
//  default boolean isAny() {
//    return !this.min().isPresent() && !this.max().isPresent();
//  }
//
//  default Optional<T> unwrapPoint() {
//    Optional<T> var0 = this.min();
//    Optional<T> var1 = this.max();
//    return var0.equals(var1) ? var0 : Optional.empty();
//  }
//
//  static <T extends Number, R extends CriterionConditionValue<T>> R fromReader(StringReader var0, b<T, R> var1, Function<String, T> var2, Supplier<DynamicCommandExceptionType> var3, Function<T, T> var4) throws CommandSyntaxException {
//    if (!var0.canRead()) {
//      throw ERROR_EMPTY.createWithContext(var0);
//    } else {
//      int var5 = var0.getCursor();
//
//      try {
//        Optional<T> var6 = readNumber(var0, var2, var3).map(var4);
//        Optional var7;
//        if (var0.canRead(2) && var0.peek() == '.' && var0.peek(1) == '.') {
//          var0.skip();
//          var0.skip();
//          var7 = readNumber(var0, var2, var3).map(var4);
//          if (var6.isEmpty() && var7.isEmpty()) {
//            throw ERROR_EMPTY.createWithContext(var0);
//          }
//        } else {
//          var7 = var6;
//        }
//
//        if (var6.isEmpty() && var7.isEmpty()) {
//          throw ERROR_EMPTY.createWithContext(var0);
//        } else {
//          return var1.create(var0, var6, var7);
//        }
//      } catch (CommandSyntaxException var8) {
//        CommandSyntaxException var6 = var8;
//        var0.setCursor(var5);
//        throw new CommandSyntaxException(var6.getType(), var6.getRawMessage(), var6.getInput(), var5);
//      }
//    }
//  }
//
//  private static <T extends Number> Optional<T> readNumber(StringReader var0, Function<String, T> var1, Supplier<DynamicCommandExceptionType> var2) throws CommandSyntaxException {
//    int var3 = var0.getCursor();
//
//    while(var0.canRead() && isAllowedInputChat(var0)) {
//      var0.skip();
//    }
//
//    String var4 = var0.getString().substring(var3, var0.getCursor());
//    if (var4.isEmpty()) {
//      return Optional.empty();
//    } else {
//      try {
//        return Optional.of((Number)var1.apply(var4));
//      } catch (NumberFormatException var6) {
//        throw ((DynamicCommandExceptionType)var2.get()).createWithContext(var0, var4);
//      }
//    }
//  }
//
//  private static boolean isAllowedInputChat(StringReader var0) {
//    char var1 = var0.peek();
//    if ((var1 < '0' || var1 > '9') && var1 != '-') {
//      if (var1 != '.') {
//        return false;
//      } else {
//        return !var0.canRead(2) || var0.peek(1) != '.';
//      }
//    } else {
//      return true;
//    }
//  }
//
//  @FunctionalInterface
//  public interface a<T extends Number, R extends CriterionConditionValue<T>> {
//    R create(Optional<T> var1, Optional<T> var2);
//  }
//
//  @FunctionalInterface
//  public interface b<T extends Number, R extends CriterionConditionValue<T>> {
//    R create(StringReader var1, Optional<T> var2, Optional<T> var3) throws CommandSyntaxException;
//  }
//
//  public static record DoubleRange(Optional<Double> min, Optional<Double> max, Optional<Double> minSq, Optional<Double> maxSq) implements CriterionConditionValue<Double> {
//    public static final DoubleRange ANY = new DoubleRange(Optional.empty(), Optional.empty());
//    public static final Codec<DoubleRange> CODEC;
//
//    private DoubleRange(Optional<Double> var0, Optional<Double> var1) {
//      this(var0, var1, squareOpt(var0), squareOpt(var1));
//    }
//
//    public DoubleRange(Optional<Double> var0, Optional<Double> var1, Optional<Double> var2, Optional<Double> var3) {
//      this.min = var0;
//      this.max = var1;
//      this.minSq = var2;
//      this.maxSq = var3;
//    }
//
//    private static DoubleRange create(StringReader var0, Optional<Double> var1, Optional<Double> var2) throws CommandSyntaxException {
//      if (var1.isPresent() && var2.isPresent() && (Double)var1.get() > (Double)var2.get()) {
//        throw ERROR_SWAPPED.createWithContext(var0);
//      } else {
//        return new DoubleRange(var1, var2);
//      }
//    }
//
//    private static Optional<Double> squareOpt(Optional<Double> var0) {
//      return var0.map((var0x) -> {
//        return var0x * var0x;
//      });
//    }
//
//    public static DoubleRange exactly(double var0) {
//      return new DoubleRange(Optional.of(var0), Optional.of(var0));
//    }
//
//    public static DoubleRange between(double var0, double var2) {
//      return new DoubleRange(Optional.of(var0), Optional.of(var2));
//    }
//
//    public static DoubleRange atLeast(double var0) {
//      return new DoubleRange(Optional.of(var0), Optional.empty());
//    }
//
//    public static DoubleRange atMost(double var0) {
//      return new DoubleRange(Optional.empty(), Optional.of(var0));
//    }
//
//    public boolean matches(double var0) {
//      if (this.min.isPresent() && (Double)this.min.get() > var0) {
//        return false;
//      } else {
//        return this.max.isEmpty() || !((Double)this.max.get() < var0);
//      }
//    }
//
//    public boolean matchesSqr(double var0) {
//      if (this.minSq.isPresent() && (Double)this.minSq.get() > var0) {
//        return false;
//      } else {
//        return this.maxSq.isEmpty() || !((Double)this.maxSq.get() < var0);
//      }
//    }
//
//    public static DoubleRange fromReader(StringReader var0) throws CommandSyntaxException {
//      return fromReader(var0, (var0x) -> {
//        return var0x;
//      });
//    }
//
//    public static DoubleRange fromReader(StringReader var0, Function<Double, Double> var1) throws CommandSyntaxException {
//      b var10001 = DoubleRange::create;
//      Function var10002 = Double::parseDouble;
//      BuiltInExceptionProvider var10003 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
//      Objects.requireNonNull(var10003);
//      return (DoubleRange)CriterionConditionValue.fromReader(var0, var10001, var10002, var10003::readerInvalidDouble, var1);
//    }
//
//    public Optional<Double> min() {
//      return this.min;
//    }
//
//    public Optional<Double> max() {
//      return this.max;
//    }
//
//    public Optional<Double> minSq() {
//      return this.minSq;
//    }
//
//    public Optional<Double> maxSq() {
//      return this.maxSq;
//    }
//
//    static {
//      CODEC = CriterionConditionValue.createCodec(Codec.DOUBLE, DoubleRange::new);
//    }
//  }
//
//  public static record IntegerRange(Optional<Integer> min, Optional<Integer> max, Optional<Long> minSq, Optional<Long> maxSq) implements CriterionConditionValue<Integer> {
//    public static final IntegerRange ANY = new IntegerRange(Optional.empty(), Optional.empty());
//    public static final Codec<IntegerRange> CODEC;
//
//    private IntegerRange(Optional<Integer> var0, Optional<Integer> var1) {
//      this(var0, var1, var0.map((var0x) -> {
//        return var0x.longValue() * var0x.longValue();
//      }), squareOpt(var1));
//    }
//
//    public IntegerRange(Optional<Integer> var0, Optional<Integer> var1, Optional<Long> var2, Optional<Long> var3) {
//      this.min = var0;
//      this.max = var1;
//      this.minSq = var2;
//      this.maxSq = var3;
//    }
//
//    private static IntegerRange create(StringReader var0, Optional<Integer> var1, Optional<Integer> var2) throws CommandSyntaxException {
//      if (var1.isPresent() && var2.isPresent() && (Integer)var1.get() > (Integer)var2.get()) {
//        throw ERROR_SWAPPED.createWithContext(var0);
//      } else {
//        return new IntegerRange(var1, var2);
//      }
//    }
//
//    private static Optional<Long> squareOpt(Optional<Integer> var0) {
//      return var0.map((var0x) -> {
//        return var0x.longValue() * var0x.longValue();
//      });
//    }
//
//    public static IntegerRange exactly(int var0) {
//      return new IntegerRange(Optional.of(var0), Optional.of(var0));
//    }
//
//    public static IntegerRange between(int var0, int var1) {
//      return new IntegerRange(Optional.of(var0), Optional.of(var1));
//    }
//
//    public static IntegerRange atLeast(int var0) {
//      return new IntegerRange(Optional.of(var0), Optional.empty());
//    }
//
//    public static IntegerRange atMost(int var0) {
//      return new IntegerRange(Optional.empty(), Optional.of(var0));
//    }
//
//    public boolean matches(int var0) {
//      if (this.min.isPresent() && (Integer)this.min.get() > var0) {
//        return false;
//      } else {
//        return this.max.isEmpty() || (Integer)this.max.get() >= var0;
//      }
//    }
//
//    public boolean matchesSqr(long var0) {
//      if (this.minSq.isPresent() && (Long)this.minSq.get() > var0) {
//        return false;
//      } else {
//        return this.maxSq.isEmpty() || (Long)this.maxSq.get() >= var0;
//      }
//    }
//
//    public static IntegerRange fromReader(StringReader var0) throws CommandSyntaxException {
//      return fromReader(var0, (var0x) -> {
//        return var0x;
//      });
//    }
//
//    public static IntegerRange fromReader(StringReader var0, Function<Integer, Integer> var1) throws CommandSyntaxException {
//      b var10001 = IntegerRange::create;
//      Function var10002 = Integer::parseInt;
//      BuiltInExceptionProvider var10003 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
//      Objects.requireNonNull(var10003);
//      return (IntegerRange)CriterionConditionValue.fromReader(var0, var10001, var10002, var10003::readerInvalidInt, var1);
//    }
//
//    public Optional<Integer> min() {
//      return this.min;
//    }
//
//    public Optional<Integer> max() {
//      return this.max;
//    }
//
//    public Optional<Long> minSq() {
//      return this.minSq;
//    }
//
//    public Optional<Long> maxSq() {
//      return this.maxSq;
//    }
//
//    static {
//      CODEC = CriterionConditionValue.createCodec(Codec.INT, IntegerRange::new);
//    }
//  }
}
