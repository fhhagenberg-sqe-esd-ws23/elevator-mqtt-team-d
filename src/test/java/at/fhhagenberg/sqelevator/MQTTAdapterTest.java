/*
package at.fhhagenberg.sqelevator;

import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.*;
import sqelevator.IElevator;

import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import static org.mockito.Mockito.*;

public class MQTTAdapterTest {

    @Mock
    private IElevator mockElevator;

    @Mock
    private MqttClient mockMqttClient;

    @InjectMocks
    private ElevatorMQTTAdapter elevatorMQTTAdapter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Get properties
        String rootPath = System.getProperty("user.dir");
        String appConfigPath = rootPath + "/properties/IElevator.properties";

        Properties elevatorProps = new Properties();
        elevatorProps.load(new FileInputStream(appConfigPath));

        // Create a properties object with necessary properties
        //Properties properties = new Properties();
        //properties.setProperty("rmi.url", elevatorProps.getProperty("rmi.url"));
        //properties.setProperty("mqtt.broker.url",elevatorProps.getProperty("rmi.url"));
        //properties.setProperty("mqtt.topic.control", elevatorProps.getProperty("rmi.url"));
        //properties.setProperty("polling.interval", "1000");

        // Use the properties in the ElevatorMQTTAdapter constructor
        elevatorMQTTAdapter = new ElevatorMQTTAdapter(elevatorProps);
    }

    @Test
    public void testElevatorPositionPublishedToMQTT() throws RemoteException, MqttException {
        // Arrange
        when(mockElevator.getElevatorPosition(0)).thenReturn(5);

        // Act
        elevatorMQTTAdapter.handle();  // You may need to adjust this depending on how your handle method works

        // Assert

        ArgumentCaptor<MqttMessage> messageCaptor = ArgumentCaptor.forClass(MqttMessage.class);
        verify(mockMqttClient).publish(eq("elevator/position"), messageCaptor.capture());
        String publishedMessage = new String(messageCaptor.getValue().getPayload());
        System.out.println(publishedMessage);
        assertEquals("5", publishedMessage);
    }



    @Test
    public void testReceivingMessageOnSubscribedTopic() throws MqttException {
        // Arrange
        String subscribedTopic = "subscribed/topic";
        byte[] payload = "Received Test Message".getBytes();
        MqttMessage receivedMessage = new MqttMessage(payload);

        doAnswer(invocation -> {
            IMqttMessageListener messageListener = invocation.getArgument(1);
            messageListener.messageArrived(subscribedTopic, receivedMessage);
            return null;
        }).when(mockMqttClient).subscribe(eq(subscribedTopic), any(IMqttMessageListener.class));

        // Assume ElevatorMQTTAdapter is already subscribed to the topic
        // You can invoke the subscription process here if it's part of the test setup
        try{
            // Act
            // Simulate the reception of the message
            // This is typically done by some part of your code that invokes the subscription
            // For this example, we manually trigger the message arrival
            IMqttMessageListener listener = elevatorMQTTAdapter.new ControlMessageListener();
            listener.messageArrived(subscribedTopic, receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Assert
        // Add assertions here to verify that the message was processed correctly
        // This depends on the specifics of how your ElevatorMQTTAdapter handles messages
    }

// Add more test methods here
    */
/**
     * Test case for verifying that a control message sets the target floor correctly.
     *
     * Test Steps:
     * 1. Arrange: Set up the target floor and create a control message with the target floor value.
     *    Create a ControlMessageListener instance to simulate the MQTT message arrival.
     * 2. Act: Trigger the messageArrived method on the ControlMessageListener with the control message.
     * 3. Assert: Verify that the Elevator instance's setTarget method is called with the expected parameters.
     *
     * @throws RemoteException if there is a remote communication error.
     * @throws MqttException if there is an MQTT-related error.
     *//*

    */
/*@Test
    public void testControlMessageSetsTargetFloor() throws RemoteException, MqttException {
        // Arrange
        int targetFloor = 3;
        MqttMessage controlMessage = new MqttMessage(Integer.toString(targetFloor).getBytes());
        ControlMessageListener listener = elevatorMQTTAdapter.new ControlMessageListener();

        // Act
        listener.messageArrived("elevator/control", controlMessage);

        // Assert
        verify(mockElevator).setTarget(0, targetFloor);
    }*//*


    @Test
    public void testShutdownDisconnectsMqttClient() throws MqttException {
        // Act
        elevatorMQTTAdapter.shutdown();

        // Assert
        verify(mockMqttClient).disconnect();
    }
}*/
