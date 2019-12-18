package redlaboratory.hangulinput;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import redlaboratory.hangulutils.HangulBuilder;
import redlaboratory.hangulutils.HangulHelper;

import java.util.List;

@Mod(HangulInput.MODID)
public class HangulInput {

    public static final String MODID = "hangulinput";

    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    private static final HangulBuilder HANGUL_BUILDER = new HangulBuilder();

    private static Suggestor suggestor;

    public HangulInput() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clearStates() {
        HANGUL_BUILDER.clearStates();
        suggestor.setSuggestionMode(false);

        LOGGER.info("clear states");
    }

    // do some heavy things on mod loading stage
    private void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("start commonSetup(FMLClientSetupEvent)");

        suggestor = new Suggestor();
        if (suggestor.isValid()) {
            LOGGER.info("");
        } else {
            LOGGER.error("failed to create suggester");
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() == null) return;

        int guiwidth = event.getGui().width;
        int guiheight = event.getGui().height;

        if (event.getGui() instanceof ChatScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    2,
                    guiheight - 2 - 12*2 - 2,
                    12, 12, "", HANGUL_BUILDER
            ));
            event.addWidget(new SpecialCharacterWidget(100, 2, suggestor));
        } else if (event.getGui() instanceof EditSignScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 12 / 2,
                    52,
                    12, 12, "", HANGUL_BUILDER
            ));
            event.addWidget(new SpecialCharacterWidget(100, 2, suggestor));
        } else if (event.getGui() instanceof AnvilScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 83,
                    guiheight / 2 - 78,
                    12, 12, "", HANGUL_BUILDER
            ));
            event.addWidget(new SpecialCharacterWidget(100, 2, suggestor));
        } else if (event.getGui() instanceof CreativeScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 + 77,
                    guiheight / 2 - 64,
                    12, 12, "", HANGUL_BUILDER
            ));
            event.addWidget(new SpecialCharacterWidget(100, 2, suggestor));
        } else if (event.getGui() instanceof WorldSelectionScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 125,
                    25,
                    12, 12, "", HANGUL_BUILDER
            ));
            event.addWidget(new SpecialCharacterWidget(100, 2, suggestor));
        } else if (event.getGui() instanceof EditBookScreen) {
            event.addWidget(new InputModeIndicatorWidget(
                    guiwidth / 2 - 60,
                    guiheight / 2 - 105,
                    12, 12, "", HANGUL_BUILDER
            ));
            event.addWidget(new SpecialCharacterWidget(100, 2, suggestor));
        }
    }

    @SubscribeEvent()
    @OnlyIn(Dist.CLIENT)
    public void onGuiOpen(GuiOpenEvent event) {
        clearStates();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiKeyboardPressed(GuiScreenEvent.MouseClickedEvent event) {
        //clearStates();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiKeyboardPressedPost(GuiScreenEvent.MouseClickedEvent.Post event) {
        if (!event.isCanceled()) {
            clearStates();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiKeyboardPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (event.getKeyCode() == ClientProxy.toggleHangulInputMode.getKey().getKeyCode()) {
            HANGUL_BUILDER.toggleHangulMode();
            clearStates();

            LOGGER.info("hangul input mode : " + HANGUL_BUILDER.isHangulMode());
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE) {
            event.setCanceled(true);

            event.getGui().keyPressed(GLFW.GLFW_KEY_BACKSPACE, event.getModifiers(), 0);

            HangulBuilder.Result result = HANGUL_BUILDER.remove();

            if (result.flagUpdate) {
                event.getGui().charTyped(result.updatedLetter, 0);
            }

            if (!result.flagUpdate && !result.flagAdd) {
                clearStates();
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
            clearStates();

            LOGGER.info("clear states");
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            LOGGER.info("hanja key");

            char[] bufferChars = HANGUL_BUILDER.getBufferChars();
            if (!HangulHelper.isEmpty(bufferChars) && suggestor.isValid()) {
                char buffer = HangulHelper.combineHanguls(bufferChars);

                suggestor.setProperSuggestions(buffer);
            }
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_TAB) {
            if (suggestor.getSuggestionMode()) {
                event.setCanceled(true);

                if ((event.getModifiers() & GLFW.GLFW_MOD_SHIFT) != 0) {
                    suggestor.increaseSelectIndex();
                } else {
                    suggestor.decreaseSelectIndex();
                }
            }
        } else {
            LOGGER.info("keycode: " + event.getKeyCode() + ", modifiers: " + event.getModifiers() + ", scancode: " + event.getScanCode());
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiKeyboard(GuiScreenEvent.KeyboardCharTypedEvent event) {
        if (event.getGui() == null) return;

        event.setCanceled(true);

        if (suggestor.getSuggestionMode()) {
            if (event.getCodePoint() == ' ') {
                char c = suggestor.getSelectedChar();
                event.getGui().keyPressed(GLFW.GLFW_KEY_BACKSPACE, event.getModifiers(), 0);
                event.getGui().charTyped(c, 0);
                clearStates();
            }
        } else {
            HangulBuilder.Result result = HANGUL_BUILDER.add(event.getCodePoint());

            if (result.flagUpdate) {
                event.getGui().keyPressed(GLFW.GLFW_KEY_BACKSPACE, event.getModifiers(), 0);
                event.getGui().charTyped(result.updatedLetter, 0);
                suggestor.setSuggestionMode(false);
            }
            if (result.flagAdd) {
                event.getGui().charTyped(result.addedLetter, 0);
                suggestor.setSuggestionMode(false);
            }

            LOGGER.info("key typed : " + result);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        if (suggestor.getSuggestionMode()) {
            int width = event.getWindow().getScaledWidth();
            int height = event.getWindow().getScaledHeight();

            LOGGER.info("render overlay: width: " + event.getWindow().getScaledWidth() + ", height: " + event.getWindow().getScaledHeight());

            GlStateManager.disableDepthTest();
            GlStateManager.disableCull();
            GlStateManager.disableTexture();
            GlStateManager.enableBlend();

            List<Suggestor.Suggestion> suggestions = suggestor.getSuggestions();

            int range = 10;
            if (suggestions.size() < range) range = suggestions.size();

            int startIndex = suggestor.getSelectedIndex() - range / 2;

            if (startIndex < 0) startIndex = 0;
            if (startIndex + range > suggestions.size()) startIndex = suggestions.size() - range;

            Tessellator tes = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tes.getBuffer();
            {
                bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                bufferBuilder.pos(2, 2, 0).color(0, 0, 0, 0x80).endVertex();
                bufferBuilder.pos(2 + 50, 2, 0).color(0, 0, 0, 0x80).endVertex();
                bufferBuilder.pos(2 + 50, 2 + range * 12, 0).color(0, 0, 0, 0x80).endVertex();
                bufferBuilder.pos(2, 2 + range * 12, 0).color(0, 0, 0, 0x80).endVertex();
                tes.draw();
            }

            GlStateManager.enableTexture();

            int y = 2;
            for (int i = startIndex; i < suggestions.size() && i < startIndex + range; i++) {
                Suggestor.Suggestion suggestion = suggestions.get(i);
                String str = "";
                str += i == suggestor.getSelectedIndex() ? "" + TextFormatting.YELLOW + TextFormatting.BOLD : "";
                str += suggestion.getCharacter();

                Minecraft.getInstance().fontRenderer.drawStringWithShadow(str, 4, 2 + y, 0xffffffff);
                y += 12;
            }

            GlStateManager.enableCull();
            GlStateManager.enableDepthTest();
        }
    }

}
