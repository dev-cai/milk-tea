package com.milktea.controller.admin;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片处理控制器
 * @author MilkTea Team
 */
@RestController
@RequestMapping("/api/uploads")
public class AdminImageController {
    
    /**
     * 生成占位符图片
     */
    @GetMapping("/images/sample/{filename}")
    public ResponseEntity<byte[]> generatePlaceholderImage(@PathVariable String filename) {
        try {
            // 创建200x200的图片
            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // 设置背景色
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(0, 0, 200, 200);
            
            // 设置边框
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawRect(0, 0, 199, 199);
            
            // 设置文字
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            
            String text = "商品图片";
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            
            g2d.drawString(text, (200 - textWidth) / 2, (200 + textHeight) / 2 - 10);
            
            // 添加文件名
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            fm = g2d.getFontMetrics();
            String shortName = filename.length() > 15 ? filename.substring(0, 12) + "..." : filename;
            int nameWidth = fm.stringWidth(shortName);
            g2d.drawString(shortName, (200 - nameWidth) / 2, 180);
            
            g2d.dispose();
            
            // 转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);
            headers.setCacheControl("public, max-age=3600"); // 缓存1小时
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
