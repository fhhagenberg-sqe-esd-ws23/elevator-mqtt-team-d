package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MqttHandler implements MqttCallback {
    MqttClient client;
    String serverURI;
    String clientID;

    public MqttHandler(String serverURI, String clientID){
        this.serverURI = serverURI;
        this.clientID = clientID;
        try {
            client = new MqttClient(this.serverURI, this.clientID);
            if(!client.isConnected())
                client.connect();
            client.setCallback(this);
        } catch (MqttException e) {
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
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("Message Arrived:");
        System.out.println(mqttMessage.toString());
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
