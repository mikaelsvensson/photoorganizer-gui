package info.photoorganizer.gui.appconfig.autoindexing;

import info.photoorganizer.database.autoindexing.AllFilesFileFilter;
import info.photoorganizer.database.autoindexing.IndexingConfigurationInterface;
import info.photoorganizer.database.autoindexing.POFileFilter;
import info.photoorganizer.database.autoindexing.RegexpFileFilter;
import info.photoorganizer.database.autoindexing.RejectedFileExtensionFileFilter;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.components.POTwoLevelChoice;
import info.photoorganizer.gui.shared.POSimpleDocumentListener;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.StringUtils;

import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

public class FileFilterPage extends Page
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public FileFilterPage(IndexingConfigurationInterface cfg)
    {
        super();
        this.cfg = cfg; 
    }
    
    private POTwoLevelChoice<FileFilterOption, JPanel> _options = null;
    private IndexingConfigurationInterface cfg = null;

    @Override
    protected void initComponents()
    {
        LinkedHashMap<FileFilterOption, JPanel> choices = new LinkedHashMap<FileFilterOption, JPanel>();
        for (FileFilterOption filterOption : FileFilterOption.values())
        {
            POFileFilter filter = null; 
            if (filterOption.handlesFileFilter(cfg.getFileFilter()))
            {
                filter = cfg.getFileFilter();
            }
            else
            {
                filter = filterOption.createFilter();
            }
            JPanel filterPanel = filterOption.createUI(filter);
            filterPanel.putClientProperty("FILTER_MODEL", filter);
            choices.put(filterOption, filterPanel);
        }
        _options = new POTwoLevelChoice<FileFilterOption, JPanel>(choices);
        _options.setSelected(FileFilterOption.valueOf(cfg.getFileFilter()));
        add(_options);
    }

    @Override
    protected boolean onOK()
    {
        POFileFilter item = (POFileFilter)_options.getSelectedChoiceValue().getClientProperty("FILTER_MODEL");
        cfg.setFileFilter(item);
        return true;
    }

}

enum FileFilterOption
    {
        NO_FILTER
        {
            @Override
            public POFileFilter createFilter()
            {
                return new AllFilesFileFilter();
            }
            
            @Override
            protected JPanel createUI(POFileFilter filter)
            {
                return new JPanel();
            }

            @Override
            public boolean handlesFileFilter(POFileFilter filter)
            {
                return filter instanceof AllFilesFileFilter;
            }
        },
        REGEXP
        {
            
            @Override
            public POFileFilter createFilter()
            {
                return new RegexpFileFilter();
            }

            @Override
            protected JPanel createUI(POFileFilter filter)
            {
                String filterText = "";
                JTextField textField = GuiComponentFactory.createTextField(filterText);
                if (handlesFileFilter(filter))
                {
                    final RegexpFileFilter regexpFilter = (RegexpFileFilter) filter;
                    filterText = regexpFilter.getParam();
                    textField.setText(filterText);
                    textField.getDocument().addDocumentListener(new POSimpleDocumentListener()
                    {
                        @Override
                        public void update(DocumentEvent e, String text)
                        {
                            regexpFilter.setParam(text);
                        }
                    });
                }
                JPanel panel = GuiComponentFactory.createUserOptionsPanel(
                        GuiComponentFactory.createLabel(getI18nString("EXPRESSION_LABEL")),
                        textField
                        );
                return panel;
            }

            @Override
            public boolean handlesFileFilter(POFileFilter filter)
            {
                return filter instanceof RegexpFileFilter;
            }
        },
        REJECTED_EXTENSIONS
        {
            @Override
            public POFileFilter createFilter()
            {
                return new RejectedFileExtensionFileFilter();
//                filter.setRejectedFileTypes(textField.getText().split(EXTENSION_SEPARATOR));
//                return filter;
            }

            @Override
            protected JPanel createUI(POFileFilter filter)
            {
                String extensionsText = "";
                JTextField textField = GuiComponentFactory.createTextField("");
                if (handlesFileFilter(filter))
                {
                    final RejectedFileExtensionFileFilter rejectedExtensionFilter = (RejectedFileExtensionFileFilter) filter;
                    extensionsText = StringUtils.join(rejectedExtensionFilter.getRejectedFileTypes().iterator(), EXTENSION_SEPARATOR);
                    textField.setText(extensionsText);
                    textField.getDocument().addDocumentListener(new POSimpleDocumentListener()
                    {
                        @Override
                        public void update(DocumentEvent e, String text)
                        {
                            rejectedExtensionFilter.setRejectedFileTypes(text.split(EXTENSION_SEPARATOR));
                        }
                    });
                }
                JPanel panel = GuiComponentFactory.createUserOptionsPanel(
                        GuiComponentFactory.createLabel(getI18nString("EXTENSIONS_LABEL")),
                        textField
                        );
                return panel;
            }

            @Override
            public boolean handlesFileFilter(POFileFilter filter)
            {
                return filter instanceof RejectedFileExtensionFileFilter;
            }
        };

        private static final String EXTENSION_SEPARATOR = ",";

        private I18n i18n = I18n.getInstance();

        public abstract POFileFilter createFilter();

        protected abstract JPanel createUI(POFileFilter filter);
        
        public String getI18nString(String key, Object... parameters)
        {
            return i18n.getString(EditIndexingConfigurationDialog.class,
                    FileFilterOption.class.getSimpleName() + "." + key, parameters);
        }
        
        public abstract boolean handlesFileFilter(POFileFilter filter);

        @Override
        public String toString()
        {
            return getI18nString(name());
        }

        public static FileFilterOption valueOf(POFileFilter filter)
        {
            for (FileFilterOption f : values())
            {
                if (f.handlesFileFilter(filter))
                {
                    return f;
                }
            }
            return null;
        }
    }