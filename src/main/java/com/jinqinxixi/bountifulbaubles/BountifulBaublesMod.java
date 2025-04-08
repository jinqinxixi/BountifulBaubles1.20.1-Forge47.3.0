package com.jinqinxixi.bountifulbaubles;


import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.sound.ModSounds;
import com.jinqinxixi.bountifulbaubles.loot.LootHandler;
import com.jinqinxixi.bountifulbaubles.system.effect.ModEffects;
import com.jinqinxixi.bountifulbaubles.system.recast.AnvilRecastRegistry;

import com.jinqinxixi.bountifulbaubles.system.modifier.CurioAttributeEvents;

import com.jinqinxixi.bountifulbaubles.system.recast.ModBrewingRecipes;
import com.jinqinxixi.bountifulbaubles.system.recast.RecastRegistry;
import com.jinqinxixi.bountifulbaubles.system.wormhole.PacketHandler;
import com.jinqinxixi.bountifulbaubles.system.wormhole.TeleportRequestManager;

import com.jinqinxixi.bountifulbaubles.item.Baubles.BottledCloudItem;
import com.jinqinxixi.bountifulbaubles.item.Baubles.WormholeMirrorItem;
import com.jinqinxixi.bountifulbaubles.item.ModCreativeModeTab;
import com.jinqinxixi.bountifulbaubles.item.ModItems;
import com.jinqinxixi.bountifulbaubles.compat.firstaid.BrokenHeartFirstAidCompat;
import com.jinqinxixi.bountifulbaubles.compat.firstaid.PhylacteryCharmFirstAidCompat;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Mod(BountifulBaublesMod.MOD_ID)
public class BountifulBaublesMod {
    // 基础配置
    public static final String MOD_ID = "bountifulbaubles";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public BountifulBaublesMod() {

        // 初始化Mixin
        MixinBootstrap.init();

        // 注册事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 核心初始化
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onGatherData);
        modEventBus.addListener(this::onCommonSetup);
        PacketHandler.register();
        // 注册子系统
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);

        // 网络包注册
        PacketHandler.register();
        NetworkHandler.registerPackets();

        // 功能模块注册
        MinecraftForge.EVENT_BUS.register(new LootHandler());
        MinecraftForge.EVENT_BUS.register(BottledCloudItem.class);
        MinecraftForge.EVENT_BUS.register(AnvilRecastRegistry.class);
        //MinecraftForge.EVENT_BUS.register(new ItemModificationEvents());
        ModBrewingRecipes.register(modEventBus);

        // 配置注册
        ModLoadingContext.get().registerConfig(
                Type.COMMON,
                ModConfig.SPEC,
                "bountifulbaubles-common.toml"
        );

        // 服务器启动事件
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientProxy.init();
        }
        // 如果FirstAid已加载，才注册兼容性事件
        if (ModList.get().isLoaded("firstaid")) {
            MinecraftForge.EVENT_BUS.register(BrokenHeartFirstAidCompat.class);
            MinecraftForge.EVENT_BUS.register(PhylacteryCharmFirstAidCompat.class);
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 初始化战利品配置
            ModConfig.loadLootConfig();
           //ItemConfig.init();
            CurioAttributeEvents.init();
            // 注册锻造台配方
            AnvilRecastRegistry.registerAllRecipes();

            // 调试日志
            LOGGER.info("[Bountiful Baubles] Common setup completed");
        });
    }

    @SubscribeEvent
    public void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(
                event.includeServer(),
                new RecastRegistry(output)
        );
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 注册命令
        event.getServer().getCommands().getDispatcher().register(
                Commands.literal(MOD_ID)
                        .requires(source -> source.hasPermission(0))
                        .then(Commands.literal("tp_accept")
                                .then(Commands.argument("requestId", StringArgumentType.string())
                                        .executes(ctx -> handleTeleportResponse(ctx, true))
                                )
                        )
                        .then(Commands.literal("tp_deny")
                                .then(Commands.argument("requestId", StringArgumentType.string())
                                        .executes(ctx -> handleTeleportResponse(ctx, false))
                                )
                        )
        );
    }

    private int handleTeleportResponse(CommandContext<CommandSourceStack> ctx, boolean accept) {
        try {
            String requestIdStr = StringArgumentType.getString(ctx, "requestId");
            UUID requestId = UUID.fromString(requestIdStr);
            ServerPlayer responder = ctx.getSource().getPlayer();

            if (responder == null) return 0;

            TeleportRequestManager.TeleportRequest request =
                    TeleportRequestManager.getRequest(requestId);

            if (request == null) {
                ctx.getSource().sendFailure(Component.translatable("msg.mirror.invalid_request"));
                return 0;
            }

            TeleportRequestManager.removeRequest(requestId);

            if (accept) {
                request.requester.teleportTo(
                        responder.serverLevel(),
                        responder.getX(),
                        responder.getY(),
                        responder.getZ(),
                        responder.getYRot(),
                        responder.getXRot()
                );
                ((WormholeMirrorItem) ModItems.WORMHOLE_MIRROR.get()).playTeleportEffects(request.requester);
                sendTeleportMessages(request.requester, responder, true);
            } else {
                sendTeleportMessages(request.requester, responder, false);
            }
            return 1;
        } catch (IllegalArgumentException e) {
            ctx.getSource().sendFailure(Component.translatable("msg.mirror.invalid_request"));
            return 0;
        }
    }

    private void sendTeleportMessages(ServerPlayer requester, ServerPlayer responder, boolean accepted) {
        Component requesterMsg = accepted ?
                Component.translatable("msg.mirror.request_accepted", responder.getScoreboardName()) :
                Component.translatable("msg.mirror.request_denied", responder.getScoreboardName());

        Component responderMsg = accepted ?
                Component.translatable("msg.mirror.accepted", requester.getScoreboardName()) :
                Component.translatable("msg.mirror.denied", requester.getScoreboardName());

        requester.sendSystemMessage(requesterMsg);
        responder.sendSystemMessage(responderMsg);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("[Bountiful Baubles] Brewing recipes registered");
        });
    }


    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemPropertyFunction blockFn = (stack, world, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;

                ItemProperties.register(ModItems.COBALT_SHIELD.get(), new ResourceLocation("blocking"), blockFn);
                ItemProperties.register(ModItems.OBSIDIAN_SHIELD.get(), new ResourceLocation("blocking"), blockFn);
                ItemProperties.register(ModItems.ANKH_SHIELD.get(), new ResourceLocation("blocking"), blockFn);

                LOGGER.info("[Bountiful Baubles] Client setup completed");
            });
        }
    }
    // 空方法保留结构
    private void addCreative(BuildCreativeModeTabContentsEvent event) {}
    static class ClientEvents {
        public static void onGuiOpen(ScreenEvent.Init.Post event) {}
    }
}
