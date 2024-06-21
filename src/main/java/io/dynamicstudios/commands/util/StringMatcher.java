package io.dynamicstudios.commands.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public class StringMatcher {

  private static final HashBasedTable<String, String, Float> CHECKED = HashBasedTable.create();
  private static final float THRESHOLD = (float) 0.50; //50% similarity

  public static boolean test(String str1, String str2) {
    return compare(str1, str2) > THRESHOLD;
  }

  private static Map <Character, Integer> generateCharMap(String str){
    Map <Character, Integer> map = new HashMap<>();
    Integer currentChar;
    for(char c: str.toCharArray()){
      currentChar = map.get(c);
      if(currentChar == null){
        map.put(c, 1);
      } else {
        map.put(c, currentChar+1);
      }
    }
    return map;
  }

  public static float compare(String str, String compareStr){
    if(CHECKED.contains(str, compareStr))return CHECKED.get(str, compareStr);
    if(CHECKED.contains(compareStr, str))return CHECKED.get(compareStr, str);
    Map<Character, Integer> strMap = generateCharMap(str);
    Map<Character, Integer> compareStrMap = generateCharMap(compareStr);
    Set<Character> charSet = compareStrMap.keySet();
    int similarChars = 0;
    int totalStrChars = str.length();
    float thisThreshold;

    if(totalStrChars < compareStrMap.size()) totalStrChars = compareStr.length();
    Iterator<Character> it = charSet.iterator();
    char currentChar;
    Integer currentCountStrMap;
    Integer currentCountCompareStrMap;
    while(it.hasNext()){
      currentChar = it.next();
      currentCountStrMap = strMap.get(currentChar);
      if(currentCountStrMap != null){
        currentCountCompareStrMap = compareStrMap.get(currentChar);
        if (currentCountCompareStrMap >= currentCountStrMap) similarChars += currentCountStrMap;
         else similarChars += currentCountCompareStrMap;
      }
    }

    thisThreshold = ((float) similarChars)/((float) totalStrChars);
    CHECKED.put(str, compareStr, thisThreshold);
    return thisThreshold;
  }

}
