package redlaboratory.hangulinput;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import redlaboratory.hangulutils.HangulBuilder;

@OnlyIn(Dist.CLIENT)
public class InputModeIndicatorWidget extends Widget {

    private HangulBuilder hangulBuilder;

    private int bgColor = 0x80000000;
    private int fontColor = 0xffffffff;

    public InputModeIndicatorWidget(int x, int y, int width, int height, String msg, HangulBuilder hangulBuilder) {
        super(x, y, width, height, ITextComponent.func_241827_a_(msg));
        this.hangulBuilder = hangulBuilder;
    }

    @Override
    public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        //super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);

        Minecraft minecraft = Minecraft.getInstance();

        int x = field_230690_l_;
        int y = field_230691_m_;
        int width = field_230688_j_;
        int height = field_230689_k_;

        func_238467_a_(matrix, x, y, x + width, y + height, bgColor);
        minecraft.fontRenderer.func_238405_a_(
                matrix,
                hangulBuilder.isHangulMode() ? "\uD55C" : "\uC601",
                x + (int) ((width - 8) / 2.0), y + (int) ((height - 8) / 2.0),
                fontColor
        );
    }

    @Override
    public void func_230431_b_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.func_230431_b_(matrix, mouseX, mouseY, partialTicks);
    }

//    public void render(int mouseX, int mouseY, float partialTicks) {
//        Minecraft minecraft = Minecraft.getInstance();

//        fill(x, y, x + width, y + height, bgColor);
//        minecraft.fontRenderer.drawStringWithShadow(
//                hangulBuilder.isHangulMode() ? "\uD55C" : "\uC601",
//                x + (int) ((width - 8) / 2.0), y + (int) ((height - 8) / 2.0),
//                fontColor
//        );
//    }

}
