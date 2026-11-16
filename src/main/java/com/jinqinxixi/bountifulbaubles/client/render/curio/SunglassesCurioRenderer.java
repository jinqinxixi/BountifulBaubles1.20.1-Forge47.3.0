package com.jinqinxixi.bountifulbaubles.client.render.curio;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.client.model.SunglassesModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * 墨镜的穿戴渲染：把墨镜盒子架在玩家脸上。
 *
 * <p>照 1.12.2 原版 Bountiful Baubles 的 {@code ItemSunglasses.onRenderObject}：绑墨镜贴图、
 * 关掉背面剔除、画一个和头同大的盒子。贴图大部分透明，只有正面镜片和两侧镜腿有图案。
 *
 * <p>和金冠（{@link CrownGoldCurioRenderer}）同一套结构：{@link ICurioRenderer#followHeadRotations}
 * 让盒子跟着玩家头部转。区别只有两点 —— 盒子盖住整个头而不是头顶，以及关了背面剔除。
 * 背面剔除必须关，否则绕到侧面时盒子背面的面会被裁掉，镜腿就看不见了，这也是原版显式调
 * {@code disableCull} 的原因。
 */
public class SunglassesCurioRenderer implements ICurioRenderer
{
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(BountifulBaublesMod.MOD_ID, "textures/models/armor/sunglasses.png");

    private final ModelPart sunglasses;

    public SunglassesCurioRenderer(ModelPart root)
    {
        this.sunglasses = SunglassesModel.sunglassesFrom(root);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource buffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch)
    {
        LivingEntity entity = slotContext.entity();

        // 盒子跟着头部转，先抄玩家头部模型当前的位置和旋转。抄会覆盖缩放，所以缩放要放在之后。
        ICurioRenderer.followHeadRotations(entity, sunglasses);

        // 模型按 16 个单位做，缩到 0.5 才是原版 8 个单位的实际大小。缩写在模型自己身上，
        // 只作用这个盒子，不碰已经抄来的头部位置。
        sunglasses.xScale = SunglassesModel.RENDER_SCALE;
        sunglasses.yScale = SunglassesModel.RENDER_SCALE;
        sunglasses.zScale = SunglassesModel.RENDER_SCALE;

        // 关背面剔除（1.20.1 里 entityTranslucent 默认就不 cull，对应原版的 disableCull）：
        // 贴图大部分透明、盒子又盖住整个头，绕到侧面时背面必须画，否则镜腿会缺。
        sunglasses.render(poseStack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY);
    }
}
