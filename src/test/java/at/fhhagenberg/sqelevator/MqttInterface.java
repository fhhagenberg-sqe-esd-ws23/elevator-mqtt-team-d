package at.fhhagenberg.sqelevator;

public interface MqttInterface {
    public static void on_publish(String str){
        System.out.println("Publish: ");
    }
    public static  void on_recieve(){
        System.out.println("Recieved: ");
    }
}
