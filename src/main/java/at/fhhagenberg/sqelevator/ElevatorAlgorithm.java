
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
import java.util.*;


public class ElevatorAlgorithm implements MqttCallback {
        private IElevator elevator;
        private MqttClient mqttClient;
        private Properties properties;

        private static List<Elevator> elevatorList;

        private static int MAXWEIGHT = 500;

        public ElevatorAlgorithm(Properties config) {
            this.properties = config;
            try {
                // Connect to MQTT broker
                String brokerUrl = this.properties.getProperty("mqtt.broker.url");
                String clientId = "ElevatorAlgorithm"; // You can customize this
                this.mqttClient = new MqttClient(brokerUrl, clientId);
                if(!mqttClient.isConnected())
                    mqttClient.connect();
                mqttClient.setCallback(this);

                // Subscribe to the topic for setting elevator parameters
                MqttSubscription[] subs = {new MqttSubscription(this.properties.getProperty("elevator.state.topic"),2),
                        new MqttSubscription(this.properties.getProperty("elevator.button.topic"),2),
                        new MqttSubscription(this.properties.getProperty("floor.button.topic"),2) };

                int numOfElevators = Integer.parseInt(this.properties.getProperty("numElevators"));
                elevatorList = new ArrayList<>(numOfElevators);
                for(int i = 0; i <  numOfElevators; i++){
                    elevatorList.add(new Elevator(i, MAXWEIGHT));
                }
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

        private int calculateElevator(int floorIdx, int buttonPressed) {
            //TODO
            System.out.println("calculating...");
            return 0;
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
            System.out.println("Message Arrived at Algo:");
            String message = mqttMessage.toString();
            System.out.println(message);
            System.out.println(topic);


            /***********prepare command, elevatorID and value************/
            //The message topic is "elevator/topic/elevID"
            String[] topicParts = topic.split("/");
            if (topicParts.length != 3) {
                throw new IllegalArgumentException("Invalid message format");
            }
            System.out.println("topic");
            String elevatorIDstring = topicParts[2];
            int floor = Integer.parseInt(elevatorIDstring);
            String topicPart = topicParts[1];


            System.out.println("topic2");
            int value = Integer.parseInt(message);

            //System.out.println("Command: " + command);
            System.out.println("ElevatorID: " + floor);
            System.out.println( "Value: " + value);

            System.out.println( "Topic0: " + topicParts[0]);
            System.out.println( "Topic1: " + topicParts[1]);
            System.out.println( "Topic2: " + topicParts[2]);

            if(topicParts[0].equals("elevator")){
                if(topicParts[1].equals("state")){
                    
                } else if (topicParts[1].equals("button")) {
                    
                }
            } else if (topicParts[0].equals("floor")) {
                int elevatorToGo = this.calculateElevator(floor, value);
                System.out.println("elevator/control/" + elevatorToGo + "\n" + "setTarget:" + floor);
                this.publishMessage("elevator/control/" + elevatorToGo,"setTarget:" + floor);
            }


//            switch (topicPart) {
//                case "setTarget":
//                    this.elevator.setTarget(elevatorID, value);
//                    break;
//                case "setServicesFloors":
//                    // Assuming the second part is the floor number
//                    // The actual method call might differ based on your elevator API
//                    this.elevator.setServicesFloors(elevatorID, value, true);
//                    break;
//                case "setCommittedDirection":
//                    // Assuming value corresponds to the direction (e.g., 0 for up, 1 for down)
//                    this.elevator.setCommittedDirection(elevatorID, value);
//                    break;
//                default:
//                    System.out.println("Unknown command: " + command);
//                    break;
//            }
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
