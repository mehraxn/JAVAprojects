package hotelroombookingsystem;
import java.lang.reflect.Modifier;
final class GuestTest { private GuestTest(){} static void run(){
    Guest g=new Guest(" G1 "," Nora "); Assertions.assertEquals("G1",g.getId(),"ID"); Assertions.assertEquals("Nora",g.getName(),"name");
    Assertions.assertTrue(Modifier.isFinal(Guest.class.getModifiers()),"Guest final");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Guest(null,"N"),"null ID"); Assertions.assertThrows(IllegalArgumentException.class,()->new Guest(" ","N"),"blank ID");
    Assertions.assertThrows(IllegalArgumentException.class,()->new Guest("G",null),"null name"); Assertions.assertThrows(IllegalArgumentException.class,()->new Guest("G"," "),"blank name");
}}
