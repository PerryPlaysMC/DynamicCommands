package io.dynamicstudios.commands.command.argument.types.helper;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Vec3D {
 public static final Vec3D ZERO;
 public final double x;
 public final double y;
 public final double z;

 public Vec3D(Vector vector) {
	this(vector.getX(), vector.getY(), vector.getZ());
 }

 public static Vec3D fromRGB24(int var0) {
	double var1 = (double) (var0 >> 16 & 255) / 255.0;
	double var3 = (double) (var0 >> 8 & 255) / 255.0;
	double var5 = (double) (var0 & 255) / 255.0;
	return new Vec3D(var1, var3, var5);
 }

 public static Vec3D atLowerCornerOf(Location var0) {
	return new Vec3D(var0.getX(), var0.getY(), var0.getZ());
 }

 public static Vec3D atLowerCornerWithOffset(Location var0, double var1, double var3, double var5) {
	return new Vec3D(var0.getX() + var1, var0.getY() + var3, var0.getZ() + var5);
 }

 public static Vec3D atCenterOf(Location var0) {
	return atLowerCornerWithOffset(var0, 0.5, 0.5, 0.5);
 }

 public static Vec3D atBottomCenterOf(Location var0) {
	return atLowerCornerWithOffset(var0, 0.5, 0.0, 0.5);
 }

 public static Vec3D upFromBottomCenterOf(Location var0, double var1) {
	return atLowerCornerWithOffset(var0, 0.5, var1, 0.5);
 }

 public Vec3D(double var0, double var2, double var4) {
	this.x = var0;
	this.y = var2;
	this.z = var4;
 }

 public Vec3D vectorTo(Vec3D var0) {
	return new Vec3D(var0.x - this.x, var0.y - this.y, var0.z - this.z);
 }

 public Vec3D normalize() {
	double var0 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	return var0 < 1.0E-4 ? ZERO : new Vec3D(this.x / var0, this.y / var0, this.z / var0);
 }

 public double dot(Vec3D var0) {
	return this.x * var0.x + this.y * var0.y + this.z * var0.z;
 }

 public Vec3D cross(Vec3D var0) {
	return new Vec3D(this.y * var0.z - this.z * var0.y, this.z * var0.x - this.x * var0.z, this.x * var0.y - this.y * var0.x);
 }

 public Vec3D subtract(Vec3D var0) {
	return this.subtract(var0.x, var0.y, var0.z);
 }

 public Vec3D subtract(double var0, double var2, double var4) {
	return this.add(-var0, -var2, -var4);
 }

 public Vec3D add(Vec3D var0) {
	return this.add(var0.x, var0.y, var0.z);
 }

 public Vec3D add(double var0, double var2, double var4) {
	return new Vec3D(this.x + var0, this.y + var2, this.z + var4);
 }

 public boolean closerThan(Vector var0, double var1) {
	return this.distanceToSqr(var0.getX(), var0.getY(), var0.getZ()) < var1 * var1;
 }

 public double distanceTo(Vec3D var0) {
	double var1 = var0.x - this.x;
	double var3 = var0.y - this.y;
	double var5 = var0.z - this.z;
	return Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
 }

 public double distanceToSqr(Vec3D var0) {
	double var1 = var0.x - this.x;
	double var3 = var0.y - this.y;
	double var5 = var0.z - this.z;
	return var1 * var1 + var3 * var3 + var5 * var5;
 }

 public double distanceToSqr(double var0, double var2, double var4) {
	double var6 = var0 - this.x;
	double var8 = var2 - this.y;
	double var10 = var4 - this.z;
	return var6 * var6 + var8 * var8 + var10 * var10;
 }

 public boolean closerThan(Vec3D var0, double var1, double var3) {
	double var5 = var0.x() - this.x;
	double var7 = var0.y() - this.y;
	double var9 = var0.z() - this.z;
	return MathHelper.lengthSquared(var5, var9) < MathHelper.square(var1) && Math.abs(var7) < var3;
 }

 public Vec3D scale(double var0) {
	return this.multiply(var0, var0, var0);
 }

 public Vec3D reverse() {
	return this.scale(-1.0);
 }

 public Vec3D multiply(Vec3D var0) {
	return this.multiply(var0.x, var0.y, var0.z);
 }

 public Vec3D multiply(double var0, double var2, double var4) {
	return new Vec3D(this.x * var0, this.y * var2, this.z * var4);
 }

 public double length() {
	return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
 }

 public double lengthSqr() {
	return this.x * this.x + this.y * this.y + this.z * this.z;
 }

 public double horizontalDistance() {
	return Math.sqrt(this.x * this.x + this.z * this.z);
 }

 public double horizontalDistanceSqr() {
	return this.x * this.x + this.z * this.z;
 }

 public boolean equals(Object var0) {
	if(this == var0) {
	 return true;
	} else if(!(var0 instanceof Vec3D)) {
	 return false;
	} else {
	 Vec3D var1 = (Vec3D) var0;
	 if(Double.compare(var1.x, this.x) != 0) {
		return false;
	 } else if(Double.compare(var1.y, this.y) != 0) {
		return false;
	 } else {
		return Double.compare(var1.z, this.z) == 0;
	 }
	}
 }

 public int hashCode() {
	long var1 = Double.doubleToLongBits(this.x);
	int var0 = (int) (var1 ^ var1 >>> 32);
	var1 = Double.doubleToLongBits(this.y);
	var0 = 31 * var0 + (int) (var1 ^ var1 >>> 32);
	var1 = Double.doubleToLongBits(this.z);
	var0 = 31 * var0 + (int) (var1 ^ var1 >>> 32);
	return var0;
 }

 public String toString() {
	return "(" + this.x + ", " + this.y + ", " + this.z + ")";
 }

 public Vec3D lerp(Vec3D var0, double var1) {
	return new Vec3D(MathHelper.lerp(var1, this.x, var0.x), MathHelper.lerp(var1, this.y, var0.y), MathHelper.lerp(var1, this.z, var0.z));
 }

 public Vec3D xRot(float var0) {
	float var1 = MathHelper.cos(var0);
	float var2 = MathHelper.sin(var0);
	double var3 = this.x;
	double var5 = this.y * (double) var1 + this.z * (double) var2;
	double var7 = this.z * (double) var1 - this.y * (double) var2;
	return new Vec3D(var3, var5, var7);
 }

 public Vec3D yRot(float var0) {
	float var1 = MathHelper.cos(var0);
	float var2 = MathHelper.sin(var0);
	double var3 = this.x * (double) var1 + this.z * (double) var2;
	double var5 = this.y;
	double var7 = this.z * (double) var1 - this.x * (double) var2;
	return new Vec3D(var3, var5, var7);
 }

 public Vec3D zRot(float var0) {
	float var1 = MathHelper.cos(var0);
	float var2 = MathHelper.sin(var0);
	double var3 = this.x * (double) var1 + this.y * (double) var2;
	double var5 = this.y * (double) var1 - this.x * (double) var2;
	double var7 = this.z;
	return new Vec3D(var3, var5, var7);
 }

 public static Vec3D directionFromRotation(float var0, float var1) {
	float var2 = MathHelper.cos(-var1 * 0.017453292F - 3.1415927F);
	float var3 = MathHelper.sin(-var1 * 0.017453292F - 3.1415927F);
	float var4 = -MathHelper.cos(-var0 * 0.017453292F);
	float var5 = MathHelper.sin(-var0 * 0.017453292F);
	return new Vec3D(var3 * var4, var5, var2 * var4);
 }

 public double get(Direction.EnumAxis var0) {
	return var0.choose(this.x, this.y, this.z);
 }

 public final double x() {
	return this.x;
 }

 public final double y() {
	return this.y;
 }

 public final double z() {
	return this.z;
 }

 static {
	ZERO = new Vec3D(0.0, 0.0, 0.0);
 }
}
