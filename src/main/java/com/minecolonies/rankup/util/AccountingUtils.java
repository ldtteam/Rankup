package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.databases.DatabaseModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;

/**
 * Lots of accounts based utils (Used for database / file config adaption)
 */
public class AccountingUtils extends ConfigUtils
{

    private SqlService sql;
    private Connection conn;

    private String table_id = "player_stats";
    private static final String UUID_COLUMN        = "UUID";
    private static final String PLAYER_NAME_COLUMN = "PLAYER_NAME";
    private static final String JOIN_DATE_COLUMN   = "JOIN_DATE";
    private static final String LAST_JOIN_COLUMN   = "LAST_JOIN";
    private static final String TIME_PLAYED_COLUMN = "TIME_PLAYED";

    public AccountingUtils(final Rankup pl)
    {
        super(pl);
    }

    private DataSource getDataSource(String jdbcUrl) throws SQLException
    {
        table_id = getDatabasesConfig().sqlTablePrefix + "player_stats";
        if (sql == null)
        {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(jdbcUrl);
    }

    private String getURI()
    {
        if (getDatabasesConfig().database.equalsIgnoreCase("mysql"))
        {
            return "jdbc:mysql://"
                     + getDatabasesConfig().sqlUsername
                     + ":"
                     + getDatabasesConfig().sqlPassword
                     + "@"
                     + getDatabasesConfig().sqlAddress
                     + "/"
                     + getDatabasesConfig().sqlDatabase;
        }
        return "jdbc:h2:" + plugin.getConfigDir() + "/h2/playerstats";
    }

    private Connection getConn()
    {
        if (conn == null)
        {
            String uri = getURI();
            try
            {
                conn = getDataSource(uri).getConnection();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return conn;
    }

    private ResultSet getQuery(final String query)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final Connection conn = getConn();

                if (conn != null)
                {
                    return conn.createStatement().executeQuery(query);
                }
            }
        }
        catch (NoModuleException | SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String toDate(final String date)
    {
        if (getDatabasesConfig().database.equalsIgnoreCase("h2"))
        {
            return "to_date('" + date + "','yyyy-mm-dd')";
        }
        return "STR_TO_DATE('" + date + "','%Y-%m-%d')";
    }

    public void createTableIfNeeded()
    {
        try
        {
            Statement stmt = getConn().createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS " + table_id + "("
                           + UUID_COLUMN + " varchar(255) NOT NULL, "
                           + PLAYER_NAME_COLUMN + " varchar(255) NOT NULL, "
                           + JOIN_DATE_COLUMN + " DATE NOT NULL, "
                           + LAST_JOIN_COLUMN + " DATE NOT NULL, "
                           + TIME_PLAYED_COLUMN + " int NOT NULL, "
                           + "PRIMARY KEY(" + UUID_COLUMN + ") )");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public String getPlayerName(final UUID uuid)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery("SELECT " + PLAYER_NAME_COLUMN + " FROM " + table_id + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

                if (results != null && results.next())
                {
                    return results.getString(PLAYER_NAME_COLUMN);
                }
            }
            else
            {
                return getAccountConfig().playerData.get(uuid).playerName;
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public String getPlayerJoinDate(final UUID uuid)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery("SELECT " + JOIN_DATE_COLUMN + " FROM " + table_id + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

                if (results != null && results.next())
                {
                    return results.getDate(JOIN_DATE_COLUMN).toString();
                }
            }
            else
            {
                return getAccountConfig().playerData.get(uuid).joinDate;
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public String getPlayerLastDate(final UUID uuid)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery("SELECT " + LAST_JOIN_COLUMN + " FROM " + table_id + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

                if (results != null && results.next())
                {
                    return results.getDate(LAST_JOIN_COLUMN).toString();
                }
            }
            else
            {
                return getAccountConfig().playerData.get(uuid).lastVisit;
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public int getPlayerTime(final UUID uuid)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery("SELECT " + TIME_PLAYED_COLUMN + " FROM " + table_id + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

                if (results != null && results.next())
                {
                    return results.getInt(TIME_PLAYED_COLUMN);
                }
            }
            else
            {
                return getAccountConfig().playerData.get(uuid).timePlayed;
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    private void updatePlayerAttribute(final UUID uuid, final String column, final String attribute, final boolean isDate) throws SQLException
    {
        final Connection conn = getConn();

        if (conn != null)
        {
            Statement stmt = conn.createStatement();

            if (isDate)
            {
                stmt.execute("UPDATE " + table_id + " SET " + column + " = " + toDate(attribute) + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");
            }
            else
            {
                stmt.execute("UPDATE " + table_id + " SET " + column + " = '" + attribute + "' WHERE " + UUID_COLUMN + " = '" + uuid + "'");
            }
        }
    }

    private void updatePlayerAttribute(final UUID uuid, final String column, final int attribute) throws SQLException
    {
        final Connection conn = getConn();

        if (conn != null)
        {
            Statement stmt = conn.createStatement();

            stmt.execute("UPDATE " + table_id + " SET " + column + " = " + attribute + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");
        }
    }

    public void updatePlayerName(final UUID uuid, final String name)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                updatePlayerAttribute(uuid, PLAYER_NAME_COLUMN, name, false);
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.get(uuid).playerName = name;
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
    }

    public void updatePlayerJoinDate(final UUID uuid, final String date)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                updatePlayerAttribute(uuid, JOIN_DATE_COLUMN, date, true);
            }
            else
            {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.get(uuid).joinDate = dateFormat.format(date);
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
    }

    public void updatePlayerLastDate(final UUID uuid, final String date)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                updatePlayerAttribute(uuid, LAST_JOIN_COLUMN, date, true);
            }
            else
            {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.get(uuid).lastVisit = dateFormat.format(date);
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
    }

    public void updatePlayerTime(final UUID uuid, final int time)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                updatePlayerAttribute(uuid, TIME_PLAYED_COLUMN, time);
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.get(uuid).timePlayed = time;
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
    }

    public int addPlayerTime(final UUID uuid, final int time)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final int timeToAdd = getPlayerTime(uuid) + time;
                updatePlayerTime(uuid, timeToAdd);
                return timeToAdd;
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                final int timeToAdd = accConfig.playerData.get(uuid).timePlayed + time;
                accConfig.playerData.get(uuid).timePlayed = timeToAdd;
                accConfig.save();
                return timeToAdd;
            }
        }
        catch (NoModuleException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public HashMap<UUID, Integer> getPlayers()
    {
        final HashMap<UUID, Integer> uuids = new HashMap<>();
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery("SELECT " + UUID_COLUMN + " from " + table_id);
                while (results != null && results.next())
                {
                    final UUID uuid = UUID.fromString(results.getString(UUID_COLUMN));
                    uuids.put(uuid, getPlayerTime(uuid));
                }
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                for (final UUID uuid : accConfig.playerData.keySet())
                {
                    uuids.put(uuid, getPlayerTime(uuid));
                }
            }
        }
        catch (NoModuleException | SQLException e)
        {
            e.printStackTrace();
        }
        return uuids;
    }

    public boolean doesPlayerExist(final UUID uuid)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                Connection conn = getConn();

                if (conn != null)
                {
                    final ResultSet results = getQuery("SELECT " + PLAYER_NAME_COLUMN + " FROM " + table_id + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

                    if (results != null)
                    {
                        return results.next();
                    }
                }
            }
            else
            {
                return getAccountConfig().playerData.containsKey(uuid);
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void addPlayer(final UUID uuid)
    {
        final User user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid).orElse(null);

        if (user == null || doesPlayerExist(uuid))
        {
            return;
        }

        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                Connection conn = getConn();

                if (conn != null)
                {
                    Statement stmt = conn.createStatement();

                    stmt.execute("INSERT INTO " + table_id
                                   + "(" + UUID_COLUMN + ", " + PLAYER_NAME_COLUMN + ", " + JOIN_DATE_COLUMN + ", " + LAST_JOIN_COLUMN + ", " + TIME_PLAYED_COLUMN + ") "
                                   + "VALUES"
                                   + "('" + uuid + "',"
                                   + " '" + user.getName() + "',"
                                   + " " + toDate(CommonUtils.dateNow(plugin)) + ","
                                   + " " + toDate(CommonUtils.dateNow(plugin)) + ","
                                   + " 0)");
                }
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.put(user.getUniqueId(), new AccountConfigData.PlayerConfig());
                accConfig.playerData.get(user.getUniqueId()).timePlayed = 0;
                accConfig.playerData.get(user.getUniqueId()).lastVisit = CommonUtils.dateNow(plugin);
                accConfig.playerData.get(user.getUniqueId()).joinDate = CommonUtils.dateNow(plugin);
                accConfig.playerData.get(user.getUniqueId()).playerName = user.getName();
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            if (e instanceof SQLException)
            {
                plugin.getLogger().info(((SQLException) e).getSQLState());
            }

            e.printStackTrace();
        }
    }

    public void updatePlayer(final UUID uuid)
    {
        final User user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid).orElse(null);

        if (user == null || !doesPlayerExist(uuid))
        {
            return;
        }

        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                if (!user.getName().equals(getPlayerName(uuid)))
                {
                    updatePlayerName(uuid, user.getName());
                }

                updatePlayerLastDate(uuid, CommonUtils.dateNow(plugin));
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.put(user.getUniqueId(), new AccountConfigData.PlayerConfig());
                accConfig.playerData.get(user.getUniqueId()).timePlayed = 0;
                accConfig.playerData.get(user.getUniqueId()).lastVisit = CommonUtils.dateNow(plugin);
                accConfig.playerData.get(user.getUniqueId()).joinDate = CommonUtils.dateNow(plugin);
                accConfig.playerData.get(user.getUniqueId()).playerName = user.getName();
                accConfig.save();
            }
        }
        catch (NoModuleException e)
        {
            e.printStackTrace();
        }
    }
}
