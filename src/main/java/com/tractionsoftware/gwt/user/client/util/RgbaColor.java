/*
 * Copyright 2010-2015 Traction Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tractionsoftware.gwt.user.client.util;

/**
 * Represents an rgba color with HSL access. Immutable with factory
 * methods for adjustments.
 */
public final class RgbaColor {

    // ----------------------------------------------------------------------
    // private members and constructors

    private int r;
    private int g;
    private int b;
    private float a;

    /**
     * Creates an RgbaColor as rgba(0,0,0,1)
     */
    public RgbaColor() {
        this(0, 0, 0, 1);
    }

    /**
     * Creates an RgbaColor with alpha=1
     *
     * @param r
     *            the red component [0-255]
     * @param g
     *            the green component [0-255]
     * @param b
     *            the blue component [0-255]
     */
    public RgbaColor(int r, int g, int b) {
        this(r, g, b, 1);
    }

    /**
     * Creates an RgbaColor
     *
     * @param r
     *            the red component [0-255]
     * @param g
     *            the green component [0-255]
     * @param b
     *            the blue component [0-255]
     * @param a
     *            the alpha component [0-1]
     */
    public RgbaColor(int r, int g, int b, float a) {
        this.r = rgbCheck(r);
        this.g = rgbCheck(g);
        this.b = rgbCheck(b);
        this.a = alphaCheck(a);
    }

    // ----------------------------------------------------------------------
    // parsing methods

    /**
     * Parses an RgbaColor from a hexadecimal, rgb, rgba, hsl, or hsla
     * value.
     *
     * @return returns the parsed color
     */
    public static RgbaColor from(String color) {
        if (color.startsWith("#")) {
            return fromHex(color);
        }
        else if (color.startsWith("rgba")) {
            return fromRgba(color);
        }
        else if (color.startsWith("rgb")) {
            return fromRgb(color);
        }
        else if (color.startsWith("hsla")) {
            return fromHsla(color);
        }
        else if (color.startsWith("hsl")) {
            return fromHsl(color);
        }
        else {
            return getDefaultColor();
        }
    }

    /**
     * Parses an RgbaColor from a hexadecimal value.
     *
     * @return returns the parsed color
     */
    public static RgbaColor fromHex(String hex) {
        if (hex.length() == 0 || hex.charAt(0) != '#') return getDefaultColor();

        // #rgb
        if (hex.length() == 4) {

            return new RgbaColor(parseHex(hex, 1, 2),
                                 parseHex(hex, 2, 3),
                                 parseHex(hex, 3, 4));

        }
        // #rrggbb
        else if (hex.length() == 7) {

            return new RgbaColor(parseHex(hex, 1, 3),
                                 parseHex(hex, 3, 5),
                                 parseHex(hex, 5, 7));

        }
        else {
            return getDefaultColor();
        }
    }

    /**
     * Parses an RgbaColor from an rgb value.
     *
     * @return the parsed color
     */
    public static RgbaColor fromRgb(String rgb) {
        if (rgb.length() == 0) return getDefaultColor();

        String[] parts = getRgbParts(rgb).split(",");
        if (parts.length == 3) {
            return new RgbaColor(parseInt(parts[0]),
                                 parseInt(parts[1]),
                                 parseInt(parts[2]));
        }
        else {
            return getDefaultColor();
        }
    }

    /**
     * Parses an RgbaColor from an rgba value.
     *
     * @return the parsed color
     */
    public static RgbaColor fromRgba(String rgba) {
        if (rgba.length() == 0) return getDefaultColor();

        String[] parts = getRgbaParts(rgba).split(",");
        if (parts.length == 4) {
            return new RgbaColor(parseInt(parts[0]),
                                 parseInt(parts[1]),
                                 parseInt(parts[2]),
                                 parseFloat(parts[3]));
        }
        else {
            return getDefaultColor();
        }
    }

    /**
     * Parses an RgbaColor from a CSS3 HSL value, e.g. hsl(25, 100%,
     * 80%)
     *
     * @return the parsed color
     */
    public static RgbaColor fromHsl(String hsl) {
        String[] parts = getHslParts(hsl).split(",");
        if (parts.length == 3) {
            float[] HSL = new float[] { parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]) };
            return fromHsl(HSL);
        }
        else {
            return getDefaultColor();
        }
    }

    /**
     * Parses an RgbaColor from a CSS3 HSLA value, e.g. hsla(25, 100%,
     * 80%, 0.5)
     *
     * @return returns the parsed color
     */
    public static RgbaColor fromHsla(String hsl) {
        String[] parts = getHslaParts(hsl).split(",");
        if (parts.length == 4) {
            float[] HSL = new float[] { parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]) };
            RgbaColor ret = fromHsl(HSL);
            if (ret != null) {
                ret.a = parseFloat(parts[3]);
            }
            return ret;
        }
        else {
            return getDefaultColor();
        }
    }

    /**
     * Convenience to get back to RGB from HSL array returned by
     * toHSL.
     */
    public static RgbaColor fromHsl(float[] hsl) {
        return fromHsl(hsl[0], hsl[1], hsl[2]);
    }

    // ----------------------------------------------------------------------
    // accessors (long names)

    /**
     * Returns the value of the red component [0-255]
     */
    public int red() {
        return r;
    }

    /**
     * Returns the value of the green component [0-255]
     */
    public int green() {
        return g;
    }

    /**
     * Returns the value of the blue component [0-255]
     */
    public int blue() {
        return b;
    }

    /**
     * Returns the value of the hue component of this color as HSL
     * [0-360)
     */
    public float hue() {
        return convertToHsl()[0];
    }

    /**
     * Returns the value of the saturation component of this color as
     * HSL [0-100]
     */
    public float saturation() {
        return convertToHsl()[1];
    }

    /**
     * Returns the value of the lightness component of this color as
     * HSL [0-100]
     */
    public float lightness() {
        return convertToHsl()[2];
    }

    /**
     * Returns the value of the alpha component [0-1]
     */
    public float alpha() {
        return a;
    }

    // ----------------------------------------------------------------------
    // accessors (shorthand)

    /**
     * Returns the value of the red component (0-255)
     */
    public int r() {
        return red();
    }

    /**
     * Returns the value of the green component (0-255)
     */
    public int g() {
        return green();
    }

    /**
     * Returns the value of the blue component (0-255)
     */
    public int b() {
        return blue();
    }

    /**
     * Returns the value of the hue component of this color as HSL
     * [0-360)
     */
    public float h() {
        return hue();
    }

    /**
     * Returns the value of the saturation component of this color as
     * HSL [0-100]
     */
    public float s() {
        return saturation();
    }

    /**
     * Returns the value of the lightness component of this color as
     * HSL [0-100]
     */
    public float l() {
        return lightness();
    }

    /**
     * Returns the value of the alpha component [0-1]
     */
    public float a() {
        return alpha();
    }

    // ----------------------------------------------------------------------
    // "mutator" methods which make copies

    /**
     * Returns a new RgbaColor with the red component set to the
     * specified value.
     */
    public RgbaColor withRed(int red) {
        return new RgbaColor(red, g, b, a);
    }

    /**
     * Returns a new RgbaColor with the blue component set to the
     * specified value.
     */
    public RgbaColor withBlue(int blue) {
        return new RgbaColor(r, g, blue, a);
    }

    /**
     * Returns a new RgbaColor with the green component set to the
     * specified value.
     */
    public RgbaColor withGreen(int green) {
        return new RgbaColor(r, green, b, a);
    }

    /**
     * Returns a new RgbaColor with the alpha component set to the
     * specified value.
     */
    public RgbaColor withAlpha(float alpha) {
        return new RgbaColor(r, g, b, alpha);
    }

    /**
     * Returns a new RgbaColor with the hue component set to the
     * specified value.
     */
    public RgbaColor withHue(float hue) {
        return withHsl(0, hueCheck(hue));
    }

    /**
     * Returns a new RgbaColor with the saturation component set to
     * the specified value.
     */
    public RgbaColor withSaturation(float saturation) {
        return withHsl(1, slCheck(saturation));
    }

    /**
     * Returns a new RgbaColor with the lightness component set to the
     * specified value.
     */
    public RgbaColor withLightness(float lightness) {
        return withHsl(2, slCheck(lightness));
    }

    /**
     * Returns a new color with a new value of the specified HSL
     * component.
     */
    private RgbaColor withHsl(int index, float value) {
        float[] HSL = convertToHsl();
        HSL[index] = value;
        return RgbaColor.fromHsl(HSL);
    }

    // ----------------------------------------------------------------------
    // interesting related colors

    /**
     * Returns a new color that is the complement of this color.
     * Equivalent to adjustHue(180).
     */
    public RgbaColor complement() {
        return adjustHue(180);
    }

    /**
     * Returns a new color that is the grayscale equivalent of this
     * color (with saturation = 0).
     */
    public RgbaColor grayscale() {
        return withSaturation(0);
    }

    /**
     * Returns a new color that is the inverse of this color where
     * each rgb component is subtracted from 255.
     */
    public RgbaColor inverse() {
        return new RgbaColor(255 - r, 255 - g, 255 - b, a);
    }

    // ----------------------------------------------------------------------
    // color transforms

    /**
     * Returns a new color that has the hue adjusted by the specified
     * amount.
     */
    public RgbaColor adjustHue(float degrees) {
        float[] HSL = convertToHsl();
        HSL[0] = hueCheck(HSL[0] + degrees); // ensure [0-360)
        return RgbaColor.fromHsl(HSL);
    }

    /**
     * Returns a new color that has the alpha adjusted by the
     * specified amount.
     */
    public RgbaColor opacify(float amount) {
        return new RgbaColor(r, g, b, alphaCheck(a + amount));
    }

    /**
     * Returns a new color that has the alpha adjusted by the
     * specified amount. Equivalent to opacity(-amount).
     */
    public RgbaColor transparentize(float amount) {
        return opacify(-amount);
    }

    /**
     * Returns a new color that has the lightness adjusted by the
     * specified amount.
     */
    public RgbaColor lighten(float amount) {
        return adjustSL(2, amount);
    }

    /**
     * Returns a new color that has the lightness adjusted by the
     * negative of the specified amount. Equivalent to
     * lighten(-amount).
     */
    public RgbaColor darken(float amount) {
        return lighten(-amount);
    }

    /**
     * Returns a new color that has the saturation adjusted by the
     * specified amount.
     */
    public RgbaColor saturate(float amount) {
        return adjustSL(1, amount);
    }

    /**
     * Returns a new color that has the saturation adjusted by the
     * negative of the specified amount. Equivalent to
     * saturate(-amount).
     */
    public RgbaColor desaturate(float amount) {
        return saturate(-amount);
    }

    private RgbaColor adjustSL(int index, float amount) {
        float[] HSL = convertToHsl();
        HSL[index] = slCheck(HSL[index] + amount);
        return RgbaColor.fromHsl(HSL);
    }

    // ----------------------------------------------------------------------
    // palettes

    public RgbaColor[] getPaletteVaryLightness(int count) {
        // a max of 80 and an offset of 10 keeps us away from the
        // edges
        float[] spread = getSpreadInRange(l(), count, 80, 10);
        RgbaColor[] ret = new RgbaColor[count];
        for (int i = 0; i < count; i++) {
            ret[i] = withLightness(spread[i]);
        }
        return ret;
    }

    /**
     * Returns a spread of integers in a range [0,max) that includes
     * count. The spread is sorted from largest to smallest.
     */
    protected static final float[] getSpreadInRange(float member, int count, int max, int offset) {
        // to find the spread, we first find the min that is a
        // multiple of max/count away from the member

        int interval = max / count;
        float min = (member + offset) % interval;

        if (min == 0 && member == max) {
            min += interval;
        }

        float[] range = new float[count];
        for (int i = 0; i < count; i++) {
            range[i] = min + interval * i + offset;
        }

        return range;
    }

    // ----------------------------------------------------------------------
    // Contrast preserving transforms

    /**
     * First attempt to lighten the color and if that overflows the
     * lightness component, darken the color.
     */
    public RgbaColor lightenOrDarken(float lighten, float darken) {
        return adjustSL(2, lighten, -darken);
    }

    /**
     * First attempt to darken the color and if that overflows the
     * lightness component, lighten the color.
     */
    public RgbaColor darkenOrLighten(float darken, float lighten) {
        return adjustSL(2, -darken, lighten);
    }

    /**
     * First attempt to saturate the color and if that overflows the
     * saturation component, desaturate the color.
     */
    public RgbaColor saturateOrDesaturate(float saturate, float desaturate) {
        return adjustSL(1, saturate, -desaturate);
    }

    /**
     * First attempt to desaturate the color and if that overflows the
     * saturation component, saturate the color.
     */
    public RgbaColor desaturateOrSaturate(float desaturate, float saturate) {
        return adjustSL(1, -desaturate, saturate);
    }

    /**
     * Takes two adjustments and applies the one that conforms to the
     * range. If the first modification moves the value out of range
     * [0-100], the second modification will be applied <b>and clipped
     * if necessary</b>.
     *
     * @param index
     *            The index in HSL
     * @param first
     *            The first modification that will be applied and
     *            bounds checked
     * @param second
     *            If 0, the first is always applied
     */
    private RgbaColor adjustSL(int index, float first, float second) {
        float[] HSL = convertToHsl();

        float firstvalue = HSL[index] + first;

        // check if it's in bounds
        if (slCheck(firstvalue) == firstvalue) {
            // it is, keep this transform
            HSL[index] = firstvalue;
        }
        else if (second == 0) {
            // just take the first one because there is no second, but
            // bounds check it.
            HSL[index] = slCheck(firstvalue);
        }
        else {
            // always take the second if the first exceeds bounds
            HSL[index] = slCheck(HSL[index] + second);
        }
        return RgbaColor.fromHsl(HSL);
    }

    // ----------------------------------------------------------------------
    // Conversion to/from HSL

    /**
     * Returns a triple of hue [0-360), saturation [0-100], and
     * lightness [0-100]. These are kept as float[] so that for any
     * RgbaColor color,
     * color.equals(RgbaColor.fromHsl(color.convertToHsl())) is true.
     *
     * <p>
     * <i>Implementation based on <a
     * href="http://en.wikipedia.org/wiki/HSL_and_HSV"
     * >wikipedia</a></i>
     */
    public float[] convertToHsl() {

        float H, S, L;

        // first normalize [0,1]
        float R = r / 255f;
        float G = g / 255f;
        float B = b / 255f;

        // compute min and max
        float M = max(R, G, B);
        float m = min(R, G, B);

        L = (M + m) / 2f;

        if (M == m) {
            // grey
            H = S = 0;
        }
        else {
            float diff = M - m;
            S = (L < 0.5) ? diff / (2f * L) : diff / (2f - 2f * L);

            if (M == R) {
                H = (G - B) / diff;
            }
            else if (M == G) {
                H = (B - R) / diff + 2f;
            }
            else {
                H = (R - G) / diff + 4f;
            }
            H *= 60;
        }

        H = hueCheck(H);
        S = slCheck(S * 100f);
        L = slCheck(L * 100f);

        return new float[] { H, S, L };
    }

    /**
     * Creates a new RgbaColor from the specified HSL components.
     *
     * <p>
     * <i>Implementation based on <a
     * href="http://en.wikipedia.org/wiki/HSL_and_HSV">wikipedia</a>
     * and <a
     * href="http://www.w3.org/TR/css3-color/#hsl-color">w3c</a></i>
     *
     * @param H
     *            Hue [0,360)
     * @param S
     *            Saturation [0,100]
     * @param L
     *            Lightness [0,100]
     *
     *
     */
    public static RgbaColor fromHsl(float H, float S, float L) {

        // convert to [0-1]
        H /= 360f;
        S /= 100f;
        L /= 100f;

        float R, G, B;

        if (S == 0) {
            // grey
            R = G = B = L;
        }
        else {
            float m2 = L <= 0.5 ? L * (S + 1f) : L + S - L * S;
            float m1 = 2f * L - m2;
            R = hue2rgb(m1, m2, H + 1 / 3f);
            G = hue2rgb(m1, m2, H);
            B = hue2rgb(m1, m2, H - 1 / 3f);
        }

        // convert [0-1] to [0-255]
        int r = Math.round(R * 255f);
        int g = Math.round(G * 255f);
        int b = Math.round(B * 255f);

        return new RgbaColor(r, g, b, 1);
    }

    /**
     * See http://www.w3.org/TR/css3-color/#hsl-color
     */
    private static float hue2rgb(float m1, float m2, float h) {
        if (h < 0) h += 1;
        if (h > 1) h -= 1;
        if (h * 6f < 1) return m1 + (m2 - m1) * 6f * h;
        if (h * 2f < 1) return m2;
        if (h * 3f < 2) return m1 + (m2 - m1) * (2 / 3f - h) * 6f;
        return m1;
    }

    // ----------------------------------------------------------------------
    // output formats

    /**
     * Returns the color in hexadecimal format (#rrggbb)
     */
    public String toHex() {
        String rs = hexpad(Integer.toHexString(r));
        String gs = hexpad(Integer.toHexString(g));
        String bs = hexpad(Integer.toHexString(b));
        return "#" + rs + gs + bs;
    }

    private static String hexpad(String s) {
        return (s.length() == 1) ? "0" + s : s;
    }

    /**
     * Returns the color in rgb format, e.g. rgb(r,g,b)
     */
    public String toRgb() {
        return "rgb(" + r + "," + g + "," + b + ")";
    }

    /**
     * Returns the color in rgba format, e.g. rgba(r,g,b,a)
     */
    public String toRgba() {
        return "rgba(" + r + "," + g + "," + b + "," + a + ")";
    }

    /**
     * Returns the CSS3 HSL representation, e.g. hsl(120, 100%, 50%)
     */
    public String toHsl() {
        float[] HSL = convertToHsl();
        return "hsl(" + Math.round(HSL[0]) + "," + Math.round(HSL[1]) + "%," + Math.round(HSL[2]) + "%)";
    }

    /**
     * Returns the CSS3 HSLA representation, e.g. hsl(120, 100%, 50%,
     * 0.5)
     */
    public String toHsla() {
        float[] HSL = convertToHsl();
        return "hsl(" + Math.round(HSL[0]) + "," + Math.round(HSL[1]) + "%," + Math.round(HSL[2]) + "%," + a + ")";
    }

    // ----------------------------------------------------------------------
    // be a good Java Object

    @Override
    public int hashCode() {
        return (int) (r * g * b * a);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RgbaColor)) return false;

        RgbaColor o = (RgbaColor) other;
        return (r == o.r && g == o.g && b == o.b && a == o.a);
    }

    @Override
    public String toString() {
        return toHex();
    }

    // ----------------------------------------------------------------------
    // bounds checking utility methods

    protected static int rgbCheck(int rgb) {
        return boundsCheck(rgb, 0, 255);
    }

    protected static float hueCheck(float hue) {
        return ((hue % 360) + 360) % 360;
    }

    protected static float slCheck(float sl) {
        return boundsCheck(sl, 0, 100);
    }

    protected static float alphaCheck(float alpha) {
        return boundsCheck(alpha, 0f, 1f);
    }

    private static int boundsCheck(int value, int lower, int upper) {
        if (value < lower) {
            return lower;
        }
        if (value > upper) {
            return upper;
        }
        return value;
    }

    private static float boundsCheck(float value, float lower, float upper) {
        if (value < lower) {
            return lower;
        }
        if (value > upper) {
            return upper;
        }
        return value;
    }

    // ----------------------------------------------------------------------
    // misc utility methods

    private float max(float x, float y, float z) {
        if (x > y) {
            // not y
            if (x > z) {
                return x;
            }
            else {
                return z;
            }
        }
        else {
            // not x
            if (y > z) {
                return y;
            }
            else {
                return z;
            }
        }
    }

    private float min(float x, float y, float z) {
        if (x < y) {
            // not y
            if (x < z) {
                return x;
            }
            else {
                return z;
            }
        }
        else {
            // not x
            if (y < z) {
                return y;
            }
            else {
                return z;
            }
        }
    }

    private static int parseHex(String hex, int s, int e) {
        int v = parseInt(hex.substring(s, e), 16);

        // handle single character parsing (e.g. #abc = #aabbcc)
        return (s + 1 == e) ? 16 * v + v : v;
    }

    private static int parseInt(String val) {
        return parseInt(val, 10);
    }

    // ----------------------------------------------------------------------
    //
    // Everything above this line is copied between the server/client
    // versions. Originally they shared the implementation but the
    // static factory methods and copy mutator methods made that too
    // difficult.
    //
    // ----------------------------------------------------------------------

    /**
     * The GWT version of this class provides a default.
     *
     * @return new RgbaColor(255,255,255)
     */
    private static RgbaColor getDefaultColor() {
        return new RgbaColor(255,255,255);
    }

    // ----------------------------------------------------------------------
    // Platform-specific utility methods used for parsing
    //

    private static native String getRgbParts(String str)
    /*-{
      var re = new RegExp("rgb\\s*\\(\\s*([0-9]+).*,\\s*([0-9]+).*,\\s*([0-9]+).*\\)", "gi");
      return str.replace(re, "$1,$2,$3");
    }-*/;

    private static native String getRgbaParts(String str)
    /*-{
      var re = new RegExp("rgba\\s*\\(\\s*([0-9]+).*,\\s*([0-9]+).*,\\s*([0-9]+).*,\\s*([0-9]*\\.?[0-9]+).*\\)", "gi");
      return str.replace(re, "$1,$2,$3,$4");
    }-*/;

    private static native String getHslParts(String str)
    /*-{
      var re = new RegExp("hsl\\s*\\(\\s*([0-9]+).*,\\s*([0-9]+).*,\\s*([0-9]+).*\\)", "gi");
      return str.replace(re, "$1,$2,$3");
    }-*/;

    private static native String getHslaParts(String str)
    /*-{
      var re = new RegExp("hsla\\s*\\(\\s*([0-9]+).*,\\s*([0-9]+).*,\\s*([0-9]+).*,\\s*([0-9]*\\.?[0-9]+).*\\)", "gi");
      return str.replace(re, "$1,$2,$3,$4");
    }-*/;

    private static native int parseInt(String val, int radix)
    /*-{
      return parseInt(val, radix) || 0;
      }-*/;

    private static native float parseFloat(String val)
    /*-{
      return parseFloat(val, 10) || 0;
      }-*/;

}
