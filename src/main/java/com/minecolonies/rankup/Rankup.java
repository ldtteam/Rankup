package com.minecolonies.rankup;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.magitechserver.magibridge.MagiBridge;
import com.minecolonies.rankup.internal.command.RankupCommand;
import com.minecolonies.rankup.internal.configurate.BaseConfig;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import com.minecolonies.rankup.qsml.InjectorModule;
import com.minecolonies.rankup.qsml.RankupLoggerProxy;
import com.minecolonies.rankup.qsml.RankupModuleConstructor;
import com.minecolonies.rankup.qsml.SubInjectorModule;
import com.minecolonies.rankup.util.Action;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.permission.Subject;
import uk.co.drnaylor.quickstart.config.AbstractConfigAdapter;
import uk.co.drnaylor.quickstart.exceptions.IncorrectAdapterTypeException;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;
import uk.co.drnaylor.quickstart.exceptions.QuickStartModuleLoaderException;
import uk.co.drnaylor.quickstart.modulecontainers.DiscoveryModuleContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

import static com.minecolonies.rankup.Plugininfo.*;

@Plugin(
  id = ID,
  name = NAME,
  version = VERSION,
  description = DESCRIPTION,
  url = URL,
         dependencies = {@Dependency(id = "magibridge", optional = true)}
)
public class Rankup
{
    private final Logger                                          logger;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final SubInjectorModule subInjectorModule = new SubInjectorModule();

    public EconomyService econ;
    public static Rankup instance = null;
    public        Game                                         game;
    private final RankupCommand                                rankupCommand;
    private final Path                                         configDir;
    private       GuiceObjectMapperFactory                     factory;
    private       Map<Class<? extends BaseConfig>, BaseConfig> configs;
    private       Injector                                     RankupInjector;
    private       DiscoveryModuleContainer                     container;

    public ConfigurationLoader<CommentedConfigurationNode> statsManager;
    public CommentedConfigurationNode                      stats;

    // Using a map for later implementation of reloadable modules.
    private Multimap<String, Action> reloadables = HashMultimap.create();

    @Inject
    public Rankup(
                   Logger logger, @DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> loader,
                   @ConfigDir(sharedRoot = false) Path configDir, GuiceObjectMapperFactory factory)
    {
        this.logger = logger;
        this.loader = loader;
        this.configDir = configDir;
        this.factory = factory;
        this.configs = new HashMap<>();
        this.rankupCommand = new RankupCommand();
        this.RankupInjector = Guice.createInjector(new InjectorModule(this, this.rankupCommand));
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event)
    {
        instance = this;
        logger.info("preInit");
        try
        {
            this.container = DiscoveryModuleContainer.builder()
                               .setPackageToScan("com.minecolonies.rankup.modules") // All modules will be in here.
                               .setLoggerProxy(new RankupLoggerProxy(this.logger))
                               .setConstructor(new RankupModuleConstructor(this)) // How modules are constructed.
                               .setConfigurationLoader(loader)
                               .setOnEnable(this::updateInjector) // Before the enable phase, update the Guice injector.
                               .setNoMergeIfPresent(true)
                               .build(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            onError();
        }
    }

    @Listener
    public void onInit(GameInitializationEvent event)
    {
        logger.info("init");
        try
        {
            this.container.loadModules(true);
        }
        catch (QuickStartModuleLoaderException.Construction | QuickStartModuleLoaderException.Enabling construction)
        {
            construction.printStackTrace();
            onError();
        }

        Sponge.getCommandManager().register(this, this.rankupCommand, "ru");
    }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event)
    {
        if (event.getService().equals(EconomyService.class))
        {
            this.econ = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event)
    {
        this.game = Sponge.getGame();
    }

    @Listener
    public void onReloadEvent(GameReloadEvent event)
    {
        try
        {
            reload();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Adds actions to perform on reload.
     *
     * @param module     The module to add the actions for.
     * @param reloadable The {@link Action} to take.
     */
    public void addReloadable(String module, Action reloadable)
    {
        this.reloadables.put(module.toLowerCase(), reloadable);
    }

    /**
     * Reloads the config, and anything else that is registered to be reloaded.
     *
     * @throws IOException if the config could not be read.
     */
    public void reload() throws IOException
    {
        this.container.reloadSystemConfig();
        reloadables.values().forEach(Action::action);

        this.getAllConfigs().remove(GroupsConfig.class);
        this.getAllConfigs().remove(AccountConfigData.class);

        final Path accountsPath = this.getConfigDir().resolve("playerstats.conf");
        AccountConfigData data = this.getConfig(accountsPath, AccountConfigData.class,
          HoconConfigurationLoader.builder().setPath(accountsPath).build());
        this.getAllConfigs().put(AccountConfigData.class, data);

        final Path groupsPath = this.getConfigDir().resolve("groups.conf");
        GroupsConfig groups = this.getConfig(groupsPath, GroupsConfig.class,
          HoconConfigurationLoader.builder().setPath(groupsPath).build());
        this.getAllConfigs().put(GroupsConfig.class, groups);

        generateGroups();
    }

    public static Rankup getInstance() { return instance; }

    /**
     * Gets the Rankup injector modules, for injecting this plugin instance into classes.
     *
     * @return The injector.
     */
    public Injector getInjector()
    {
        return this.RankupInjector;
    }

    public void generateGroups()
    {
        GroupsConfig groupsConfig = (GroupsConfig) this.getAllConfigs().get(GroupsConfig.class);

        List<Subject> disabledGroups = new ArrayList<>();

        for (final Subject subject : CoreModule.perms.getGroups().getAllSubjects())
        {
            final String id = subject.getIdentifier();

            if (!groupsConfig.groups.containsKey(id))
            {
                logger.info("Config doesn't contain group: " + id);
                groupsConfig.groups.put(id, new GroupsConfig.GroupConfig());

                groupsConfig.groups.get(id).enabled = true;
                groupsConfig.groups.get(id).rank = 0;
            }

            if (!groupsConfig.groups.get(id).enabled)
            {
                groupsConfig.groups.get(id).rank = -1;
                disabledGroups.add(subject);
            }
            else if (groupsConfig.groups.get(id).rank == -1)
            {
                groupsConfig.groups.get(id).enabled = false;
                disabledGroups.add(subject);
            }
        }

        groupsConfig.save();
    }

    /**
     * Stages a class for addition to the Guice injector
     *
     * @param key    The {@link Class} to add.
     * @param getter The {@link Supplier} that gets the class.
     * @param <T>    The type.
     */
    public <T> void addToSubInjectorModule(Class<T> key, Supplier<T> getter)
    {
        this.subInjectorModule.addBinding(key, getter);
    }

    /**
     * Updates the injector with the latest bindings.
     */
    private void updateInjector()
    {
        this.RankupInjector = this.RankupInjector.createChildInjector(this.subInjectorModule);
        this.subInjectorModule.reset();
    }

    /**
     * Unregisters all listeners in case of error.
     */
    private void onError()
    {
        Sponge.getEventManager().unregisterPluginListeners(this);
    }

    /**
     * Gets the {@link AbstractConfigAdapter}
     *
     * @param id                 The ID of the module that the adapter is registered to.
     * @param configAdapterClass The {@link Class} of the adapter
     * @param <T>                The type of the adapter
     * @return An {@link Optional} that will contain the adapter.
     */
    public <T extends AbstractConfigAdapter<?>> Optional<T> getConfigAdapter(String id, Class<T> configAdapterClass)
    {
        try
        {
            return Optional.of(this.container.getConfigAdapterForModule(id, configAdapterClass));
        }
        catch (NoModuleException | IncorrectAdapterTypeException e)
        {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public <M extends BaseConfig> M getConfig(Path file, Class<M> clazz, ConfigurationLoader loader)
    {
        try
        {
            if (!Files.exists(file))
            {
                Files.createFile(file);
            }

            TypeToken token = TypeToken.of(clazz);
            ConfigurationNode node = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(this.factory));
            M config = (M) node.getValue(token, clazz.newInstance());
            config.init(loader, node, token);
            config.save();
            return config;
        }
        catch (IOException | ObjectMappingException | IllegalAccessException | InstantiationException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Map<Class<? extends BaseConfig>, BaseConfig> getAllConfigs()
    {
        return this.configs;
    }

    /**
     * Gets the {@link DiscoveryModuleContainer}
     *
     * @return The {@link DiscoveryModuleContainer}
     */
    public DiscoveryModuleContainer getModuleContainer()
    {
        return this.container;
    }

    public Logger getLogger()
    {
        return this.logger;
    }

    public Path getConfigDir()
    {
        return this.configDir;
    }
}