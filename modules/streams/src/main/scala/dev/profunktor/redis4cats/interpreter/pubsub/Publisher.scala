/*
 * Copyright 2018-2019 ProfunKtor
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

package dev.profunktor.redis4cats.interpreter.pubsub

import cats.effect._
import cats.syntax.functor._
import dev.profunktor.redis4cats.algebra.{ PubSubStats, PublishCommands }
import dev.profunktor.redis4cats.domain.RedisChannel
import dev.profunktor.redis4cats.streams.Subscription
import dev.profunktor.redis4cats.effect.JRFuture
import fs2.Stream
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection

class Publisher[F[_]: ConcurrentEffect: ContextShift, K, V](pubConnection: StatefulRedisPubSubConnection[K, V])
    extends PublishCommands[Stream[F, *], K, V] {

  private[redis4cats] val pubSubStats: PubSubStats[Stream[F, *], K] = new LivePubSubStats(pubConnection)

  override def publish(channel: RedisChannel[K]): Stream[F, V] => Stream[F, Unit] =
    _.evalMap { message =>
      JRFuture { F.delay(pubConnection.async().publish(channel.underlying, message)) }.void
    }

  override def pubSubChannels: Stream[F, List[K]] =
    pubSubStats.pubSubChannels

  override def pubSubSubscriptions(channel: RedisChannel[K]): Stream[F, Subscription[K]] =
    pubSubStats.pubSubSubscriptions(channel)

  override def pubSubSubscriptions(channels: List[RedisChannel[K]]): Stream[F, List[Subscription[K]]] =
    pubSubStats.pubSubSubscriptions(channels)

}
