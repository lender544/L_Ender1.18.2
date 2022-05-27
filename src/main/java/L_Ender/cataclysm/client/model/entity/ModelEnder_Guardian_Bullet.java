package L_Ender.cataclysm.client.model.entity;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelEnder_Guardian_Bullet extends AdvancedEntityModel<Entity> {

	public AdvancedModelBox renderer;

	public ModelEnder_Guardian_Bullet()
	{
		this.texWidth = 64;
		this.texHeight = 32;
		this.renderer = new AdvancedModelBox(this);
		this.renderer.setTextureOffset(0, 0)
				.addBox(-4.0F, -4.0F, -1.0F, 8, 8, 2, 0.0F);
		this.renderer.setTextureOffset(0, 10)
				.addBox(-1.0F, -4.0F, -4.0F, 2, 8, 8, 0.0F);
		this.renderer.setTextureOffset(20, 0)
				.addBox(-4.0F, -1.0F, -4.0F, 8, 2, 8, 0.0F);
		this.renderer.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(renderer);
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(renderer);
	}

	@Override
	public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		this.renderer.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
		this.renderer.rotateAngleX = headPitch * ((float) Math.PI / 180F);
	}
}