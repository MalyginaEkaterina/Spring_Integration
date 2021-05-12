package ru.geekbrains.spring;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class ItBlogConsumer {
    private static final String EXCHANGE_NAME = "itBlog";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();
        System.out.println("My queue name: " + queueName);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String topic = delivery.getEnvelope().getRoutingKey();
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "' from '" + topic + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
        System.out.println(" [*] Waiting for messages");

        Scanner in = new Scanner(System.in);
        while (true) {
            String command = in.next();
            if (command.equals("exit")) {
                in.close();
                channel.close();
                connection.close();
                break;
            } else if (command.equals("add_topic")) {
                String topicName = in.next().trim();
                channel.queueBind(queueName, EXCHANGE_NAME, topicName);
            } else if (command.equals("delete_topic")) {
                String topicName = in.next().trim();
                channel.queueUnbind(queueName, EXCHANGE_NAME, topicName);
            }
        }
    }
}
