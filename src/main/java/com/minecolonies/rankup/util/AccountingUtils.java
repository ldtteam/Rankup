package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.databases.DatabaseModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.sql.SqlService;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Lots of accounts based utils (Used for database / file config adaption)
 */
public class AccountingUtils extends ConfigUtils
{

    private SqlService sql;

    private static final String TABLE_ID           = "players";
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
        if (sql == null)
        {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(jdbcUrl);
    }

    private Connection getConn()
    {
        String uri = "jdbc:h2:" + plugin.getConfigDir() + "/playerstats";

        try
        {
            return getDataSource(uri).getConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
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
                    Statement stmt = conn.createStatement();

                    return stmt.executeQuery(query);
                }
            }
        }
        catch (NoModuleException | SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String getPlayerName(final UUID uuid)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final ResultSet results = getQuery("SELECT " + PLAYER_NAME_COLUMN + " FROM " + TABLE_ID + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

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
                final ResultSet results = getQuery("SELECT " + JOIN_DATE_COLUMN + " FROM " + TABLE_ID + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

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
                final ResultSet results = getQuery("SELECT " + LAST_JOIN_COLUMN + " FROM " + TABLE_ID + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

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
                final ResultSet results = getQuery("SELECT " + TIME_PLAYED_COLUMN + " FROM " + TABLE_ID + " WHERE " + UUID_COLUMN + " = '" + uuid + "'");

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
                stmt.execute("UPDATE " + TABLE_ID + " SET " + column + " = to_date('" + attribute + "', 'dd/mm/yyyy') WHERE " + UUID_COLUMN + " = " + uuid);
            }
            else
            {
                stmt.execute("UPDATE " + TABLE_ID + " SET " + column + " = '" + attribute + "' WHERE " + UUID_COLUMN + " = " + uuid);
            }
        }
    }

    private void updatePlayerAttribute(final UUID uuid, final String column, final int attribute) throws SQLException
    {
        final Connection conn = getConn();

        if (conn != null)
        {
            Statement stmt = conn.createStatement();

            stmt.execute("UPDATE " + TABLE_ID + " SET " + column + " = " + attribute + " WHERE " + UUID_COLUMN + " = " + uuid);
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
                DateFormat dateFormat = new SimpleDateFormat(getCoreConfig().dateFormat);

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
                DateFormat dateFormat = new SimpleDateFormat(getCoreConfig().dateFormat);

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

    public void addPlayerTime(final UUID uuid, final int time)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                final int timeToAdd = getPlayerTime(uuid) + time;
                updatePlayerTime(uuid, timeToAdd);
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.get(uuid).timePlayed = accConfig.playerData.get(uuid).timePlayed + time;
                accConfig.save();
            }
        }
        catch (NoModuleException e)
        {
            e.printStackTrace();
        }
    }

    public boolean doesPlayerExist(final Player player)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                Connection conn = getConn();

                if (conn != null)
                {
                    final ResultSet results = getQuery("SELECT " + PLAYER_NAME_COLUMN + " FROM " + TABLE_ID + " WHERE " + UUID_COLUMN + " = '" + player.getUniqueId() + "'");

                    if (results != null)
                    {
                        return results != null && !results.next();
                    }
                }
            }
            else
            {
                return getAccountConfig().playerData.containsKey(player.getUniqueId());
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void addPlayer(final Player player)
    {
        if (doesPlayerExist(player))
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

                    stmt.execute("INSERT INTO players"
                                   + "(UUID, PLAYER_NAME, JOIN_DATE, LAST_JOIN, TIME_PLAYED) "
                                   + "VALUES"
                                   + "('" + player.getUniqueId() + "',"
                                   + " '" + player.getName() + "',"
                                   + " to_date('" + CommonUtils.dateNow(plugin) + "', 'dd/mm/yyyy'),"
                                   + " to_date('" + CommonUtils.dateNow(plugin) + "', 'dd/mm/yyyy'),"
                                   + " 0)");
                }
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.put(player.getUniqueId(), new AccountConfigData.PlayerConfig());
                accConfig.playerData.get(player.getUniqueId()).timePlayed = 0;
                accConfig.playerData.get(player.getUniqueId()).lastVisit = CommonUtils.dateNow(plugin);
                accConfig.playerData.get(player.getUniqueId()).joinDate = CommonUtils.dateNow(plugin);
                accConfig.playerData.get(player.getUniqueId()).playerName = player.getName();
                accConfig.save();
            }
        }
        catch (SQLException | NoModuleException e)
        {
            e.printStackTrace();
        }
    }

    public void updatePlayer(final Player player)
    {
        try
        {
            if (plugin.getModuleContainer().isModuleLoaded(DatabaseModule.ID))
            {
                Connection conn = getConn();

                if (conn != null)
                {
                    final UUID uuid = player.getUniqueId();
                    if (!player.getName().equals(getPlayerName(uuid)))
                    {
                        updatePlayerName(uuid, player.getName());
                    }

                    updatePlayerLastDate(uuid, CommonUtils.dateNow(plugin));
                }
            }
            else
            {
                final AccountConfigData accConfig = getAccountConfig();
                accConfig.playerData.put(player.getUniqueId(), new AccountConfigData.PlayerConfig());
                accConfig.playerData.get(player.getUniqueId()).timePlayed = 0;
                accConfig.playerData.get(player.getUniqueId()).lastVisit = CommonUtils.dateNow(plugin);
                accConfig.playerData.get(player.getUniqueId()).joinDate = CommonUtils.dateNow(plugin);
                accConfig.playerData.get(player.getUniqueId()).playerName = player.getName();
                accConfig.save();
            }
        }
        catch (NoModuleException e)
        {
            e.printStackTrace();
        }
    }
}
