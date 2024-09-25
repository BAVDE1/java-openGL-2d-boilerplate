package src.rendering.text;

import src.utility.Logging;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/** State Machine */
public class FontManager {
    public static class Glyph {
        public final int width, height;
        public final int x, y;

        public Glyph(int width, int height, int x, int y) {
            this.width = width;     this.height = height;
            this.x = x;             this.y = y;
        }
    }

    public static final int DEFAULT_TEXTURE_SLOT = 0;
    public static final int LORA = 0;

    private static int fontStyle = Font.PLAIN;
    private static int fontSize = 16;

    HashMap<Character, Glyph> glyphMap = new HashMap<>();

    public static void loadFont(String font) {
        Font loadedFont = new Font(font, fontStyle, fontSize);

        if (!loadedFont.getFamily().equalsIgnoreCase(font)) {
            Logging.warn("Font '%s' could not be found, using '%s' instead", font, loadedFont.getFamily());
        }
    }

    public static void loadCustomFont(int font) {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(getFileForFont(font)));
            customFont.deriveFont(fontStyle, fontSize);
        } catch (IOException | FontFormatException e) {
            Logging.danger("Error preparing font, aborting. Thrown message:\n%s", e);
        }
    }

    public static String getFileForFont(int font) {
        return switch (font) {
            case LORA -> "res/fonts/Lora/Lora-VariableFont_wght.ttf";
            default -> {
                Logging.danger("Font '%s' does not exist and could not be loaded", font);
                yield "";
            }
        };
    }

    public static void setFontAttributes(int newFontStyle, int newFontSize) {
        setFontStyle(newFontStyle);
        setFontSize(newFontSize);
    }

    public static void setFontStyle(int newFontStyle) {
        fontStyle = newFontStyle;
    }

    public static void setFontSize(int newFontSize) {
        fontSize = newFontSize;
    }
}
