package at.fhhagenberg.sqelevator;
import org.junit.jupiter.api.Test;
import at.fhhagenberg.sqelevator.Elevator.Direction;
import java.util.List;

public class AlgoTest {
    @Test
    public void testAlgo() throws InterruptedException {
        // Create Algo
        Algorithmus algo = new Algorithmus(4,10,Direction.ELEVATOR_DIRECTION_UNCOMMITTED);
        // Create Elevator Requests
        algo.request(0,10, Direction.ELEVATOR_DIRECTION_UP, true);
        algo.request(1,4, Direction.ELEVATOR_DIRECTION_UP, true);
        algo.request(2,5, Direction.ELEVATOR_DIRECTION_UP, true);
        algo.request(3,1, Direction.ELEVATOR_DIRECTION_UP, true);

        // Run the elevator until all requests are served
        List<String> results;
        int sleepCounter = 0;
        // Calculate Queue
        while (!algo.isEmpty()) {
            results = algo.calcTargets();
            for(int i = 0; i < results.size(); i++){
                System.out.println("Results: "+ results.get(i));
            }
            System.out.println("Sleeping after working");
        }
        // To reset Elevators to 0 position
        algo.request(0,0,Direction.ELEVATOR_DIRECTION_UNCOMMITTED, false);
        System.out.println(algo.calcTargets());
    }

}

