package info.photoorganizer.gui.components.tagfield;

import info.photoorganizer.gui.components.thumblist.DefaultImageLoader;
import info.photoorganizer.gui.shared.KeyModifiers;
import info.photoorganizer.gui.shared.Keys;
import info.photoorganizer.util.StringUtils;
import info.photoorganizer.util.WordInfo;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

public class POTagField<T> extends JTextField implements DocumentListener, CaretListener, KeyListener, FocusListener, ListSelectionListener
{
    private static final Logger L = info.photoorganizer.util.Log.getLogger(POTagField.class);
    
    private class SuggestionItem
    {
        private T object = null;
        private String label = null;
        public T getObject()
        {
            return object;
        }
        public void setObject(T object)
        {
            this.object = object;
        }
        public String getLabel()
        {
            return label;
        }
        public void setLabel(String label)
        {
            this.label = label;
        }
        public SuggestionItem(T object, String label)
        {
            super();
            this.object = object;
            this.label = label;
        }
        @Override
        public String toString()
        {
            return label;
        }
    }
    
    private class ChooseSuggestion extends AbstractAction
    {
        
        private int _offset = 0;

        public ChooseSuggestion(int offset)
        {
            super();
            _offset = offset;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            int index = _suggestionlist.getSelectedIndex() + _offset;
            int listLength = _suggestionlist.getModel().getSize();
            if (index < 0)
            {
                index = listLength - 1;
            }
            else if (index >= listLength)
            {
                index = 0;
            }
            _suggestionlist.setSelectedIndex(index);
        }
        
    }
    
    private class AcceptAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                

                @Override
                public void run()
                {
                    String sentence = getText();
                    int position = getCaretPosition() - 1;
                    position = position >= 0 ? position : 0;
                    WordInfo wordInfo = getWord(position, false);
                    int startPos = wordInfo.getPositionOfFirstCharacter();// getWordStartPos(position, sentence);
                    int endPos = wordInfo.getPositionOfLastCharacter();// getWordEndPos(position, sentence);
                    if (validateWordPosition(startPos, endPos))
                    {
                        String replacementWord = e.getActionCommand();
                        if (null == replacementWord || replacementWord.equals(String.valueOf((char)Keys.ENTER.getKeyCode())))
                        {
                            //WordInfo wordInfo = getWord(position, false);
                            replacementWord = wordInfo.getWord();
                        }
                        int startOfReplacement = replaceWord(position, replacementWord, true);
                        
                        int quotationOffset = replacementWord.indexOf(_wordSeparator) >= 0 ? 1 : 0;
                        setCaretPosition(startOfReplacement + replacementWord.length() + 1 + quotationOffset);
                        
                        hideSuggestionMenu();
                    }
                }
            });
//            if (_mode == Mode.CHOOSING_SUGGESTION)
//            {
//                try
//                {
//                    getDocument().insertString(getSelectionEnd(), String.valueOf(_wordSeparator), null);
//                    setCaretPosition(getSelectionEnd() + 1);
//                }
//                catch (BadLocationException e1)
//                {
//                    e1.printStackTrace();
//                }
//            }
        }
        
    }
    private enum Mode
    {
        CHOOSING_SUGGESTION,
        TYPING
    }
    
    private static final String ACTION_ACCEPT = "accept";
    
    private static final String ACTION_NEXT = "next";

    private static final String ACTION_PREVIOUS = "previous";
    private JPopupMenu _menu = null;
    private Mode _mode = Mode.TYPING;
    
    private ActionListener _onMenuItem_actionPerformed = new ActionListener()
    {
        
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            getActionMap().get(ACTION_ACCEPT).actionPerformed(e);
        }
        
    };

    private char _quotationCharacter = '"';

    private JDialog _suggestionsFrame = null;

    private char _wordSeparator = ' ';

    POTagFieldSuggestionProvider<T> _wordProvider = null;

    private JList _suggestionlist;

    public POTagField()
    {
        super();
        init();
    }
    
    public POTagField(int columns)
    {
        super(columns);
        init();
    }
    
    public POTagField(String text)
    {
        super(text);
        init();
    }
    
    public POTagField(String text, int columns)
    {
        super(text, columns);
        init();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e)
    {
    }
    
    @Override
    public void focusGained(FocusEvent arg0)
    {
    }
    
    @Override
    public void focusLost(FocusEvent e)
    {
        JDialog suggestionsFrame = getSuggestionsFrame();
        if (e.getOppositeComponent() != suggestionsFrame && e.getOppositeComponent() != _suggestionlist)
//        if (e.getOppositeComponent() != getMenu())
        {
            hideSuggestionMenu();
            suggestionsFrame.dispose();
            suggestionsFrame = null;
        }
    }
    
    public char getQuotationCharacter()
    {
        return _quotationCharacter;
    }
    
    public WordInfo getWord(int caretPosition, boolean includeQuotes)
    {
        for (WordInfo wordInfo : getWords(includeQuotes))
        {
            if (caretPosition >= wordInfo.getPositionOfFirstCharacter() && caretPosition <= wordInfo.getPositionOfLastCharacter() + 1)
            {
                L.finer("When the caret is positioned right before character " + (caretPosition+1) + " it touches the tag '" + wordInfo.getWord() + "'.");
                return wordInfo;
            }
        }
        L.finer("When the caret is positioned right before character " + (caretPosition+1) + "it does not touch any tag.");
        return null;
    }
    public POTagFieldSuggestionProvider<T> getWordProvider()
    {
        return _wordProvider;
    }
    
    @Override
    public void caretUpdate(CaretEvent arg0)
    {
        //getWord(arg0.getDot(), true);
        suggest(arg0.getDot(), false);
    }
    
    public List<WordInfo> getWords()
    {
        return getWords(false);
    }
    
    public List<WordInfo> getWords(boolean includeQuotes)
    {
        return StringUtils.split(getText(), _wordSeparator, _quotationCharacter, includeQuotes);
    }
    
    public char getWordSeparator()
    {
        return _wordSeparator;
    }
    
    @Override
    public void insertUpdate(DocumentEvent e)
    {
        if (e.getLength() == 1)
        {
            // Ignore changes due to cut-and-paste operations?
            suggest(getCaretPosition()+1/* e.getOffset()*/, true);
        }
        else
        {
//            hideSuggestionMenu();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
    }
    
    @Override
    public void keyTyped(KeyEvent e)
    {
    }
    
    @Override
    public void removeUpdate(DocumentEvent e)
    {
        if (e.getLength() == 1)
        {
            // Ignore changes due to cut-and-paste operations?
//            suggest(e.getOffset() - 1, true);
        }
        else
        {
//            hideSuggestionMenu();
        }
//        hideSuggestionMenu();
    }
    
    public void setQuotationCharacter(char quotationCharacter)
    {
        _quotationCharacter = quotationCharacter;
    }
    
    public void setWordProvider(POTagFieldSuggestionProvider<T> wordProvider)
    {
        _wordProvider = wordProvider;
    }
    
    public void setWordSeparator(char wordSeparator)
    {
        _wordSeparator = wordSeparator;
    }
    
    private JPopupMenu getMenu()
    {
        if (_menu == null)
        {
            _menu = new JPopupMenu();
            Font defaultFont = _menu.getFont();
            _menu.setFont(new Font(defaultFont.getName(), defaultFont.getStyle(), (int) (0.8 * defaultFont.getSize())));
        }
        return _menu;
    }
    
    private JDialog getSuggestionsFrame()
    {
        return getSuggestionsFrame(null, -1);
    }
    private JDialog getSuggestionsFrame(final List<T> suggestions, final int selectedIndex)
    {
        if (null == _suggestionsFrame)
        {
            _suggestionsFrame = new JDialog(SwingUtilities.getWindowAncestor(this));
            _suggestionsFrame.setLayout(new BoxLayout(_suggestionsFrame.getContentPane(), BoxLayout.Y_AXIS));
            _suggestionsFrame.setResizable(false);
            //_suggestionsFrame.setFocusable(false);
            _suggestionsFrame.setLocationRelativeTo(POTagField.this);
            _suggestionsFrame.setUndecorated(true);
            _suggestionsFrame.setAlwaysOnTop(true);
            _suggestionsFrame.getContentPane().setBackground(getBackground());
            _suggestionsFrame.getContentPane().setFont(getFont());
            
            _suggestionlist = new JList(new DefaultListModel());
            _suggestionlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            _suggestionlist.setLayoutOrientation(JList.VERTICAL);
            _suggestionlist.addListSelectionListener(this);
            
            _suggestionsFrame.getContentPane().add(_suggestionlist);
            
        }
        if (suggestions != null)
        {
            DefaultListModel listModel = (DefaultListModel) _suggestionlist.getModel();
            listModel.removeAllElements();
            //_suggestionsFrame.getContentPane().removeAll();
            for (T suggestion : suggestions)
            {
                listModel.addElement(/*new SuggestionItem(suggestion, */_wordProvider.toString(suggestion)/*)*/);
//                JLabel label = new JLabel(_wordProvider.toString(suggestion));
//                label.setFont(getFont());
//                _suggestionsFrame.getContentPane().add(label);
            }
            if (selectedIndex >= 0 && selectedIndex < _suggestionlist.getModel().getSize())
            {
                _suggestionlist.setSelectedIndex(selectedIndex);
            }
        }
        _suggestionsFrame.pack();
        return _suggestionsFrame;
    }
    
    private int getWordEndPos(int caretPosition, String sentence)
    {
        int wordEndPos = caretPosition;
        while (wordEndPos < sentence.length() && Character.isLetter(sentence.charAt(wordEndPos)))
        {
            wordEndPos++;
        }
        return wordEndPos - 1;
    }
    
    private int getWordStartPos(int caretPosition, String sentence)
    {
        int wordStartPos = caretPosition;
        if (sentence.charAt(wordStartPos) == _wordSeparator)
        {
            wordStartPos--;
        }
        while (wordStartPos >= 0 && Character.isLetter(sentence.charAt(wordStartPos)))
        {
            wordStartPos--;
        }
        return wordStartPos + 1;
    }
    
    private void hideSuggestionMenu()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            
            @Override
            public void run()
            {
                getSuggestionsFrame().setVisible(false);
//                final JPopupMenu menu = getMenu();
//                menu.setVisible(false);
//                _mode = Mode.TYPING;
            }
        });
    }

    private void init()
    {
        getDocument().addDocumentListener(this);
        addKeyListener(this);
        initKeyListeners();
        addFocusListener(this);
        //addCaretListener(this);
    }

    private void initKeyListeners()
    {
        getInputMap().put(KeyStroke.getKeyStroke(Keys.ENTER.getKeyCode(), KeyModifiers.NONE.getValue()), ACTION_ACCEPT);
        getActionMap().put(ACTION_ACCEPT, new AcceptAction());
        
        getInputMap().put(KeyStroke.getKeyStroke(Keys.UP.getKeyCode(), KeyModifiers.NONE.getValue()), ACTION_PREVIOUS);
        getActionMap().put(ACTION_PREVIOUS, new ChooseSuggestion(-1));
        
        getInputMap().put(KeyStroke.getKeyStroke(Keys.DOWN.getKeyCode(), KeyModifiers.NONE.getValue()), ACTION_NEXT);
        getActionMap().put(ACTION_NEXT, new ChooseSuggestion(1));
    }

    private int replaceWord(int caretPosition, String replacementWord, boolean appendSeparator)
    {
        int startOfReplacement = 0;
        StringBuilder sb = new StringBuilder();
        WordInfo existing = getWord(caretPosition, true);
        String sentence = getText();
        String before = sentence.substring(0, existing.getPositionOfFirstCharacter());
        String after = sentence.substring(existing.getPositionOfLastCharacter() + 1);
        sb.append(before);
        boolean containsSeparator = replacementWord.indexOf(_wordSeparator) >= 0;
        startOfReplacement = before.length();
        if (containsSeparator)
        {
            sb.append(_quotationCharacter).append(replacementWord).append(_quotationCharacter);
            startOfReplacement++;
        }
        else
        {
            sb.append(replacementWord);
        }
        if (appendSeparator)
        {
            sb.append(_wordSeparator);
        }
        sb.append(after);
        L.finer("Replacing '" + sentence + "' with '" + sb.toString() + "'");
        setText(sb.toString());
        return startOfReplacement;
    }

    private void showSuggestionMenu(final int x, final List<T> suggestions, final int caretPositionInSentence, final int wordStartPos, final boolean selectFirstSuggestion)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            
            @Override
            public void run()
            {
//                final JPopupMenu menu = getMenu();
//                menu.removeAll();
//                
//                for (T suggestion : suggestions)
//                {
//                    String word = _wordProvider.toString(suggestion);
//                    JMenuItem menuItem = new JMenuItem(word);
//                    menuItem.addActionListener(_onMenuItem_actionPerformed);
//                    menuItem.setActionCommand(word);
//                    menuItem.setFont(menu.getFont());
//                    menu.add(menuItem);
//                }
//                if (menu.isVisible())
//                {
//                    System.err.println("Packing menu");
//                    menu.pack();
//                }
//                else
//                {
                    L.finer("Displaying menu at " + x + " " + getHeight());
//                    menu.show(POTagField.this, x, getHeight());
                    
                    JDialog f = getSuggestionsFrame(suggestions, selectFirstSuggestion ? 0 : -1);
                    Point locationOnScreen = getLocationOnScreen();
                    f.setLocation(locationOnScreen.x + x, locationOnScreen.y + getHeight());
                    f.setVisible(true);
//                }
//                _mode = Mode.CHOOSING_SUGGESTION;

//                if (selectFirstSuggestion)
//                {
//                    String replacementWord = suggestions.get(0).toString();
//                    suggestWordEnd(caretPositionInSentence, replacementWord);
//                }
                
//                requestFocusInWindow();
                requestFocus();
                
            }
        });
    }

    private void suggest(final int caretPositionInSentence, boolean typeFirstSuggestion)
    {
        try
        {
            String sentence = getText();
            if (sentence.length() > 0)
            {
                WordInfo wordInfo = getWord(caretPositionInSentence, false);
                if (null != wordInfo)
                {
                    final int wordStartPos = wordInfo.getPositionOfFirstCharacter();// getWordStartPos(positionInWord, sentence);
                    int wordEndPos = wordInfo.getPositionOfLastCharacter();// getWordEndPos(positionInWord, sentence);
                
                    int wordLen = wordEndPos - wordStartPos + 1;
                    String word = getText(wordStartPos, wordLen);
                    
                    final int xOffset = getScrollOffset();
                    final int xPos = getFontMetrics(getFont()).stringWidth(getText(0, wordStartPos));
                    
                    final List<T> suggestions = _wordProvider.getSuggestions(word);
                    System.out.println("Display " + suggestions + " at " + (xPos - xOffset));
                    
                    if (suggestions.size() > 0)
                    {
//                        SwingUtilities.invokeLater(new Runnable()
//                        {
//                            
//                            @Override
//                            public void run()
//                            {
//                                int startOfSuggestion = positionInWord + 1;
//                                String suggestion = suggestions.get(0).toString().substring(startOfSuggestion - wordStartPos);
//                                replaceSelection(suggestion);
//                                setCaretPosition(startOfSuggestion + suggestion.length());
//                                moveCaretPosition(startOfSuggestion);
//                            }
//                        });
                        showSuggestionMenu(xPos - xOffset, suggestions, caretPositionInSentence, wordStartPos, typeFirstSuggestion);
                    }
                    else
                    {
                        hideSuggestionMenu();
                    }
                }
                else
                {
                    hideSuggestionMenu();
                }
            }
            else
            {
                hideSuggestionMenu();
            }
        }
        catch (BadLocationException e1)
        {
            e1.printStackTrace();
        }
    }

    private boolean validateWordPosition(int wordStartPos, int wordEndPos)
    {
        return wordStartPos >= 0 && wordEndPos >= wordStartPos && wordEndPos < getText().length();
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        final Object object = _suggestionlist.getSelectedValue();
        if (null != object)
        {
            L.finer("Item '" + object.toString() + "' has been selected.");
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    suggestWordEnd(getCaretPosition(), object.toString());
                    requestFocus();
                }
            });
        }
        else
        {
            L.finer("No item has been selected.");
        }
    }

    private void suggestWordEnd(final int caretPosition, String replacementWord)
    {
        WordInfo wordInfo = getWord(caretPosition, false);
        int caretPositionInWord = caretPosition - wordInfo.getPositionOfFirstCharacter()/* + 1*/;
        
        L.finer("Caret is at character " + (caretPosition + 1) + " in the sentence and at " + (caretPositionInWord + 1) + " in the word.");
        
        int startOfReplacement = replaceWord(caretPosition, replacementWord, false);
        int quotationOffset = replacementWord.indexOf(_wordSeparator) >= 0 ? 1 : 0;
        setCaretPosition(startOfReplacement + replacementWord.length() + quotationOffset);
        //moveCaretPosition(positionInSentence + 1 + quotationOffset);
        moveCaretPosition(startOfReplacement + caretPositionInWord);
    }

    public boolean isFocusLost(FocusEvent e)
    {
        return e.getOppositeComponent() != this && e.getOppositeComponent() != _suggestionlist && e.getOppositeComponent() != _suggestionsFrame;
    }
    
}
