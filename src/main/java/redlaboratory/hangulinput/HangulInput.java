package redlaboratory.hangulinput;

import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import redlaboratory.hangulutils.HangulBuilder;

@Mod(HangulInput.MODID)
public class HangulInput {

    public static final String MODID = "hangulinput";

    private static final HangulBuilder HANGUL_BUILDER = new HangulBuilder();

    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public HangulInput() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() == null) return;

        // TODO
//        int guiwidth = event.getGui().width;
//        int guiheight = event.getGui().height;
        int guiwidth = event.getGui().field_230708_k_;
        int guiheight = event.getGui().field_230709_l_;

        if (event.getGui() instanceof ChatScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    2,
                    guiheight - 2 - 12*2 - 2,
                    12, 12, "", HANGUL_BUILDER
            ));
        } else if (event.getGui() instanceof EditSignScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 12 / 2,
                    52,
                    12, 12, "", HANGUL_BUILDER
            ));
        } else if (event.getGui() instanceof AnvilScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 83,
                    guiheight / 2 - 78,
                    12, 12, "", HANGUL_BUILDER
            ));
        } else if (event.getGui() instanceof CreativeScreen) {
//            event.addWidget(new InputModeIndicatorWidget(
//                    guiwidth / 2 + 77,
//                    guiheight / 2 - 64,
//                    12, 12, "", HANGUL_BUILDER
//            ));
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth - 14,
                    guiheight - 14,
                    12, 12, "", HANGUL_BUILDER
            ));
        } else if (event.getGui() instanceof WorldSelectionScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 125,
                    25,
                    12, 12, "", HANGUL_BUILDER
            ));
        } else if (event.getGui() instanceof AddServerScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 120,
                    70,
                    12, 12, "", HANGUL_BUILDER
            ));
        } else if (event.getGui() instanceof EditBookScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 100,
                    30,
                    12, 12, "", HANGUL_BUILDER
            ));
        } else if (event.getGui() instanceof CreateWorldScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 120,
                    64,
                    12 ,12, "", HANGUL_BUILDER
            ));
        } else if (event.getGui() instanceof ServerListScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 120,
                    119,
                    12, 12, "", HANGUL_BUILDER
            ));
        } else {
            LOGGER.info(event.getGui().getClass().getName());
        }
    }

    @SubscribeEvent()
    @OnlyIn(Dist.CLIENT)
    public void onGuiOpen(GuiOpenEvent event) {
        HANGUL_BUILDER.clearStates();

        LOGGER.info("clear states");
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiKeyboardPressed(GuiScreenEvent.MouseClickedEvent event) {
        HANGUL_BUILDER.clearStates();

        LOGGER.info("clear states");
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiKeyboardPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (event.getKeyCode() == ClientProxy.toggleHangulInputMode.getKey().getKeyCode()) {
            HANGUL_BUILDER.toggleHangulMode();
            HANGUL_BUILDER.clearStates();

            LOGGER.info("hangul input mode : " + HANGUL_BUILDER.isHangulMode());
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE) {
            event.setCanceled(true);

            // TODO
//            event.getGui().keyPressed(GLFW.GLFW_KEY_BACKSPACE, event.getModifiers(), 0);
            event.getGui().func_231046_a_(GLFW.GLFW_KEY_BACKSPACE, event.getModifiers(), 0);

            HangulBuilder.Result result = HANGUL_BUILDER.remove();

            if (result.flagUpdate) {
                // TODO
//                event.getGui().charTyped(result.updatedLetter, 0);
                event.getGui().func_231042_a_(result.updatedLetter, 0);
            }

            if (!result.flagUpdate && !result.flagAdd) {
                HANGUL_BUILDER.clearStates();
            }

            LOGGER.info("remove stroke : " + result);
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_UP
                || event.getKeyCode() == GLFW.GLFW_KEY_DOWN
                || event.getKeyCode() == GLFW.GLFW_KEY_LEFT
                || event.getKeyCode() == GLFW.GLFW_KEY_RIGHT
                || event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE
                || event.getKeyCode() == GLFW.GLFW_KEY_ENTER
                || event.getKeyCode() == GLFW.GLFW_KEY_KP_ENTER
                || event.getKeyCode() == GLFW.GLFW_KEY_DELETE) {
            HANGUL_BUILDER.clearStates();

            LOGGER.info("clear states");
        }
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiKeyboard(GuiScreenEvent.KeyboardCharTypedEvent event) {
        if (event.getGui() == null) return;

        event.setCanceled(true);

        HangulBuilder.Result result = HANGUL_BUILDER.add(event.getCodePoint());

        if (result.flagUpdate) {
            // TODO
//            event.getGui().keyPressed(GLFW.GLFW_KEY_BACKSPACE, event.getModifiers(), 0);
//            event.getGui().charTyped(result.updatedLetter, 0);
            event.getGui().func_231046_a_(GLFW.GLFW_KEY_BACKSPACE, event.getModifiers(), 0);
            event.getGui().func_231042_a_(result.updatedLetter, 0);

        }
        if (result.flagAdd) {
            // TODO
//            event.getGui().charTyped(result.addedLetter, 0);
            event.getGui().func_231042_a_(result.addedLetter, 0);
        }

        LOGGER.info("key typed : " + result);
    }

}
