/*
 * Copyright 2018 Fs2 Redis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gvolpe.fs2redis

import io.lettuce.core.RedisClient
import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands

object model {

  trait Fs2RedisClient {
    def underlying: RedisClient
  }
  case class DefaultRedisClient(underlying: RedisClient) extends Fs2RedisClient

  trait Fs2RedisChannel[A] {
    def value: A
  }
  case class DefaultChannel(value: String) extends Fs2RedisChannel[String]

  trait Fs2RedisPubSubCommands[K, V] {
    def underlying: RedisPubSubAsyncCommands[K, V]
  }
  case class DefaultPubSubCommands[K, V](underlying: RedisPubSubAsyncCommands[K, V])
      extends Fs2RedisPubSubCommands[K, V]

  trait Fs2RedisCodec[K, V] {
    def underlying: RedisCodec[K, V]
  }
  case class DefaultRedisCodec[K, V](underlying: RedisCodec[K, V]) extends Fs2RedisCodec[K, V]

  case class RedisMessage(value: String) extends AnyVal
  case class Subscriptions[A](channel: Fs2RedisChannel[A], number: Long)
}
