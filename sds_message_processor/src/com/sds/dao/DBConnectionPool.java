package com.sds.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class DBConnectionPool {

    private final static Logger log = Logger.getLogger(DBConnectionPool.class.getName());
    private final ComboPooledDataSource cpds;

    private DBConnectionPool() {
        cpds = new ComboPooledDataSource();
    }

    private static class InstanceHolder {

        static final DBConnectionPool instance = new DBConnectionPool();
    }

    public synchronized static DBConnectionPool getInstance() {
        DBConnectionPool connect = null;
        try {
            connect = InstanceHolder.instance;
        } catch (Exception ex) {
            log.error(ex);
        }
        return connect;
    }

    public Connection getConnection() throws SQLException {
        Connection con = this.cpds.getConnection();
        return con;
    }
    
    public static void closeConnection(Connection connection) {
        //close connection
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            log.warn("error closing connection", ex);
        }
    }
}
