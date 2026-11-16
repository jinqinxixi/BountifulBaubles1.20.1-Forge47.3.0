package com.jinqinxixi.bountifulbaubles.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * 金冠模型：戴在玩家头上的一个金冠盒子。
 *
 * <p>照 1.12.2 原版 Bountiful Baubles 的金冠头盔模型做的。原版用了一个自定义 UV 的盒子类
 * （{@code ModelCustomUVBox}），那是因为 1.12.2 的标准盒子不允许 UV 尺寸和几何尺寸不同。
 * 这里换了个等价的做法：盒子几何做成 16 个单位、贴图按 16 像素一个面，渲染时整体缩到一半，
 * 出来的大小和原版的 8 个单位盒子一样，UV 精度也保持原版水平。
 *
 * <p>盒子的位置是照原版来的：原点 (-4, -14.5, -4)、边长 8，挂在头部上，所以盒子比头略大一圈、
 * 底边压在头顶，看起来就是扣在头上的一顶冠。
 */
public class CrownGoldModel
{
    /** 贴图尺寸，和原版金冠贴图一致。 */
    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 32;

    /**
     * 渲染时整体缩小的倍数。
     *
     * <p>几何按 16 个单位做、UV 才有 16 像素一个面的精度。原版的盒子是 8 个单位宽，但那个尺寸和
     * 玩家的头一样大（头部盒子也是 8×8×8），叠在头盔上会打架，所以这里再收一圈：整体缩到原版的
     * 0.95 倍，冠比头略小一点，戴着头盔时不容易穿模。
     *
     * <p>0.475 是 0.5（还原原版宽度的倍数）乘 0.95。改这个值时 {@link #ORIGIN_Y} 要跟着改，否则
     * 冠会陷进脑袋里或者飘到天上去 —— 盒子的底边位置是两者算出来的，不是单独由哪一个决定。
     */
    public static final float RENDER_SCALE = 0.475F;

    /** 盒子的边长（几何单位），见 {@link #RENDER_SCALE}。 */
    private static final int BOX = 16;

    /**
     * 盒子的位置。
     *
     * <p>X、Z 按原版居中放。Y 是算出来的：要让盒子的底边正好落在头顶上，底边在像素坐标里是
     * {@code (ORIGIN_Y + BOX) * RENDER_SCALE}，玩家的头顶是 -8 像素，解出来就是 -33。
     */
    private static final float ORIGIN_X = -8.0F;
    private static final float ORIGIN_Y = -33.0F;
    private static final float ORIGIN_Z = -8.0F;

    private CrownGoldModel()
    {
    }

    /**
     * 金冠的模型定义。
     *
     * <p>只有一个盒子，挂在根节点上；戴在头上时由渲染器把根节点摆到头的位置。
     */
    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("crown",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(ORIGIN_X, ORIGIN_Y, ORIGIN_Z, BOX, BOX, BOX),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    /** 取出那个盒子，供渲染器使用。 */
    public static ModelPart crownFrom(ModelPart root)
    {
        return root.getChild("crown");
    }
}
