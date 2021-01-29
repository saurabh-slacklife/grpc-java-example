package com.trek.cache;

import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.annotation.Command;
import io.lettuce.core.dynamic.annotation.Key;
import io.lettuce.core.dynamic.annotation.Value;

public interface UserRedisCommands extends Commands {

  @Command("SET")
  byte[] saveUser(@Key byte[] userId, @Value byte[] user);

  @Command("SET")
  byte[] updateUser(@Key byte[] userId, @Value byte[] user);

  @Command("GET")
  byte[] retrieveUser(@Key byte[] userId);

  @Command("DEL")
  byte[] deleteUser(@Key byte[] userId);

}
