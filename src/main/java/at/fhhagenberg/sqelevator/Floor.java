package at.fhhagenberg.sqelevator;

/**
 * Represents a floor in a building with up and down buttons.
 */
public class Floor {

    /** The number of the floor. */
    public int floorNumber;

    /** Indicates whether the up button on this floor is pressed. */
    public boolean upButton;

    /** Indicates whether the down button on this floor is pressed. */
    public boolean downButton;

    /**
     * Constructs a floor object with the specified floor number.
     *
     * @param floorNumber The number of the floor.
     */
    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.upButton = false;
        this.downButton = false;
    }

    /**
     * Gets the number of this floor.
     *
     * @return The floor number.
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Checks if the up button on this floor is pressed.
     *
     * @return True if the up button is pressed, otherwise false.
     */
    public boolean isUpButtonPressed() {
        return upButton;
    }

    /**
     * Checks if the down button on this floor is pressed.
     *
     * @return True if the down button is pressed, otherwise false.
     */
    public boolean isDownButtonPressed() {
        return downButton;
    }

    /**
     * Simulates pressing the up button on this floor.
     */
    public void pressUpButton() {
        upButton = true;
    }

    /**
     * Simulates pressing the down button on this floor.
     */
    public void pressDownButton() {
        downButton = true;
    }

    /**
     * Simulates releasing both up and down buttons on this floor.
     */
    public void releaseButtons() {
        upButton = false;
        downButton = false;
    }
}
