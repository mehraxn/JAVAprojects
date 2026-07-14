package hotelroombookingsystem;
import java.lang.reflect.Modifier; import java.math.BigDecimal; import java.time.LocalDate;
final class HotelTest { private HotelTest(){} static void run(){
    Hotel h=new Hotel(); Room external=new Room("1","Double",new BigDecimal("100"),2); h.addRoom(external); h.addRoom(new Room("2","Single",new BigDecimal("80"),1));
    Assertions.assertTrue(Modifier.isFinal(Hotel.class.getModifiers()),"Hotel final"); Assertions.assertEquals(2,h.listRooms().size(),"rooms");
    Assertions.assertThrows(UnsupportedOperationException.class,()->h.listRooms().clear(),"room list immutable"); Assertions.assertThrows(IllegalArgumentException.class,()->h.addRoom(new Room("1","X",BigDecimal.ONE,1)),"duplicate room");
    LocalDate in=LocalDate.of(2026,8,10),out=LocalDate.of(2026,8,13); Guest g=new Guest("G","Name"); BookingSnapshot b=h.bookRoom("1",g,in,out,2);
    Assertions.assertEquals("B0001",b.getId(),"generated ID"); Assertions.assertEquals(1,h.listBookings().size(),"booked"); Assertions.assertEquals(1,h.findAvailableRooms(in,out).size(),"availability");
    Assertions.assertThrows(UnsupportedOperationException.class,()->h.listBookings().clear(),"booking list immutable"); Assertions.assertThrows(UnsupportedOperationException.class,()->h.findAvailableRooms(in,out).clear(),"available immutable");
    Assertions.assertEquals(0.0,h.calculateOccupancy(out),"checkout date not occupied");
    Assertions.assertEquals(2,h.findAvailableRooms(out,out.plusDays(1)).size(),"checkout date available");
    assertOverlap(h,in,out,in,out,"same"); assertOverlap(h,in,out,in.plusDays(1),out.plusDays(1),"starts inside"); assertOverlap(h,in,out,in.minusDays(1),out.minusDays(1),"ends inside");
    assertOverlap(h,in,out,in.minusDays(1),out.plusDays(1),"contains"); assertOverlap(h,in,out,in.plusDays(1),out.minusDays(1),"contained");
    int before=h.listBookings().size(); Assertions.assertEquals(before,1,"failures unchanged");
    BookingSnapshot after=h.bookRoom("1",new Guest("A","After"),out,out.plusDays(2)); Assertions.assertNotNull(after,"adjacent after");
    BookingSnapshot beforeBooking=h.bookRoom("1",new Guest("P","Before"),in.minusDays(2),in); Assertions.assertNotNull(beforeBooking,"adjacent before");
    Assertions.assertEquals(50.0,h.calculateOccupancy(in),"occupied percent"); Assertions.assertEquals(50.0,h.calculateOccupancy(out),"adjacent booking occupied");
    h.cancelBooking(b.getId()); Assertions.assertEquals(BookingStatus.CANCELLED,h.findBookingById(b.getId()).getStatus(),"cancel status");
    Assertions.assertEquals(2,h.findAvailableRooms(in.plusDays(1),out).size(),"cancel restores"); Assertions.assertEquals(0.0,h.calculateOccupancy(in.plusDays(1)),"cancel occupancy");
    Assertions.assertThrows(IllegalStateException.class,()->h.cancelBooking(b.getId()),"already cancelled"); Assertions.assertThrows(IllegalArgumentException.class,()->h.cancelBooking("bad"),"missing cancellation");
    Assertions.assertThrows(IllegalArgumentException.class,()->h.bookRoom("bad",g,in,out),"unknown room"); Assertions.assertThrows(IllegalArgumentException.class,()->h.bookRoom("2",g,in,in),"invalid range");
    Assertions.assertThrows(IllegalArgumentException.class,()->h.getRoom("bad"),"missing room"); Assertions.assertEquals(3,h.listBookings().size(),"cancelled retained");
    Assertions.assertEquals("Double",h.getRoom("1").getType(),"snapshot query"); Assertions.assertEquals(2,external.getCapacity(),"input safe");
  }
  private static void assertOverlap(Hotel h,LocalDate in,LocalDate out,LocalDate start,LocalDate end,String message){int count=h.listBookings().size();Assertions.assertThrows(IllegalStateException.class,()->h.bookRoom("1",new Guest("X"+message,message),start,end),message);Assertions.assertEquals(count,h.listBookings().size(),message+" unchanged");}
}
