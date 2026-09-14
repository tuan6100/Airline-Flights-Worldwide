package online.anhht.airline.checkin.adapter.inbound;

import online.anhht.airline.checkin.adapter.inbound.CheckInController.BoardingPassDto;
import online.anhht.airline.checkin.port.inbound.CheckInUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/checkin/boarding-passes")
public class BoardingControlController {

    private final CheckInUseCase checkInUseCase;

    public BoardingControlController(CheckInUseCase checkInUseCase) {
        this.checkInUseCase = checkInUseCase;
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<List<BoardingPassDto>> getBoardingPassesForFlight(@PathVariable String flightId) {
        List<BoardingPassDto> list = checkInUseCase.getBoardingPassesForFlight(flightId).stream()
                .map(BoardingPassDto::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }
}
