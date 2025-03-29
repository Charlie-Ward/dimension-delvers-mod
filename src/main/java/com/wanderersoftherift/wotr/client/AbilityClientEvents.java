package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.hud.AbilityBar;
import com.wanderersoftherift.wotr.gui.hud.EffectBar;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.item.skillgem.AbilitySlots;
import com.wanderersoftherift.wotr.network.SelectAbilitySlotPayload;
import com.wanderersoftherift.wotr.networking.data.UseAbility;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.wanderersoftherift.wotr.client.ModClientEvents.ABILITY_SLOT_KEYS;
import static com.wanderersoftherift.wotr.client.ModClientEvents.NEXT_ABILITY_KEY;
import static com.wanderersoftherift.wotr.client.ModClientEvents.PREV_ABILITY_KEY;
import static com.wanderersoftherift.wotr.client.ModClientEvents.USE_ABILITY_KEY;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class AbilityClientEvents {

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        AbilitySlots abilitySlots = Minecraft.getInstance().player.getData(ModAttachments.ABILITY_SLOTS);
        for (int i = 0; i < ModClientEvents.ABILITY_SLOT_KEYS.size(); i++) {
            while (ABILITY_SLOT_KEYS.get(i).consumeClick()) {
                PacketDistributor.sendToServer(new UseAbility(i));
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
            PacketDistributor.sendToServer(new UseAbility(abilitySlots.getSelectedSlot()));
            selectionUpdated = false; // Because using a slot selected the slot
        }
        if (selectionUpdated) {
            PacketDistributor.sendToServer(new SelectAbilitySlotPayload(abilitySlots.getSelectedSlot()));
        }

    }

    @SubscribeEvent
    public static void hudRender(RenderGuiEvent.Post event) {
        AbilityBar.render(event.getGuiGraphics(), Minecraft.getInstance().player, Minecraft.getInstance().level, event.getPartialTick());
        EffectBar.render(event.getGuiGraphics(), Minecraft.getInstance().player, Minecraft.getInstance().level, event.getPartialTick());
    }
}
