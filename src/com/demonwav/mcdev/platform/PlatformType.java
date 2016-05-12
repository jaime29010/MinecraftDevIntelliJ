package com.demonwav.mcdev.platform;

import com.demonwav.mcdev.platform.bukkit.BukkitModuleType;
import com.demonwav.mcdev.platform.bukkit.PaperModuleType;
import com.demonwav.mcdev.platform.bukkit.SpigotModuleType;
import com.demonwav.mcdev.platform.bungeecord.BungeeCordModuleType;
import com.demonwav.mcdev.platform.forge.ForgeModuleType;
import com.demonwav.mcdev.platform.sponge.SpongeModuleType;

public enum PlatformType {
    BUKKIT {
        @Override
        public MinecraftModuleType getModuleType() {
            return BukkitModuleType.getInstance();
        }
    },
    SPIGOT {
        @Override
        public MinecraftModuleType getModuleType() {
            return SpigotModuleType.getInstance();
        }
    },
    PAPER {
        @Override
        public MinecraftModuleType getModuleType() {
            return PaperModuleType.getInstance();
        }
    },
    FORGE {
        @Override
        public MinecraftModuleType getModuleType() {
            return ForgeModuleType.getInstance();
        }
    },
    SPONGE {
        @Override
        public MinecraftModuleType getModuleType() {
            return SpongeModuleType.getInstance();
        }
    },
    BUNGEECORD {
        @Override
        public MinecraftModuleType getModuleType() {
            return BungeeCordModuleType.getInstance();
        }
    },
    ;

    public abstract MinecraftModuleType getModuleType();
}
