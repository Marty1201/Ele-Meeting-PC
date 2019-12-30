package com.chinaunicom.elemeetingpc.database.dutils;

import com.chinaunicom.elemeetingpc.database.models.Annotation;
import com.chinaunicom.elemeetingpc.database.models.DictionaryInfo;
import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.database.models.IdentityInfo;
import com.chinaunicom.elemeetingpc.database.models.IssueFileRelation;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.database.models.SyncParams;
import com.chinaunicom.elemeetingpc.database.models.UserInfo;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle ORMLite ConnectionSource configuration, a ConnectionSource is a
 * factory for connections to the physical SQL database.
 *
 * @author chenxi 创建时间：2019-6-18 16:40:10
 */
public class DbManager {

    private static final Logger logger = LoggerFactory.getLogger(DbManager.class);
    //database URL
    //private static final String JDBC_DRIVER_HD = "jdbc:h2:~/EleMeetingPCDB";//production enviroment
    private static final String JDBC_DRIVER_HD = "jdbc:h2:./EleMeetingPCDB";//development enviroment
    //database accout & password(production enviroment)
    //public static final String USER = "admin";
    //public static final String PASS = "#752poi#";

    private static ConnectionSource connectionSource;

    /**
     * Initialize our database.
     */
    public static void initDatabase() {
        createConnectionSource();
        //dropTable();
        createTable();
        closeConnectionSource();
    }

    /**
     * Create a connection source to our database.
     */
    public static void createConnectionSource() {
        try {
            connectionSource = new JdbcConnectionSource(JDBC_DRIVER_HD);//development enviroment
            //connectionSource =  new JdbcConnectionSource(JDBC_DRIVER_HD, USER, PASS);//production enviroment
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.DbManager.createConnectionSource"), ex);
        }
    }

    /**
     * Return the connection source, never null.
     *
     * @return connectionSource
     */
    public static ConnectionSource getConnectionSource() {
        if (connectionSource == null) {
            try {
                createConnectionSource();
            } catch (Exception ex) {
                DialogsUtils.errorAlert("system.malfunction");
                logger.error(FxmlUtils.getResourceBundle().getString("error.DbManager.getConnectionSource"), ex);
            }
        }
        return connectionSource;
    }

    /**
     * Close the connection source.
     */
    public static void closeConnectionSource() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception ex) {
                DialogsUtils.errorAlert("system.malfunction");
                logger.error(FxmlUtils.getResourceBundle().getString("error.DbManager.closeConnectionSource"), ex);
            }
        }
    }

    /**
     * to do.
     */
    private static void dropTable() {

    }

    /**
     * Create tables if not exist.
     */
    private static void createTable() {
        try {
            TableUtils.createTableIfNotExists(connectionSource, UserInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, OrganInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, IdentityInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, MeetInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, IssueInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, FileResource.class);
            TableUtils.createTableIfNotExists(connectionSource, MeetIssueRelation.class);
            TableUtils.createTableIfNotExists(connectionSource, IssueFileRelation.class);
            TableUtils.createTableIfNotExists(connectionSource, MeetUserRelation.class);
            TableUtils.createTableIfNotExists(connectionSource, FileUserRelation.class);
            TableUtils.createTableIfNotExists(connectionSource, DictionaryInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, SyncParams.class);
            TableUtils.createTableIfNotExists(connectionSource, Annotation.class);
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.DbManager.createTable"), ex);
        }
    }
}
