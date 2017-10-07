package org.liquidengine.legui.system.renderer.nvg.util;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_RIGHT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;
import static org.lwjgl.nanovg.NanoVG.NVG_HOLE;
import static org.lwjgl.nanovg.NanoVG.nnvgText;
import static org.lwjgl.nanovg.NanoVG.nnvgTextBreakLines;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgBoxGradient;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgResetScissor;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRect;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.optional.TextState;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.font.FontRegistry;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NVGTextRow;

/**
 * Created by Aliaksandr_Shcherbin on 2/2/2017.
 */
public final class NvgRenderUtils {

    private NvgRenderUtils() {
    }

    /**
     * Used to render text state to rectangle bounds.
     *
     * @param nvgContext nanovg context.
     * @param pos rectangle position.
     * @param size rectangle size.
     * @param textState text state to render.
     */
    public static void renderTextStateLineToBounds(long nvgContext, Vector2f pos, Vector2f size, TextState textState) {
        renderTextStateLineToBounds(nvgContext, pos, size, textState, true);
    }

    /**
     * Used to render text state to rectangle bounds.
     *
     * @param nvgContext nanovg context.
     * @param pos rectangle position.
     * @param size rectangle size.
     * @param textState text state to render.
     * @param hide true if need to hide out of bounds textState.
     */
    public static void renderTextStateLineToBounds(long nvgContext, Vector2f pos, Vector2f size, TextState textState, boolean hide) {
        Vector4f pad = textState.getPadding();
        String font = textState.getFont() == null ? FontRegistry.DEFAULT : textState.getFont();
        HorizontalAlign horizontalAlign = textState.getHorizontalAlign();
        VerticalAlign verticalAlign = textState.getVerticalAlign();
        renderTextLineToBounds(nvgContext,
            /* X position             */ pos.x + 0.5f + pad.x,
            /* Y position             */ pos.y + pad.y,
            /* Width                  */ size.x - pad.x - pad.z,
            /* Height                 */ size.y - pad.y - pad.w,
            /* font size              */ textState.getFontSize(),
            /* font name              */font,
            /* text color             */ textState.getTextColor(),
            /* text to render         */ textState.getText(),
            /* horizontal alignment   */ horizontalAlign,
            /* vertical alignment     */ verticalAlign,
            /* hide out of bound text */ hide);
    }

    /**
     * Used to render textState to rectangle bounds.
     *
     * @param context nanovg context.
     * @param x x position of rectangle.
     * @param y y position of rectangle.
     * @param w width of rectangle.
     * @param h height of rectangle.
     * @param fontSize titleFont size.
     * @param font titleFont name which contains in titleFont register.
     * @param textColor textState color.
     * @param text textState.
     * @param horizontalAlign horizontal align.
     * @param verticalAlign vertical align.
     * @param hide true if need to hide out of bounds textState.
     */
    public static void renderTextLineToBounds(long context,
        float x, float y, float w, float h,
        float fontSize,
        String font,
        Vector4f textColor,
        String text,
        HorizontalAlign horizontalAlign,
        VerticalAlign verticalAlign, boolean hide) {

        NVGColor colorA = NVGColor.calloc();
        renderTextLineToBounds(context, x, y, w, h, fontSize, font, textColor, colorA, text, horizontalAlign, verticalAlign, hide);
        colorA.free();
    }


    /**
     * Used to renderNvg textState to rectangle bounds.
     *
     * @param context nanovg context.
     * @param x x position of rectangle.
     * @param y y position of rectangle.
     * @param w width of rectangle.
     * @param h height of rectangle.
     * @param fontSize titleFont size.
     * @param font titleFont name which contains in titleFont register.
     * @param textColor textState color.
     * @param nvgColor nvg textState color.
     * @param text textState.
     * @param horizontalAlign horizontal align.
     * @param verticalAlign vertical align.
     * @param hide true if need to hide out of bounds textState.
     */
    public static void renderTextLineToBounds(long context,
        float x, float y, float w, float h,
        float fontSize,
        String font,
        Vector4f textColor,
        NVGColor nvgColor,
        String text,
        HorizontalAlign horizontalAlign, VerticalAlign verticalAlign, boolean hide) {
        nvgFontSize(context, fontSize);
        nvgFontFace(context, font);

        ByteBuffer byteText = null;
        try {
            alignTextInBox(context, horizontalAlign, verticalAlign);
            byteText = memUTF8(text, false);
            long start = memAddress(byteText);
            long end = start + byteText.remaining();
            long rowStart = start;
            long rowEnd = end;
            if (hide) {
                NVGTextRow.Buffer buffer = NVGTextRow.calloc(1);
                int rows = nnvgTextBreakLines(context, start, end, w, memAddress(buffer), 1);
                if (rows != 0) {
                    NVGTextRow row = buffer.get(0);
                    rowStart = row.start();
                    rowEnd = row.end();
                }
                buffer.free();
            }
            renderTextLine(context, x, y, w, h, textColor, nvgColor, horizontalAlign, verticalAlign, rowStart, rowEnd);
        } finally {
            if (byteText != null) {
                memFree(byteText);
            }
        }
    }

    /**
     * Used to render text line.
     *
     * @param context nanovg context.
     * @param x left bound of rectangle.
     * @param y top bound of rectangle.
     * @param w rectangle width.
     * @param h rectangle height.
     * @param textColor text color
     * @param nvgColor nanovg color.
     * @param horizontalAlign horizontal align.
     * @param verticalAlign vertical align.
     * @param rowStart pointer to start of string to render.
     * @param rowEnd pointer to end of string to render.
     */
    private static void renderTextLine(long context, float x, float y, float w, float h, Vector4f textColor, NVGColor nvgColor, HorizontalAlign horizontalAlign,
        VerticalAlign verticalAlign, long rowStart, long rowEnd) {
        float tx = x + w * horizontalAlign.index / 2f;
        float ty = y + h * verticalAlign.index / 2f;

        nvgBeginPath(context);
        NVGColor textColorN = textColor.w == 0 ? NvgColorUtil.rgba(0.0f, 0.0f, 0.0f, 1f, nvgColor) : NvgColorUtil.rgba(textColor, nvgColor);
        nvgFillColor(context, textColorN);
        nnvgText(context, tx, ty, rowStart, rowEnd);
    }

    /**
     * Used to draw rectangle.
     *
     * @param context nanovg context.
     * @param color color.
     * @param x x position of rectangle.
     * @param y y position of rectangle.
     * @param w rectangle width.
     * @param h rectangle height.
     */
    public static void drawRectangle(long context, Vector4fc color, float x, float y, float w, float h) {
        NVGColor nvgColor = NvgColorUtil.rgba(color, NVGColor.calloc());
        nvgBeginPath(context);
        nvgFillColor(context, nvgColor);
        nvgRect(context, x, y, w, h);
        nvgFill(context);
        nvgColor.free();
    }


    public static void drawRectangle(long context, Vector4fc color, Vector4f bounds) {
        drawRectangle(context, color, bounds.x, bounds.y, bounds.z, bounds.w);
    }


    public static void drawRectangle(long context, Vector4fc color, Vector2f position, Vector2f size) {
        drawRectangle(context, color, position.x, position.y, size.x, size.y);
    }

    /**
     * Used to renderNvg textState to rectangle bounds.
     *
     * @param context nanovg context.
     * @param x x position of rectangle.
     * @param y y position of rectangle.
     * @param w width of rectangle.
     * @param h height of rectangle.
     * @param fontSize titleFont size.
     * @param font titleFont name which contains in titleFont register.
     * @param textColor textState color.
     * @param text textState.
     * @param horizontalAlign horizontal align.
     * @param verticalAlign vertical align.
     */
    public static void renderTextLineToBounds(long context, float x, float y, float w, float h, float fontSize,
        String font, Vector4f textColor, String text, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign) {
        renderTextLineToBounds(context, x, y, w, h, fontSize, font, textColor, text, horizontalAlign, verticalAlign, true);
    }


    public static float[] calculateTextBoundsRect(long context, Vector4f rect, String text, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign) {
        return calculateTextBoundsRect(context, rect.x, rect.y, rect.z, rect.w, text, horizontalAlign, verticalAlign);
    }

    public static float[] calculateTextBoundsRect(long context, float x, float y, float w, float h, String text, HorizontalAlign horizontalAlign,
        VerticalAlign verticalAlign) {
        ByteBuffer byteText = null;
        try {
            byteText = memUTF8(text, false);
            return calculateTextBoundsRect(context, x, y, w, h, byteText, horizontalAlign, verticalAlign);
        } finally {
            if (byteText != null) {
                memFree(byteText);
            }
        }
    }

    public static float[] calculateTextBoundsRect(long context, float x, float y, float w, float h, ByteBuffer text, HorizontalAlign horizontalAlign,
        VerticalAlign verticalAlign) {
        float bounds[] = new float[4];
        nvgTextBounds(context, x, y, text, bounds);
        return createBounds(x, y, w, h, horizontalAlign, verticalAlign, bounds);
    }


    public static float[] createBounds(float x, float y, float w, float h, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign, float[] bounds) {
        float ww = bounds[2] - bounds[0];
        float hh = bounds[3] - bounds[1];
        return createBounds(x, y, w, h, horizontalAlign, verticalAlign, /*bounds, */ww, hh);
    }

    public static float[] createBounds(float w, float h, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign, float[] bounds, float ww, float hh) {
        int hp = horizontalAlign == HorizontalAlign.LEFT ? 0 : horizontalAlign == HorizontalAlign.CENTER ? 1 : 2;
        int vp = verticalAlign == VerticalAlign.TOP ? 0 : verticalAlign == VerticalAlign.MIDDLE ? 1 : verticalAlign == VerticalAlign.BOTTOM ? 2 : 3;
        float x1 = bounds[0] + (w + ww) * 0.5f * hp;

        float baseline = (vp > 2 ? hh / 4.0f : 0);
        float vv = (vp == 3 ? 1 : vp);
        float y1 = bounds[1] + (h + hh) * 0.5f * vv + (vp > 2 ? (+baseline) : 0);
        return new float[]{
            x1, y1, ww, hh,
            x1 - (ww * 0.5f * hp), y1 - (hh * 0.5f * vv) - baseline, ww, hh
        };
    }

    public static float[] createBounds(float x, float y, float w, float h, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign, float tw, float th) {
        int hp = horizontalAlign.index;
        int vp = verticalAlign.index;

        float x1 = x + w * 0.5f * hp;

        float baseline = (vp > 2 ? th / 4.0f : 0);
        float vv = (vp == 3 ? 1 : vp);
        float y1 = y + h * 0.5f * vv + (vp > 2 ? (+baseline) : 0);
        return new float[]{
            x1, y1, tw, th,
            x1 - (tw * 0.5f * hp), y1 - (th * 0.5f * vv) - baseline, tw, th
        };
    }


    public static void alignTextInBox(long context, HorizontalAlign hAlig, VerticalAlign vAlig) {
        int hAlign = hAlig == HorizontalAlign.CENTER ? NVG_ALIGN_CENTER : hAlig == HorizontalAlign.LEFT ? NVG_ALIGN_LEFT : NVG_ALIGN_RIGHT;
        int vAlign = vAlig == VerticalAlign.TOP ? NVG_ALIGN_TOP : vAlig == VerticalAlign.BOTTOM ?
            NVG_ALIGN_BOTTOM : vAlig == VerticalAlign.MIDDLE ? NVG_ALIGN_MIDDLE : NVG_ALIGN_BASELINE;
        nvgTextAlign(context, hAlign | vAlign);
    }

    public static void drawRectStroke(long context, Vector4fc rect, Vector4fc color, float borderRadius, float strokeWidth) {
        drawRectStroke(context, rect.x(), rect.y(), rect.z(), rect.w(), color, borderRadius, strokeWidth);
    }

    public static void drawRectStroke(long context, Vector2fc position, Vector2fc size, Vector4fc color, float borderRadius, float strokeWidth) {
        drawRectStroke(context, position.x(), position.y(), size.x(), size.y(), color, borderRadius, strokeWidth);
    }

    public static void drawRectStroke(long context, float x, float y, float w, float h, Vector4fc strokeColor, float borderRadius, float strokeWidth) {
        NVGColor nvgColor = NvgColorUtil.rgba(strokeColor, NVGColor.calloc());
        nvgBeginPath(context);
        nvgStrokeWidth(context, strokeWidth);
        nvgRoundedRect(context, x, y, w, h, borderRadius);
        nvgStrokeColor(context, nvgColor);
        nvgStroke(context);
        nvgColor.free();
    }

    public static void dropShadow(long context, float x, float y, float w, float h, float cornerRadius, Vector4f shadowColor) {
        NVGPaint shadowPaint = NVGPaint.calloc();
        NVGColor colorA = NVGColor.calloc();
        NVGColor colorB = NVGColor.calloc();

        nvgBoxGradient(context, x, y + 2, w, h, cornerRadius * 2, 10, NvgColorUtil.rgba(shadowColor, colorA), NvgColorUtil.rgba(0, 0, 0, 0, colorB),
            shadowPaint);
        nvgBeginPath(context);
        nvgRect(context, x - 10, y - 10, w + 20, h + 30);
        nvgRoundedRect(context, x, y, w, h, cornerRadius);
        nvgPathWinding(context, NVG_HOLE);
        nvgFillPaint(context, shadowPaint);
        nvgFill(context);

        shadowPaint.free();
        colorA.free();
        colorB.free();
    }

    /**
     * Creates scissor for provided component by it's parent components.
     *
     * @param context nanovg context.
     * @param gui {@link Component}.
     */
    public static void createScissor(long context, Component gui) {
        Component parent = gui.getParent();
        createScissorByParent(context, parent);
    }

    /**
     * Creates scissor for provided bounds.
     *
     * @param context nanovg context.
     * @param bounds bounds.
     */
    public static void createScissor(long context, Vector4f bounds) {
        nvgScissor(context, bounds.x, bounds.y, bounds.z, bounds.w);
    }

    /**
     * Intersects scissor for provided bounds.
     *
     * @param context nanovg context.
     * @param bounds bounds.
     */
    public static void intersectScissor(long context, Vector4f bounds) {
        nvgIntersectScissor(context, bounds.x, bounds.y, bounds.z, bounds.w);
    }

    /**
     * Creates scissor by provided component and it's parent components.
     *
     * @param context nanovg context.
     * @param parent parent component.
     */
    public static void createScissorByParent(long context, Component parent) {
        if (parent != null) {
            Vector2f p = parent.getAbsolutePosition();
            Vector2f s = parent.getSize();
            nvgScissor(context, p.x, p.y, s.x, s.y);

            while ((parent = parent.getParent()) != null) {
                p = parent.getAbsolutePosition();
                s = parent.getSize();
                nvgIntersectScissor(context, p.x, p.y, s.x, s.y);
            }
        }
    }

    /**
     * Used to reset scissor.
     *
     * @param context nanovg context pointer.
     */
    public static void resetScissor(long context) {
        nvgResetScissor(context);
    }

    /**
     * Used to call function wrapped to scissor call.
     *
     * @param nanovg nanovg context.
     * @param component component to create scissor.
     * @param function function to call.
     */
    public static void drawInScissor(long nanovg, Component component, Runnable function) {
        createScissor(nanovg, component);
        function.run();
        resetScissor(nanovg);
    }

    public static boolean visibleInParents(Component component) {
        List<Component> parentList = new ArrayList<>();
        for (Component parent = component.getParent(); parent != null; parent = parent.getParent()) {
            parentList.add(parent);
        }
        if (parentList.size() > 0) {
            Vector2f pos = new Vector2f(0, 0);
            Vector2f rect = new Vector2f(0, 0);
            Vector2f absolutePosition = component.getAbsolutePosition();
            float lx = absolutePosition.x;
            float rx = absolutePosition.x + component.getSize().x;
            float ty = absolutePosition.y;
            float by = absolutePosition.y + component.getSize().y;

            for (int i = parentList.size() - 1; i >= 0; i--) {
                Component parent = parentList.get(i);
                pos.add(parent.getPosition());
                rect.set(pos).add(parent.getSize());

                if (lx > rect.x || rx < pos.x || ty > rect.y || by < pos.y) {
//                    System.out.println("SKIPPING " + component.getClass().getSimpleName() + " " + lx + " " + rx);
                    return false;
                }
            }
        }
        return true;
    }
}
