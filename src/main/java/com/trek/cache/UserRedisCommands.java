package com.trek.cache;

import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.annotation.Command;

public interface UserRedisCommands extends Commands {

  @Command("SET ?0 ?1")
  String setUser(String userId, String user);

  @Command("GET ?0")
  String getUser(String userId);

}
