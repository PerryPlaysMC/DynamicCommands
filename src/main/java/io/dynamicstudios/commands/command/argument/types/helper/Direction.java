package io.dynamicstudios.commands.command.argument.types.helper;

import com.google.common.collect.Iterators;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum Direction {
  DOWN(0, 1, -1, "down", Direction.EnumAxisDirection.NEGATIVE, Direction.EnumAxis.Y, new Vector(0, -1, 0)),
  UP(1, 0, -1, "up", Direction.EnumAxisDirection.POSITIVE, Direction.EnumAxis.Y, new Vector(0, 1, 0)),
  NORTH(2, 3, 2, "north", Direction.EnumAxisDirection.NEGATIVE, Direction.EnumAxis.Z, new Vector(0, 0, -1)),
  SOUTH(3, 2, 0, "south", Direction.EnumAxisDirection.POSITIVE, Direction.EnumAxis.Z, new Vector(0, 0, 1)),
  WEST(4, 5, 1, "west", Direction.EnumAxisDirection.NEGATIVE, Direction.EnumAxis.X, new Vector(-1, 0, 0)),
  EAST(5, 4, 3, "east", Direction.EnumAxisDirection.POSITIVE, Direction.EnumAxis.X, new Vector(1, 0, 0));

  private final int data3d;
  private final int oppositeIndex;
  private final int data2d;
  private final String name;
  private final EnumAxis axis;
  private final EnumAxisDirection axisDirection;
  private final Vector normal;
  private static final Direction[] VALUES = values();
  private static final Direction[] BY_3D_DATA = Arrays.stream(VALUES).sorted(Comparator.comparingInt((var0) -> var0.data3d)).toArray(Direction[]::new);
  private static final Direction[] BY_2D_DATA = Arrays.stream(VALUES).filter((var0) -> var0.getAxis().isHorizontal()).sorted(Comparator.comparingInt((var0) -> var0.data2d)).toArray(Direction[]::new);

  Direction(final int var2, final int var3, final int var4, final String var5, final EnumAxisDirection var6, final EnumAxis var7, final Vector var8) {
    this.data3d = var2;
    this.data2d = var4;
    this.oppositeIndex = var3;
    this.name = var5;
    this.axis = var7;
    this.axisDirection = var6;
    this.normal = var8;
  }
  public static float getViewXRot(Entity entity, float var0) {
    return var0 == 1.0F ? entity.getLocation().getYaw() : MathHelper.lerp(var0, entity.getLocation().getYaw(), entity.getLocation().getYaw());
  }

  public static float getViewYRot(Entity entity, float var0) {
    return var0 == 1.0F ? entity.getLocation().getPitch() : MathHelper.lerp(var0, entity.getLocation().getPitch(), entity.getLocation().getPitch());
  }

  public static Direction[] orderedByNearest(Entity var0) {
    float var1 = getViewXRot(var0, 1.0F) * 0.017453292F;
    float var2 = -getViewYRot(var0,1.0F) * 0.017453292F;
    float var3 = MathHelper.sin(var1);
    float var4 = MathHelper.cos(var1);
    float var5 = MathHelper.sin(var2);
    float var6 = MathHelper.cos(var2);
    boolean var7 = var5 > 0.0F;
    boolean var8 = var3 < 0.0F;
    boolean var9 = var6 > 0.0F;
    float var10 = var7 ? var5 : -var5;
    float var11 = var8 ? -var3 : var3;
    float var12 = var9 ? var6 : -var6;
    float var13 = var10 * var4;
    float var14 = var12 * var4;
    Direction var15 = var7 ? EAST : WEST;
    Direction var16 = var8 ? UP : DOWN;
    Direction var17 = var9 ? SOUTH : NORTH;
    if (var10 > var12) {
      if (var11 > var13) {
        return makeDirectionArray(var16, var15, var17);
      } else {
        return var14 > var11 ? makeDirectionArray(var15, var17, var16) : makeDirectionArray(var15, var16, var17);
      }
    } else if (var11 > var14) {
      return makeDirectionArray(var16, var17, var15);
    } else {
      return var13 > var11 ? makeDirectionArray(var17, var15, var16) : makeDirectionArray(var17, var16, var15);
    }
  }

  private static Direction[] makeDirectionArray(Direction var0, Direction var1, Direction var2) {
    return new Direction[]{var0, var1, var2, var2.getOpposite(), var1.getOpposite(), var0.getOpposite()};
  }

  public int get3DDataValue() {
    return this.data3d;
  }

  public int get2DDataValue() {
    return this.data2d;
  }

  public EnumAxisDirection getAxisDirection() {
    return this.axisDirection;
  }

  public Direction getOpposite() {
    return from3DDataValue(this.oppositeIndex);
  }


  public Direction getCounterClockWise(EnumAxis var0) {
    Direction var10000;
    switch (var0.ordinal()) {
      case 0: var10000 = this != WEST && this != EAST ? this.getCounterClockWiseX() : this; break;
      case 1: var10000 = this != UP && this != DOWN ? this.getCounterClockWise() : this; break;
      case 2: var10000 = this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this; break;
      default: var10000 = null;
    }

    return var10000;
  }

  public Direction getClockWise() {
    Direction var10000;
    switch (this.ordinal()) {
      case 2: var10000 = EAST; break;
      case 3: var10000 = WEST; break;
      case 4: var10000 = NORTH; break;
      case 5: var10000 = SOUTH; break;
      default: throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
    }

    return var10000;
  }

  private Direction getClockWiseX() {
    Direction var10000;
    switch (this.ordinal()) {
      case 0: var10000 = SOUTH; break;
      case 1: var10000 = NORTH; break;
      case 2: var10000 = DOWN; break;
      case 3: var10000 = UP; break;
      default: throw new IllegalStateException("Unable to get X-rotated facing of " + this);
    }

    return var10000;
  }

  private Direction getCounterClockWiseX() {
    Direction var10000;
    switch (this.ordinal()) {
      case 0: var10000 = NORTH; break;
      case 1: var10000 = SOUTH; break;
      case 2: var10000 = UP; break;
      case 3: var10000 = DOWN; break;
      default: throw new IllegalStateException("Unable to get X-rotated facing of " + this);
    }

    return var10000;
  }

  private Direction getClockWiseZ() {
    Direction var10000;
    switch (this.ordinal()) {
      case 0:
        var10000 = WEST;
        break;
      case 1:
        var10000 = EAST;
        break;
      case 2:
      case 3:
      case 4:
        var10000 = UP;
        break;
      case 5:
        var10000 = DOWN;
        break;
      default:
        throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
    }

    return var10000;
  }

  private Direction getCounterClockWiseZ() {
    Direction var10000;
    switch (this.ordinal()) {
      case 0:
        var10000 = EAST;
        break;
      case 1:
        var10000 = WEST;
        break;
      case 2:
      case 3:
      case 4:
        var10000 = DOWN;
        break;
      case 5:
        var10000 = UP;
        break;
      default:
        throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
    }

    return var10000;
  }

  public Direction getCounterClockWise() {
    Direction var10000;
    switch (this.ordinal()) {
      case 2: var10000 = WEST; break;
      case 3: var10000 = EAST; break;
      case 4: var10000 = SOUTH; break;
      case 5: var10000 = NORTH; break;
      default: throw new IllegalStateException("Unable to get CCW facing of " + this);
    }

    return var10000;
  }

  public int getStepX() {
    return (int) this.normal.getX();
  }

  public int getStepY() {
    return (int) this.normal.getY();
  }

  public int getStepZ() {
    return (int) this.normal.getZ();
  }

  public String getName() {
    return this.name;
  }

  public EnumAxis getAxis() {
    return this.axis;
  }


  public static Direction from3DDataValue(int var0) {
    return BY_3D_DATA[MathHelper.abs(var0 % BY_3D_DATA.length)];
  }

  public static Direction from2DDataValue(int var0) {
    return BY_2D_DATA[MathHelper.abs(var0 % BY_2D_DATA.length)];
  }

  @Nullable
  public static Direction fromDelta(int var0, int var1, int var2) {
    if (var0 == 0) {
      if (var1 == 0) {
        if (var2 > 0) {
          return SOUTH;
        }

        if (var2 < 0) {
          return NORTH;
        }
      } else if (var2 == 0) {
        if (var1 > 0) {
          return UP;
        }

        return DOWN;
      }
    } else if (var1 == 0 && var2 == 0) {
      if (var0 > 0) {
        return EAST;
      }

      return WEST;
    }

    return null;
  }

  public static Direction fromYRot(double var0) {
    return from2DDataValue(MathHelper.floor(var0 / 90.0 + 0.5) & 3);
  }

  public static Direction fromAxisAndDirection(EnumAxis var0, EnumAxisDirection var1) {
    Direction var10000;
    switch (var0.ordinal()) {
      case 0:  var10000 = var1 == Direction.EnumAxisDirection.POSITIVE ? EAST : WEST; break;
      case 1: var10000 = var1 == Direction.EnumAxisDirection.POSITIVE ? UP : DOWN; break;
      case 2: var10000 = var1 == Direction.EnumAxisDirection.POSITIVE ? SOUTH : NORTH; break;
      default: var10000 = null;
    }
    return var10000;
  }

  public float toYRot() {
    return (float)((this.data2d & 3) * 90);
  }

  public static Direction getNearest(double var0, double var2, double var4) {
    return getNearest((float)var0, (float)var2, (float)var4);
  }

  public static Direction getNearest(float var0, float var1, float var2) {
    Direction var3 = NORTH;
    float var4 = Float.MIN_VALUE;
    Direction[] var5 = VALUES;
    int var6 = var5.length;

    for(int var7 = 0; var7 < var6; ++var7) {
      Direction var8 = var5[var7];
      float var9 = var0 * (float)var8.normal.getX() + var1 * (float)var8.normal.getY() + var2 * (float)var8.normal.getZ();
      if (var9 > var4) {
        var4 = var9;
        var3 = var8;
      }
    }

    return var3;
  }

  public static Direction getNearest(Vec3D var0) {
    return getNearest(var0.x, var0.y, var0.z);
  }

  public String toString() {
    return this.name;
  }

  public String getSerializedName() {
    return this.name;
  }

  public static Direction get(EnumAxisDirection var0, EnumAxis var1) {
    Direction[] var2 = VALUES;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Direction var5 = var2[var4];
      if (var5.getAxisDirection() == var0 && var5.getAxis() == var1) {
        return var5;
      }
    }

    String var10002 = String.valueOf(var0);
    throw new IllegalArgumentException("No such direction: " + var10002 + " " + var1);
  }

  public Vector getNormal() {
    return this.normal;
  }

  public boolean isFacingAngle(float var0) {
    float var1 = var0 * 0.017453292F;
    float var2 = -MathHelper.sin(var1);
    float var3 = MathHelper.cos(var1);
    return (float)this.normal.getX() * var2 + (float)this.normal.getZ() * var3 > 0.0F;
  }

  public enum EnumAxis implements Predicate<Direction> {
    X("x") {
      public int choose(int var0, int var1, int var2) {
        return var0;
      }

      public double choose(double var0, double var2, double var4) {
        return var0;
      }
    },
    Y("y") {
      public int choose(int var0, int var1, int var2) {
        return var1;
      }

      public double choose(double var0, double var2, double var4) {
        return var2;
      }
    },
    Z("z") {
      public int choose(int var0, int var1, int var2) {
        return var2;
      }

      public double choose(double var0, double var2, double var4) {
        return var4;
      }
    };

    public static final EnumAxis[] VALUES = values();
    private final String name;

    EnumAxis(final String var2) {
      this.name = var2;
    }

    public String getName() {
      return this.name;
    }

    public boolean isVertical() {
      return this == Y;
    }

    public boolean isHorizontal() {
      return this == X || this == Z;
    }

    public String toString() {
      return this.name;
    }

    public boolean test(@Nullable Direction var0) {
      return var0 != null && var0.getAxis() == this;
    }

    public EnumDirectionLimit getPlane() {
      EnumDirectionLimit var10000;
      switch (this.ordinal()) {
        case 0:
        case 2:
          var10000 = Direction.EnumDirectionLimit.HORIZONTAL;
          break;
        case 1:
          var10000 = Direction.EnumDirectionLimit.VERTICAL;
          break;
        default:
          var10000 = null;
          break;
      }

      return var10000;
    }

    public String getSerializedName() {
      return this.name;
    }

    public abstract int choose(int var1, int var2, int var3);

    public abstract double choose(double var1, double var3, double var5);
  }

  public enum EnumAxisDirection {
    POSITIVE(1, "Towards positive"),
    NEGATIVE(-1, "Towards negative");

    private final int step;
    private final String name;

    EnumAxisDirection(final int var2, final String var3) {
      this.step = var2;
      this.name = var3;
    }

    public int getStep() {
      return this.step;
    }

    public String getName() {
      return this.name;
    }

    public String toString() {
      return this.name;
    }

    public EnumAxisDirection opposite() {
      return this == POSITIVE ? NEGATIVE : POSITIVE;
    }
  }

  public enum EnumDirectionLimit implements Iterable<Direction>, Predicate<Direction> {
    HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new EnumAxis[]{Direction.EnumAxis.X, Direction.EnumAxis.Z}),
    VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new EnumAxis[]{Direction.EnumAxis.Y});

    private final Direction[] faces;
    private final EnumAxis[] axis;

    EnumDirectionLimit(final Direction[] var2, final EnumAxis[] var3) {
      this.faces = var2;
      this.axis = var3;
    }

    public boolean test(Direction var0) {
      return var0 != null && var0.getAxis().getPlane() == this;
    }

    public Iterator<Direction> iterator() {
      return Iterators.forArray(this.faces);
    }

    public Stream<Direction> stream() {
      return Arrays.stream(this.faces);
    }

    public int length() {
      return this.faces.length;
    }
  }
}
