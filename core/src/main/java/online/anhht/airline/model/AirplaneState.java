package online.anhht.airline.model;

/**
 * State interface for the Airplane state machine.
 */
public interface AirplaneState {
    String getStateName();
    void handleTakeOff(Airplane airplane);
    void handleLanding(Airplane airplane);
    void handleEnterMaintenance(Airplane airplane);
    void handleExitMaintenance(Airplane airplane);
}
