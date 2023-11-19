package at.fhhagenberg.sqelevator;

public class Floor {

    public int floorNumber;
    public boolean upButton;
    public boolean downButton;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.upButton = false;
        this.downButton = false;
    }

    // Getters and setters for floor properties
    // Assume implementations for getters and setters for floor properties

    public int getFloorNumber() {
        return floorNumber;
    }

    public boolean isUpButtonPressed() {
        return upButton;
    }

    public boolean isDownButtonPressed() {
        return downButton;
    }

    public void pressUpButton() {
        upButton = true;
    }

    public void pressDownButton() {
        downButton = true;
    }
    public void releaseButtons(){
        upButton = false;
        downButton = false;
    }
}
