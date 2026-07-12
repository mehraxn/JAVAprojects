package hotelroombookingsystem;
import java.lang.reflect.Modifier; import java.math.BigDecimal; import java.time.LocalDate;
final class BookingTest { private BookingTest(){} static void run(){
    Room room=new Room("1","Double",new BigDecimal("99.95"),2); Guest guest=new Guest("G","Name"); LocalDate in=LocalDate.of(2026,8,10);
    Booking b=new Booking("B",room,guest,in,in.plusDays(3),2);
    Assertions.assertEquals("B",b.getId(),"ID"); Assertions.assertEquals(room,b.getRoom(),"room"); Assertions.assertEquals(guest,b.getGuest(),"guest");
    Assertions.assertEquals(in,b.getCheckIn(),"in"); Assertions.assertEquals(in.plusDays(3),b.getCheckOut(),"out"); Assertions.assertEquals(3L,b.getNumberOfNights(),"nights");
    Assertions.assertBigDecimalEquals(new BigDecimal("299.85"),b.calculateTotalPrice(),"decimal multi-night total");
    Assertions.assertBigDecimalEquals(new BigDecimal("99.95"),new Booking("S",room,guest,in,in.plusDays(1)).calculateTotalPrice(),"single night");
    Assertions.assertEquals(2,b.getGuestCount(),"guest count"); Assertions.assertEquals(BookingStatus.ACTIVE,b.getStatus(),"active");
    Assertions.assertTrue(Modifier.isFinal(Booking.class.getModifiers()),"Booking final");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking(null,room,guest,in,in.plusDays(1)),"null ID");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking(" ",room,guest,in,in.plusDays(1)),"blank ID");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking("B",null,guest,in,in.plusDays(1)),"null room");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking("B",room,null,in,in.plusDays(1)),"null guest");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking("B",room,guest,null,in.plusDays(1)),"null in");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking("B",room,guest,in,null),"null out");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking("B",room,guest,in,in),"same day");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking("B",room,guest,in,in.minusDays(1)),"backwards");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking("B",room,guest,in,in.plusDays(1),0),"zero guests");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Booking("B",room,guest,in,in.plusDays(1),3),"capacity");
}}
