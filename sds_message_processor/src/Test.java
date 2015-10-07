
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 *
 * @author Samuel Kamochu
 */
public class Test {

    public static void main(String[] args) {

        String pattern = "[mMfF]";
         System.out.println("Matching " + Pattern.compile(pattern).matcher("g").matches());

//        for (int i = 0; i < 10; i++) {
//
//            System.out.println("Matching " + i + " = " + Pattern.compile(pattern).matcher("" + i).matches());
//        }

    }

}
