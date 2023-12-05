package at.fhhagenberg.sqelevator;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.disconnect.Mqtt5Disconnect;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HiveMQTest {

    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883"; // Use HiveMQ Cloud

    private static Mqtt5Client mqttClient;

    @BeforeAll
    public static void setUp() {
        // Initialize Mqtt5Client with HiveMQ Cloud URL
        mqttClient = Mqtt5Client.builder().serverHost("broker.hivemq.com").serverPort(1883).build();
    }

    @Test
    public void testConnect() {
        Mqtt5Connect connect = Mqtt5Connect.builder().build();
        mqttClient.toBlocking().connect(connect);
        // Add assertions as needed
        assertEquals(true, mqttClient.getState().isConnected());
    }

    @Test
    public void testPublishAndSubscribe() {
        // Connect to the broker
        Mqtt5Connect connect = Mqtt5Connect.builder().build();
        mqttClient.toBlocking().connect(connect);

        // Publish a message
        Mqtt5Publish publish = Mqtt5Publish.builder().topic("test/topic").payload("Hello, MQTT!".getBytes()).build();
        mqttClient.toBlocking().publish(publish);

        // Wait for the message to be received
        mqttClient.toBlocking().subscribeWith()
                .topicFilter("test/topic")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();

        // Add a short delay to allow subscription to take effect
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Disconnect from the broker
        Mqtt5Disconnect disconnect = Mqtt5Disconnect.builder().build();
        mqttClient.toBlocking().disconnect(disconnect);
        assertEquals(false, mqttClient.getState().isConnected());
    }

    @AfterAll
    public static void tearDown() {
        // Disconnect and clean up resources
        if (mqttClient.getState().isConnected()) {
            Mqtt5Disconnect disconnect = Mqtt5Disconnect.builder().build();
            mqttClient.toBlocking().disconnect(disconnect);
        }
    }
}
