package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.databases.DatabaseModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.sql.SqlService;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

/**
 * Lots of accounts based utils (Used for database / file config adaption)
 */
public class AccountingUtils extends ConfigUtils
{

    public SqlService sql;

    private static final String TABLE_ID = "players";
    private static final String UUID_COLUMN = "UUID";
    private static final String PLAYER_NAME_COLUMN = "PLAYER_NAME";
    private static final String JOIN_DATE_COLUMN = "JOIN_DATE";
    private static final String LAST_JOIN_COLUMN = "LAST_JOIN";
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

    public void addPlayer(final Player player)
    {
        String uri = "jdbc:h2:" + plugin.getConfigDir() + "/playerstats";

        try (Connection conn = getDataSource(uri).getConnection())
        {
            Statement stmt = conn.createStatement();

            final ResultSet results = getQuery("SELECT " + PLAYER_NAME_COLUMN + " FROM " + TABLE_ID + " WHERE " + UUID_COLUMN + " = '" + player.getUniqueId() + "'");

            if (!results.next())
            {
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
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

}
