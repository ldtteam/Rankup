package com.minecolonies.rankup.qsml;

import com.google.inject.AbstractModule;
import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.internal.command.RankupCommand;

public class InjectorModule extends AbstractModule
{

    private final Rankup        rankup2;
    private final RankupCommand rankupCommand;

    public InjectorModule(Rankup phonon, RankupCommand command)
    {
        this.rankup2 = phonon;
        this.rankupCommand = command;
    }

    @Override
    protected void configure()
    {
        bind(Rankup.class).toInstance(this.rankup2);
        bind(RankupCommand.class).toInstance(this.rankupCommand);
    }
}
