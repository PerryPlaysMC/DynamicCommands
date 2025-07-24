package io.dynamicstudios.commands.command.argument.types.helper;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Optional;

public class AxisAlignedBB {
 private static final double EPSILON = 1.0E-7;
 public final double minX;
 public final double minY;
 public final double minZ;
 public final double maxX;
 public final double maxY;
 public final double maxZ;

 public AxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
	this.minX = Math.min(minX, maxX);
	this.minY = Math.min(minY, maxY);
	this.minZ = Math.min(minZ, maxZ);
	this.maxX = Math.max(minX, maxX);
	this.maxY = Math.max(minY, maxY);
	this.maxZ = Math.max(minZ, maxZ);
 }

 public AxisAlignedBB(Location var0) {
	this(var0.getX(), var0.getY(), var0.getZ(), var0.getX() + 1, var0.getY() + 1, var0.getZ() + 1);
 }

 public AxisAlignedBB(Vec3D var0, Vec3D var1) {
	this(var0.x, var0.y, var0.z, var1.x, var1.y, var1.z);
 }

 public AxisAlignedBB(BoundingBox boundingBox) {
	this(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
 }

 public static AxisAlignedBB unitCubeFromLowerCorner(Vec3D var0) {
	return new AxisAlignedBB(var0.x, var0.y, var0.z, var0.x + 1.0, var0.y + 1.0, var0.z + 1.0);
 }

 public static AxisAlignedBB encapsulatingFullBlocks(Location var0, Location var1) {
	return new AxisAlignedBB(Math.min(var0.getX(), var1.getX()), Math.min(var0.getY(), var1.getY()), Math.min(var0.getZ(), var1.getZ()), Math.max(var0.getX(), var1.getX()) + 1, Math.max(var0.getY(), var1.getY()) + 1, Math.max(var0.getZ(), var1.getZ()) + 1);
 }

 public AxisAlignedBB setMinX(double var0) {
	return new AxisAlignedBB(var0, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
 }

 public AxisAlignedBB setMinY(double var0) {
	return new AxisAlignedBB(this.minX, var0, this.minZ, this.maxX, this.maxY, this.maxZ);
 }

 public AxisAlignedBB setMinZ(double var0) {
	return new AxisAlignedBB(this.minX, this.minY, var0, this.maxX, this.maxY, this.maxZ);
 }

 public AxisAlignedBB setMaxX(double var0) {
	return new AxisAlignedBB(this.minX, this.minY, this.minZ, var0, this.maxY, this.maxZ);
 }

 public AxisAlignedBB setMaxY(double var0) {
	return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, var0, this.maxZ);
 }

 public AxisAlignedBB setMaxZ(double var0) {
	return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, var0);
 }

 public boolean equals(Object var0) {
	if(this == var0) {
	 return true;
	} else if(!(var0 instanceof AxisAlignedBB)) {
	 return false;
	} else {
	 AxisAlignedBB var1 = (AxisAlignedBB) var0;
	 if(Double.compare(var1.minX, this.minX) != 0) {
		return false;
	 } else if(Double.compare(var1.minY, this.minY) != 0) {
		return false;
	 } else if(Double.compare(var1.minZ, this.minZ) != 0) {
		return false;
	 } else if(Double.compare(var1.maxX, this.maxX) != 0) {
		return false;
	 } else if(Double.compare(var1.maxY, this.maxY) != 0) {
		return false;
	 } else {
		return Double.compare(var1.maxZ, this.maxZ) == 0;
	 }
	}
 }

 public int hashCode() {
	long var0 = Double.doubleToLongBits(this.minX);
	int var2 = (int) (var0 ^ var0 >>> 32);
	var0 = Double.doubleToLongBits(this.minY);
	var2 = 31 * var2 + (int) (var0 ^ var0 >>> 32);
	var0 = Double.doubleToLongBits(this.minZ);
	var2 = 31 * var2 + (int) (var0 ^ var0 >>> 32);
	var0 = Double.doubleToLongBits(this.maxX);
	var2 = 31 * var2 + (int) (var0 ^ var0 >>> 32);
	var0 = Double.doubleToLongBits(this.maxY);
	var2 = 31 * var2 + (int) (var0 ^ var0 >>> 32);
	var0 = Double.doubleToLongBits(this.maxZ);
	var2 = 31 * var2 + (int) (var0 ^ var0 >>> 32);
	return var2;
 }

 public AxisAlignedBB contract(double var0, double var2, double var4) {
	double var6 = this.minX;
	double var8 = this.minY;
	double var10 = this.minZ;
	double var12 = this.maxX;
	double var14 = this.maxY;
	double var16 = this.maxZ;
	if(var0 < 0.0) {
	 var6 -= var0;
	} else if(var0 > 0.0) {
	 var12 -= var0;
	}

	if(var2 < 0.0) {
	 var8 -= var2;
	} else if(var2 > 0.0) {
	 var14 -= var2;
	}

	if(var4 < 0.0) {
	 var10 -= var4;
	} else if(var4 > 0.0) {
	 var16 -= var4;
	}

	return new AxisAlignedBB(var6, var8, var10, var12, var14, var16);
 }

 public AxisAlignedBB expandTowards(Vec3D var0) {
	return this.expandTowards(var0.x, var0.y, var0.z);
 }

 public AxisAlignedBB expandTowards(double var0, double var2, double var4) {
	double var6 = this.minX;
	double var8 = this.minY;
	double var10 = this.minZ;
	double var12 = this.maxX;
	double var14 = this.maxY;
	double var16 = this.maxZ;
	if(var0 < 0.0) {
	 var6 += var0;
	} else if(var0 > 0.0) {
	 var12 += var0;
	}

	if(var2 < 0.0) {
	 var8 += var2;
	} else if(var2 > 0.0) {
	 var14 += var2;
	}

	if(var4 < 0.0) {
	 var10 += var4;
	} else if(var4 > 0.0) {
	 var16 += var4;
	}

	return new AxisAlignedBB(var6, var8, var10, var12, var14, var16);
 }

 public AxisAlignedBB inflate(double var0, double var2, double var4) {
	double var6 = this.minX - var0;
	double var8 = this.minY - var2;
	double var10 = this.minZ - var4;
	double var12 = this.maxX + var0;
	double var14 = this.maxY + var2;
	double var16 = this.maxZ + var4;
	return new AxisAlignedBB(var6, var8, var10, var12, var14, var16);
 }

 public AxisAlignedBB inflate(double var0) {
	return this.inflate(var0, var0, var0);
 }

 public AxisAlignedBB intersect(AxisAlignedBB var0) {
	double var1 = Math.max(this.minX, var0.minX);
	double var3 = Math.max(this.minY, var0.minY);
	double var5 = Math.max(this.minZ, var0.minZ);
	double var7 = Math.min(this.maxX, var0.maxX);
	double var9 = Math.min(this.maxY, var0.maxY);
	double var11 = Math.min(this.maxZ, var0.maxZ);
	return new AxisAlignedBB(var1, var3, var5, var7, var9, var11);
 }

 public AxisAlignedBB minmax(AxisAlignedBB var0) {
	double var1 = Math.min(this.minX, var0.minX);
	double var3 = Math.min(this.minY, var0.minY);
	double var5 = Math.min(this.minZ, var0.minZ);
	double var7 = Math.max(this.maxX, var0.maxX);
	double var9 = Math.max(this.maxY, var0.maxY);
	double var11 = Math.max(this.maxZ, var0.maxZ);
	return new AxisAlignedBB(var1, var3, var5, var7, var9, var11);
 }

 public AxisAlignedBB move(double var0, double var2, double var4) {
	return new AxisAlignedBB(this.minX + var0, this.minY + var2, this.minZ + var4, this.maxX + var0, this.maxY + var2, this.maxZ + var4);
 }

 public AxisAlignedBB move(Location var0) {
	return new AxisAlignedBB(this.minX + var0.getX(), this.minY + var0.getY(), this.minZ + var0.getZ(), this.maxX + var0.getX(), this.maxY + var0.getY(), this.maxZ + var0.getZ());
 }

 public AxisAlignedBB move(Vec3D var0) {
	return this.move(var0.x, var0.y, var0.z);
 }

 public AxisAlignedBB move(Vector var0) {
	return this.move(var0.getX(), var0.getY(), var0.getZ());
 }

 public boolean intersects(AxisAlignedBB var0) {
	return this.intersects(var0.minX, var0.minY, var0.minZ, var0.maxX, var0.maxY, var0.maxZ);
 }

 public boolean intersects(double var0, double var2, double var4, double var6, double var8, double var10) {
	return this.minX < var6 && this.maxX > var0 && this.minY < var8 && this.maxY > var2 && this.minZ < var10 && this.maxZ > var4;
 }

 public boolean intersects(Vec3D var0, Vec3D var1) {
	return this.intersects(Math.min(var0.x, var1.x), Math.min(var0.y, var1.y), Math.min(var0.z, var1.z), Math.max(var0.x, var1.x), Math.max(var0.y, var1.y), Math.max(var0.z, var1.z));
 }

 public boolean contains(Vec3D var0) {
	return this.contains(var0.x, var0.y, var0.z);
 }

 public boolean contains(double var0, double var2, double var4) {
	return var0 >= this.minX && var0 < this.maxX && var2 >= this.minY && var2 < this.maxY && var4 >= this.minZ && var4 < this.maxZ;
 }

 public double getSize() {
	double var0 = this.getXsize();
	double var2 = this.getYsize();
	double var4 = this.getZsize();
	return (var0 + var2 + var4) / 3.0;
 }

 public double getXsize() {
	return this.maxX - this.minX;
 }

 public double getYsize() {
	return this.maxY - this.minY;
 }

 public double getZsize() {
	return this.maxZ - this.minZ;
 }

 public AxisAlignedBB deflate(double var0, double var2, double var4) {
	return this.inflate(-var0, -var2, -var4);
 }

 public AxisAlignedBB deflate(double var0) {
	return this.inflate(-var0);
 }

 public Optional<Vec3D> clip(Vec3D var0, Vec3D var1) {
	double[] var2 = new double[]{1.0};
	double var3 = var1.x - var0.x;
	double var5 = var1.y - var0.y;
	double var7 = var1.z - var0.z;
	Direction var9 = getDirection(this, var0, var2, null, var3, var5, var7);
	if(var9 == null) {
	 return Optional.empty();
	} else {
	 double var10 = var2[0];
	 return Optional.of(var0.add(var10 * var3, var10 * var5, var10 * var7));
	}
 }

 private static Direction getDirection(AxisAlignedBB var0, Vec3D var1, double[] var2, Direction var3, double var4, double var6, double var8) {
	if(var4 > 1.0E-7) {
	 var3 = clipPoint(var2, var3, var4, var6, var8, var0.minX, var0.minY, var0.maxY, var0.minZ, var0.maxZ, Direction.WEST, var1.x, var1.y, var1.z);
	} else if(var4 < -1.0E-7) {
	 var3 = clipPoint(var2, var3, var4, var6, var8, var0.maxX, var0.minY, var0.maxY, var0.minZ, var0.maxZ, Direction.EAST, var1.x, var1.y, var1.z);
	}

	if(var6 > 1.0E-7) {
	 var3 = clipPoint(var2, var3, var6, var8, var4, var0.minY, var0.minZ, var0.maxZ, var0.minX, var0.maxX, Direction.DOWN, var1.y, var1.z, var1.x);
	} else if(var6 < -1.0E-7) {
	 var3 = clipPoint(var2, var3, var6, var8, var4, var0.maxY, var0.minZ, var0.maxZ, var0.minX, var0.maxX, Direction.UP, var1.y, var1.z, var1.x);
	}

	if(var8 > 1.0E-7) {
	 var3 = clipPoint(var2, var3, var8, var4, var6, var0.minZ, var0.minX, var0.maxX, var0.minY, var0.maxY, Direction.NORTH, var1.z, var1.x, var1.y);
	} else if(var8 < -1.0E-7) {
	 var3 = clipPoint(var2, var3, var8, var4, var6, var0.maxZ, var0.minX, var0.maxX, var0.minY, var0.maxY, Direction.SOUTH, var1.z, var1.x, var1.y);
	}

	return var3;
 }

 private static Direction clipPoint(double[] var0, Direction var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, Direction var18, double var19, double var21, double var23) {
	double var25 = (var8 - var19) / var2;
	double var27 = var21 + var25 * var4;
	double var29 = var23 + var25 * var6;
	if(0.0 < var25 && var25 < var0[0] && var10 - 1.0E-7 < var27 && var27 < var12 + 1.0E-7 && var14 - 1.0E-7 < var29 && var29 < var16 + 1.0E-7) {
	 var0[0] = var25;
	 return var18;
	} else {
	 return var1;
	}
 }

 public double distanceToSqr(Vec3D var0) {
	double var1 = Math.max(Math.max(this.minX - var0.x, var0.x - this.maxX), 0.0);
	double var3 = Math.max(Math.max(this.minY - var0.y, var0.y - this.maxY), 0.0);
	double var5 = Math.max(Math.max(this.minZ - var0.z, var0.z - this.maxZ), 0.0);
	return MathHelper.lengthSquared(var1, var3, var5);
 }

 public String toString() {
	return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
 }

 public boolean hasNaN() {
	return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
 }

 public Vec3D getCenter() {
	return new Vec3D(MathHelper.lerp(0.5, this.minX, this.maxX), MathHelper.lerp(0.5, this.minY, this.maxY), MathHelper.lerp(0.5, this.minZ, this.maxZ));
 }

 public Vec3D getBottomCenter() {
	return new Vec3D(MathHelper.lerp(0.5, this.minX, this.maxX), this.minY, MathHelper.lerp(0.5, this.minZ, this.maxZ));
 }

 public Vec3D getMinPosition() {
	return new Vec3D(this.minX, this.minY, this.minZ);
 }

 public Vec3D getMaxPosition() {
	return new Vec3D(this.maxX, this.maxY, this.maxZ);
 }

 public static AxisAlignedBB ofSize(Vec3D var0, double var1, double var3, double var5) {
	return new AxisAlignedBB(var0.x - var1 / 2.0, var0.y - var3 / 2.0, var0.z - var5 / 2.0, var0.x + var1 / 2.0, var0.y + var3 / 2.0, var0.z + var5 / 2.0);
 }
}
