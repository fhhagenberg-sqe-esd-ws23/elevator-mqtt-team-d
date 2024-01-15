
package at.fhhagenberg.sqelevator;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import at.fhhagenberg.sqelevator.Elevator.DoorStatus;
import sqelevator.IElevator;
import at.fhhagenberg.sqelevator.Elevator.Direction;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.util.*;


public class ElevatorAlgorithm implements MqttCallback {
        private IElevator elevator;
        private MqttClient mqttClient;
        private Properties properties;

        private static List<Elevator> elevatorList;
    private static List<Boolean> floorList;


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
                int numOfFloors = Integer.parseInt(this.properties.getProperty("numFloors"));
                elevatorList = new ArrayList<>(numOfElevators);
                floorList  = new ArrayList<>(numOfFloors);
                for(int i = 0; i <  numOfElevators; i++){
                    elevatorList.add(new Elevator(i, MAXWEIGHT,Integer.parseInt(this.properties.getProperty("numFloors")) ));
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
            int selectedElevator = -1;
            int minDistance = Integer.MAX_VALUE;
            int numOfElevators = Integer.parseInt(this.properties.getProperty("numElevators"));

            for (int elevatorNumber = 0; elevatorNumber < numOfElevators; elevatorNumber++) {
                int currentFloor = elevatorList.get(elevatorNumber).getCurrentFloor();
                int distance = Math.abs(currentFloor - floorIdx);
                int direction = elevatorList.get(elevatorNumber).getDirection().ordinal();
                boolean isIdle = direction == IElevator.ELEVATOR_DIRECTION_UNCOMMITTED;

                // If the elevator is idle or moving towards the request floor, consider it for selection
                if (isIdle || (direction == IElevator.ELEVATOR_DIRECTION_UP && currentFloor < floorIdx)
                        || (direction == IElevator.ELEVATOR_DIRECTION_DOWN && currentFloor > floorIdx)) {
                    // Choose the elevator with the shortest distance to the request floor
                    if (distance < minDistance) {
                        minDistance = distance;
                        selectedElevator = elevatorNumber;
                    }
                }
            }

            // If no elevator is moving towards or currently idle, just pick the closest one
            if (selectedElevator == -1) {
                for (int elevatorNumber = 0; elevatorNumber < numOfElevators; elevatorNumber++) {
                    int currentFloor = elevatorList.get(elevatorNumber).getCurrentFloor();
                    int distance = Math.abs(currentFloor - floorIdx);

                    if (distance < minDistance) {
                        minDistance = distance;
                        selectedElevator = elevatorNumber;
                    }
                }
            }

            return selectedElevator;
        }

        public void moveElevator(int elevatorIdx, int floor){
            System.out.println("moving...");

            Elevator.Direction direction = Elevator.Direction.ELEVATOR_DIRECTION_UNCOMMITTED;
            if(elevatorList.get(elevatorIdx).getCurrentFloor() > floor){
                direction = Elevator.Direction.ELEVATOR_DIRECTION_DOWN;
            }
            else if (elevatorList.get(elevatorIdx).getCurrentFloor() < floor){
                direction = Elevator.Direction.ELEVATOR_DIRECTION_UP;
            }
            this.publishMessage("elevator/control/" + elevatorIdx,"setTarget:" + floor);
            this.publishMessage("elevator/control/" + elevatorIdx,"setCommittedDirection:" + direction.ordinal());
        }

        public void pollElevatorState()
        {
            MqttSubscription[] subs = {new MqttSubscription(this.properties.getProperty("elevator.state.topic"),2)};

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
            int index = Integer.parseInt(elevatorIDstring);
            String topicPart = topicParts[1];


            System.out.println("topic2");
            int value = Integer.parseInt(message);

            //System.out.println("Command: " + command);
            System.out.println("ElevatorID: " + index);
            System.out.println( "Value: " + value);

            System.out.println( "Topic0: " + topicParts[0]);
            System.out.println( "Topic1: " + topicParts[1]);
            System.out.println( "Topic2: " + topicParts[2]);

            if (topicParts[0].equals("elevator")) {
                int elevatorIndex = index; // Index for elevatorList
                Elevator elevator = elevatorList.get(elevatorIndex);

                switch (topicParts[1]) {
                    case "position":
                        elevator.setCurrentFloor(value);
                        // TODO: Additional logic for elevator position
                        break;
                    case "committedDirection":
                        elevator.setDirection(Direction.values()[value]);
                        // TODO: Additional logic for committed direction
                        break;
                    case "acceleration":
                        elevator.setSpeed(value);
                        // TODO: Additional logic for elevator acceleration
                        break;
                    case "doorStatus":
                        elevator.setDoorStatus(DoorStatus.values()[value]);
                        // TODO: Additional logic for elevator door status
                        break;
                    case "currentFloor":
                        elevator.setCurrentFloor(value);
                        // TODO: Additional logic for current floor
                        break;
                    case "speed":
                        elevator.setSpeed(value);
                        // TODO: Additional logic for elevator speed
                        break;
                    case "weight":
                        elevator.setWeight(value);
                        // TODO: Additional logic for elevator weight
                        break;
                    case "capacity":
                        elevator.setMaxWeightCapacity(value);
                        // TODO: Additional logic for elevator capacity
                        break;
                    case "target":
                        elevator.setTargetFloor(value);
                        // TODO: Additional logic for elevator target floor
                        break;
                    case "button":
                        elevatorList.get(index).pressedButtons.set(value, true);
                        break;
                    default:
                        // Handle unknown topicParts[1]
                        break;
                }
            } else if (topicParts[0].equals("floor")) {
                int floorIndex = index; // Index for floorList

                switch (topicParts[1]) {
                    case "button":
                        floorList.set(index, true);
                        //TODO Put this shit into a update function that just updates elevator controls
            // //                int elevatorToGo = this.calculateElevator(index, value);
            // //                System.out.println("elevator/control/" + elevatorToGo + "\n" + "setTarget:" + index);
            // //                elevatorList.get(elevatorToGo).move();
            // //                this.moveElevator(elevatorToGo, index);
                        break;
                    default:
                        // Handle unknown topicParts[1]
                        break;
                }
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
