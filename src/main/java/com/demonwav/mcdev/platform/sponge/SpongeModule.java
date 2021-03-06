package com.demonwav.mcdev.platform.sponge;

import com.demonwav.mcdev.asset.PlatformAssets;
import com.demonwav.mcdev.buildsystem.BuildSystem;
import com.demonwav.mcdev.platform.AbstractModule;
import com.demonwav.mcdev.platform.MinecraftModuleType;
import com.demonwav.mcdev.platform.PlatformType;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * Created by gabizou on 5/19/2016.
 */
public class SpongeModule extends AbstractModule {

    SpongeModule(@NotNull Module module) {
        this.module = module;
        buildSystem = BuildSystem.getInstance(module);
        if (buildSystem != null) {
            buildSystem.reImport(module, PlatformType.SPONGE);
        }
    }

    @NotNull
    public Module getModule() {
        return module;
    }

    @Override
    public MinecraftModuleType getModuleType() {
        return SpongeModuleType.getInstance();
    }

    @Override
    public PlatformType getType() {
        return PlatformType.SPONGE;
    }

    @Override
    public Icon getIcon() {
        return PlatformAssets.SPONGE_ICON;
    }

    @Override
    public boolean isEventClassValid(PsiClass eventClass, PsiMethod method) {
        return "org.spongepowered.api.event.Event".equals(eventClass.getQualifiedName());
    }

    @Override
    public String writeErrorMessageForEventParameter(PsiClass eventClass) {
        return "Parameter is not an instance of org.spongepowered.api.event.Event\n" +
        "Compiling and running this listener may result in a runtime exception";
    }
}
