
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Samuel Kamochu
 */
public class Test {

    public static void main(String[] args) {

        DBConnectionPool pool = DBConnectionPool.getInstance();

        Connection con = null;
        try {
            con = pool.getConnection();
            System.out.println(DataManager.getNodes(con).toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DBConnectionPool.closeConnection(con);

        String str = "SUB1213";
        System.out.println(str.subSequence(0, 3));

    }

    public static void passByReference(String str) {
        str = "new value";
    }

}
