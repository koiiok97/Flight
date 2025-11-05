package com.gridnine.testing;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        flights.forEach(System.out::println);

        System.out.println("\nВылеты до текущего момента времени:");
        new FlightFilterProcess(new PastDepartureFilter())
                .process(flights)
                .forEach(System.out::println);

        System.out.println("\nСегменты с датой прилёта раньше даты вылета:");
        new FlightFilterProcess(new InvalidSegmentsFilter())
                .process(flights)
                .forEach(System.out::println);

        System.out.println("\nПерелеты, где общее время, проведённое на земле, превышает два часа:");
        new FlightFilterProcess(new GroundTimeFilter())
                .process(flights)
                .forEach(System.out::println);
    }
}

interface FlightFilter {
    boolean test(Flight flight);
}

class PastDepartureFilter implements FlightFilter {

    @Override
    public boolean test(Flight flight) {
        return flight.getSegments().stream()
                .anyMatch(segment -> segment.getDepartureDate().isBefore(LocalDateTime.now()));
    }
}

class InvalidSegmentsFilter implements FlightFilter {

    @Override
    public boolean test(Flight flight) {
        return flight.getSegments().stream()
                .anyMatch(segment -> segment.getArrivalDate().isBefore(segment.getDepartureDate()));
    }
}

class GroundTimeFilter implements FlightFilter {

    @Override
    public boolean test(Flight flight) {
        List<Segment> segments = flight.getSegments();
        if (segments.size() < 2) return false;

        long totalTime = 0;
        for (int i = 1; i < segments.size(); i++) {
            LocalDateTime previousArrival = segments.get(i - 1).getArrivalDate();
            LocalDateTime nextDeparture = segments.get(i).getDepartureDate();
            totalTime += Duration.between(previousArrival, nextDeparture).toHours();
        }
        return totalTime > 2;
    }
}

class FlightFilterProcess {
    private final FlightFilter filter;

    public FlightFilterProcess(FlightFilter filter) {
        this.filter = filter;
    }

    public List<Flight> process(List<Flight> flights) {
        return flights.stream()
                .filter(filter::test)
                .toList();
    }
}