package com.minecolonies.rankup.modules.databases.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

public class dataCommand extends RankupSubcommand
{
    private SqlService sql;

    @Override
    protected String[] getAliases()
    {
        return new String[] {"data"};
    }

    @Override
    protected Optional<Text> getDescription()
    {
        return Optional.of(Text.of("None"));
    }

    @Override
    protected Optional<String> getPermission()
    {
        return Optional.empty();
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException
    {
        myMethodThatQueries();

        if (src instanceof Player)
        {
            final UUID uuid = ((Player) src).getUniqueId();
            src.sendMessage(Text.of(getPlugin().accUtils.getPlayerName(uuid)));
            src.sendMessage(Text.of(getPlugin().accUtils.getPlayerJoinDate(uuid)));
            src.sendMessage(Text.of(getPlugin().accUtils.getPlayerLastDate(uuid)));
            src.sendMessage(Text.of(getPlugin().accUtils.getPlayerTime(uuid)));
        }

        return CommandResult.success();
    }

    public DataSource getDataSource(String jdbcUrl) throws SQLException
    {
        if (sql == null)
        {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(jdbcUrl);
    }

    public void myMethodThatQueries()
    {
        String uri = "jdbc:h2:" + getPlugin().getConfigDir() + "/playerstats";
        String sql = "SELECT * FROM players";

        try (Connection conn = getDataSource(uri).getConnection())
        {
            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS players("
                           + "UUID varchar(255) NOT NULL, "
                           + "PLAYER_NAME varchar(255) NOT NULL, "
                           + "JOIN_DATE DATE NOT NULL, "
                           + "LAST_JOIN DATE NOT NULL, "
                           + "TIME_PLAYED int NOT NULL, "
                           + "PRIMARY KEY (UUID) )");

            /*
            stmt.execute("INSERT INTO players"
                           + "(UUID, PLAYER_NAME, JOIN_DATE, LAST_JOIN, TIME_PLAYED) "
                           + "VALUES"
                           + "('64b15b8a-dd25-35fa-925c-6f9ae1c47609',"
                           + " 'Asherslab',"
                           + " to_date('04/11/2017', 'dd/mm/yyyy'),"
                           + " to_date('12/10/2017', 'dd/mm/yyyy'),"
                           + " 20064)");*/

            ResultSet results = stmt.executeQuery(sql);

            while (results.next())
            {
                getPlugin().getLogger().info("SQL: " + results.getString("UUID"));
                getPlugin().getLogger().info("SQL: " + results.getString("PLAYER_NAME"));
                getPlugin().getLogger().info("SQL: " + results.getDate("JOIN_DATE"));
                getPlugin().getLogger().info("SQL: " + results.getDate("LAST_JOIN"));
                getPlugin().getLogger().info("SQL: " + results.getInt("TIME_PLAYED"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void addPlayer(final Player player)
    {
        String uri = "jdbc:h2:" + getPlugin().getConfigDir() + "/playerstats";

        try (Connection conn = getDataSource(uri).getConnection())
        {
            Statement stmt = conn.createStatement();

            stmt.execute("INSERT INTO players"
                           + "(UUID, PLAYER_NAME, JOIN_DATE, LAST_JOIN, TIME_PLAYED) "
                           + "VALUES"
                           + "('" + player.getUniqueId() + "',"
                           + " '" + player.getName() + "',"
                           + " to_date('" + dateNow() + "', 'dd/mm/yyyy'),"
                           + " to_date('" + dateNow() + "', 'dd/mm/yyyy'),"
                           + " 0)");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private String dateNow()
    {
        CoreConfig config = getPlugin().getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        DateFormat dateFormat = new SimpleDateFormat(config.dateFormat);
        java.util.Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
}
