package info.photoorganizer.gui.components.frame;

import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.POAction;
import info.photoorganizer.gui.components.POCardLayout;
import info.photoorganizer.gui.components.POWrappedLabel;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class POGuideDialog extends PODialog
{
    private enum ActionName
    {
        NEXT_PAGE,
        PREVIOUS_PAGE,
        OK,
        CANCEL
    }
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel buttonsPanel;

    public POGuideDialog(Dialog owner, String title, CloseOperation defaultCloseOperation)
    {
        super(owner, title, defaultCloseOperation, GuiComponentFactory.createBorderLayoutPanel());
//        initActions();
//        initComponents();
    }
    
    {
        initActions();
    }
    
    public POGuideDialog(Frame owner, String title, CloseOperation defaultCloseOperation)
    {
        super(owner, title, defaultCloseOperation, GuiComponentFactory.createBorderLayoutPanel());
//        initActions();
//        initComponents();
    }

    public POGuideDialog(Frame owner, String title)
    {
        super(owner, title, CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBorderLayoutPanel());
//        initActions();
//        initComponents();
    }
    
    public POGuideDialog(Dialog owner, String title)
    {
        super(owner, title, CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBorderLayoutPanel());
//        initActions();
//        initComponents();
    }
    
    protected abstract POGuidePage[] getPagesImpl();
    
    private POGuidePage[] _pages = null;
    
    private POGuidePage[] getPages() 
    {
        if (null == _pages)
        {
            _pages = getPagesImpl();
        }
        return _pages;
    }
    
    private JPanel _pagePanel = null;
    private POCardLayout _cardLayout = null;
    private JLabel _pageHeader = null;
    
    private JLabel getPageHeader()
    {
        if (null == _pageHeader)
        {
            _pageHeader = new JLabel();
            Font defaultFont = _pageHeader.getFont();
            _pageHeader.setFont(new Font(defaultFont.getName(), defaultFont.getStyle(), (int) (defaultFont.getSize() * 1.5)));
        }
        return _pageHeader;
    }
    private POWrappedLabel _pageDescription = null;
    
    private POWrappedLabel getPageDescription()
    {
        if (null == _pageDescription)
        {
//            _pageDescription = new JTextArea();
//            _pageDescription.setEditable(false);
//            _pageDescription.setLineWrap(true);
//            _pageDescription.setWrapStyleWord(true);
//            _pageDescription.setBackground(getPagePanel().getBackground());
            _pageDescription = new POWrappedLabel(new POWrappedLabel.LineLengthWrapper(80));
        }
        return _pageDescription;
    }

    private POCardLayout getCardLayout()
    {
        if (null == _cardLayout)
        {
            _cardLayout = new POCardLayout();
        }
        return _cardLayout;
    }
    
    private String getPageHeaderText(POGuidePage panel)
    {
        int i=0;
        POGuidePage[] pages = getPages();
        for(;i < pages.length && pages[i] != panel;i++);
        return getI18nText("PAGE_HEADER", i+1, pages.length, panel.getName());
    }

    private JPanel getPagePanel()
    {
        if (null == _pagePanel)
        {
            _pagePanel = new JPanel(getCardLayout());
            int i=1;
            for (POGuidePage panel : getPages())
            {
                panel.initComponents();
                panel.addComponentListener(new ComponentListener()
                {

                    @Override
                    public void componentShown(ComponentEvent e)
                    {
                        POGuidePage panel = (POGuidePage) e.getComponent();
//                        _cardLayout.getCurrentCard();
                        getPageHeader().setText(getPageHeaderText(panel));
                        getPageDescription().setText(panel.getDescription());
                        pack();
                    }

                    @Override
                    public void componentResized(ComponentEvent e)
                    {
                    }

                    @Override
                    public void componentMoved(ComponentEvent e)
                    {
                    }

                    @Override
                    public void componentHidden(ComponentEvent e)
                    {
                    }
                });
                _pagePanel.add(panel, "page" + i++);
            }
            _pagePanel.addContainerListener(new ContainerListener()
            {
                
                @Override
                public void componentRemoved(ContainerEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void componentAdded(ContainerEvent e)
                {
                    e.getComponent();
                }
            });
            getCardLayout().first(_pagePanel);
        }
        return _pagePanel;
    }

    protected void initComponents()
    {
        add(getButtons(), BorderLayout.SOUTH);
        add(getPagePanel(), BorderLayout.CENTER);
        add(GuiComponentFactory.createBoxLayoutPanel(false, getPageHeader(), getPageDescription()),
                BorderLayout.NORTH);
    }

    private void initActions()
    {
        getActionMap().put(ActionName.CANCEL, new POAction(getI18nText(ActionName.CANCEL.name()))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose(POCloseReason.CANCEL);
            }
        });
        getActionMap().put(ActionName.OK, new POAction(getI18nText(ActionName.OK.name()))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                POGuidePage currentPage = (POGuidePage) getCardLayout().getCurrentCard();
                if (currentPage.onOK())
                {
                    dispose(POCloseReason.OK);
                }
            }
        });
        getActionMap().put(ActionName.NEXT_PAGE, new POAction(getI18nText(ActionName.NEXT_PAGE.name()))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                POGuidePage currentPage = (POGuidePage) getCardLayout().getCurrentCard();
                if (currentPage.onOK())
                {
                    getCardLayout().next(getPagePanel());
                }
            }
        });
        getActionMap().put(ActionName.PREVIOUS_PAGE, new POAction(getI18nText(ActionName.PREVIOUS_PAGE.name()))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                POGuidePage currentPage = (POGuidePage) getCardLayout().getCurrentCard();
                if (currentPage.onOK())
                {
                    getCardLayout().previous(getPagePanel());
                }
            }
        });
    }
    
    private JPanel getButtons()
    {
        if (null == buttonsPanel)
        {
            buttonsPanel = GuiComponentFactory.createFlowLayoutPanel(
                    FlowLayoutAlignment.RIGHT, 
                    GuiComponentFactory.createButton(getActionMap().get(ActionName.PREVIOUS_PAGE)),
                    GuiComponentFactory.createButton(getActionMap().get(ActionName.NEXT_PAGE)),
                    GuiComponentFactory.createButton(getActionMap().get(ActionName.CANCEL)),
                    GuiComponentFactory.createButton(getActionMap().get(ActionName.OK))
                    );
        }
        return buttonsPanel;
    }
    
}
