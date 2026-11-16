package com.jinqinxixi.bountifulbaubles.client.render.curio;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.client.model.CrownGoldModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
 * 金冠的穿戴渲染：把金冠盒子扣在玩家头上。
 *
 * <p>1.12.2 原版是一个渲染层遍历饰品栏、遇到实现渲染接口的饰品就画。1.20.1 的 Curios 换成了
 * 注册制：{@code CuriosRendererRegistry} 登记一个 {@link ICurioRenderer}，Curios 自己的渲染层
 * 会在合适的时机回调它。这个类就是那个回调，做的事和原版一样。
 *
 * <p>跟随头部：{@link ICurioRenderer#followHeadRotations} 把玩家头部模型当前的位置和旋转抄到冠上，
 * 所以玩家转头、低头时冠跟着动 —— 原版是把冠直接挂在头模型下面的，效果相同。
 */
public class CrownGoldCurioRenderer implements ICurioRenderer
{
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(BountifulBaublesMod.MOD_ID, "textures/models/armor/crown_gold.png");

    private final ModelPart crown;

    public CrownGoldCurioRenderer(ModelPart root)
    {
        this.crown = CrownGoldModel.crownFrom(root);
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

        // 冠挂在头上，先照着玩家头部模型当前的位置和旋转摆好自己。
        //
        // 这一步必须在前，有个不显然的原因：它抄过来的不只有位置和旋转，还有缩放。抄完之后
        // 再把冠缩到原版大小，否则抄进来的 1.0 会把缩放直接覆盖掉。
        ICurioRenderer.followHeadRotations(entity, crown);

        // 模型按 16 个单位做（换取 UV 精度），缩到 0.5 才是原版 8 个单位的实际大小。
        //
        // 缩放写在模型自己身上，不是写在 pose 上：写在 pose 上会把已经抄过来的头部位置一起缩小，
        // 冠就会往脑袋里陷。ModelPart 的缩放在它自己的位移与旋转之后生效，只影响这个盒子。
        crown.xScale = CrownGoldModel.RENDER_SCALE;
        crown.yScale = CrownGoldModel.RENDER_SCALE;
        crown.zScale = CrownGoldModel.RENDER_SCALE;

        crown.render(poseStack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)), light,
                OverlayTexture.NO_OVERLAY);
    }
}
