package com.demonwav.mcdev.platform.bukkit;

import com.demonwav.mcdev.asset.PlatformAssets;
import com.demonwav.mcdev.platform.PlatformType;

import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.openapi.module.ModuleTypeManager;

import javax.swing.Icon;

public class PaperModuleType extends SpigotModuleType {

    private static final String ID = "PAPER_MODULE_TYPE";

    public PaperModuleType() {
        super(ID);
    }

    public static PaperModuleType getInstance() {
        return (PaperModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    public PlatformType getPlatformType() {
        return PlatformType.PAPER;
    }

    @Override
    public Icon getBigIcon() {
        return PlatformAssets.PAPER_ICON_2X;
    }

    @Override
    public Icon getIcon() {
        return PlatformAssets.PAPER_ICON;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return PlatformAssets.PAPER_ICON;
    }

    @Override
    public void populateFileTemplateGroupDescriptor(FileTemplateGroupDescriptor group) {
        // NOOP
    }
}
