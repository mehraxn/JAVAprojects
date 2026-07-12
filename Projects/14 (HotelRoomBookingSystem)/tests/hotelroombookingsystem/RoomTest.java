package hotelroombookingsystem;
import java.lang.reflect.Modifier; import java.math.BigDecimal;
final class RoomTest { private RoomTest(){} static void run(){
    Room r=new Room(" 101 "," Double ",new BigDecimal("120.50"),2);
    Assertions.assertEquals("101",r.getNumber(),"number"); Assertions.assertEquals("Double",r.getType(),"type");
    Assertions.assertBigDecimalEquals(new BigDecimal("120.50"),r.getNightlyRate(),"rate"); Assertions.assertEquals(2,r.getCapacity(),"capacity");
    Assertions.assertTrue(Modifier.isFinal(Room.class.getModifiers()),"Room final");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Room(null,"T",BigDecimal.ONE,1),"null number");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Room(" ","T",BigDecimal.ONE,1),"blank number");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Room("1",null,BigDecimal.ONE,1),"null type");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Room("1"," ",BigDecimal.ONE,1),"blank type");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Room("1","T",null,1),"null rate");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Room("1","T",BigDecimal.ZERO,1),"zero rate");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Room("1","T",BigDecimal.ONE.negate(),1),"negative rate");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Room("1","T",BigDecimal.ONE,0),"zero capacity");
}}
