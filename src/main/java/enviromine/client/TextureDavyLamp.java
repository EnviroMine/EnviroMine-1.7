package enviromine.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;

public class TextureDavyLamp extends TextureAtlasSprite
{
	public int lampState;
	
	public TextureDavyLamp(String icoName, int lampState)
	{
		super(icoName);
		this.lampState = 0;
	}
	
	public void updateAnimation()
	{
        ++this.tickCounter;
        
        int litSize = (this.getFrameCount() - 1) / 2;
        int frameOffset = lampState == 0? 0 : (lampState * (litSize-1)) + 1;
        
        frameCounter = lampState == 0? 0 : (frameCounter + 1) % litSize;
        
        TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(frameCounter + frameOffset), this.width, this.height, this.originX, this.originY, false, false);
	}
}
