package com.jinqinxixi.bountifulbaubles.client.render.curio;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * 安卡护符的穿戴渲染：把护符图标贴在胸口。
 *
 * <p>做法照 Trinkets and Baubles 里荣耀之盾（{@code DAMAGE_SHIELD}）的 {@code ChestItem}：
 * 安卡护符和荣耀之盾一样是平面图标（{@code item/generated}），直接把物品图标渲染到胸口即可，
 * 不建模型、不画贴图。物品图标是现成的，省掉一整条 UV 对不上的坑。
 *
 * <p>定位参数照海之石那组（{@code 0, 0.16, 0.14, 0}），贴在胸口正中；安卡护符本来就是胸前饰品，
 * 和海之石位置相近。
 */
public class AnkhCharmCurioRenderer implements ICurioRenderer
{
    /** 胸口定位，单位是格：照荣耀之盾 DAMAGE_SHIELD 那一组（0.17, 0.22, 0.16, 0.14）。 */
    private static final float OFFSET_X = 0.17F;
    private static final float OFFSET_Y = 0.22F;
    private static final float OFFSET_Z = 0.16F;

    /** 穿胸甲时图标往前挪的距离，避免被胸甲吞掉。照荣耀之盾的值。 */
    private static final float ARMOR_SHIFT = 0.14F;

    /** 图标大小，照荣耀之盾用的 0.1875（16 像素图标缩成 3 像素）。 */
    private static final float SCALE = 0.1875F;

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
        if (!(parent.getModel() instanceof HumanoidModel<?> model))
        {
            return;
        }

        LivingEntity entity = slotContext.entity();

        pose.pushPose();

        // 跟着躯干走。
        model.body.translateAndRotate(pose);

        // 物品图标默认背对观察者，翻过来才朝镜头。
        pose.mulPose(Axis.XP.rotationDegrees(180.0F));

        pose.translate(OFFSET_X, -OFFSET_Y, OFFSET_Z);

        if (!entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty())
        {
            pose.translate(OFFSET_X - ARMOR_SHIFT, 0.0F, -(OFFSET_Z - 0.2F));
        }

        pose.scale(SCALE, SCALE, SCALE);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY,
                pose, buffers, entity.level(), 0);

        pose.popPose();
    }
}
