package client.view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class VerticalButtonPanel extends JPanel {
	private List<TitleContentButton> buttons = new ArrayList<>();
	private Comparator<TitleContentButton> customComparator = null;
	private SortOption currentSortOption = SortOption.TITLE_ASC;
	private boolean autoSort = false;

	public enum SortOption {
		TITLE_ASC, TITLE_DESC, TIMESTAMP_ASC, TIMESTAMP_DESC, CUSTOM
	}
	
	public VerticalButtonPanel() {
		this(new ArrayList<TitleContentButton>());
	}
	
	public VerticalButtonPanel(List<TitleContentButton> items) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (TitleContentButton item : items) {
			addButton(item);
		}
	}

	// 자동 정렬 여부 설정
	public void setAutoSort(boolean autoSort) {
		this.autoSort = autoSort;
	}

	// 정렬 기준 설정
	public void setSortOption(SortOption mode) {
		this.currentSortOption = mode;
		this.customComparator = null; // CUSTOM 해제
	}

	// 사용자 정의 Comparator 사용
	public void setCustomComparator(Comparator<TitleContentButton> comparator) {
		this.customComparator = comparator;
		this.currentSortOption = SortOption.CUSTOM;
	}

	// 버튼 추가 (중복 방지 + 자동 정렬 반영)
	public boolean addButton(TitleContentButton item) {
		if (findButtonByTitle(item.getTitle()) != null) {
			return false;
		}
		buttons.add(item);
		if (autoSort) {
			sortAndRefresh();
		} else {
			JButton btn = item.getButton();
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			add(btn);
			add(Box.createRigidArea(new Dimension(0, 10)));
			revalidate();
			repaint();
		}
		return true;
	}

	public TitleContentButton findButtonByTitle(String title) {
		for (TitleContentButton item : buttons) {
			if (item.getTitle().equals(title)) {
				return item;
			}
		}
		return null;
	}

	public boolean updateButton(String title, String newTitle, String newContent) {
		TitleContentButton target = findButtonByTitle(title);
		if (target != null) {
			target.setTitleAndContent(newTitle, newContent);
			if (autoSort) {
				sortAndRefresh();
			} else {
				revalidate();
				repaint();
			}
			return true;
		}
		return false;
	}


	public void removeButton(TitleContentButton btnToRemove) {
	    if (buttons.remove(btnToRemove)) {
	        remove(btnToRemove.getButton());
	        revalidate();
	        repaint();
	    }
	}
	
	public boolean removeButtonByTitle(String title) {
		Iterator<TitleContentButton> iterator = buttons.iterator();
		while (iterator.hasNext()) {
			TitleContentButton item = iterator.next();
			if (item.getTitle().equals(title)) {
				remove(item.getButton());
				iterator.remove();
				revalidate();
				repaint();
				return true;
			}
		}
		return false;
	}

	public void removeAllButtons() {
	    buttons.clear();
	    removeAll();

	    revalidate();
	    repaint();
	}

	public void sortAndRefresh() {
		Comparator<TitleContentButton> comparator = switch (currentSortOption) {
		case TITLE_DESC -> Comparator.comparing(TitleContentButton::getTitle).reversed();
		case TIMESTAMP_ASC -> Comparator.comparing(TitleContentButton::getTimestamp);
		case TIMESTAMP_DESC -> Comparator.comparing(TitleContentButton::getTimestamp).reversed();
		case CUSTOM -> customComparator;
		default -> Comparator.comparing(TitleContentButton::getTitle);
		};

		buttons.sort(comparator);
		removeAll();
		for (TitleContentButton item : buttons) {
			JButton btn = item.getButton();
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			add(btn);
			add(Box.createRigidArea(new Dimension(0, 10)));
		}
		revalidate();
		repaint();
	}

	public void filterByTag(String tag) {
		removeAll();
		for (TitleContentButton item : buttons) {
			if (tag == null || tag.isEmpty() || item.getTag().equals(tag)) {
				JButton btn = item.getButton();
				btn.setAlignmentX(Component.CENTER_ALIGNMENT);
				add(btn);
				add(Box.createRigidArea(new Dimension(0, 10)));
			}
		}
		revalidate();
		repaint();
	}

}
