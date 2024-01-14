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

import java.rmi.Naming;
import java.util.function.Consumer;

public class MqttHandler implements MqttCallback {
    MqttClient client;
    String serverURI;
    String clientID;

    Consumer<String> messageArrivedCallback;


    public MqttHandler(String serverURI, String clientID, Consumer<String> messageArrivedCallback) {
        this.serverURI = serverURI;
        this.clientID = clientID;
        this.messageArrivedCallback = messageArrivedCallback;
        try {
            client = new MqttClient(this.serverURI, this.clientID);
            if(!client.isConnected())
                client.connect();
            client.setCallback(this);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void updateConnections(){
        try {
            if(!client.isConnected()) {
                this.client = new MqttClient(this.serverURI, this.clientID);
                client.connect();
            }
            client.setCallback(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void publishOnTopic(String topic, String msg){
        MqttMessage message = new MqttMessage();
        message.setPayload(msg.getBytes());

        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void subscribeToTopic(String topic) {
        // Subscribe
        MqttSubscription sub = new MqttSubscription(topic,2);
        MqttSubscription[] subs = {sub};

        try{
            client.subscribe(subs);
        }catch (MqttException e){
            System.out.println("Subscription failed:");
            System.out.println(e);
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
        System.out.println("Message Arrived:" + mqttMessage.toString());
        System.out.println("Handler Topic:" + topic);

        // Call the callback function with the received message
        if (messageArrivedCallback != null) {
            messageArrivedCallback.accept(mqttMessage.toString());
        }
        this.updateConnections();
    }

    @Override
    public void deliveryComplete(IMqttToken iMqttToken) {
        System.out.println("Delivery Complete");
    }

    @Override
    public void connectComplete(boolean b, String s) {
        System.out.println("Connection Complete");
    }

    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {
        System.out.println("Auth Packet Arrived");
    }
}
