package com.trek.dao;

import com.trek.cache.UserRedisCommands;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.dynamic.RedisCommandFactory;
import io.lettuce.core.resource.DefaultClientResources;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RedisServiceFactory {

  private static int DEFAULT_REDIS_PORT = 6379;
  private static int DEFAULT_REDIS_DB = 0;

  private RedisClient redisClient;

  public RedisServiceFactory(String host, int port, int database) {
    this.redisClient = this.initializeRedisClient(host, port, database);
  }

  public RedisServiceFactory(String host) {
    this.redisClient = this.initializeRedisClient(host, DEFAULT_REDIS_PORT, DEFAULT_REDIS_DB );
  }

  private RedisClient initializeRedisClient(String host, int port, int database) {
    RedisURI redisURI = RedisURI.Builder.redis(host, port).withDatabase(database).build();
    return RedisClient.create(DefaultClientResources.builder().build(), redisURI);
  }

  public RedisCommandFactory getDefaultCommandFactory() {
    return new RedisCommandFactory(this.redisClient
        .connect());
  }

  public RedisCommandFactory getStringByteCodecFactory() {
    return new RedisCommandFactory(this.redisClient
        .connect(), Arrays.asList(new StringCodec(StandardCharsets.UTF_8), new ByteArrayCodec()));
  }

  public RedisCommandFactory getByteCodecFactory() {
    return new RedisCommandFactory(this.redisClient
        .connect(new ByteArrayCodec()));
  }

  public RedisCommandFactory getStringCodecFactory() {
    return new RedisCommandFactory(this.redisClient
        .connect(new StringCodec(StandardCharsets.UTF_8)));
  }



}
