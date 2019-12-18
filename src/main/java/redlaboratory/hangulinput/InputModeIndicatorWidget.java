package redlaboratory.hangulinput;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import redlaboratory.hangulutils.HangulBuilder;

@OnlyIn(Dist.CLIENT)
public class InputModeIndicatorWidget extends Widget {

    private HangulBuilder hangulBuilder;

    private int bgColor = 0x80000000;
    private int fontColor = 0xffffffff;

    public InputModeIndicatorWidget(int x, int y, int width, int height, String msg, HangulBuilder hangulBuilder) {
        super(x, y, width, height, msg);
        this.hangulBuilder = hangulBuilder;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();

        fill(x, y, x + width, y + height, bgColor);
        minecraft.fontRenderer.drawStringWithShadow(
                hangulBuilder.isHangulMode() ? "\uD55C" : "\uC601",
                x + (int) ((width - 8) / 2.0), y + (int) ((height - 8) / 2.0),
                fontColor
        );
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        super.renderButton(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        return false;
    }

}
