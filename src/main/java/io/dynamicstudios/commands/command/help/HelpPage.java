package io.dynamicstudios.commands.command.help;

import io.dynamicstudios.commands.command.DynamicSubCommand;
import io.dynamicstudios.json.DynamicJText;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 09/2022
 **/
public abstract class HelpPage {

  public abstract DynamicJText help(DynamicSubCommand<?> command, int page);

  public boolean isPaginated() {
    return true;
  }

  public <T> List<List<T>> splitList(List<T> list, int n) {
    List<List<T>> result = new ArrayList<>();
    for(int i = 0; i < list.size(); i+=n){
      result.add(list.subList(i, Math.min(i+n, list.size())));
    }
    return result;
  }

}
