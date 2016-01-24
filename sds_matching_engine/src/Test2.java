
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class Test2 {

    public static void main(String[] args) {
        
        String str = "256700110012";
        System.out.println(str.substring(3));

        String date1 = "2015-01-26";
        String date2 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        System.out.println(date1);
        System.out.println(date2);

        Date nextMatchDate;
        Date currentDate;

        try {
            nextMatchDate = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
            currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(date2);

            if (nextMatchDate.compareTo(currentDate) <= 0) {
                System.out.println("we can do the matching now");
            } else {
                System.out.println("We have to wait");
            }

        } catch (ParseException ex) {
            Logger.getLogger(Test2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
