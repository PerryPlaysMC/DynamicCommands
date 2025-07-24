package io.dynamicstudios.commands.util;

public class Pair<K,V> {

 K key;
 V value;

 public Pair(K key, V value) {
	this.key = key;
	this.value = value;
 }


 public K key() {
	return key;
 }

 public Pair<K,V> key(K key) {
	this.key = key;
	return this;
 }

 public V value() {
	return value;
 }

 public Pair<K,V> value(V value) {
	this.value = value;
	return this;
 }
}
