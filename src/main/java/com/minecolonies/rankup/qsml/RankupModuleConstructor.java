package com.minecolonies.rankup.qsml;

import com.minecolonies.rankup.Rankup;
import uk.co.drnaylor.quickstart.Module;
import uk.co.drnaylor.quickstart.exceptions.QuickStartModuleLoaderException;
import uk.co.drnaylor.quickstart.loaders.ModuleConstructor;

public class RankupModuleConstructor implements ModuleConstructor
{
    private final Rankup rankup;

    public RankupModuleConstructor(Rankup rankup)
    {
        this.rankup = rankup;
    }

    /**
     * Instantiates modules
     *
     * @param moduleClass The {@link Class} of the module to load.
     * @return The module
     *
     * @throws QuickStartModuleLoaderException.Construction if there was a failure in instantiation.
     */
    @Override
    public Module constructModule(Class<? extends Module> moduleClass) throws QuickStartModuleLoaderException.Construction
    {
        return rankup.getInjector().getInstance(moduleClass);
    }
}
