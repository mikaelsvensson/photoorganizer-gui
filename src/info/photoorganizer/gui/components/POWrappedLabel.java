package info.photoorganizer.gui.components;

import javax.swing.Icon;
import javax.swing.JLabel;

public class POWrappedLabel extends JLabel
{
    public static class LineLengthWrapper implements Wrapper
    {
        private int _maxLength = 0;

        public LineLengthWrapper(int maxLength)
        {
            super();
            _maxLength = maxLength;
        }

        public int getMaxLength()
        {
            return _maxLength;
        }

        public void setMaxLength(int maxLength)
        {
            _maxLength = maxLength;
        }

        @Override
        public String wrap(String input)
        {
            final String LINE_FEED = "<br>";
            
            StringBuilder sb = new StringBuilder("<html>");
            int pos = sb.length();
            sb.append(input);
            while ((pos += _maxLength) < sb.length())
            {
                for (;!Character.isWhitespace(sb.charAt(pos));pos--);
                sb.deleteCharAt(pos);
                sb.insert(pos, LINE_FEED);
                pos += LINE_FEED.length();
            }
            sb.append("</html>");
            return sb.toString();
        }
    }
    
    public interface Wrapper
    {
        String wrap(String input);
    }
    
//    private String _text = null;
    private Wrapper _wrapper = null;

    public Wrapper getWrapper()
    {
        return _wrapper;
    }

    public POWrappedLabel()
    {
        super();
    }

    public POWrappedLabel(Icon image, int horizontalAlignment)
    {
        super(image, horizontalAlignment);
    }

    public POWrappedLabel(Icon image)
    {
        super(image);
    }

    public POWrappedLabel(String text, Icon icon, int horizontalAlignment)
    {
        super(text, icon, horizontalAlignment);
    }

    public POWrappedLabel(String text, int horizontalAlignment)
    {
        super(text, horizontalAlignment);
    }

    public POWrappedLabel(String text)
    {
        super(text);
    }
    
    public POWrappedLabel(Icon image, int horizontalAlignment, Wrapper wrapper)
    {
        super(image, horizontalAlignment);
        setWrapper(wrapper);
    }
    
    public POWrappedLabel(Icon image, Wrapper wrapper)
    {
        super(image);
        setWrapper(wrapper);
    }
    
    public POWrappedLabel(String text, Icon icon, int horizontalAlignment, Wrapper wrapper)
    {
        super(text, icon, horizontalAlignment);
        setWrapper(wrapper);
    }
    
    public POWrappedLabel(String text, int horizontalAlignment, Wrapper wrapper)
    {
        super(text, horizontalAlignment);
        setWrapper(wrapper);
    }
    
    public POWrappedLabel(String text, Wrapper wrapper)
    {
        super(text);
        setWrapper(wrapper);
    }
    
    public POWrappedLabel(Wrapper wrapper)
    {
        super();
        setWrapper(wrapper);
    }
    

    public void setWrapper(Wrapper wrapper)
    {
        _wrapper = wrapper;
    }

    @Override
    public void setText(String text)
    {
        text = _wrapper != null ? _wrapper.wrap(text) : text;
        super.setText(text);
    }
}
