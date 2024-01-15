package at.fhhagenberg.sqelevator;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import sqelevator.IElevator;

public class Main {
    private static ElevatorMQTTAdapter elevatorMQTTAdapter;

    private static ElevatorAlgorithm elevatorAlgo;
    private static Properties elevatorProps;
    public static void main(String[] args) {

        try {
        // Get properties
        String rootPath = System.getProperty("user.dir");
        String appConfigPath = rootPath + "/properties/IElevator.properties";

        elevatorProps = new Properties();
        elevatorProps.load(new FileInputStream(appConfigPath));

        elevatorMQTTAdapter = new ElevatorMQTTAdapter(elevatorProps);
        elevatorAlgo = new ElevatorAlgorithm(elevatorProps);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        elevatorMQTTAdapter.handle();
        elevatorAlgo.handle();
        System.out.println("Hello, World!");

    }

}
