package at.fhhagenberg.sqelevator;
import org.apache.commons.lang.ObjectUtils.Null;
import org.eclipse.paho.client.mqttv3.*;

import sqelevator.IElevator;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

    public class ElevatorMQTTAdapter {
        private IElevator elevator;
        private MqttClient mqttClient;
        private Properties properties;

        public ElevatorMQTTAdapter(Properties config) {
            this.properties = config;
            try {
                // Connect to RMI interface
                String rmiUrl = config.getProperty("rmi.url");
                this.elevator = (IElevator) Naming.lookup(rmiUrl);

                // Connect to MQTT broker
                String brokerUrl = this.properties.getProperty("mqtt.broker.url");
                String clientId = "ElevatorMQTTAdapter"; // You can customize this
                this.mqttClient = new MqttClient(brokerUrl, clientId);
                this.mqttClient.connect();
                


                // Subscribe to the topic for setting elevator parameters
                String controlTopic = this.properties.getProperty("mqtt.topic.control");
                this.mqttClient.subscribe(controlTopic, new ControlMessageListener());
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Implement the method to poll elevator state and publish to MQTT
        private void pollElevatorState() {
            // Implement polling logic and publish MQTT messages
            // ...
            try {
            // Example: publish elevator position
            int elevatorPosition = this.elevator.getElevatorPosition(0);
            publishMessage("elevator/position", String.valueOf(elevatorPosition));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Implement a method to publish messages to MQTT
        private void publishMessage(String topic, String message) {
            try {
                // Use a synchronized block to ensure atomicity
                synchronized (this) {
                    // Check if the client is connected
                    if (this.mqttClient.isConnected()) {
                        System.out.println("HURE------------------------------------------------------------------------------------------------------------------------------");
                        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                        mqttMessage.setQos(1); // You can adjust the QoS level
                        this.mqttClient.publish(topic, mqttMessage);
                    } else {
                        System.err.println("MQTT client is not connected. Message not published.");
                        this.mqttClient.connect();
                
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        }

        // Implement a listener for MQTT control messages
        private class ControlMessageListener implements IMqttMessageListener {
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                // Process control message and call corresponding RMI method
                // ...
                System.out.println("ControlMessageListener called");
                // Example: set target floor based on the control message
                int targetPositon = Integer.parseInt(message.toString());
                elevator.setElevatorPosition(0, targetPositon);
            }
        }

        // Implement a method to handle cleanup on shutdown
        public void shutdown() {
            try {
                this.mqttClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void handle() {

            // Schedule polling task at fixed intervals
            long pollingInterval = Long.parseLong(this.properties.getProperty("polling.interval"));
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    pollElevatorState();
                }
            }, 0, pollingInterval);

            // Add shutdown hook to gracefully disconnect from MQTT broker on application exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                shutdown();
                timer.cancel();
            }));
        }

    }