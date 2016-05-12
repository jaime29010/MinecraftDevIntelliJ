package com.demonwav.mcdev.platform.bukkit;

import com.demonwav.mcdev.platform.MinecraftModuleType;
import com.demonwav.mcdev.asset.PlatformAssets;
import com.demonwav.mcdev.platform.PlatformType;
import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.openapi.module.ModuleTypeManager;

import javax.swing.Icon;

public class BukkitModuleType extends MinecraftModuleType {

    private static final String ID = "BUKKIT_MODULE_TYPE";

    public BukkitModuleType() {
        super(ID);
    }

    public BukkitModuleType(String ID) {
        super(ID);
    }

    public static BukkitModuleType getInstance() {
        return (BukkitModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    public PlatformType getPlatformType() {
        return PlatformType.BUKKIT;
    }

    @Override
    public Icon getBigIcon() {
        return PlatformAssets.BUKKIT_ICON_2X;
    }

    @Override
    public Icon getIcon() {
        return PlatformAssets.BUKKIT_ICON;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return PlatformAssets.BUKKIT_ICON;
    }

    @Override
    public void populateFileTemplateGroupDescriptor(FileTemplateGroupDescriptor group) {
        group.addTemplate(new FileTemplateDescriptor(BukkitTemplate.MAIN_CLASS, PlatformAssets.BUKKIT_ICON));
        group.addTemplate(new FileTemplateDescriptor(BukkitTemplate.PLUGIN_DESCRIPTION_FILE, PlatformAssets.BUKKIT_ICON));
        group.addTemplate(new FileTemplateDescriptor(BukkitTemplate.POM, PlatformAssets.BUKKIT_ICON));
    }
}
