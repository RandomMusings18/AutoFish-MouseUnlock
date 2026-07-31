package com.edenxander.autofish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class AutoFishClient implements ClientModInitializer {

    private static KeyBinding toggleKey;

    private static boolean enabled = false; // both autofish + mouse unlock share this flag

    private static int recastCooldown = 0;
    private static int biteCooldown = 0;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autofish-mouseunlock.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.autofish-mouseunlock"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // Single key toggles BOTH AutoFish and Mouse Unlock together
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (enabled) {
                    client.mouse.unlockCursor();
                    client.player.sendMessage(Text.literal("AutoFish + Mouse Unlock: §aON"), true);
                } else {
                    client.mouse.lockCursor();
                    client.player.sendMessage(Text.literal("AutoFish + Mouse Unlock: §cOFF"), true);
                }
            }

            // Keep mouse unlocked while enabled (re-apply every tick so game can't re-grab)
            if (enabled && client.currentScreen == null) {
                client.mouse.unlockCursor();
            }

            if (!enabled) return;

            if (recastCooldown > 0) {
                recastCooldown--;
                return;
            }
            if (biteCooldown > 0) {
                biteCooldown--;
            }

            ItemStack main = client.player.getMainHandStack();
            ItemStack off = client.player.getOffHandStack();
            boolean holdingRod = main.getItem() instanceof FishingRodItem || off.getItem() instanceof FishingRodItem;
            if (!holdingRod) return;

            FishingBobberEntity bobber = client.player.fishHook;

            if (bobber == null) {
                // No bobber → cast (same as player_press_use)
                useRod(client);
                recastCooldown = 10;
                return;
            }

            // Bite detection – mirrors the "!!!" check via velocity spike
            boolean bite = false;
            try {
                if (bobber.getVelocity().y < -0.04 && !bobber.isOnGround() && bobber.getY() < client.player.getY()) {
                    bite = true;
                }
            } catch (Exception ignored) {}

            if (bite && biteCooldown <= 0) {
                useRod(client); // reel in
                biteCooldown = 15;
                recastCooldown = 20; // matches original 0.3s delay
            }
        });
    }

    private void useRod(MinecraftClient client) {
        if (client.interactionManager == null || client.player == null) return;
        Hand hand = client.player.getMainHandStack().getItem() instanceof FishingRodItem ? Hand.MAIN_HAND : Hand.OFF_HAND;
        client.interactionManager.interactItem(client.player, hand);
        client.player.swingHand(hand);
    }
}
