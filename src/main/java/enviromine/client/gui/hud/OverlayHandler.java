package enviromine.client.gui.hud;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import enviromine.client.gui.Gui_EventManager;
import enviromine.utils.RenderAssist;

@SideOnly(Side.CLIENT)
public class OverlayHandler
{

		public int id;
		public int amplitude;
		public int phase;
		public int interval, intervalPhase;
		public int peakWait, peakWaitPhase;
		public int peakSpeed, baseSpeed;
		public int R, G, B;
		public Boolean pulse;
		public ResourceLocation resource;
		
		/**
		 *  OverlayHandler creates basic settings for PulseWave
		 * @param id 
		 * @param pulse (Currently Not used)
		 */
		public OverlayHandler (int id, Boolean pulse)
		{
			this.id = id;
			this.amplitude = 111;
			this.phase = 0;
			this.intervalPhase = 0;
			this.interval = 200;
			this.peakWait = 100;
			this.peakWaitPhase = 0;
			this.peakSpeed = 1;
			this.baseSpeed = 1;

			this.pulse = pulse;
			this.resource = Gui_EventManager.blurOverlayResource;
			
			this.R = 255;
			this.G = 255;
			this.B = 255;
		}
		
		/** Sets Overlay Color**/
		public void setRGB(int R, int G, int B)
		{
			this.R = R;
			this.G = G;
			this.B = B;
		}
		
		public int getRGBA(int alpha)
		{
			return RenderAssist.getColorFromRGBA(this.R, this.G, this.B , alpha);
		}
		
		/**
		 *  Amplitude: is peak of the wave 
		 *  Interval: is a pause between each wave
		 *  PeakWait: is a pause when hits top of wave
		 *   PeakSpeed: is speed to reach top of wave (Amplitude)
		 *  BaseSpeed: is speed to reach Buttom of wave
		 * 
		 * Default: setPulseVar(111, 0, 100, 1, 1)
		 * @param amplitude
		 * @param interval
		 * @param peakWait
		 * @param peakSpeed
		 * @param baseSpeed
		 * @return
		 */
		public OverlayHandler setPulseVar(int amplitude, int interval, int peakWait, int peakSpeed, int baseSpeed)
		{
			this.amplitude = amplitude > 111 ? 111 : amplitude ;
			this.interval = interval;
			this.peakWait = peakWait;
			this.peakSpeed = peakSpeed;
			this.baseSpeed = baseSpeed;
			return this;
		}
		
		public void setResource(ResourceLocation resource)
		{
			this.resource = resource;
		}
		
		
		/**
		 * Pass an Overlay and returns alpha numbers
		 * Currently optimized for Gas Mask will need to change later on to be more universal
		 * @param overlay
		 * @return
		 */
		public static int PulseWave(OverlayHandler overlay)
	    {
	        int alpha;

	        alpha = (int)(Math.sin( Math.toRadians(overlay.phase) ) *  overlay.amplitude );
	        alpha = (255 / overlay.amplitude) * alpha;
	        
	        if(alpha >= 254) alpha = 254;
	        else if(alpha <= 0) alpha = 0;
	        //Moving up to peak
	        
	        if(overlay.phase <=  (overlay.amplitude*2))	 
	        {
	        	if(overlay.phase >= overlay.amplitude && overlay.peakWaitPhase <= overlay.peakWait)
	        	{
	        		overlay.peakWaitPhase++;
	        	}
	        	else
	        	{
	        		overlay.phase += overlay.peakSpeed;
	        	}
	        	
	        }
	        else if(overlay.intervalPhase <= overlay.interval)
	        {
	        	overlay.intervalPhase++;
	        }
	        else  
	        {
	        	overlay.phase = 0;
	        	overlay.peakWaitPhase = 0;
	        	overlay.intervalPhase = 0;
	        }
	        
            
	        return alpha;
	    }
}

