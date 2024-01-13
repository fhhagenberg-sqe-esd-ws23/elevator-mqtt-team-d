import at.fhhagenberg.sqelevator.ElevatorManager;
import org.junit.jupiter.api.Test;
import at.fhhagenberg.sqelevator.ElevatorExample;

public class EmptyRunTest {
    @Test
    public void testExample(){
        ElevatorManager controller = new ElevatorManager();
        ElevatorExample elevatorExample = new ElevatorExample(controller);
    }
}
