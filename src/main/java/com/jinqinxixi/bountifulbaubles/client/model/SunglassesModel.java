package com.jinqinxixi.bountifulbaubles.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * 墨镜模型：戴在头上的一个盒子，盖住整个头部，贴图上只有中间一条墨镜带和两片镜片。
 *
 * <p>照 1.12.2 原版 Bountiful Baubles 的 {@code ModelSunglasses} 做的。原版也是一个盒子
 * （{@code -4,-8,-4} 到 {@code +4,0,+4}，正好和头部同大），用自定义 UV 类让每个面的贴图尺寸和
 * 几何尺寸不同。这里换等价做法：盒子几何做成 16 个单位、渲染时缩到一半，UV 精度保持原版水平 ——
 * 和金冠同一个套路，见 {@link CrownGoldModel}。
 *
 * <p>盒子盖住整个头，贴图大部分透明，只有正面（镜片）和两侧（镜腿）那一条有图案，画出来就是
 * 一副架在脸上的墨镜。原版还关了背面剔除（{@code disableCull}），否则盒子背面的面会被裁掉，
 * 绕到侧面时镜腿就看不见了；渲染器里也照做了。
 */
public class SunglassesModel
{
    /** 贴图尺寸，和原版墨镜贴图一致。 */
    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 32;

    /**
     * 渲染时缩小的倍数。
     *
     * <p>几何按 16 个单位做、UV 才有 16 像素一个面的精度；原版盒子是 8 个单位，这里缩一半还原。
     * 缩在渲染时施加（见渲染器），模型本身不动。
     */
    public static final float RENDER_SCALE = 0.5F;

    /** 盒子边长（几何单位），是原版 8 个单位的两倍，见 {@link #RENDER_SCALE}。 */
    private static final int BOX = 16;

    /** 盒子位置，按原版 (-4, -8, -4) 的两倍。头部盒子是 y[-8,0]，所以这里 y[-16,0] 正好重合。 */
    private static final float ORIGIN_X = -8.0F;
    private static final float ORIGIN_Y = -16.0F;
    private static final float ORIGIN_Z = -8.0F;

    private SunglassesModel()
    {
    }

    /** 墨镜的模型定义：一个和头部同大的盒子。 */
    public static LayerDefinition createLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("sunglasses",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(ORIGIN_X, ORIGIN_Y, ORIGIN_Z, BOX, BOX, BOX),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    /** 取出那个盒子，供渲染器使用。 */
    public static ModelPart sunglassesFrom(ModelPart root)
    {
        return root.getChild("sunglasses");
    }
}
