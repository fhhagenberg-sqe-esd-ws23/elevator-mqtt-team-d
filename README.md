# SQE-Elevator-Project Team D

## Setup and Run:
### Requirements
- docker
- Elevator Sim
  
### 1. Run hivemq

```sh
docker run -p 8080:8080 -p 1883:1883 hivemq/hivemq4 
```
### 2. Run Simulator
Make sure the settings of the simulator (e.g. number of floors and number of elevator) matches those of the properties/IElevator.properties.

### 3. Run ElevatorAlgo

```sh
main.jar
### 4. Wait for elevator...```

### 5. ???

### 6. Profit

## Properties File
Config Properties:
```
version=1.0
name=IElevatorProperties
date=2023-12-05
numFloors=25
numElevators=3
floorHeight=10
```
MqttTopics:
```
# RMI configuration
rmi.url=rmi://localhost:1099/ElevatorSim

# MQTT configuration
mqtt.broker.url=tcp://0.0.0.0:1883
mqtt.topic.control=elevator/control
polling.interval=500

# MQTT Topics for Elevator Updates

# Elevator State Updates
elevator.state.topic=elevator/state/+

# Elevator Button Press Updates
elevator.button.topic=elevator/button/+

# Elevator Direction Updates
elevator.direction.topic=elevator/direction/+

# Elevator Target Updates
elevator.target.topic=elevator/target/+

# Floor Button Press Updates
floor.buttonup.topic=floor/buttonup/+
floor.buttondown.topic=floor/buttondown/+

# Elevator Service Updates
elevator.service.topic=elevator/service/+

# Clock Tick Updates
elevator.clock.tick.topic=elevator/clock/tick

```


## Documentation:
- clone repo and generate javedocs with:
```sh
mvn javadoc:javadoc
```
- open via browser:
```sh
target/site/apidocs/index.html
```