package at.fhhagenberg.sqelevator;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;

import at.fhhagenberg.sqelevator.Elevator.DoorStatus;
import at.fhhagenberg.sqelevator.Elevator.Direction;
import java.util.*;


public class ElevatorAlgorithm implements MqttCallback {
    private MqttClient mqttClient;
    private Properties properties;

    public static List<Elevator> elevatorList;
    public static List<Boolean> floorListUp;
    public static List<Boolean> floorListDown;
    private static Timer timer;

    private static int MAXWEIGHT = 500;

        public ElevatorAlgorithm(Properties config) {
            this.properties = config;
            try {
                // Connect to MQTT broker
                String brokerUrl = this.properties.getProperty("mqtt.broker.url");
                String clientId = "ElevatorAlgorithm"; // You can customize this
                this.mqttClient = new MqttClient(brokerUrl, clientId);
                MqttConnectionOptions connOpts = new MqttConnectionOptions();
                if(!mqttClient.isConnected()) {
                    connOpts.setCleanStart(false);
                    connOpts.setSessionExpiryInterval(null);
                    connOpts.setAutomaticReconnect(true);
                    connOpts.setKeepAliveInterval(60);
                    mqttClient.connect(connOpts);
                }
                mqttClient.setCallback(this);

                // Subscribe to the topic for setting elevator parameters
                MqttSubscription[] subs = {new MqttSubscription(this.properties.getProperty("elevator.state.topic"),2),
                        new MqttSubscription(this.properties.getProperty("elevator.button.topic"),2),
                        new MqttSubscription(this.properties.getProperty("floor.buttonup.topic"),2),
                        new MqttSubscription(this.properties.getProperty("floor.buttondown.topic"),2),
                        new MqttSubscription("elevator/+/+",2)};

                int numOfElevators = Integer.parseInt(this.properties.getProperty("numElevators"));
                int numOfFloors = Integer.parseInt(this.properties.getProperty("numFloors"));
                elevatorList = new ArrayList<>(numOfElevators);
                floorListUp  = new ArrayList<>(Collections.nCopies(numOfFloors, false));
                floorListDown  = new ArrayList<>(Collections.nCopies(numOfFloors, false));

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

        private void moveElevator(int elevatorIndex, int floorIdx, Direction dir){
            this.publishMessage("elevator/control/" + elevatorIndex,"setCommittedDirection:" + dir.ordinal());
            elevatorList.get(elevatorIndex).setDirection(dir);
            if (dir != Direction.ELEVATOR_DIRECTION_UNCOMMITTED) {
                this.publishMessage("elevator/control/" + elevatorIndex,"setTarget:" + floorIdx);
                elevatorList.get(elevatorIndex).setTargetFloor(floorIdx);
            }
        }

        private void findNextTarget(int elevatorIndex) {
            int numFloors = floorListUp.size();
            Elevator ele = (elevatorList.get(elevatorIndex));
            Direction dir = ele.getDirection();
            int currentFloor = ele.getCurrentFloor();
            try {
            if(ele.getSpeed() != 0 || ele.getDoorStatus() != DoorStatus.ELEVATOR_DOORS_CLOSED){
                moveElevator(elevatorIndex, elevatorList.get(elevatorIndex).getTargetFloor(), dir);
                return ;
            }


            ele.pressedButtons.set(currentFloor, false);

            if(dir == Direction.ELEVATOR_DIRECTION_UP){
                floorListUp.set(currentFloor, false);
                for(int i  = currentFloor; i < numFloors; i++){
                    if(floorListUp.get(i) || ele.pressedButtons.get(i)){
                        moveElevator(elevatorIndex, i, dir);
                        return;
                    }
                }
                for(int i  = currentFloor; i >= 0 ; i--){
                    if( floorListDown.get(i) || ele.pressedButtons.get(i)){
                        moveElevator(elevatorIndex, i, Direction.ELEVATOR_DIRECTION_DOWN);
                        return;
                    }
                }
                moveElevator(elevatorIndex, 0, Direction.ELEVATOR_DIRECTION_UNCOMMITTED);
                return;
            }
            else if (dir == Direction.ELEVATOR_DIRECTION_DOWN) {
                floorListDown.set(currentFloor, false);
                for(int i  = currentFloor; i >= 0 ; i--){
                    if(floorListDown.get(i) || ele.pressedButtons.get(i)){
                        moveElevator(elevatorIndex, i, dir);
                        return;
                    }
                }
                for(int i  = currentFloor; i < numFloors; i++){
                    if(floorListUp.get(i) || ele.pressedButtons.get(i)){
                        moveElevator(elevatorIndex, i, Direction.ELEVATOR_DIRECTION_UP);
                        return;
                    }
                }
                moveElevator(elevatorIndex, 0, Direction.ELEVATOR_DIRECTION_UNCOMMITTED);
                return;
            }
            else if (dir == Direction.ELEVATOR_DIRECTION_UNCOMMITTED) {
                int minDistance = Integer.MAX_VALUE;
                int closestTrueIndex = currentFloor;;
                floorListUp.set(currentFloor, false);
                floorListDown.set(currentFloor, false);
                for (int i = 0; i < ele.pressedButtons.size(); i++) {
                    if (ele.pressedButtons.get(i) || floorListUp.get(i)) {
                        int distance = Math.abs(currentFloor - i);
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestTrueIndex = i;
                        }
                    }
                }
                for (int i = 0; i < ele.pressedButtons.size(); i++) {
                    if (ele.pressedButtons.get(i) || floorListDown.get(i)) {
                        int distance = Math.abs(currentFloor - i);
                        if (distance < minDistance) {
                            minDistance = distance;
                            if( i > closestTrueIndex)
                            {
                                closestTrueIndex = i;
                                //System.out.println("Closest Idx: " + closestTrueIndex);
                            }
                        }
                    }
                }
                if(closestTrueIndex != currentFloor){
                    moveElevator(elevatorIndex, closestTrueIndex, closestTrueIndex > currentFloor ? Direction.ELEVATOR_DIRECTION_UP:Direction.ELEVATOR_DIRECTION_DOWN);
                    return;
                }
            }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void handle() {

            // Schedule polling task at fixed intervals
            long pollingInterval = Long.parseLong(this.properties.getProperty("polling.interval"));
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    for (Elevator elevator : elevatorList) {
                        findNextTarget(elevator.getElevatorNumber());
                    }
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
            System.out.println("Algo Disconnected");
        }

        @Override
        public void mqttErrorOccurred(MqttException e) {
            System.out.println("Error Occured");
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

            String message = mqttMessage.toString();


            /***********prepare command, elevatorID and value************/
            //The message topic is "elevator/topic/elevID"
            String[] topicParts = topic.split("/");
            if (topicParts.length != 3) {
                throw new IllegalArgumentException("Invalid message format");
            }

            String elevatorIDstring = topicParts[2];
            int index = Integer.parseInt(elevatorIDstring);
            String topicPart = topicParts[1];



            int value = Integer.parseInt(message);


            if (topicParts[0].equals("elevator")) {
                Elevator elevator = elevatorList.get(index);
                //System.out.println("Command: " + command);
                //System.out.println("ElevatorIdx: " + index);
                //System.out.println( "Msg: " + value);

                switch (topicParts[1]) {
                    case "position":
                        float tmp = value/Integer.parseInt(this.properties.getProperty("floorHeight")) ;
                        //System.out.println("currentFloor:" + tmp + " heightval:" + value);
                        elevator.setCurrentFloor(value/Integer.parseInt(this.properties.getProperty("floorHeight")));
                        break;
                    case "committedDirection":
                        //System.out.println("Direction of Elev:" +  index + " is " + value);
                        elevator.setDirection(Direction.values()[value]);
                        break;
                    case "acceleration":
                        //System.out.println("Elev:" +  index + " acceleration: " + value);
                        elevator.setSpeed(value);
                        break;
                    case "doorStatus":
                        //System.out.println("Elev:" +  index + " doorStatus: " + value);
                        elevator.setDoorStatus(DoorStatus.values()[value]);
                        break;
                    case "currentFloor":
                        //System.out.println("Elev:" +  index + " in Floor " + value);
                        elevator.setCurrentFloor(value);
                        break;
                    case "speed":
                       // System.out.println("Elev:" +  index + " speed: " + value);
                        elevator.setSpeed(value);
                        break;
                    case "weight":
                        //System.out.println("Elev:" +  index + " weight: " + value);
                        elevator.setWeight(value);
                        break;
                    case "capacity":
                        //System.out.println("Elev:" +  index + " capacity: " + value);
                        elevator.setMaxWeightCapacity(value);
                        break;
                    case "target":
                        //System.out.println("Elev:" +  index + " target: " + value);
                        elevator.setTargetFloor(value);
                        break;
                    case "button":
                        //System.out.println("Button pressed in Elev:" +  index + " Floor: " + value);
                        elevatorList.get(index).pressedButtons.set(value, true);
                        break;
                    default:
                        // Handle unknown topicParts[1]
                        break;
                }
            } else if (topicParts[0].equals("floor")) {
                int floorIndex = index; // Index for floorList

                switch (topicParts[1]) {
                    case "buttonup":
                        if (value == 1){
                            floorListUp.set(index, true);
                        }
                        else
                        {
                            floorListUp.set(index, false);
                        }
                        //System.out.println("ButtonUP floor:" +  index);


                        break;
                    case "buttondown":
                        if (value == 1){
                            floorListDown.set(index, true);
                        }
                        else
                        {
                            floorListDown.set(index, false);
                        }
                        //System.out.println("ButtonDOWN floor:" +  index);

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

        public void reset()
        {
            int numOfFloors = Integer.parseInt(this.properties.getProperty("numFloors"));
            elevatorList.clear();
            floorListUp  = new ArrayList<>(Collections.nCopies(numOfFloors, false));
            floorListDown  = new ArrayList<>(Collections.nCopies(numOfFloors, false));

            for(int i = 0; i <  Integer.parseInt(this.properties.getProperty("numElevators")); i++){
                elevatorList.add(new Elevator(i, MAXWEIGHT,numOfFloors));
            }
        }
        public void teardown()
        {
            timer.cancel();
        }

    }
