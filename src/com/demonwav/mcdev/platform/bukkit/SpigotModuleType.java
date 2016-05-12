package com.demonwav.mcdev.platform.bukkit;

import com.demonwav.mcdev.asset.PlatformAssets;
import com.demonwav.mcdev.platform.PlatformType;

import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.openapi.module.ModuleTypeManager;

import javax.swing.Icon;

public class SpigotModuleType extends BukkitModuleType {

    private static final String ID = "SPIGOT_MODULE_TYPE";

    public SpigotModuleType() {
        super(ID);
    }

    public SpigotModuleType(String ID) {
        super(ID);
    }

    public static SpigotModuleType getInstance() {
        return (SpigotModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    public PlatformType getPlatformType() {
        return PlatformType.SPIGOT;
    }

    @Override
    public Icon getBigIcon() {
        return PlatformAssets.SPIGOT_ICON_2X;
    }

    @Override
    public Icon getIcon() {
        return PlatformAssets.SPIGOT_ICON;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return PlatformAssets.SPIGOT_ICON;
    }

    @Override
    public void populateFileTemplateGroupDescriptor(FileTemplateGroupDescriptor group) {
        // NOOP
    }
}
