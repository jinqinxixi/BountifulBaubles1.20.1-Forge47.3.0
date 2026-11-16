package com.jinqinxixi.bountifulbaubles.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.List;

/**
 * 项链的穿戴模型：一个和玩家躯干同形的盒子，贴图上只有胸前那一小块画着吊坠。
 *
 * <p>完全照 1.12.2 原版 Bountiful Baubles 的做法：原版取 {@code ModelBiped} 的躯干部件
 * （{@code bipedBody}，盒子 {@code 8×12×4}、UV 起点 {@code 16,16}），只画那一个盒子。贴图大部分
 * 透明，只有胸前一小块画着吊坠，画出来就是胸前挂了件坠饰。
 *
 * <p>有一处不显然、但正是它决定能不能看见：**贴图是 128×64，模型却按 64×32 声明**。
 *
 * <p>原版就是这样错开的 —— 贴图文件 128×64，而 {@code ModelBiped} 用的是默认的 64×32 声明。
 * UV 归一化按 64×32 算，采样到 128×64 的贴图上就整体放大一倍，躯干盒子的六个面于是落在
 * {@code u[32,80] v[32,64]} 这一块里。而六张项链贴图的图案都在 {@code u[43,52] v[32,50]}，
 * 正好落在这块区域的偏左位置，也就是盒子正面靠上的地方。
 *
 * <p>换句话说，贴图是照着「躯干盒子的展开图放大一倍」画的，模型声明少一半才让两者对上。声明成
 * 128×64 反而对不上：采样区域会缩回 {@code u[16,40] v[16,32]}，和图案（{@code u[43,52]}）
 * 完全错开，渲染出一片全透明 —— 看上去就像项链根本没画出来。
 */
public class AmuletModel extends HumanoidModel<LivingEntity>
{
    /**
     * 模型声明的贴图尺寸。
     *
     * <p>比贴图实际尺寸（128×64）小一半，这是刻意的，见类注释 —— 原版也这么声明。
     */
    public static final int DECLARED_TEXTURE_WIDTH = 64;
    public static final int DECLARED_TEXTURE_HEIGHT = 32;

    /**
     * 渲染时整块模型缩小的倍数。
     *
     * <p>盒子是 8×12×4 模型单位，乘上它之后约 0.57×0.86×0.29 格，是胸前一件坠饰的大小。
     *
     * <p>数值看似比原版大，是因为两个版本的模型单位换算不同，而这里要让盒子最终长得一样大：
     *
     * <ul>
     *   <li>1.12.2 的 {@code render(scale)} 里，那个 scale 就是单位换算系数，原版传 1.0（不换算），
     *       盒子 8 单位直接当 8 个世界单位，再被外层的 {@code scale(0.07125)} 缩成 0.57 格。
     *   <li>1.20.1 的 {@code ModelPart.Cube} 无条件把顶点除以 16，没有开关可以关掉。想让同一个
     *       0.57 格的结果，缩放就得是 {@code 0.07125 × 16}。
     * </ul>
     */
    public static final float PLATE_SCALE = 0.07125F * 16.0F;

    public AmuletModel(ModelPart root)
    {
        super(root, RenderType::entityTranslucent);
    }

    /** 头部不画东西，项链只有躯干一件。 */
    @Override
    protected Iterable<ModelPart> headParts()
    {
        return Collections.emptyList();
    }

    /** 只画躯干那一件。 */
    @Override
    protected Iterable<ModelPart> bodyParts()
    {
        return List.of(this.body);
    }

    /**
     * 画出来，缩到原版的大小。
     *
     * <p>照 Artifacts 的 {@code NecklaceModel}：缩放写在这里、画之前施加，而不是由调用方在外面套
     * 一层。这样缩放只作用于盒子本身，不碰躯干 pivot 的位置。盒子坐标照原版（{@code 8×12×4}），
     * 再乘 {@link #PLATE_SCALE} 就是胸前一件坠饰的大小。
     */
    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer buffer,
            int light,
            int overlay,
            float red,
            float green,
            float blue,
            float alpha)
    {
        poseStack.pushPose();
        poseStack.scale(PLATE_SCALE, PLATE_SCALE, PLATE_SCALE);
        this.bodyParts().forEach(part ->
                part.render(poseStack, buffer, light, overlay, red, green, blue, alpha));
        poseStack.popPose();
    }

    /**
     * 项链的模型定义：躯干形状的盒子，位置与 UV 起点都照原版。
     *
     * <p>{@code createMesh} 会把整套人形部件建出来，这里把躯干换成原版那个盒子，其余部件留着不用
     * —— {@link #bodyParts()} 和 {@link #headParts()} 决定了哪几件会被画出来。
     */
    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, DECLARED_TEXTURE_WIDTH, DECLARED_TEXTURE_HEIGHT);
    }
}
