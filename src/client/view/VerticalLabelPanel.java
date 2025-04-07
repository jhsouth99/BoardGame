package client.view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VerticalLabelPanel extends JPanel {
    private List<TitleContentLabel> labels = new ArrayList<>();

    // 정렬 기준 enum
    public enum SortOption {
        TITLE_ASC, TITLE_DESC, TIMESTAMP_ASC, TIMESTAMP_DESC, CUSTOM
    }

    private SortOption currentSortOption = SortOption.TITLE_ASC;
	private Comparator<TitleContentLabel> customComparator;

	public VerticalLabelPanel() {
		this(new ArrayList<TitleContentLabel>());
	}
	
    public VerticalLabelPanel(List<TitleContentLabel> items) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for (TitleContentLabel item : items) {
            addLabel(item);
        }
    }

    // 생성
    public boolean addLabel(TitleContentLabel label) {
        if (findLabelByTitle(label.getTitle()) != null) return false;
        labels.add(label);
        sortAndRefresh(); // 자동 정렬
        return true;
    }

    // 읽기
    public TitleContentLabel findLabelByTitle(String title) {
        for (TitleContentLabel item : labels) {
            if (item.getTitle().equals(title)) return item;
        }
        return null;
    }

    // 수정
    public boolean updateLabel(String title, String newTitle, String newContent) {
        TitleContentLabel item = findLabelByTitle(title);
        if (item != null) {
            item.setTitleAndContent(newTitle, newContent);
            return true;
        }
        return false;
    }
    
    public void removeLabel(TitleContentLabel btnToRemove) {
	    if (labels.remove(btnToRemove)) {
	        remove(btnToRemove.getLabel());
	        revalidate();
	        repaint();
	    }
	}
    
    // 삭제
    public boolean removeLabelByTitle(String title) {
        Iterator<TitleContentLabel> iterator = labels.iterator();
        while (iterator.hasNext()) {
            TitleContentLabel item = iterator.next();
            if (item.getTitle().equals(title)) {
                remove(item.getLabel());
                iterator.remove();
                revalidate();
                repaint();
                return true;
            }
        }
        return false;
    }

	public void removeAllLabels() {
	    labels.clear();
	    removeAll();

	    revalidate();
	    repaint();
	}

    // 정렬 기준 설정
    public void setSortOption(SortOption option) {
        this.currentSortOption = option;
        sortAndRefresh();
    }

	// 사용자 정의 Comparator 사용
	public void setCustomComparator(Comparator<TitleContentLabel> comparator) {
		this.customComparator = comparator;
		this.currentSortOption = SortOption.CUSTOM;
	}

    // 정렬 및 화면 갱신
	public void sortAndRefresh() {
		Comparator<TitleContentLabel> comparator = switch (currentSortOption) {
		case TITLE_DESC -> Comparator.comparing(TitleContentLabel::getTitle).reversed();
		case TIMESTAMP_ASC -> Comparator.comparing(TitleContentLabel::getTimestamp);
		case TIMESTAMP_DESC -> Comparator.comparing(TitleContentLabel::getTimestamp).reversed();
		case CUSTOM -> customComparator;
		default -> Comparator.comparing(TitleContentLabel::getTitle);
		};

        labels.sort(comparator);
        removeAll();

        for (TitleContentLabel item : labels) {
            JLabel lbl = item.getLabel();
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(lbl);
            add(Box.createRigidArea(new Dimension(0, 10)));
        }

        revalidate();
        repaint();
    }

    // 태그 필터링
    public void filterByTag(String tag) {
        removeAll();
        for (TitleContentLabel item : labels) {
            if (tag == null || tag.isEmpty() || item.getTag().equals(tag)) {
                JLabel lbl = item.getLabel();
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(lbl);
                add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        revalidate();
        repaint();
    }
}
