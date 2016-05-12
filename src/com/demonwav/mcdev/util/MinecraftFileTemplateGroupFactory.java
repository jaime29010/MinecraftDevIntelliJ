package com.demonwav.mcdev.util;

import com.demonwav.mcdev.asset.PlatformAssets;
import com.demonwav.mcdev.platform.PlatformType;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;

public class MinecraftFileTemplateGroupFactory implements FileTemplateGroupDescriptorFactory {

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("Minecraft", PlatformAssets.MINECRAFT_ICON);

        for (PlatformType type : PlatformType.values()) {
            type.getModuleType().populateFileTemplateGroupDescriptor(group);
        }

        return group;
    }
}
