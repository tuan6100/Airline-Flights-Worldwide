package online.anhht.airline.model;

/**
 * Represents the Airplane in parked/ground state at an airport.
 */
public class AirplaneParkingState implements AirplaneState {

    @Override
    public String getStateName() {
        return "PARKED";
    }

    @Override
    public void handleTakeOff(Airplane airplane) {
        airplane.changeState(new AirplaneInFlightState());
    }

    @Override
    public void handleLanding(Airplane airplane) {
        throw new IllegalStateException("Airplane " + airplane.getId() + " is already parked on the ground.");
    }

    @Override
    public void handleEnterMaintenance(Airplane airplane) {
        airplane.changeState(new AirplaneMaintenanceState());
    }

    @Override
    public void handleExitMaintenance(Airplane airplane) {
        throw new IllegalStateException("Airplane " + airplane.getId() + " is not currently in maintenance.");
    }
}
