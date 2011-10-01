package info.photoorganizer.gui;

import info.photoorganizer.gui.components.PORadioButton;
import info.photoorganizer.gui.components.frame.POUserInterfaceTheme;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.tagfield.POTagFieldSuggestionProvider;
import info.photoorganizer.gui.components.view.ViewLayout;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.gui.shared.POActionListenerEvent;
import info.photoorganizer.gui.shared.POActionListenerListener;
import info.photoorganizer.gui.shared.SpringUtilities;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.util.StringUtils;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GuiComponentFactory
{

    public static void addComponentsToContainer(Container panel, Component... components)
    {
        if (components != null)
        {
            for (Component c : components)
            {
                panel.add(c);
            }
        }
    }

    public static JPanel createBorderLayoutPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        return panel;
    }
    
    /*
    public static <T extends Enum<?>> JPanel createEnumCardLayout(final Class<T> cls, T initialValue)
    {
        final CardLayout layout = new CardLayout();
        final JPanel cards = new JPanel(layout);
        JPanel panel = createBoxLayoutPanel(false, createEnumDropDownList(cls, initialValue, new POActionListener()
        {
            
            @Override
            public void actionPerformedImpl(ActionEvent event)
            {
                layout.show(cards, event.getActionCommand());
                for (T e : cls.getEnumConstants())
                {
                    if (e.name().equals(event.getActionCommand()))
                    {
                        
                    }
                }
                
            }
        }));
        return panel;
    }
    */
    public static JPanel createViewLayoutPanel()
    {
        JPanel panel = new JPanel(new ViewLayout());
        return panel;
    }

    public static JPanel createBoxLayoutPanel(boolean horizontal, JComponent... components)
    {
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, horizontal ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        
        for (JComponent component : components)
        {
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        
        addComponentsToContainer(panel, components);
        
        return panel;
    }

    public static JPanel createFlowLayoutPanel(FlowLayoutAlignment align, Component... components)
    {
        JPanel panel = new JPanel(new FlowLayout(align.getValue()));
        
        addComponentsToContainer(panel, components);
        
        return panel;
    }

    public static JPanel createSpringLayoutPanel(int rows, int cols, Component... components)
    {
        JPanel panel = new JPanel();
        initSpringLayoutPanel(panel, rows, cols, components);        
        return panel;
    }
    
    public static void initSpringLayoutPanel(JPanel panel, int rows, int cols, Component... components)
    {
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        
        if (rows * cols == components.length)
        {
            addComponentsToContainer(panel, components);
            SpringUtilities.makeCompactGrid(panel, rows, cols, 5, 0, 5, 0);
        }
    }

    public static JPanel createUserOptionsPanel(Component... components)
    {
        JPanel panel = new JPanel();
        initUserOptionsPanel(panel, components);
        return panel;
    }
    
    public static void initUserOptionsPanel(JPanel panel, Component... components)
    {
        int cols = 2; 
        int rows = components.length / cols;
        initSpringLayoutPanel(panel, rows, cols, components);
    }

    public static JButton createButton(String text, POActionListener actionListener)
    {
        final JButton button = new JButton(text);
        if (null != actionListener)
        {
            button.addActionListener(actionListener);
            actionListener.addListener(new POActionListenerListener()
            {
                @Override
                public void actionListenerEnabledChange(POActionListenerEvent event)
                {
                    button.setEnabled(event.getSource().isEnabled());
                }
            });
            button.setEnabled(actionListener.isEnabled());
        }
        return button;
    }
    
    public static JButton createButton(Action action)
    {
        final JButton button = new JButton(action);
        return button;
    }

    public static JLabel createLabel(String text)
    {
        JLabel l = new JLabel(text);
        return l;
    }

    public static <T extends Enum<?>> ButtonGroup createEnumRadioButtonGroup(Class<T> cls, POActionListener actionListener)
    {
        ButtonGroup group = new ButtonGroup();
        for (T e : cls.getEnumConstants())
        {
            JRadioButton button = new JRadioButton(e.toString());
            if (null != actionListener)
            {
                button.addActionListener(actionListener);
            }
            button.setActionCommand(e.name());
            group.add(button);
        }
        return group;
    }
    
    public static <T extends Enum<?>> JComboBox createEnumDropDownList(Class<T> cls, T initialValue, final POActionListener actionListener)
    {
        JComboBox list = new JComboBox(cls.getEnumConstants());
        if (null != actionListener)
        {
            list.addItemListener(new ItemListener()
            {
                
                @Override
                public void itemStateChanged(ItemEvent event)
                {
                    T e = (T) event.getItem();
                    actionListener.actionPerformed(new ActionEvent(event.getSource(), event.getID(), e.name()));
                }
            });
        }
        if (null != initialValue)
        {
            list.setSelectedItem(initialValue);
        }
        return list;
    }
    
    public static AbstractButton[] createButtonArray(ButtonGroup buttons)
    {
        AbstractButton[] res = new AbstractButton[buttons.getButtonCount()];
        int i=0;
        Enumeration<AbstractButton> enumeration = buttons.getElements();
        while (enumeration.hasMoreElements())
        {
            res[i++] = enumeration.nextElement();
        }
        return res;
    }

    public static JPanel createAccordion(Map<Object, JPanel> choices)
    {
        ButtonGroup buttonGroup = new ButtonGroup();
    
        JComponent[] components = new JComponent[choices.size() * 2];
        int i = 0;
        EmptyBorder panelBorder = null;
        for (Entry<Object, JPanel> entry : choices.entrySet())
        {
            Object labelObject = entry.getKey();
            JPanel panel = entry.getValue();
            
            PORadioButton button = new PORadioButton(labelObject.toString());
            button.setValue(labelObject);
            button.setAlignmentX(0);
            button.addChangeListener(GuiComponentFactory._accordionRadioButton_onStateChanged);
            button.putClientProperty(GuiComponentFactory.ACCORDION_PANEL, panel);
            
            if (null == panelBorder)
            {
                panelBorder = new EmptyBorder(0, (int) (1.3 * button.getPreferredSize().getHeight()), 0, 0);
            }
            panel.setAlignmentX(0);
            panel.setBorder(panelBorder);
            
            components[i++] = button;
            components[i++] = panel;
            
            buttonGroup.add(button);
        }
        
        JPanel panel = createBoxLayoutPanel(false, components);
        
        return panel;
    }

    public static JTextField createTextField(String initialText)
    {
        JTextField field = new JTextField(initialText);
        return field;
    }

    private static final String ACCORDION_PANEL = "PODialogAccordionItem";
    private static ChangeListener _accordionRadioButton_onStateChanged = new ChangeListener()
    {
    
        @Override
        public void stateChanged(ChangeEvent e)
        {
            JRadioButton source = (JRadioButton) e.getSource();
            JPanel panel = (JPanel) source.getClientProperty(ACCORDION_PANEL);
            panel.setEnabled(source.isSelected());
        }
    };
    public static void initDefaultLookAndFeel()
    {
        if (!lookAndFeelInitialised)
        {
//            try
//            {
//                UIManager.setLookAndFeel("com.easynth.lookandfeel.EaSynthLookAndFeel");
//            }
//            catch (ClassNotFoundException e)
//            {
//                e.printStackTrace();
//            }
//            catch (InstantiationException e)
//            {
//                e.printStackTrace();
//            }
//            catch (IllegalAccessException e)
//            {
//                e.printStackTrace();
//            }
//            catch (UnsupportedLookAndFeelException e)
//            {
//                e.printStackTrace();
//            }
            POUserInterfaceTheme.init();
            POUserInterfaceTheme.applyTheme(POUserInterfaceTheme.LIGHT_GREY);
            lookAndFeelInitialised = true;
        }
    }
    
    public static boolean lookAndFeelInitialised = false;

    public static void show(final Window frame)
    {
        show(frame, true);
    }
    
    public static void show(final Window frame, final boolean pack)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            
            @Override
            public void run()
            {
                if (pack)
                {
                    frame.pack();
                }
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public static void showModalDialog(final Dialog dialog)
    {
        showModalDialog(dialog, true);
    }
    
    public static void showModalDialog(final Dialog dialog, final boolean pack)
    {
        Runnable runnable = new Runnable()
        {
            
            @Override
            public void run()
            {
                if (pack)
                {
                    dialog.pack();
                }
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        };
        
        if (SwingUtilities.isEventDispatchThread())
        {
            runnable.run();
        }
        else
        {
            try
            {
                SwingUtilities.invokeAndWait(runnable);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static POTagField<KeywordTagDefinition> createTagField(KeywordTagDefinition[] tags, int fieldWidth, POTagFieldSuggestionProvider<KeywordTagDefinition> keywordWordprovider)
    {
        POTagField<KeywordTagDefinition> component = new POTagField<KeywordTagDefinition>(
                StringUtils.join(tags, KeywordTagDefinition.DEFAULT_KEYWORD_SEPARATOR, true, KeywordTagDefinition.DEFAULT_KEYWORD_QUOTATION_MARK), 
                fieldWidth);
        component.setQuotationCharacter(KeywordTagDefinition.DEFAULT_KEYWORD_QUOTATION_MARK);
        component.setWordSeparator(KeywordTagDefinition.DEFAULT_KEYWORD_SEPARATOR);
        component.setWordProvider(keywordWordprovider);
        return component;
    }

}
