package net.fabricmc.loader.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jcabi.manifests.Manifests;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.entrypoint.EntrypointTransformer;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.fabricmc.loader.launch.knot.Knot;
import net.fabricmc.loader.metadata.BuiltinModMetadata;
import net.fabricmc.loader.util.Arguments;
import net.fabricmc.loader.util.UrlUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class GenericApplicationProvider implements GameProvider {
    static EntrypointTransformer transformer = new EntrypointTransformer(it -> ImmutableList.of());
    String entrypoint;
    String mainEntry;
    String[] entrypointNames;
    String applicationName;
    String[] arguments;
    private Path gameJar;
    private Object instance;
    private String version;

    public GenericApplicationProvider(String mainEntry, String[] entrypoints, String applicationName) {
        this.mainEntry = mainEntry;
        this.entrypointNames = entrypoints;
        this.applicationName = applicationName;
        try {
            this.version = Manifests.read("Game-Version");
        } catch (IllegalArgumentException e) {
            this.version = "0.0.0+unknown";
        }
        GameProviders.addProvider(this);
    }

    @Override
    public String getGameId() {
        return this.applicationName.replace(' ', '_').toLowerCase();
    }

    @Override
    public String getGameName() {
        return applicationName;
    }

    @Override
    public String getRawGameVersion() {
        return version;
    }

    @Override
    public String getNormalizedGameVersion() {
        return version;
    }

    @Override
    public Collection<BuiltinMod> getBuiltinMods() {
        URL url;

        try {
            url = gameJar.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(
                new BuiltinMod(url, new BuiltinModMetadata.Builder(getGameId(), getNormalizedGameVersion())
                        .setName(getGameName())
                        .build())
        );
    }

    @Override
    public String getEntrypoint() {
        return entrypoint;
    }

    @Override
    public Path getLaunchDirectory() {
        if (arguments == null || arguments.length == 0) {
            return new File(".").toPath();
        }

        Arguments args = new Arguments();
        args.parse(arguments);
        return FabricLauncherBase.getLaunchDirectory(args).toPath();
    }

    @Override
    public boolean isObfuscated() {
        return false;
    }

    @Override
    public boolean requiresUrlClassLoader() {
        return false;
    }

    @Override
    public List<Path> getGameContextJars() {
        return Collections.singletonList(gameJar);
    }

    @Override
    public boolean locateGame(EnvType envType, String[] args, ClassLoader loader) {
        this.arguments = args;
        return locateGame(envType, loader);
    }

    public boolean locateGame(EnvType envType, ClassLoader loader) {
        List<String> entrypointClasses;

        entrypointClasses = Lists.newArrayList(mainEntry);

        Optional<GameProviderHelper.EntrypointResult> entrypointResult = GameProviderHelper.findFirstClass(loader, entrypointClasses);
        if (!entrypointResult.isPresent()) {
            return false;
        }

        entrypoint = entrypointResult.get().entrypointName;
        gameJar = entrypointResult.get().entrypointPath;

        return true;
    }

    @Override
    public EntrypointTransformer getEntrypointTransformer() {
        return transformer;
    }

    @Override
    public void launch(ClassLoader loader) {
        String targetClass = entrypoint;
        try {
            // Additional things to be put on knot classloader in dev
            for (String name : entrypointNames) {
                Optional<GameProviderHelper.EntrypointResult> n_entry = GameProviderHelper.findFirstClass(loader, Collections.singletonList(name));
                URL n_url = UrlUtil.asUrl(n_entry.get().entrypointPath);
                Knot.getLauncher().propose(n_url);
            }

            Optional<GameProviderHelper.EntrypointResult> entry = GameProviderHelper.findFirstClass(loader, Collections.singletonList(mainEntry));
            URL url = UrlUtil.asUrl(entry.get().entrypointPath);
            Knot.getLauncher().propose(url);

            Class<?> c = loader.loadClass(targetClass);
            Method m = c.getMethod("main", String[].class);
            m.invoke(null, (Object) arguments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getLaunchArguments(boolean sanitize) {
        return new String[0];
    }

    @Override
    public boolean canOpenErrorGui() {
        return false;
    }
}
