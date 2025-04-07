package client.view.minigame.rhythm;

import java.awt.Image;
import javax.swing.ImageIcon;

public class ImageManager {
    private Image perfectImage;
    private Image goodImage;
    private Image missImage;
    
    public ImageManager() {
        loadImages();
    }
    
    private void loadImages() {
        try {
            // 실제 프로젝트에서는 실제 파일 경로를 사용해야 합니다
            perfectImage = new ImageIcon("images/rhythm/perfect.png").getImage();
            goodImage = new ImageIcon("images/rhythm/good.png").getImage();
            missImage = new ImageIcon("images/rhythm/miss.png").getImage();
        } catch (Exception e) {
            System.out.println("이미지 로딩 오류: " + e.getMessage());
            // 이미지가 없을 경우 기본 텍스트로 대체됩니다
        }
    }
    
    public Image getPerfectImage() {
        return perfectImage;
    }
    
    public Image getGoodImage() {
        return goodImage;
    }
    
    public Image getMissImage() {
        return missImage;
    }
}