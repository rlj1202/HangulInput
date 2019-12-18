package redlaboratory.hangulinput;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {

    public static final KeyBinding toggleHangulInputMode = new KeyBinding(
            HangulInput.MODID + ".key.toggleHangulInputMode",
            GLFW.GLFW_KEY_LEFT_CONTROL,
            "key.categories." + HangulInput.MODID
    );

    static {
        ClientRegistry.registerKeyBinding(toggleHangulInputMode);
    }

}
