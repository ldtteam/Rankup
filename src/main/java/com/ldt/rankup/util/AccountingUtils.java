package com.ldt.rankup.util;

import com.ldt.rankup.Rankup;
import com.ldt.rankup.modules.core.config.AccountConfigData;
import com.ldt.rankup.modules.databases.DatabaseModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Lots of accounts based utils (Used for database / file config adaption)
 */
public class AccountingUtils extends ConfigUtils
{

    private SqlService sql;
    private Connection conn;

    private              String tableId            = TABLE_SUFFIX;
    private static final String TABLE_SUFFIX       = "player_stats";
    private static final String UUID_COLUMN        = "UUID";
    private static final String PLAYER_NAME_COLUMN = "PLAYER_NAME";
    private static final String JOIN_DATE_COLUMN   = "JOIN_DATE";
    private static final String LAST_JOIN_COLUMN   = "LAST_JOIN";
    private static final String TIME_PLAYED_COLUMN = "TIME_PLAYED";

    public AccountingUtils(final Rankup pl)
    {
        super(pl);
    }

    @SuppressWarnings("squid:S2259")
    private DataSource getDataSource(String jdbcUrl) throws SQLException
    {
        tableId = getDatabasesConfig().sqlTablePrefix + TABLE_SUFFIX;
        if (sql == null)
        {
            sql = Sponge.getServiceManager().provide(SqlService.class).orElse(null);
        }
        return sql.getDataSource(jdbcUrl);
    }

    private static String getColumn(final String column)
    {
        switch (column.toUpperCase(Locale.ENGLISH))
        {
            case UUID_COLUMN:
                return UUID_COLUMN;
            case PLAYER_NAME_COLUMN:
                return PLAYER_NAME_COLUMN;
            case JOIN_DATE_COLUMN:
                return JOIN_DATE_COLUMN;
            case LAST_JOIN_COLUMN:
                return LAST_JOIN_COLUMN;
            case TIME_PLAYED_COLUMN:
                return TIME_PLAYED_COLUMN;
            default:
                return "";
        }
    }

    private String getURI()
    {
        if ("mysql".equalsIgnoreCase(getDatabasesConfig().database))
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
        return "jdbc:h2:" + getPlugin().getConfigDir() + "/h2/playerstats";
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
                getPlugin().getLogger().warn("Could not get Connection", e);
            }
        }
        return conn;
    }

    @SuppressWarnings("squid:S2095")
    private ResultSet getQuery(final String query)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID) && conn != null)
            {
                final PreparedStatement stmt = conn.prepareStatement(query);
                return stmt.executeQuery();
            }
        }
        catch (NoModuleException | SQLException e)
        {
            getPlugin().getLogger().warn("Get Query failed", e);
        }
        return null;
    }

    private String toDate(final String date)
    {
        final String newDate = CommonUtils.dateFormat(date);
        if ("h2".equalsIgnoreCase(getDatabasesConfig().database))
        {
            return "to_date('" + newDate + "','yyyy-mm-dd')";
        }
        return "STR_TO_DATE('" + newDate + "','%Y-%m-%d')";
    }

    public void createTableIfNeeded()
    {
        try
        {
            tableId = getDatabasesConfig().sqlTablePrefix + TABLE_SUFFIX;
            String statement = "CREATE TABLE IF NOT EXISTS " + tableId + "("
                                 + UUID_COLUMN + " varchar(60) NOT NULL, "
                                 + PLAYER_NAME_COLUMN + " varchar(40) NOT NULL, "
                                 + JOIN_DATE_COLUMN + " DATE NOT NULL, "
                                 + LAST_JOIN_COLUMN + " DATE NOT NULL, "
                                 + TIME_PLAYED_COLUMN + " int NOT NULL, "
                                 + "PRIMARY KEY(" + UUID_COLUMN + ") )";

            try (PreparedStatement stmt = getConn().prepareStatement(statement))
            {
                stmt.execute();
            }
        }
        catch (SQLException e)
        {
            getPlugin().getLogger().warn("Table Creation Failed", e);
        }
    }

    public String getPlayerName(final UUID uuid)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery(
                  Constants.SQL.SELECT + " " + PLAYER_NAME_COLUMN + " " + Constants.SQL.FROM + " " + tableId + " " + Constants.SQL.WHERE + " " + UUID_COLUMN + " = '" + uuid + "'");

                if (results != null && results.next())
                {
                    final String result = results.getString(PLAYER_NAME_COLUMN);
                    results.close();
                    return result;
                }
            }
            else
            {
                return getAccountConfig().playerData.get(uuid).playerName;
            }
        }
        catch (SQLException | NoModuleException e)
        {
            getPlugin().getLogger().warn("Get Player Name failed", e);
        }
        return "";
    }

    public String getPlayerJoinDate(final UUID uuid)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery(
                  Constants.SQL.SELECT + " " + JOIN_DATE_COLUMN + " " + Constants.SQL.FROM + " " + tableId + " " + Constants.SQL.WHERE + " " + UUID_COLUMN + " = '" + uuid + "'");

                if (results != null && results.next())
                {
                    final String result = results.getDate(JOIN_DATE_COLUMN).toString();
                    results.close();
                    return result;
                }
            }
            else
            {
                return getAccountConfig().playerData.get(uuid).joinDate;
            }
        }
        catch (SQLException | NoModuleException e)
        {
            getPlugin().getLogger().warn("Get Player Join Date failed", e);
        }
        return "";
    }

    public String getPlayerLastDate(final UUID uuid)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery(
                  Constants.SQL.SELECT + " " + LAST_JOIN_COLUMN + " " + Constants.SQL.FROM + " " + tableId + " " + Constants.SQL.WHERE + " " + UUID_COLUMN + " = '" + uuid + "'");

                if (results != null && results.next())
                {
                    final String result = results.getDate(LAST_JOIN_COLUMN).toString();
                    results.close();
                    return result;
                }
            }
            else
            {
                return getAccountConfig().playerData.get(uuid).lastVisit;
            }
        }
        catch (SQLException | NoModuleException e)
        {
            getPlugin().getLogger().warn("Get Player Last Join Date failed", e);
        }
        return "";
    }

    public int getPlayerTime(final UUID uuid)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery(
                  Constants.SQL.SELECT + " " + TIME_PLAYED_COLUMN + " " + Constants.SQL.FROM + " " + tableId + " " + Constants.SQL.WHERE + " " + UUID_COLUMN + " = '" + uuid + "'");

                if (results != null && results.next())
                {
                    final Integer result = results.getInt(TIME_PLAYED_COLUMN);
                    results.close();
                    return result;
                }
            }
            else
            {
                return getAccountConfig().playerData.get(uuid).timePlayed;
            }
        }
        catch (SQLException | NoModuleException e)
        {
            getPlugin().getLogger().warn("Get Player Time failed", e);
        }
        return 0;
    }

    private void updatePlayerAttribute(final UUID uuid, final String column, final String attribute, final boolean isDate) throws SQLException
    {
        if (getConn() != null)
        {
            String statement = Constants.SQL.UPDATE + " "
                                 + tableId + " "
                                 + Constants.SQL.SET + " "
                                 + getColumn(column) + " = "
                                 + "? "
                                 + Constants.SQL.WHERE + " "
                                 + UUID_COLUMN + " = "
                                 + "?";

            try (PreparedStatement stmt = getConn().prepareStatement(statement))
            {

                if (isDate)
                {
                    stmt.setDate(1, Date.valueOf(CommonUtils.dateFormat(attribute)));
                    stmt.setString(2, uuid.toString());
                    stmt.execute();
                }
                else
                {
                    stmt.setString(1, attribute);
                    stmt.setString(2, uuid.toString());
                    stmt.execute();
                }
            }
        }
    }

    private void updatePlayerAttribute(final UUID uuid, final String column, final int attribute) throws SQLException
    {
        if (getConn() != null)
        {

            String statement = Constants.SQL.UPDATE + " "
                                 + tableId + " "
                                 + Constants.SQL.SET + " "
                                 + getColumn(column) + " = "
                                 + "? "
                                 + Constants.SQL.WHERE + " "
                                 + UUID_COLUMN + " = "
                                 + "?";

            try (PreparedStatement stmt = getConn().prepareStatement(statement))
            {
                stmt.setInt(1, attribute);
                stmt.setString(2, uuid.toString());
                stmt.execute();
            }
        }
    }

    public void updatePlayerName(final UUID uuid, final String name)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
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
            getPlugin().getLogger().warn("Update Player Name failed", e);
        }
    }

    public void updatePlayerJoinDate(final UUID uuid, final String date)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                updatePlayerAttribute(uuid, JOIN_DATE_COLUMN, date, true);
            }
            else
            {

                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.get(uuid).joinDate = CommonUtils.dateFormat(date);
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            getPlugin().getLogger().warn("Update Player Join Date failed", e);
        }
    }

    public void updatePlayerLastDate(final UUID uuid, final String date)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                updatePlayerAttribute(uuid, LAST_JOIN_COLUMN, date, true);
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.get(uuid).lastVisit = CommonUtils.dateFormat(date);
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            getPlugin().getLogger().warn("Update Player Last Join Date failed", e);
        }
    }

    public void updatePlayerTime(final UUID uuid, final int time)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
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
            getPlugin().getLogger().warn("Update Player Time failed", e);
        }
    }

    public int addPlayerTime(final UUID uuid, final int time)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
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
            getPlugin().getLogger().warn("Add Player Time failed", e);
        }
        return -1;
    }

    public Map<UUID, Integer> getPlayers()
    {
        final HashMap<UUID, Integer> uuids = new HashMap<>();
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery(Constants.SQL.SELECT + " " + UUID_COLUMN + " from " + tableId);
                while (results != null && results.next())
                {
                    final UUID uuid = UUID.fromString(results.getString(UUID_COLUMN));
                    uuids.put(uuid, getPlayerTime(uuid));
                }
                if (results != null)
                {
                    results.close();
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
            getPlugin().getLogger().warn("Get Players failed", e);
        }
        return uuids;
    }

    public boolean doesPlayerExist(final UUID uuid)
    {
        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                if (getConn() != null)
                {
                    final ResultSet results = getQuery(
                      Constants.SQL.SELECT + " " + PLAYER_NAME_COLUMN + " " + Constants.SQL.FROM + " " + tableId + " " + Constants.SQL.WHERE + " " + UUID_COLUMN + " = '" + uuid
                        + "'");

                    if (results != null)
                    {
                        final Boolean hasNext = results.next();
                        results.close();
                        return hasNext;
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
            getPlugin().getLogger().warn("Does Player Exist failed", e);
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
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                if (getConn() != null)
                {
                    String statement = "INSERT INTO " + tableId
                                         + "(" + UUID_COLUMN + ", " + PLAYER_NAME_COLUMN + ", " + JOIN_DATE_COLUMN + ", " + LAST_JOIN_COLUMN + ", "
                                         + TIME_PLAYED_COLUMN + ") "
                                         + "VALUES"
                                         + "(?,"
                                         + " ?,"
                                         + " " + toDate(CommonUtils.dateNow()) + ","
                                         + " " + toDate(CommonUtils.dateNow()) + ","
                                         + " 0)";

                    try (PreparedStatement stmt = getConn().prepareStatement(statement))
                    {
                        stmt.setString(1, uuid.toString());
                        stmt.setString(2, user.getName());
                        stmt.execute();
                    }
                }
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.put(user.getUniqueId(), new AccountConfigData.PlayerConfig());
                accConfig.playerData.get(user.getUniqueId()).timePlayed = 0;
                accConfig.playerData.get(user.getUniqueId()).lastVisit = CommonUtils.dateNow();
                accConfig.playerData.get(user.getUniqueId()).joinDate = CommonUtils.dateNow();
                accConfig.playerData.get(user.getUniqueId()).playerName = user.getName();
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            getPlugin().getLogger().warn("Add Player failed", e);
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
            if (getPlugin().getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                if (!user.getName().equals(getPlayerName(uuid)))
                {
                    updatePlayerName(uuid, user.getName());
                }

                updatePlayerLastDate(uuid, CommonUtils.dateNow());
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.put(user.getUniqueId(), new AccountConfigData.PlayerConfig());
                accConfig.playerData.get(user.getUniqueId()).timePlayed = 0;
                accConfig.playerData.get(user.getUniqueId()).lastVisit = CommonUtils.dateNow();
                accConfig.playerData.get(user.getUniqueId()).joinDate = CommonUtils.dateNow();
                accConfig.playerData.get(user.getUniqueId()).playerName = user.getName();
                accConfig.save();
            }
        }
        catch (NoModuleException e)
        {
            getPlugin().getLogger().warn("Update Player failed", e);
        }
    }
}
