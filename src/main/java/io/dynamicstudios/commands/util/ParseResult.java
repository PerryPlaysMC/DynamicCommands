package io.dynamicstudios.commands.util;

public class ParseResult<T> {

 T value;
 ParseStatus status;

 public ParseResult(T value, ParseStatus status) {
	this.value = value;
	this.status = status;
 }

 public T value() {
	return value;
 }

 public ParseStatus status() {
	return status;
 }

 public enum ParseStatus {
	BEGIN,PARSING,PARSED;
 }

}
