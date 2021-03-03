package com.github.supermoonie.cef;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.jdbc.db.SqliteDatabaseType;
import com.j256.ormlite.table.TableUtils;
import org.sqlite.JDBC;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author supermoonie
 * @since 2020/11/24
 */
public class DaoCollections {

    private static final String DATABASE_URL = JDBC.PREFIX + dbPath() + "/jcef.db?date_string_format=yyyy-MM-dd HH:mm:ss&encoding=UTF8";

    private static JdbcPooledConnectionSource connectionSource = null;

    public static void init() throws SQLException {
        connectionSource = new JdbcPooledConnectionSource(DATABASE_URL);
        connectionSource.setDatabaseType(new SqliteDatabaseType());
        setupDatabase(connectionSource);
    }

    public static void close() throws IOException {
        if (null != connectionSource) {
            connectionSource.close();
        }
    }

    public static <T> Dao<T, Integer> getDao(Class<T> clazz) {
        try {
            return DaoManager.createDao(connectionSource, clazz);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setupDatabase(JdbcConnectionSource connectionSource) throws SQLException {
        int localVersion = AppPreferences.getState().getInt(AppPreferences.KEY_LOCAL_VERSION, 0);
        if (localVersion < AppPreferences.DEFAULT_LOCAL_VERSION) {
            TableUtils.dropTable(connectionSource, House.class, true);
            TableUtils.createTable(connectionSource, House.class);
        }

    }

    private static String dbPath() {
        String homeDir = System.getProperty("user.home");
        File dbDir = new File(homeDir + File.separator + ".proxy_fx");
        if (!dbDir.exists() && !dbDir.mkdir()) {
            throw new RuntimeException(dbDir.getAbsolutePath() + " create fail!");
        }
        return dbDir.getAbsolutePath();
    }

    public static JdbcPooledConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
