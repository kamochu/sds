
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author Samuel Kamochu
 */
public class Test {

    public static void main(String[] args) {

        String str = "256700110012";

        System.out.println(str.substring(3));
        
        String date1 ="1970-01-01";
        String date2 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        System.out.println(date1);
        System.out.println(date2);
        

    }

}
