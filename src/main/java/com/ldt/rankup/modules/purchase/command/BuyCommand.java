package com.ldt.rankup.modules.purchase.command;

import com.ldt.rankup.internal.command.RankupSubcommand;
import com.ldt.rankup.util.RankingUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Command used to buy into a group.
 */
@NonnullByDefault
public class BuyCommand extends RankupSubcommand
{
    private static final Text ACCEPT_KEY = Text.of("accept");

    @Override
    protected String[] getAliases()
    {
        return new String[] {"buy"};
    }

    @Override
    protected Optional<Text> getDescription()
    {
        return Optional.of(Text.of("Allows the player to buy the next rank up in the chain."));
    }

    @Override
    protected Optional<String> getPermission()
    {
        return Optional.of("rankup.purchase.base");
    }

    @Override
    public CommandElement[] getArguments()
    {
        return new CommandElement[] {
          GenericArguments.optionalWeak(GenericArguments.bool(ACCEPT_KEY))
        };
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException
    {

        if (src instanceof Player)
        {
            final Player player = (Player) src;

            if ("".equals(getPlugin().getPerms().getNextGroup(player)))
            {
                src.sendMessage(Text.of("You are in the highest group possible already."));
                return CommandResult.success();
            }

            if (args.<Boolean>getOne(ACCEPT_KEY).orElse(false))
            {
                UniqueAccount acc = getPlugin().getEcon().getOrCreateAccount(player.getUniqueId()).orElse(null);

                if (acc == null)
                {
                    return CommandResult.success();
                }

                final int playerMoney = acc.getBalance(getPlugin().getEcon().getDefaultCurrency()).intValue();
                final int moneyNeeded = getPlugin().getConfigUtils().getGroupsConfig(player).groups.get(getPlugin().getPerms().getNextGroup(player)).moneyNeeded;

                if (playerMoney >= moneyNeeded)
                {
                    acc.withdraw(getPlugin().getEcon().getDefaultCurrency(), BigDecimal.valueOf(moneyNeeded), Cause.of(EventContext.empty(), "Rankup purchase"));
                    src.sendMessage(Text.of("Ranking up! New Balance: " + acc.getBalance(getPlugin().getEcon().getDefaultCurrency()).intValue()));
                    RankingUtils.rankUp(player, getPlugin());
                }
                else
                {
                    src.sendMessage(Text.of(Color.RED, "You do not have enough money, please us /ru check to see requirements."));
                }
            }
            else
            {
                final List<String> message = getModuleData(player, getPlayerData(player, getPlugin().getConfigUtils().getPurchaseConfig().purchaseMessageTemplate));

                for (final Text text : convertToText(message))
                {
                    src.sendMessage(text);
                }
            }
        }

        return CommandResult.success();
    }
}
