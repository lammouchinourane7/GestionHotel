package com.hotel.roomservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String HOTEL_EVENTS_EXCHANGE = "hotel.events.exchange";
    public static final String ROOM_UPDATED_ROUTING_KEY = "room.updated";
    public static final String ROOM_UNAVAILABLE_ROUTING_KEY = "room.unavailable";

    @Bean
    public TopicExchange hotelEventsExchange() {
        return new TopicExchange(HOTEL_EVENTS_EXCHANGE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
