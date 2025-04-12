package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.client.ModKeybinds;
import com.wanderersoftherift.wotr.network.SelectAbilitySlotPayload;
import com.wanderersoftherift.wotr.network.UseAbilityPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.wanderersoftherift.wotr.init.client.ModKeybinds.ABILITY_SLOT_KEYS;
import static com.wanderersoftherift.wotr.init.client.ModKeybinds.NEXT_ABILITY_KEY;
import static com.wanderersoftherift.wotr.init.client.ModKeybinds.PREV_ABILITY_KEY;
import static com.wanderersoftherift.wotr.init.client.ModKeybinds.USE_ABILITY_KEY;

/**
 * Events related to abilities - key activation detection and mana ticking.
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class AbilityClientEvents {

    @SubscribeEvent
    public static void processAbilityKeys(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        AbilitySlots abilitySlots = Minecraft.getInstance().player.getData(ModAttachments.ABILITY_SLOTS);
        for (int i = 0; i < ModKeybinds.ABILITY_SLOT_KEYS.size(); i++) {
            while (ABILITY_SLOT_KEYS.get(i).consumeClick()) {
                PacketDistributor.sendToServer(new UseAbilityPayload(i));
                abilitySlots.setSelectedSlot(i);
            }
        }

        boolean selectionUpdated = false;
        while (PREV_ABILITY_KEY.consumeClick()) {
            abilitySlots.decrementSelected();
            selectionUpdated = true;
        }
        while (NEXT_ABILITY_KEY.consumeClick()) {
            abilitySlots.incrementSelected();
            selectionUpdated = true;
        }
        while (USE_ABILITY_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new UseAbilityPayload(abilitySlots.getSelectedSlot()));
            selectionUpdated = false; // Because using a slot selected the slot
        }
        if (selectionUpdated) {
            PacketDistributor.sendToServer(new SelectAbilitySlotPayload(abilitySlots.getSelectedSlot()));
        }
    }

    @SubscribeEvent
    public static void tickMana(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ManaData manaData = player.getData(ModAttachments.MANA);
        manaData.tick(player);
    }

}
