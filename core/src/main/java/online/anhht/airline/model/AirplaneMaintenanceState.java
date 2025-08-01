package online.anhht.airline.model;

/**
 * Represents the Airplane in maintenance/hangar state.
 */
public class AirplaneMaintenanceState implements AirplaneState {

    @Override
    public String getStateName() {
        return "MAINTENANCE";
    }

    @Override
    public void handleTakeOff(Airplane airplane) {
        throw new IllegalStateException("Airplane " + airplane.getId() + " cannot take off while in maintenance.");
    }

    @Override
    public void handleLanding(Airplane airplane) {
        throw new IllegalStateException("Airplane " + airplane.getId() + " is in maintenance, not in flight.");
    }

    @Override
    public void handleEnterMaintenance(Airplane airplane) {
        throw new IllegalStateException("Airplane " + airplane.getId() + " is already in maintenance.");
    }

    @Override
    public void handleExitMaintenance(Airplane airplane) {
        airplane.changeState(new AirplaneParkingState());
    }
}
