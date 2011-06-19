package info.photoorganizer.gui.components.frame;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class PODialogTheme
{
    private static final String DEFAULT_FOREGROUND_KEY = "text";
    private static final String DEFAULT_ACCENT_KEY = "nimbusBase";
    private static final String DEFAULT_BACKGROUND_KEY = "control";
    
    public static final PODialogTheme LIGHT_GREY = new PODialogTheme("Light Grey", new Color(200,200,200), new Color(50,50,50), new Color(150,150,150));
    public static final PODialogTheme DARK_GREY = new PODialogTheme("Dark Grey", new Color(50,50,50), new Color(150,150,150), new Color(150,150,150));
    
    private static Map<String, Color> _defaultColors = new HashMap<String, Color>();
    
    private static final String[] CFG_ACCENT_KEYS = {
        DEFAULT_ACCENT_KEY,   
        "nimbusSelectedText",   
        "nimbusSelectionBackground",    
        "nimbusSelection",      
        "desktop",
        "textBackground",   
        "textHighlight",
        
        "Tree.dropLineColor",
        "Tree.selectionBackground",
    };
    private static final String[] CFG_BACKGROUND_KEYS = {
        
        DEFAULT_BACKGROUND_KEY,      
        "nimbusLightBackground",    
        "activeCaption",    
        "background",   
        "controlDkShadow",      
        "controlHighlight",     
        "controlLHighlight",    
        "controlShadow",    
        "inactiveCaption",     
        "menu",     
        "nimbusBlueGrey",   
        "nimbusBorder",     
        "scrollbar",
        
        "FileChooser.background",
        "FileChooser.disabled",
        "DesktopIcon.background",
        "DesktopIcon.disabled",
        "RootPane.background",
        "RootPane.disabled",
        "TextPane.background",
        "TextPane.disabled",
        "DesktopPane.background",
        "DesktopPane.disabled",
        "Menu.background",
        "Menu.disabled",
        "Button.background",
        "Button.disabled",
        "Panel.background",
        "Panel.disabled",
        "MenuBar.background",
        "MenuBar.disabled",
        "Tree.background",
        "Tree.disabled",
        "Tree.textBackground",
        "Tree:TreeCell[Enabled+Focused].background",
        "TabbedPane.background",
        "TabbedPane.disabled",
        "TabbedPane.highlight",
        "TabbedPane.shadow",
        "PopupMenu.background",
        "PopupMenu.disabled",
        "ScrollPane.background",
        "ScrollPane.disabled",
        "ScrollPane.foreground"
    };
    private static final String[] CFG_FOREGROUND_KEYS = {
        DEFAULT_FOREGROUND_KEY,     
        "nimbusDisabledText",   
        "controlText",     
        "infoText", 
        "textForeground",   
        "textHighlightText",   
        "textInactiveText",  
        "menuText",
        
        "FileChooser.disabledText",
        "FileChooser.foreground",
        "DesktopIcon.disabledText",
        "DesktopIcon.foreground",
        "RootPane.disabledText",
        "RootPane.foreground",
        "TextPane.disabledText",
        "TextPane.foreground",
        "TextPane[Disabled].textForeground",
        "TextPane[Selected].textForeground",
        "DesktopPane.disabledText",
        "DesktopPane.foreground",
        "Menu.disabledText",
        "Menu.foreground",
        "Menu[Disabled].textForeground",
        "Menu[Enabled+Selected].textForeground",
        "Menu[Enabled].textForeground",
        "Button.disabledText",
        "Button.foreground",
        "Button[Disabled].textForeground",
        "Panel.disabledText",
        "Panel.foreground",
        "MenuBar.disabledText",
        "MenuBar.foreground",
        "MenuBar:Menu[Disabled].textForeground",
        "MenuBar:Menu[Enabled].textForeground",
        "MenuBar:Menu[Selected].textForeground",
        "Tree.disabledText",
        "Tree.foreground",
        "Tree.selectionForeground",
        "Tree.textForeground",
        "Tree:\"Tree.cellRenderer\"[Disabled].textForeground",
        "Tree:TreeCell[Enabled+Selected].textForeground",
        "Tree:TreeCell[Enabled].background",
        "Tree:TreeCell[Focused+Selected].textForeground",
        "TabbedPane.darkShadow",
        "TabbedPane.disabledText",
        "TabbedPane.foreground",
        "TabbedPane:TabbedPaneTab[Disabled].textForeground",
        "TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].textForeground",
        "TabbedPane:TabbedPaneTab[Pressed+Selected].textForeground",
        "PopupMenu.disabledText",
        "PopupMenu.foreground",
        "ScrollPane.disabledText"
    };
    private static final String[] CFG_MISC_KEYS = {
        "info",     
        "nimbusAlertYellow",    
        "nimbusFocus",      
        "nimbusGreen",      
        "nimbusInfoBlue",   
        "nimbusOrange",     
        "nimbusRed"
    };
    
    private static final String THEME_NAME = "Nimbus";
    
    static 
    {
        init();
    }
    
    public static void applyTheme(PODialogTheme theme)
    {
        for (String key : CFG_BACKGROUND_KEYS)
        {
            applyThemeColor(key, theme.getBackgroundBase(), getBrightnessDifference(_defaultColors.get(DEFAULT_BACKGROUND_KEY), theme.getBackgroundBase()));
        }
        for (String key : CFG_FOREGROUND_KEYS)
        {
            applyThemeColor(key, theme.getForegroundBase(), getBrightnessDifference(_defaultColors.get(DEFAULT_FOREGROUND_KEY), theme.getForegroundBase()));
        }
        for (String key : CFG_ACCENT_KEYS)
        {
            applyThemeColor(key, theme.getAccentBase(), getBrightnessDifference(_defaultColors.get(DEFAULT_ACCENT_KEY), theme.getAccentBase()));
        }
    }

    private static float getBrightnessDifference(Color color1, Color color2)
    {
        float[] defaultBackgroundComponents = new float[3];
        Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), defaultBackgroundComponents);

        float[] themeBackgroundComponents = new float[3];
        Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), themeBackgroundComponents);
        
//            float hue = themeBaseColorComponents[0];
//            float saturation = themeBaseColorComponents[1];
        float brightnessDiff = defaultBackgroundComponents[2] - themeBackgroundComponents[2];
        
        return brightnessDiff;
    }

    private static void applyThemeColor(String key, Color themeBaseColor, float brightnessDiff)
    {
        Color defaultColor = _defaultColors.get(key);
        float[] defaultColorComponents = new float[3];
        Color.RGBtoHSB(defaultColor.getRed(), defaultColor.getGreen(), defaultColor.getBlue(), defaultColorComponents);

        float[] themeBaseColorComponents = new float[3];
        Color.RGBtoHSB(themeBaseColor.getRed(), themeBaseColor.getGreen(), themeBaseColor.getBlue(), themeBaseColorComponents);
        
        float hue = themeBaseColorComponents[0];
        float saturation = themeBaseColorComponents[1];
        float brightness = defaultColorComponents[2] - brightnessDiff;
        System.out.format("Color %s: S=%f B=%f\n", key, saturation, brightness);
        
        Color themeColor = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        UIManager.put(key, themeColor);
    }
    
    public static void init()
    {
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (THEME_NAME.equals(info.getName())) {
                try
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    initNimbusColors();
                }
                catch (ClassNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (InstantiationException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (UnsupportedLookAndFeelException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private static void initDefaultColor(String key)
    {
        Object prop = UIManager.get(key);
        if (prop instanceof Color)
        {
            Color colorProp = (Color) prop;
            _defaultColors.put(key, colorProp);
        }
    }
    
    private static void initDefaultColors()
    {
        for (String key : CFG_BACKGROUND_KEYS)
        {
            initDefaultColor(key);
        }
        for (String key : CFG_FOREGROUND_KEYS)
        {
            initDefaultColor(key);
        }
        for (String key : CFG_ACCENT_KEYS)
        {
            initDefaultColor(key);
        }
    }
    
    private static void initNimbusColors()
    {
        initDefaultColors();
//        UIManager.put("nimbusBase", new Color(130,130,130));
//        UIManager.put("nimbusBlueGrey", new Color(200,200,200));
//        UIManager.put("control", new Color(100,100,100));
    }

    private Color _accentBase = null;

    private Color _backgroundBase = null;

    private Color _foregroundBase = null;
    
    private String _name = null;

    public PODialogTheme(String name, Color backgroundBase, Color foregroundBase, Color accentBase)
    {
        super();
        _name = name;
        _backgroundBase = backgroundBase;
        _foregroundBase = foregroundBase;
        _accentBase = accentBase;
    }

    public Color getAccentBase()
    {
        return _accentBase;
    }

    public Color getBackgroundBase()
    {
        return _backgroundBase;
    }

    public Color getForegroundBase()
    {
        return _foregroundBase;
    }

    public String getName()
    {
        return _name;
    }
}
