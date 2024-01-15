package at.fhhagenberg.sqelevator;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AlgoTest {
    @Test
    public void testAlgo() throws InterruptedException {
        // Create Algo
        Algorithmus algo = new Algorithmus(4,10,Direction.NONE);
        // Create Elevator Requests
        algo.request(0,10, Direction.UP, true);
        algo.request(1,4, Direction.UP, true);
        algo.request(2,5, Direction.UP, true);
        algo.request(3,1, Direction.UP, true);

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
        algo.request(0,0,Direction.NONE, false);
        System.out.println(algo.calcTargets());
    }

}

