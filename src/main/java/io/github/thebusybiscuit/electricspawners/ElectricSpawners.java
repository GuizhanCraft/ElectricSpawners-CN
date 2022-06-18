package io.github.thebusybiscuit.electricspawners;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import net.guizhanss.guizhanlib.updater.GuizhanBuildsUpdater;
import net.guizhanss.guizhanlib.utils.PlayerHeadUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ElectricSpawners extends JavaPlugin implements SlimefunAddon {

    @Override
    public void onEnable() {
        Config cfg = new Config(this);

        // Setting up bStats
        new Metrics(this, 6163);

        // Auto update
        if (cfg.getBoolean("options.auto-update") &&
            getDescription().getVersion().startsWith("Build ")) {
            new GuizhanBuildsUpdater(this, getFile(), "ybw0014", "ElectricSpawners-CN", "master", false).start();
        }

        // Item groups
        ItemGroup itemGroup = new ItemGroup(
            new NamespacedKey(this, "electric_spawners"),
            new CustomItemStack(
                PlayerHeadUtil.getFromHash("db6bd9727abb55d5415265789d4f2984781a343c68dcaf57f554a5e9aa1cd"),
                "&9电力刷怪笼"
            )
        );

        // Research
        Research research = new Research(
            new NamespacedKey(this, "electric_spawners"),
            4820,
            "电力刷怪笼",
            30
        );

        // Framework
        new SlimefunItem(
            itemGroup,
            Items.FRAMEWORK,
            RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                null, SlimefunItems.PLUTONIUM, null,
                SlimefunItems.ELECTRIC_MOTOR, null, SlimefunItems.ELECTRIC_MOTOR,
                SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.BLISTERING_INGOT_3
            }
        ).register(this);

        for (String mob : cfg.getStringList("mobs")) {
            try {
                EntityType type = EntityType.valueOf(mob);
                new ElectricSpawner(itemGroup, mob, type, research).register(this);
            } catch (IllegalArgumentException x) {
                getLogger().log(Level.WARNING, "尝试注册电力刷怪笼时发生错误,该生物类型无效: \"{0}\"", mob);
            } catch (Exception x) {
                getLogger().log(Level.SEVERE, x, () -> "尝试注册电力刷怪笼时发生预期之外的错误,生物类型: \"" + mob + "\"");
            }
        }

        research.register();
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/ybw0014/ElectricSpawners-CN/issues";
    }
}