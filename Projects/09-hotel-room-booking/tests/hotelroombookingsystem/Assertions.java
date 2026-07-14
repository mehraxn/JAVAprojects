package hotelroombookingsystem;
import java.math.BigDecimal;
final class Assertions {
    private static int checks; private Assertions() { }
    static int getChecks(){return checks;}
    static void assertEquals(Object e,Object a,String m){checks++;if(!java.util.Objects.equals(e,a))fail(m);}
    static void assertNotEquals(Object e,Object a,String m){checks++;if(java.util.Objects.equals(e,a))fail(m);}
    static void assertTrue(boolean c,String m){checks++;if(!c)fail(m);}
    static void assertFalse(boolean c,String m){assertTrue(!c,m);}
    static void assertNull(Object v,String m){checks++;if(v!=null)fail(m);}
    static void assertNotNull(Object v,String m){checks++;if(v==null)fail(m);}
    static void assertContains(String t,String s,String m){checks++;if(t==null||!t.contains(s))fail(m);}
    static void assertBigDecimalEquals(BigDecimal e,BigDecimal a,String m){checks++;if(e==null||a==null||e.compareTo(a)!=0)fail(m);}
    static void assertThrows(Class<? extends Throwable> type,Runnable action,String m){checks++;try{action.run();}catch(Throwable t){if(type.isInstance(t))return;throw new AssertionError(m+" (wrong exception)",t);}fail(m+" (no exception)");}
    private static void fail(String m){throw new AssertionError(m);}
}
