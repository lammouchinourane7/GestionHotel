package com.hotel.reservationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String HOTEL_EVENTS_EXCHANGE = "hotel.events.exchange";
    public static final String CLIENT_DELETED_QUEUE = "reservation.client.deleted.queue";
    public static final String ROOM_UNAVAILABLE_QUEUE = "reservation.room.unavailable.queue";
    public static final String CLIENT_DELETED_ROUTING_KEY = "client.deleted";
    public static final String ROOM_UNAVAILABLE_ROUTING_KEY = "room.unavailable";
    public static final String RESERVATION_CREATED_ROUTING_KEY = "reservation.created";
    public static final String RESERVATION_CANCELLED_ROUTING_KEY = "reservation.cancelled";
    public static final String RESERVATION_CONFIRMED_ROUTING_KEY = "reservation.confirmed";

    @Bean
    public TopicExchange hotelEventsExchange() {
        return new TopicExchange(HOTEL_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue clientDeletedQueue() {
        return new Queue(CLIENT_DELETED_QUEUE, true);
    }

    @Bean
    public Queue roomUnavailableQueue() {
        return new Queue(ROOM_UNAVAILABLE_QUEUE, true);
    }

    @Bean
    public Binding clientDeletedBinding(Queue clientDeletedQueue, TopicExchange hotelEventsExchange) {
        return BindingBuilder.bind(clientDeletedQueue).to(hotelEventsExchange).with(CLIENT_DELETED_ROUTING_KEY);
    }

    @Bean
    public Binding roomUnavailableBinding(Queue roomUnavailableQueue, TopicExchange hotelEventsExchange) {
        return BindingBuilder.bind(roomUnavailableQueue).to(hotelEventsExchange).with(ROOM_UNAVAILABLE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
