package com.wutsi.stream.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Consumer
import com.wutsi.stream.Event
import com.wutsi.stream.EventHandler
import com.wutsi.stream.EventStream
import com.wutsi.stream.ObjectMapperBuilder
import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.charset.Charset
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.ExecutorService

class RabbitMQEventStream(
    private val name: String,
    private val uri: URI,
    private val handler: EventHandler,
    private val executorService: ExecutorService,
    private val factory: ConnectionFactory = ConnectionFactory()
) : EventStream {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQEventStream::class.java)
    }

    private val queue: String = "${name}_queue_in"
    private val topic: String = toTopicName(name)
    private val channel: Channel
    private val connection: Connection
    private val mapper: ObjectMapper = ObjectMapperBuilder().build()
    private val consumer: Consumer

    init {
        factory.setUri(uri)
        connection = factory.newConnection(executorService)
        channel = connection.createChannel()

        // Queue
        LOGGER.info("Setup queue: $queue")
        channel.queueDeclare(
            queue,
            true, /* durable */
            false, /* exclusive */
            false, /* autoDelete */
            null
        )

        // Consumer
        consumer = RabbitMQConsumer(handler, mapper, channel)
        channel.basicConsume(queue, false, "", consumer)

        // Topic
        LOGGER.info("Setup topic: $topic")
        channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true)
    }

    override fun close() {
        channel.close()
        connection.close()
    }

    override fun enqueue(type: String, payload: Any) {
        LOGGER.info("enqueue($type, $payload)")

        val event = createEvent(type, payload)
        val json: String = mapper.writeValueAsString(event)
        channel.basicPublish(
            "",
            this.queue,
            null, /* basic-properties */
            json.toByteArray(Charset.forName("utf-8"))
        )
    }

    override fun publish(type: String, payload: Any) {
        LOGGER.info("publish($type, $payload)")

        val event = createEvent(type, payload)
        val json: String = mapper.writeValueAsString(event)
        channel.basicPublish(
            this.topic,
            "", /* routing-key */
            null, /* basic-properties */
            json.toByteArray(Charset.forName("utf-8"))
        )
    }

    override fun subscribeTo(source: String) {
        channel.queueBind(
            queue,
            toTopicName(source),
            ""
        )
    }

    private fun createEvent(type: String, payload: Any) = Event(
        id = UUID.randomUUID().toString(),
        type = type,
        timestamp = OffsetDateTime.now(),
        payload = mapper.writeValueAsString(payload)
    )

    private fun toTopicName(name: String) = "${name}_topic_out"
}
