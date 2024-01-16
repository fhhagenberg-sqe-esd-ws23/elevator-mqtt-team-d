package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.plugins.MockMaker;
import sqelevator.IElevator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static org.mockito.Mockito.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import sqelevator.IElevator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorAlgorithmTest {
    private static MqttHandler mqttTestClient;
    private static ElevatorMQTTAdapter elevatorMQTTAdapter;

    private static ElevatorAlgorithm elevatorAlgo;
    private static Properties elevatorProps;
    private static ElevatorManager elevatorManager;

    public MqttHandler mqttClient;
    ElevatorAlgorithm algo;
    @BeforeEach
    @Test
    public void setUp() throws IOException, MqttException {
        // Get props
        String rootPath = System.getProperty("user.dir");
        String appConfigPath = rootPath + "/properties/IElevator.properties";
        elevatorProps = new Properties();
        elevatorProps.load(new FileInputStream(appConfigPath));
        // Create Algo
        algo = new ElevatorAlgorithm(elevatorProps);
        // Create mqtt client
        String brokerUrl = elevatorProps.getProperty("mqtt.broker.url");
        String clientId = "ElevatorMQTTAdapter"; // You can customize this
        mqttClient = new MqttHandler(brokerUrl,clientId,(topic,msg)->{
            System.out.println("Topic: "+ topic.toString());
            System.out.println("Message: "+ msg.toString());
        });
        if(!mqttClient.client.isConnected()){
            throw new IOException("MQTT Not connected");
        }else{
            System.out.println("MQTT connected");
        }
        // Subscribe to correct channels
/*
        MqttSubscription[] subscriptions = new MqttSubscription[3];
        subscriptions[0] = new MqttSubscription("elevator/control/+");
        subscriptions[1] = new MqttSubscription("hello2");
        subscriptions[2] = new MqttSubscription("hello3");
*/      mqttClient.subscribeToTopic("floor/buttonup/3");
        mqttClient.subscribeToTopic("elevator/control/+");
        mqttClient.subscribeToTopic("elevator/control/0");
    }
    @Test
    public void emptyTest(){
        System.out.println("Empty Algo Test");
    }
    @Test
    public void testMoveElevator() throws MqttException {
        mqttClient.publishOnTopic("floor/buttonup/3","target=4");
        algo.handle();
//        mqttClient.publish("floor/buttonup/3","Hello".getBytes(),1,true);
    }
}

    //TODO Integration test:
    // - InitAdapter -
    // - InitAlgo -
    // - InitRMI -
    // - Manipulate ElevatorManager State setTarget:3
    // - Manipulated ElevatorManager State gets Polled and Published by Adapter via MQTT
    // - Algo Reacts and publishes command for elevator to go (setTarget:3) ... and set it in ElevatorManager
    // - Check ElevatorManagerState in test if modified (getTarget == 3)
    // - Simulate elevator moving by manipulating ElevManagerState
    // - publish new elev location
    // - Should check in Algo if new location has been saved

