package io.github.thebusybiscuit.electricspawners;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.guizhanlib.minecraft.helper.entity.EntityTypeHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ElectricSpawner extends SimpleSlimefunItem<BlockTicker> implements EnergyNetComponent {

    private static final int ENERGY_CONSUMPTION = 240;
    private static int lifetime = 0;

    private final EntityType entity;

    public ElectricSpawner(ItemGroup category, String mob, EntityType type, Research research) {
        // @formatter:off
        super(category, Items.getSpawner(type), RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            null, SlimefunItems.PLUTONIUM, null,
            SlimefunItems.ELECTRIC_MOTOR, new CustomItemStack(Material.SPAWNER, "&b已修复的刷怪笼", "&7类型: &b" + ChatUtils.humanize(type.toString())), SlimefunItems.ELECTRIC_MOTOR,
            SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.BLISTERING_INGOT_3
        });
        // @formatter:on

        this.entity = type;

        addItemHandler(onBlockPlace());

        new BlockMenuPreset(getId(), "&c电力刷怪笼") {

            @Override
            public void init() {
                for (int i = 0; i < 9; i++) {
                    if (i != 4) {
                        addItem(i, ChestMenuUtils.getBackground(), (p, slot, item, action) -> false);
                    }
                }
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals("false")) {
                    menu.replaceExistingItem(4, new CustomItemStack(Material.GUNPOWDER, "&7启用: &4\u2718", "", "&e> 点击启用"));
                    menu.addMenuClickHandler(4, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", "true");
                        newInstance(menu, b);
                        return false;
                    });
                } else {
                    menu.replaceExistingItem(4, new CustomItemStack(Material.REDSTONE, "&7启用: &2\u2714", "", "&e> 点击禁用"));
                    menu.addMenuClickHandler(4, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", "false");
                        newInstance(menu, b);
                        return false;
                    });
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(p.getUniqueId().toString()) || p.hasPermission("slimefun.cargo.bypass");
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };

        research.addItems(this);
    }

    private BlockPlaceHandler onBlockPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                Block b = e.getBlock();
                Player p = e.getPlayer();
                BlockStorage.addBlockInfo(b, "enabled", "false");
                BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
            }
        };
    }

    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    protected void tick(Block b) {
        if (lifetime % 3 != 0) {
            return;
        }

        if (BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals("false")) {
            return;
        }

        if (getCharge(b.getLocation()) < getEnergyConsumption()) {
            return;
        }

        int count = 0;
        for (Entity n : b.getWorld().getNearbyEntities(b.getLocation(), 4.0, 4.0, 4.0)) {
            if (n.getType().equals(this.entity)) {
                count++;

                if (count > 6) {
                    return;
                }
            }
        }

        removeCharge(b.getLocation(), getEnergyConsumption());
        b.getWorld().spawnEntity(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.5D, b.getZ() + 0.5D), this.entity);
    }

    @Override
    public BlockTicker getItemHandler() {
        return new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                ElectricSpawner.this.tick(b);
            }

            @Override
            public void uniqueTick() {
                lifetime++;
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }

        };
    }

    @Override
    public int getCapacity() {
        return 2048;
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

}
