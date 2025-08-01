rootProject.name = "AirlineFlightsWorldwide"

include("core")
include("flight-planning-scheduling")
include("flight-operations")
include("inventory-cabin-capacity")
include("sales-booking-ticketing")
include("departure-control-checkin")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
