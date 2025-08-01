package online.anhht.airline.model;

/**
 * Represents the Airplane in active airborne/in-flight state.
 */
public class AirplaneInFlightState implements AirplaneState {

    @Override
    public String getStateName() {
        return "IN_FLIGHT";
    }

    @Override
    public void handleTakeOff(Airplane airplane) {
        throw new IllegalStateException("Airplane " + airplane.getId() + " is already in flight.");
    }

    @Override
    public void handleLanding(Airplane airplane) {
        airplane.changeState(new AirplaneParkingState());
    }

    @Override
    public void handleEnterMaintenance(Airplane airplane) {
        throw new IllegalStateException("Cannot place airplane " + airplane.getId() + " into maintenance while in flight.");
    }

    @Override
    public void handleExitMaintenance(Airplane airplane) {
        throw new IllegalStateException("Airplane " + airplane.getId() + " is in flight, not in maintenance.");
    }
}
