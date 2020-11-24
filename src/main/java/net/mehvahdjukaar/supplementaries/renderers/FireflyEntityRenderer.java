package net.mehvahdjukaar.supplementaries.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.mehvahdjukaar.supplementaries.common.CommonUtil;
import net.mehvahdjukaar.supplementaries.entities.FireflyEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
@OnlyIn(Dist.CLIENT)
public class FireflyEntityRenderer extends EntityRenderer<FireflyEntity> {
    public FireflyEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(FireflyEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
                       int packedLightIn) {
        matrixStackIn.push();
        // TextureAtlasSprite sprite =
        // Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_PARTICLES_TEXTURE);

        float r = 1;
        float g = 1;
        float b = 1;
        float a =(float) MathHelper.lerp(partialTicks, entityIn.getAlpha(), entityIn.getPrevAlpha());

        matrixStackIn.translate(0.0D, (double) 0.5F, 0.0D);
        matrixStackIn.rotate(this.renderManager.getCameraOrientation());
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
        float f9 = 0.32F;
        matrixStackIn.scale(0.3F, 0.3F, 0.3F);
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getBeaconBeam(CommonUtil.FIREFLY_TEXTURE, true));

        RendererUtil.addQuadSide(builder, matrixStackIn, -0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f, 0, 0, 1, 1, r, g,b, a, 240, 0, 0, 1, 0);

        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }


    @Override
    public ResourceLocation getEntityTexture(FireflyEntity entity) {
        return CommonUtil.FIREFLY_TEXTURE;
    }
}