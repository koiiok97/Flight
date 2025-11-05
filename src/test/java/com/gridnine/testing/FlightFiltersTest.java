package com.gridnine.testing;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlightFiltersTest {

    @Test
    void pastDepartureFilter_detectsFlightsDepartingInPast() {
        LocalDateTime now = LocalDateTime.now();
        Flight pastFlight = new Flight(List.of(
                new Segment(now.minusDays(1), now.plusHours(1))
        ));
        Flight futureFlight = new Flight(List.of(
                new Segment(now.plusDays(1), now.plusDays(1).plusHours(2))
        ));

        PastDepartureFilter filter = new PastDepartureFilter();

        assertTrue(filter.test(pastFlight));
        assertFalse(filter.test(futureFlight));
    }

    @Test
    void invalidSegmentsFilter_detectsArrivalBeforeDeparture() {
        LocalDateTime now = LocalDateTime.now();
        Flight valid = new Flight(List.of(
                new Segment(now, now.minusHours(1))
        ));
        Flight invalid = new Flight(List.of(
                new Segment(now, now.plusDays(1))
        ));

        InvalidSegmentsFilter filter = new InvalidSegmentsFilter();

        assertTrue(filter.test(valid));
        assertFalse(filter.test(invalid));
    }

    @Test
    void groundTimeFilter_detectsGroundTimeOverTwoHours() {
        LocalDateTime now = LocalDateTime.now();
        Flight longGroundTime = new Flight(List.of(
                new Segment(now, now.plusHours(2)),
                new Segment(now.plusHours(5), now.plusHours(6))
        ));
        Flight shortGroundTime = new Flight(List.of(
                new Segment(now, now.plusHours(2)),
                new Segment(now.plusHours(3), now.plusHours(4))
        ));

        GroundTimeFilter filter = new GroundTimeFilter();

        assertTrue(filter.test(longGroundTime));
        assertFalse(filter.test(shortGroundTime));
    }
}

