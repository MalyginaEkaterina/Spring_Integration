package ru.geekbrains.spring;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class ItBlogProducer {
    private static final String EXCHANGE_NAME = "itBlog";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            Scanner in = new Scanner(System.in);
            while (true) {
                String command = in.next();
                if (command.equals("exit")) {
                    in.close();
                    break;
                } else {
                    String msg = in.nextLine().trim();
                    channel.basicPublish(EXCHANGE_NAME, command, null, msg.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + msg + "' to '" + command + "'");
                }
            }
        }
    }
}
