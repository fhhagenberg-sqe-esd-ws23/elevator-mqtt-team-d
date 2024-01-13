/*package at.fhhagenberg.sqelevator;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.disconnect.Mqtt5Disconnect;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import org.junit.jupiter.api.Test;
import sqelevator.IElevator;



 */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class TestBk {
    /*
        @Test
    public void testPublish() {
        // Set up HiveMQ client
        Mqtt5Client mqttClient = Mqtt5Client.builder()
                .serverHost("broker.hivemq.com")
                .serverPort(1883)
                .build();

        try {
            // Connect to the broker
            Mqtt5Connect connect = Mqtt5Connect.builder().build();
            mqttClient.toBlocking().connect(connect);

            // Publish a message
            String topic = "test/topic";
            String message = "Hello, HiveMQ!";
            Mqtt5Publish publish = Mqtt5Publish.builder().topic(topic).payload(message.getBytes()).build();
            mqttClient.toBlocking().publish(publish);

            // Subscribe to the same topic
            CountDownLatch messageReceivedLatch = new CountDownLatch(1);
            Mqtt5Subscribe subscribe = Mqtt5Subscribe.builder()
                    .topicFilter(topic)
                    .qos(MqttQos.EXACTLY_ONCE)
                    .build();

            mqttClient.toAsync().subscribe(subscribe).whenComplete((subAck, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    System.out.println("Subscribed to topic: " + topic);
                }
            });

            // Wait for the message to be received
            //
            mqttClient.toAsync().publishes(MqttGlobalPublishFilter.ALL).subscribe(publishReceived -> {
                System.out.println("Received message: " + new String(publishReceived.getPayloadAsBytes()));
                messageReceivedLatch.countDown();
            });
            //

    // Add a short delay to allow subscription to take effect
            try {
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Disconnect from the broker
    Mqtt5Disconnect disconnect = Mqtt5Disconnect.builder().build();
            mqttClient.toBlocking().disconnect(disconnect);

    // Wait for the messageReceivedLatch countdown
            messageReceivedLatch.await(5, TimeUnit.SECONDS);

} catch (Exception e) {
        e.printStackTrace();
        }
        }

@Test
public void testConnect() throws InterruptedException {
        Mqtt5Connect connect = Mqtt5Connect.builder().build();
        System.out.println("State: " + mqttClient.getState());
        // Wait for the connection to be established
//        mqttClient.toBlocking().connect(connect).wait();
//        System.out.println("State after: " + mqttClient.getState());
        // Add assertions as needed
        assertEquals(true, mqttClient.getState().isConnected());



        try {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IElevator stub_client = (IElevator) registry.lookup("ElevatorSim");

        // Now you can call RMI functions on the stub
        int committedDirection = 1;
        stub_client.setTarget(0, committedDirection);
        System.out.println("Committed Direction: " + committedDirection);

        assertEquals(1, committedDirection);
        // stub_client = new ElevatorManager();

        // // Get properties
        // String rootPath = System.getProperty("user.dir");
        // String appConfigPath = rootPath + "/properties/IElevator.properties";

        // Properties elevatorProps = new Properties();
        // elevatorProps.load(new FileInputStream(appConfigPath));

        // // Adding floors to the data model
        // int numFloors = Integer.parseInt(elevatorProps.getProperty("numFloors"));
        // for (int i = 0; i < numFloors; i++) {
        //     stub_client.floors.add(new Floor(i + 1)); // Floor numbering starts from 1
        // }

        // // Adding elevators to the data model
        // int numElevators = Integer.parseInt(elevatorProps.getProperty("numElevators"));
        // stub_client.addElevators(numElevators); // Adds 3 elevators

        // // Setting floor height
        // stub_client.setFloorHeight(4);// Example: Each floor is 4 units high


        } catch (Exception e) {
        e.printStackTrace();
        }
        }

@Test
public void testPublishAndSubscribe() {
        try {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IElevator stub_client = (IElevator) registry.lookup("ElevatorManager");

        int committedTarget= stub_client.getTarget(0);
        System.out.println("Committed Direction: " + committedTarget);

        assertEquals(0, committedTarget);


        // Connect to the broker
        Mqtt5Connect connect = Mqtt5Connect.builder().build();
        mqttClient.toBlocking().connect(connect);


        // Publish a message
        Mqtt5Publish publish = Mqtt5Publish.builder().topic("elevator/control").payload("4".getBytes()).build();
        mqttClient.toBlocking().publish(publish);

        // Wait for the message to be received
        mqttClient.toBlocking().subscribeWith()
        .topicFilter("elevator/control")
        .qos(MqttQos.EXACTLY_ONCE)
        .send();

        // Add a short delay to allow subscription to take effect
        try {
        Thread.sleep(1000);
        } catch (InterruptedException e) {
        e.printStackTrace();
        }


        committedTarget= stub_client.getTarget(0);
        System.out.println("Committed Direction: " + committedTarget);
        assertEquals(4, committedTarget);


        // Disconnect from the broker
        Mqtt5Disconnect disconnect = Mqtt5Disconnect.builder().build();
        mqttClient.toBlocking().disconnect(disconnect);
        assertEquals(false, mqttClient.getState().isConnected());
        }
        catch (Exception e) {
        e.printStackTrace();
        }
        }
     */

}
