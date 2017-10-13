package com.minecolonies.rankup.qsml.modulespec;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.internal.Reloadable;
import com.minecolonies.rankup.internal.command.RankupCommand;
import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.internal.listener.ConditionalListener;
import com.minecolonies.rankup.internal.listener.ListenerBase;
import com.minecolonies.rankup.util.Action;
import org.spongepowered.api.Sponge;
import uk.co.drnaylor.quickstart.Module;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A module that has no configuration.
 */
public abstract class StandardModule implements Module
{

    private final String        moduleId;
    private final String        moduleName;
    @Inject
    private       Rankup        plugin;
    @Inject
    private       RankupCommand command;

    private final String packageName;

    public StandardModule()
    {
        ModuleData md = this.getClass().getAnnotation(ModuleData.class);
        this.moduleId = md.id();
        this.moduleName = md.name();
        this.packageName = this.getClass().getPackage().getName() + ".";
    }

    protected String getModuleId()
    {
        return this.moduleId;
    }

    protected String getModuleName()
    {
        return this.moduleName;
    }

    protected Rankup getPlugin()
    {
        return this.plugin;
    }

    @Override
    public final void onEnable()
    {
        // Any classes can make use of the injector.
        // This section will be used to do any common tasks, such as scan for commands/listeners/hooks/whatever

        // Get all the subcommands to register.
        List<Class<? extends RankupSubcommand>> subcommandList = getStreamForModule(RankupSubcommand.class)
                                                                   .collect(Collectors.toList());

        // For each command, instantiate and register.
        final Injector injector = this.plugin.getInjector();

        // Add the subcommands.
        subcommandList.forEach(x ->
        {
            RankupSubcommand subcommand = injector.getInstance(x);
            if (subcommand instanceof Reloadable)
            {
                Reloadable r = (Reloadable) subcommand;
                this.plugin.addReloadable(this.moduleId, r::onReload);

                // Reload it now
                r.onReload();
            }

            this.command.registerSubCommand(subcommand);
        });

        // Now, listeners.
        registerListeners(this.plugin, injector);

        Rankup2Enable();
    }

    // Separate method for if we use a disableable module in the future.
    protected void registerListeners(Rankup plugin, Injector injector)
    {
        List<Class<? extends ListenerBase>> listenerClass = getStreamForModule(ListenerBase.class).collect(Collectors.toList());

        // Instantiate them all.
        listenerClass.forEach(x ->
        {
            ConditionalListener cl = x.getAnnotation(ConditionalListener.class);
            ListenerBase base = injector.getInstance(x);
            if (base instanceof Reloadable)
            {
                Reloadable r = (Reloadable) base;
                this.plugin.addReloadable(this.moduleId, r::onReload);

                // Reload it now
                r.onReload();
            }

            if (cl != null)
            {
                // Create the reloadable.
                try
                {
                    Predicate<Rankup> p = cl.value().newInstance();
                    Action a = () ->
                    {
                        Sponge.getEventManager().unregisterListeners(base);
                        if (p.test(plugin))
                        {
                            Sponge.getEventManager().registerListeners(plugin, base);
                        }
                    };

                    this.plugin.addReloadable(moduleId, a);
                    a.action();
                }
                catch (Exception e)
                {
                    // Developers - this should never happen!
                    e.printStackTrace();
                }
            }
            else
            {
                Sponge.getEventManager().registerListeners(plugin, base);
            }
        });
    }

    public void Rankup2Enable() {}

    /*
     * This is where the magic happens, folks!
     */
    @SuppressWarnings("unchecked")
    private <T> Stream<Class<? extends T>> getStreamForModule(Class<T> assignableClass)
    {
        return plugin.getModuleContainer().getLoadedClasses().stream()
                 .filter(assignableClass::isAssignableFrom)
                 .filter(x -> x.getPackage().getName().startsWith(packageName))
                 .filter(x -> !Modifier.isAbstract(x.getModifiers()) && !Modifier.isInterface(x.getModifiers()))
                 .map(x -> (Class<? extends T>) x);
    }
}