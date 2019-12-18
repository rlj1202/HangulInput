package redlaboratory.hangulinput;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.logging.LogManager;

public class SpecialCharacterWidget extends Widget {

    private Suggestor suggestor;

    private int bgColor = 0x80000000;

    private int maxColumns = 10;
    private int maxRows = 10;

    private int gridMargin = 2;
    private int gridSize = gridMargin * 2 + 8;

    public SpecialCharacterWidget(int x, int y, Suggestor suggestor) {
        this(x, y, 0, 0, "", suggestor);
    }

    public SpecialCharacterWidget(int x, int y, int width, int height, String msg, Suggestor suggestor) {
        super(x, y, width, height, msg);

        this.suggestor = suggestor;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();

        if (suggestor.getSuggestionMode()) {
            List<Suggestor.Suggestion> suggestions = suggestor.getSuggestions();

            int columns = Integer.min(maxColumns, suggestions.size());
            int rows = (int) Math.ceil(suggestions.size() / (float) columns);

            int displayRows = Integer.min(rows, maxRows);

            width = columns * gridSize;
            height = displayRows * gridSize;

            int selectedRow = suggestor.getSelectedIndex() / columns;
            int startRow = selectedRow - displayRows / 2;
            startRow = Integer.max(startRow, 0);
            startRow = Integer.min(startRow, rows - displayRows);

            // draw background
            fill(x, y, x + width + 2, y + height, bgColor);

            // draw scrollbar
            int scrollbarHeight = (int) (height / (float) rows * displayRows);
            double scrollbarStep;
            if (rows != displayRows) {
                scrollbarStep = (height - scrollbarHeight) / (float) (rows - displayRows);
            } else {
                scrollbarStep = 0;
            }
            int scrollbarX = x + width;
            int scrollbarY = y + (int) Math.ceil(scrollbarStep * startRow);
            fill(scrollbarX, scrollbarY, scrollbarX + 2, scrollbarY + scrollbarHeight, 0xffffffff);

            // draw characters
            for (int offset = 0; offset < displayRows; offset++) {
                int row = startRow + offset;

                int gridX;
                int gridY = y + offset * gridSize;

                for (int col = 0; col < columns; col++) {
                    int index = row * columns + col;
                    if (index >= suggestions.size()) break;

                    gridX = x + col * gridSize;

                    String text = "";
                    if (index == suggestor.getSelectedIndex()) {
                        text += "" + TextFormatting.YELLOW + TextFormatting.BOLD;
                    }
                    text += suggestions.get(index).getCharacter();

                    if (gridX <= mouseX && mouseX < gridX + gridSize &&
                            gridY <= mouseY && mouseY < gridY + gridSize) {
                        fill(gridX, gridY, gridX + gridSize, gridY + gridSize, 0xffff0000);
                    }

                    minecraft.fontRenderer.drawStringWithShadow(text, gridX + gridMargin, gridY + gridMargin, 0xffffffff);
                }
            }

            /*
            int range = Integer.min(10, suggestions.size());

            int startIndex = suggestor.getSelectedIndex() - range / 2;
            startIndex = Integer.max(startIndex, 0);
            startIndex = Integer.min(startIndex, suggestions.size() - range);

            // draw background
            fill(x, y, x + width, y + range * 12, 0x80000000);

            for (int offset = 0; offset < range; offset++) {
                int index = offset + startIndex;
                Suggestor.Suggestion suggestion = suggestions.get(index);

                int textX = x + 2;
                int textY = y + 2 + offset * 12;

                String str = "";
                if (textX <= mouseX && mouseX <= textX + width &&
                        textY <= mouseY && mouseY <= textY + 8) {
                    str += "";
                }
                if (index == suggestor.getSelectedIndex()) {
                    str += "" + TextFormatting.YELLOW + TextFormatting.BOLD;
                }
                str += suggestion.getCharacter();

                minecraft.fontRenderer.drawStringWithShadow(str, textX, textY, 0xffffffff);
            }
            */
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (suggestor.getSuggestionMode()) {
            LogManager.getLogManager().getLogger("").info("clicked!");
            return true;
        }

        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int modifiers, int scancode) {
        LogManager.getLogManager().getLogger("").info("yea");

        if ((modifiers & GLFW.GLFW_KEY_LEFT_ALT) > 0) {
            LogManager.getLogManager().getLogger("").info("yea");

            this.x = (int) Minecraft.getInstance().mouseHelper.getMouseX();
            this.y = (int) Minecraft.getInstance().mouseHelper.getMouseY();

            return true;
        }

        return false;
    }

}
