package client.view;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

import javax.swing.JButton;
import javax.swing.UIManager;

public class TitleContentButton {
    private String title;
    private String content;
    private String tag;
    private LocalDateTime timestamp;
    private JButton button;

    public TitleContentButton(String title, String content) {
        this(title, content, "", LocalDateTime.now());
    }

    public TitleContentButton(String title, String content, String tag, LocalDateTime timestamp) {
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.timestamp = timestamp;
        this.button = new JButton(buildHtml(title, content));
        this.button.setFocusPainted(false);
    }

    private String buildHtml(String title, String content) {
        return "<html><center>" + title + "<br/>" + content + "</center></html>";
    }

    private void updateButtonText() {
        button.setText(buildHtml(title, content));
        updateTooltip(); // 자동 툴팁
    }

    public JButton getButton() { return button; }

    public String getTitle() { return title; }

    public String getContent() { return content; }

    public String getTag() { return tag; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTitle(String newTitle) {
        this.title = newTitle;
        updateButtonText();
    }

    public void setContent(String newContent) {
        this.content = newContent;
        updateButtonText();
    }

    public void setTitleAndContent(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
        updateButtonText();
    }
    
    public void setTag(String newTag) {
        this.tag = newTag;
        updateTooltip();
    }

    public void setTimestamp(LocalDateTime newTimestamp) {
        this.timestamp = newTimestamp;
        updateTooltip();
    }
    
    public void addActionListener(ActionListener listener) {
        button.addActionListener(listener);
    }

    public void setTooltip(String tooltip) {
        button.setToolTipText(tooltip);
    }

    public void highlight(Color backgroundColor, Color foregroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    public void clearHighlight() {
        button.setBackground(UIManager.getColor("Button.background"));
        button.setForeground(UIManager.getColor("Button.foreground"));
        button.setOpaque(true);
        button.setBorderPainted(true);
    }

    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }
    
    private void updateTooltip() {
        String tip = "태그: " + (tag.isEmpty() ? "없음" : tag)
                   + " | 생성일: " + timestamp.toString();
        button.setToolTipText(tip);
    }
}
