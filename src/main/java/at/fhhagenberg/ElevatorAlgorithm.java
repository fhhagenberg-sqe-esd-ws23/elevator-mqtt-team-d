
package at.fhhagenberg;
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


public class ElevatorAlgorithm {
    
}
