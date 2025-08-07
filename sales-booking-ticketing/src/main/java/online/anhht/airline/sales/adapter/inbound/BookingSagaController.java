package online.anhht.airline.sales.adapter.inbound;

import online.anhht.airline.sales.application.BookingSagaCoordinator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sales/saga")
public class BookingSagaController {

    private final BookingSagaCoordinator sagaCoordinator;

    public BookingSagaController(BookingSagaCoordinator sagaCoordinator) {
        this.sagaCoordinator = sagaCoordinator;
    }

    @PostMapping("/book")
    public ResponseEntity<BookingSagaCoordinator.BookingSagaResult> executeBookingSaga(
            @RequestBody BookingSagaCoordinator.BookingSagaCommand command
    ) {
        BookingSagaCoordinator.BookingSagaResult result = sagaCoordinator.executeBookingSaga(command);
        if (result.success()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
