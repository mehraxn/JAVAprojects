package trainticketreservationsystem;

public class Main {
    public static void main(String[] args) {
        ReservationSystem reservationSystem = new ReservationSystem();
        Route route = new Route("R-BM", "Berlin", "Munich");
        reservationSystem.addRoute(route);

        Train train = new Train("ICE-101", route);
        for (int seatNumber = 1; seatNumber <= 5; seatNumber++) {
            train.addSeat(new Seat(seatNumber));
        }
        reservationSystem.addTrain(train);

        Reservation reservation = reservationSystem.reserveSeat("ICE-101", 3, "Sofia");
        System.out.println("Reserved seat " + reservation.getSeatNumber()
                + " for " + reservation.getPassengerName());
        System.out.println("Available seats: "
                + reservationSystem.getAvailableSeats("ICE-101").size());
        System.out.println("Berlin to Munich trips: "
                + reservationSystem.searchByRoute("berlin", "MUNICH").size());

        reservationSystem.cancelReservation(reservation.getId());
        System.out.println("Available seats after cancellation: "
                + reservationSystem.getAvailableSeats("ICE-101").size());
    }
}
