package enviromine.utils;

import enviromine.client.gui.hud.HUDRegistry;

/**
 * 
 * Use Alignment to define the HudElement's "anchor"<br>
 * as in which side of the screen it moves with.
 * 
 * @author maxpowa
 * 
 */
public enum Alignment {
    TOPLEFT, TOPCENTER, TOPRIGHT, CENTERLEFT, CENTERCENTER, CENTERRIGHT, BOTTOMLEFT, BOTTOMCENTER, BOTTOMRIGHT;

    public static Alignment fromString(String string) {
        if (string.equals("TOPLEFT"))
            return TOPLEFT;
        if (string.equals("TOPCENTER"))
            return TOPCENTER;
        if (string.equals("TOPRIGHT"))
            return TOPRIGHT;
        if (string.equals("CENTERLEFT"))
            return CENTERLEFT;
        if (string.equals("CENTERCENTER"))
            return CENTERCENTER;
        if (string.equals("CENTERRIGHT"))
            return CENTERRIGHT;
        if (string.equals("BOTTOMLEFT"))
            return BOTTOMLEFT;
        if (string.equals("BOTTOMCENTER"))
            return BOTTOMCENTER;
        if (string.equals("BOTTOMRIGHT"))
            return BOTTOMRIGHT;

        return CENTERCENTER;
    }

    public static Alignment calculateAlignment(int x, int y) 
    {
        return calculateAlignment(x, y, HUDRegistry.screenWidth, HUDRegistry.screenHeight);
    }

    public static Alignment calculateAlignment(int x, int y, int screenWidth, int screenHeight) 
    {
        x = (int) (3.0F / screenWidth * x);
        y = (int) (3.0F / screenHeight * y);

        if (x == 0 && y == 0)
            return TOPLEFT;
        if (x == 1 && y == 0)
            return TOPCENTER;
        if (x == 2 && y == 0)
            return TOPRIGHT;
        if (x == 0 && y == 1)
            return CENTERLEFT;
        if (x == 1 && y == 1)
            return CENTERCENTER;
        if (x == 2 && y == 1)
            return CENTERRIGHT;
        if (x == 0 && y == 2)
            return BOTTOMLEFT;
        if (x == 1 && y == 2)
            return BOTTOMCENTER;
        if (x == 2 && y == 2)
            return BOTTOMRIGHT;

        return CENTERCENTER;
    }

    public static boolean isTop(Alignment alignment) 
    {
        return alignment == TOPLEFT || alignment == TOPCENTER || alignment == TOPRIGHT;
    }

    public static boolean isVerticalCenter(Alignment alignment) 
    {
        return alignment == CENTERLEFT || alignment == CENTERCENTER || alignment == CENTERRIGHT;
    }

    public static boolean isBottom(Alignment alignment) 
    {
        return alignment == BOTTOMLEFT || alignment == BOTTOMCENTER || alignment == BOTTOMRIGHT;
    }

    public static boolean isLeft(Alignment alignment) 
    {
        return alignment == TOPLEFT || alignment == CENTERLEFT || alignment == BOTTOMLEFT;
    }

    public static boolean isHorizontalCenter(Alignment alignment) 
    {
        return alignment == TOPCENTER || alignment == CENTERCENTER || alignment == BOTTOMCENTER;
    }

    public static boolean isRight(Alignment alignment) 
    {
        return alignment == TOPRIGHT || alignment == CENTERRIGHT || alignment == BOTTOMRIGHT;
    }
}