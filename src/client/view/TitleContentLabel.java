package client.view;

import java.time.LocalDateTime;

import javax.swing.JLabel;

public class TitleContentLabel {
    private String title;
    private String content;
    private String tag;
    private LocalDateTime timestamp;
    private JLabel label;

    public TitleContentLabel(String title, String content) {
        this(title, content, "", LocalDateTime.now());
    }

    public TitleContentLabel(String title, String content, String tag, LocalDateTime timestamp) {
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.timestamp = timestamp;
        this.label = new JLabel(buildHtml(title, content));
        this.label.setToolTipText(buildTooltip());
    }

    private String buildHtml(String title, String content) {
        return "<html><center><h1>" + title + "</h1>" + content + "</center></html>";
    }

    private String buildTooltip() {
        return "태그: " + (tag.isEmpty() ? "없음" : tag) +
               " | 생성일: " + timestamp.toString();
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
        updateLabel();
    }

    public void setContent(String newContent) {
        this.content = newContent;
        updateLabel();
    }

    public void setTitleAndContent(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
        updateLabel();
    }

    public void setTag(String newTag) {
        this.tag = newTag;
        updateTooltip();
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        updateTooltip();
    }

    private void updateLabel() {
        label.setText(buildHtml(title, content));
        updateTooltip();
    }

    private void updateTooltip() {
        label.setToolTipText(buildTooltip());
    }

    public JLabel getLabel() { return label; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTag() { return tag; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
