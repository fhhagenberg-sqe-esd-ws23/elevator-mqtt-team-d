package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

// Class to manage elevators and populate the data model
public class ElevatorManager  implements IElevator{
    // Method to populate the data model from the IElevator API
    List<Elevator> elevators;
    List<Floor> floors;
    private int floorHeight;

    public void addElevators(int num ){
        for(int i = 0; i < num; i++){
            elevators.add(new Elevator(1, 50));
        }
    }

    public void setFloorHeight(int newHeight)
    {
        this.floorHeight = newHeight;
    }

    @Override
    public int getCommittedDirection(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getDirection().ordinal();
    }

    @Override
    public int getElevatorAccel(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getSpeed();
    }

    @Override
    public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
        return false;
    }

    @Override
    public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getDoorStatus().ordinal();
    }

    @Override
    public int getElevatorFloor(int elevatorNumber) throws RemoteException {
        return 0;
    }

    @Override
    public int getElevatorNum() throws RemoteException {
        return 0;
    }

    @Override
    public int getElevatorPosition(int elevatorNumber) throws RemoteException {
        return 0;
    }

    @Override
    public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getSpeed();
    }

    @Override
    public int getElevatorWeight(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getWeight();
    }

    @Override
    public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getMaxWeightCapacity();
    }

    @Override
    public boolean getFloorButtonDown(int floor) throws RemoteException {
        return false;
    }

    @Override
    public boolean getFloorButtonUp(int floor) throws RemoteException {
        return false;
    }

    @Override
    public int getFloorHeight() throws RemoteException {
        return floorHeight;
    }

    @Override
    public int getFloorNum() throws RemoteException {
        return 0;
    }

    @Override
    public boolean getServicesFloors(int elevatorNumber, int floor) throws RemoteException {
        return this.elevators.get(elevatorNumber).getServiceFloors().contains(floor);
    }

    @Override
    public int getTarget(int elevatorNumber) throws RemoteException {
        return  this.elevators.get(elevatorNumber).getTargetFloor();
    }

    @Override
    public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {
        this.elevators.get(elevatorNumber).setDirection(Elevator.Direction.values()[direction]);
    }

    @Override
    public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
        if(service){
            this.elevators.get(elevatorNumber).addServiceFloor(floor);
        }
        else{
            this.elevators.get(elevatorNumber).removeServiceFloor(floor);
        }
    }

    @Override
    public void setTarget(int elevatorNumber, int target) throws RemoteException {
        this.elevators.get(elevatorNumber).setTargetFloor(target);
    }

    @Override
    public long getClockTick() throws RemoteException {
        return 0;
    }
}