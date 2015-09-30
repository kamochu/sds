
/**
 *
 * @author Samuel Kamochu
 */
public class Test {

    public static void main(String[] args) {

        String str = "SUB1213";
        
        System.out.println(str.subSequence(0, 3));

    }

    public static void passByReference(String str) {
        str = "new value";
    }

}
