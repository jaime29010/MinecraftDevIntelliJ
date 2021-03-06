package com.demonwav.mcdev.platform;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlatformUtil {
    private static final Map<VirtualFile, AbstractModule> map = new HashMap<>();

    @Nullable
    public static AbstractModule getInstance(@NotNull Module module) {
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getContentRoots();
        if (roots.length > 0) {
            VirtualFile moduleRoot = roots[0];

            ModuleType moduleType = ModuleUtil.getModuleType(module);

            if (moduleType instanceof MinecraftModuleType) {
                return map.computeIfAbsent(moduleRoot, m -> ((MinecraftModuleType) moduleType).generateModule(module));
            } else { // last ditch effort for gradle multi projects
                String[] paths = ModuleManager.getInstance(module.getProject()).getModuleGroupPath(module);
                if (paths != null && paths.length > 0) {
                    // The first element will be the module's parent
                    String parentName = paths[0];
                    Module parentModule = ModuleManager.getInstance(module.getProject()).findModuleByName(parentName);
                    if (parentModule != null) {
                        ModuleType parentModuleType = ModuleUtil.getModuleType(parentModule);
                        if (parentModuleType instanceof MinecraftModuleType) {
                            roots = ModuleRootManager.getInstance(parentModule).getContentRoots();
                            if (roots.length > 0) {
                                return map.computeIfAbsent(roots[0], m -> ((MinecraftModuleType) parentModuleType).generateModule(parentModule));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}