package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import sqelevator.IElevator;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

    public class ElevatorMQTTAdapter implements MqttCallback {
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
                if(!mqttClient.isConnected())
                    mqttClient.connect();
                mqttClient.setCallback(this);

                // Subscribe to the topic for setting elevator parameters
                MqttSubscription sub = new MqttSubscription("elevator/control/+",2);
                MqttSubscription[] subs = {sub};

                try{
                    mqttClient.subscribe(subs);
                }catch (MqttException e){
                    System.out.println("Subscription failed:");
                    System.out.println(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Implement the method to poll elevator state and publish to MQTT
        private void pollElevatorState() {
            try {
                // Assuming elevatorNumber is defined in your class
                int elevatorNumber = 0;  // Example elevator number
                for (int i = 0; i < Integer.parseInt(properties.getProperty("numElevators")); i++)
                {

                   // Publish elevator's position
                   int elevatorPosition = this.elevator.getElevatorPosition(elevatorNumber);
                   publishMessage("elevator/position/" + elevatorNumber, String.valueOf(elevatorPosition));

                   // Publish elevator's committed direction
                   int committedDirection = this.elevator.getCommittedDirection(elevatorNumber);
                   publishMessage("elevator/committedDirection/" + elevatorNumber, String.valueOf(committedDirection));

                   // Publish elevator's acceleration
                   int elevatorAccel = this.elevator.getElevatorAccel(elevatorNumber);
                   publishMessage("elevator/acceleration/" + elevatorNumber, String.valueOf(elevatorAccel));

                   // Publish elevator's door status
                   int elevatorDoorStatus = this.elevator.getElevatorDoorStatus(elevatorNumber);
                   publishMessage("elevator/doorStatus/" + elevatorNumber, String.valueOf(elevatorDoorStatus));

                   // Publish elevator's current floor
                   int elevatorFloor = this.elevator.getElevatorFloor(elevatorNumber);
                   publishMessage("elevator/currentFloor/" + elevatorNumber, String.valueOf(elevatorFloor));

                   // Publish elevator's speed
                   int elevatorSpeed = this.elevator.getElevatorSpeed(elevatorNumber);
                   publishMessage("elevator/speed/" + elevatorNumber, String.valueOf(elevatorSpeed));

                   // Publish elevator's weight
                   int elevatorWeight = this.elevator.getElevatorWeight(elevatorNumber);
                   publishMessage("elevator/weight/" + elevatorNumber, String.valueOf(elevatorWeight));

                   // Publish elevator's capacity
                   int elevatorCapacity = this.elevator.getElevatorCapacity(elevatorNumber);
                   publishMessage("elevator/capacity/" + elevatorNumber, String.valueOf(elevatorCapacity));

                    // Publish elevator's target floor
                    int targetFloor = this.elevator.getTarget(i);
                    publishMessage("elevator/target/" + i, String.valueOf(targetFloor));


                    // Publish pressed buttons of elevator
                    for(int j = 0 ; j < Integer.parseInt(properties.getProperty("numFloors")); j++)
                    {
                        if (this.elevator.getElevatorButton(i, j))
                        {
                            publishMessage("elevator/button/" + i, String.valueOf(j));// Publish elevator's target floor
                        }
                    }
                }
                for(int i = 0; i < Integer.parseInt(properties.getProperty("numFloors")); i++)
                {
                    if(this.elevator.getFloorButtonUp(i))
                    {
                        publishMessage("floor/buttonup/" + i, "1");
                    }
                    if(this.elevator.getFloorButtonDown(i)){
                        publishMessage("floor/buttondown/" + i, "1");
                    }
                }


                // Additional getter functions can be added here in a similar manner

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Implement a method to publish messages to MQTT
        private void publishMessage(String topic, String message) {
            try {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(1); // You can adjust the QoS level
                this.mqttClient.publish(topic, mqttMessage);
            } catch (Exception e) {
                e.printStackTrace();
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

        @Override
        public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
            System.out.println("Disconnected");
        }

        @Override
        public void mqttErrorOccurred(MqttException e) {
            System.out.println("Error Occured");
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            //System.out.println("Message Arrived at Adapter:");
            String message = mqttMessage.toString();
            //System.out.println(message);
            //System.out.println(topic);


            /***********prepare command, elevatorID and value************/
            //The message topic is "elevator/topic/elevID"
            String[] topicParts = topic.split("/");
            if (topicParts.length != 3) {
                throw new IllegalArgumentException("Invalid message format");
            }
            String elevatorIDstring = topicParts[2];
            int elevatorID = Integer.parseInt(elevatorIDstring);

            // Assuming the message format is "command:value"
            String[] commandParts = message.split(":");
            if (commandParts.length != 2) {
                throw new IllegalArgumentException("Invalid message format");
            }
            String command = commandParts[0];
            int value = Integer.parseInt(commandParts[1]);

            //System.out.println("Command: " + command);
            //System.out.println("ElevatorID: " + elevatorID);
            //System.out.println( "Value: " + value);


            switch (command) {
                case "setTarget":
                    this.elevator.setTarget(elevatorID, value);
                    System.out.println("Target:    " +  value + " | elev: " + elevatorID);
                    break;
                case "setServicesFloors":
                    // Assuming the second part is the floor number
                    // The actual method call might differ based on your elevator API
                    this.elevator.setServicesFloors(elevatorID, value, true);
                    break;
                case "setCommittedDirection":
                    // Assuming value corresponds to the direction (e.g., 0 for up, 1 for down)
                    this.elevator.setCommittedDirection(elevatorID, value);
                    System.out.println("Direction: "+ value + " | elev: " + elevatorID);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }


        @Override
        public void deliveryComplete(IMqttToken iMqttToken) {
            //System.out.println("Adapter Delivery Complete");
        }

        @Override
        public void connectComplete(boolean b, String s) {
            //System.out.println("Adapter Connection Complete");
        }

        @Override
        public void authPacketArrived(int i, MqttProperties mqttProperties) {
            System.out.println("Adapter Auth Packet Arrived");
        }

    }