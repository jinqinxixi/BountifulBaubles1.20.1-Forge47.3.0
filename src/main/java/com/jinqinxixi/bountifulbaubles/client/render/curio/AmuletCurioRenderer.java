package com.jinqinxixi.bountifulbaubles.client.render.curio;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.client.model.AmuletModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * 项链的穿戴渲染：把吊坠画在胸前。
 *
 * <p>流程照的是 Artifacts 模组的 {@code GenericArtifactRenderer} —— 它那边的项链、腰带、手套都走
 * 这一套，是验证过的。四步：先让模型摆好姿势（{@code setupAnim}），再让它抄一遍玩家躯干的位置和
 * 角度（{@code copyPropertiesTo}），然后照常画出来。
 *
 * <p>为什么是抄而不是自己摆：玩家转身、低头、弯腰、骑乘时躯干会动，这些姿态变化全都写在玩家自己
 * 的模型上。抄一份过来，项链就自动跟着走，不必一处处去对应。这也是模型要继承
 * {@link HumanoidModel} 的原因 —— 只有同型才能这么抄。
 */
public class AmuletCurioRenderer implements ICurioRenderer
{
    private final AmuletModel model;
    private final ResourceLocation texture;

    public AmuletCurioRenderer(ModelPart root, ResourceLocation texture)
    {
        this.model = new AmuletModel(root);
        this.texture = texture;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack pose,
            RenderLayerParent<T, M> parent,
            MultiBufferSource buffers,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch)
    {
        LivingEntity entity = slotContext.entity();

        pose.pushPose();

        // 摆好姿势，再抄一遍玩家躯干的位置和角度 —— 顺序和 Artifacts 一致，抄要在摆之后，
        // 否则摆姿势那一步会把抄来的角度覆盖掉。
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        followBodyRotations(entity);

        // 穿胸甲时整体前移一点并略微放大（原版如此）：胸甲模型比身体大一圈，项链不动的话会被
        // 埋进胸甲里看不见。这里的缩放（1.1）和模型内部的最终缩放（PLATE_SCALE）是两回事 ——
        // 这个 1.1 在最终缩放之前施加，让项链凸出胸甲表面。
        if (!entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty())
        {
            pose.translate(0.0F, -0.02F, -0.045F);
            pose.scale(1.1F, 1.1F, 1.1F);
        }

        VertexConsumer consumer = buffers.getBuffer(model.renderType(texture));

        model.renderToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        pose.popPose();
    }

    /**
     * 把玩家躯干当前的位置和角度抄到项链的模型上。
     *
     * <p>从渲染器那里取玩家正在用的模型 —— 也就是这一帧真正画在屏幕上的那一份 —— 而不是另建一个
     * 玩家模型再算一遍姿势。抄的是「正在显示的样子」，所以不会和玩家对不上。
     *
     * <p>必须抄的不只位置和角度，还有 {@code young}。模型默认 {@code young = true}，而
     * {@code AgeableListModel.renderToBuffer} 会据此走「婴儿缩放」分支 —— 把模型缩到 0.5 倍再
     * 往下挪 1.5 格，项链就跑到腰以下去了。抄成玩家的 {@code young}（false）才走正常渲染。
     */
    private void followBodyRotations(LivingEntity entity)
    {
        EntityRenderer<? super LivingEntity> renderer =
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer
                && livingRenderer.getModel() instanceof HumanoidModel<?> playerModel)
        {
            copyBody(playerModel, model);
            model.young = playerModel.young;
        }
    }

    /** 躯干的位置与角度，外加缩放 —— 缩放在「渲染时整体缩小」之外还要一并抄走，见类注释。 */
    private static void copyBody(HumanoidModel<?> from, HumanoidModel<LivingEntity> to)
    {
        ModelPart source = from.body;
        ModelPart target = to.body;

        target.x = source.x;
        target.y = source.y;
        target.z = source.z;
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
    }
}
