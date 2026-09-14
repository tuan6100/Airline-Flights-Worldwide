package online.anhht.airline.sales;

import online.anhht.airline.model.FareCondition;
import online.anhht.airline.sales.application.BookingSagaCoordinator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Booking Saga & Concurrency Integration Tests")
class BookingSagaIntegrationTest {

    @Autowired
    private BookingSagaCoordinator sagaCoordinator;

    @Test
    @DisplayName("Should successfully execute end-to-end booking saga across flight, inventory, and sales")
    void shouldExecuteBookingSagaSuccessfully() {
        BookingSagaCoordinator.BookingSagaCommand command = new BookingSagaCoordinator.BookingSagaCommand(
                "SAGA01",
                "1234567890123",
                "PAX-8888",
                "JOHN DOE",
                "{\"email\":\"john@example.com\"}",
                "FL-1001",
                "773",
                "1A",
                FareCondition.BUSINESS,
                BigDecimal.valueOf(500.00)
        );

        BookingSagaCoordinator.BookingSagaResult result = sagaCoordinator.executeBookingSaga(command);

        assertTrue(result.success());
        assertNotNull(result.bookRef());
        assertEquals(6, result.bookRef().length());
        assertNotNull(result.ticketNo());
        assertEquals(13, result.ticketNo().length());
        assertEquals(0, BigDecimal.valueOf(500.00).compareTo(result.totalAmount()));
    }

    @Test
    @DisplayName("Should prevent double-booking on concurrent booking requests for the same flight and seat")
    void shouldPreventDoubleBookingUnderConcurrentLoad() throws InterruptedException {
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    String bookRef = String.format("CC%04d", index);
                    String ticketNo = String.format("99900000000%02d", index);
                    BookingSagaCoordinator.BookingSagaCommand cmd = new BookingSagaCoordinator.BookingSagaCommand(
                            bookRef,
                            ticketNo,
                            "PAX-" + index,
                            "PASSENGER " + index,
                            "{}",
                            "FL-CONCUR",
                            "320",
                            "3A",
                            FareCondition.ECONOMY,
                            BigDecimal.valueOf(150.00)
                    );
                    BookingSagaCoordinator.BookingSagaResult res = sagaCoordinator.executeBookingSaga(cmd);
                    if (res.success()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, successCount.get(), "Only one thread should successfully book seat 3A");
        assertEquals(9, failCount.get(), "9 threads should fail and trigger compensating actions");
    }
}
