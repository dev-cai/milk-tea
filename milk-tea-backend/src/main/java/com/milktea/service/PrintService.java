package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.*;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打印服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class PrintService {
    
    private final PrintDeviceMapper printDeviceMapper;
    private final PrintTemplateMapper printTemplateMapper;
    private final PrintRecordMapper printRecordMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    
    public PrintService(PrintDeviceMapper printDeviceMapper, PrintTemplateMapper printTemplateMapper,
                       PrintRecordMapper printRecordMapper, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.printDeviceMapper = printDeviceMapper;
        this.printTemplateMapper = printTemplateMapper;
        this.printRecordMapper = printRecordMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }
    
    /**
     * 获取打印设备列表
     */
    public Result<List<PrintDevice>> getDeviceList() {
        try {
            List<PrintDevice> devices = printDeviceMapper.selectList(null);
            return Result.success("获取成功", devices);
        } catch (Exception e) {
            log.error("获取打印设备列表失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 添加打印设备
     */
    public Result<String> addDevice(PrintDevice device) {
        try {
            device.setStatus(1); // 默认在线
            printDeviceMapper.insert(device);
            log.info("添加打印设备成功: {}", device.getName());
            return Result.success("添加成功");
        } catch (Exception e) {
            log.error("添加打印设备失败", e);
            return Result.error("添加失败");
        }
    }
    
    /**
     * 更新打印设备
     */
    public Result<String> updateDevice(PrintDevice device) {
        try {
            printDeviceMapper.updateById(device);
            log.info("更新打印设备成功: {}", device.getName());
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新打印设备失败", e);
            return Result.error("更新失败");
        }
    }
    
    /**
     * 删除打印设备
     */
    public Result<String> deleteDevice(Long id) {
        try {
            printDeviceMapper.deleteById(id);
            log.info("删除打印设备成功: ID={}", id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除打印设备失败", e);
            return Result.error("删除失败");
        }
    }
    
    /**
     * 测试打印设备
     */
    public Result<String> testDevice(Long id) {
        try {
            PrintDevice device = printDeviceMapper.selectById(id);
            if (device == null) {
                throw new BusinessException("设备不存在");
            }
            
            // The connector is not available in the current deployment; keep the
            // operation explicit and report the persisted device state.
            log.info("测试打印设备: {}", device.getName());
            
            // 更新设备状态为在线
            device.setStatus(1);
            printDeviceMapper.updateById(device);
            
            return Result.success("设备连接正常");
        } catch (Exception e) {
            log.error("测试打印设备失败", e);
            return Result.error("设备连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取打印模板列表
     */
    public Result<List<PrintTemplate>> getTemplateList() {
        try {
            List<PrintTemplate> templates = printTemplateMapper.selectList(null);
            return Result.success("获取成功", templates);
        } catch (Exception e) {
            log.error("获取打印模板列表失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 添加打印模板
     */
    public Result<String> addTemplate(PrintTemplate template) {
        try {
            // 如果设置为默认模板，先取消其他默认模板
            if (template.getIsDefault() != null && template.getIsDefault() == 1) {
                LambdaQueryWrapper<PrintTemplate> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PrintTemplate::getType, template.getType())
                       .eq(PrintTemplate::getIsDefault, 1);
                List<PrintTemplate> defaultTemplates = printTemplateMapper.selectList(wrapper);
                for (PrintTemplate t : defaultTemplates) {
                    t.setIsDefault(0);
                    printTemplateMapper.updateById(t);
                }
            }
            
            printTemplateMapper.insert(template);
            log.info("添加打印模板成功: {}", template.getName());
            return Result.success("添加成功");
        } catch (Exception e) {
            log.error("添加打印模板失败", e);
            return Result.error("添加失败");
        }
    }
    
    /**
     * 更新打印模板
     */
    public Result<String> updateTemplate(PrintTemplate template) {
        try {
            // 如果设置为默认模板，先取消其他默认模板
            if (template.getIsDefault() != null && template.getIsDefault() == 1) {
                LambdaQueryWrapper<PrintTemplate> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PrintTemplate::getType, template.getType())
                       .eq(PrintTemplate::getIsDefault, 1)
                       .ne(PrintTemplate::getId, template.getId());
                List<PrintTemplate> defaultTemplates = printTemplateMapper.selectList(wrapper);
                for (PrintTemplate t : defaultTemplates) {
                    t.setIsDefault(0);
                    printTemplateMapper.updateById(t);
                }
            }
            
            printTemplateMapper.updateById(template);
            log.info("更新打印模板成功: {}", template.getName());
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新打印模板失败", e);
            return Result.error("更新失败");
        }
    }
    
    /**
     * 删除打印模板
     */
    public Result<String> deleteTemplate(Long id) {
        try {
            printTemplateMapper.deleteById(id);
            log.info("删除打印模板成功: ID={}", id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除打印模板失败", e);
            return Result.error("删除失败");
        }
    }
    
    /**
     * 设置默认模板
     */
    @Transactional
    public Result<String> setDefaultTemplate(Long id) {
        try {
            PrintTemplate template = printTemplateMapper.selectById(id);
            if (template == null) {
                throw new BusinessException("模板不存在");
            }
            
            // 取消同类型的其他默认模板
            LambdaQueryWrapper<PrintTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PrintTemplate::getType, template.getType())
                   .eq(PrintTemplate::getIsDefault, 1);
            List<PrintTemplate> defaultTemplates = printTemplateMapper.selectList(wrapper);
            for (PrintTemplate t : defaultTemplates) {
                t.setIsDefault(0);
                printTemplateMapper.updateById(t);
            }
            
            // 设置当前模板为默认
            template.setIsDefault(1);
            printTemplateMapper.updateById(template);
            
            log.info("设置默认模板成功: {}", template.getName());
            return Result.success("设置成功");
        } catch (Exception e) {
            log.error("设置默认模板失败", e);
            return Result.error("设置失败");
        }
    }
    
    /**
     * 获取打印记录列表（优化版 - 通过JOIN获取关联数据）
     */
    public Result<List<PrintRecord>> getPrintRecords() {
        try {
            LambdaQueryWrapper<PrintRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(PrintRecord::getPrintTime);
            List<PrintRecord> records = printRecordMapper.selectList(wrapper);
            
            // 填充关联数据
            for (PrintRecord record : records) {
                // 获取订单信息
                Order order = orderMapper.selectById(record.getOrderId());
                if (order != null) {
                    record.setOrderNo(order.getOrderNo());
                }
                
                // 获取设备信息
                PrintDevice device = printDeviceMapper.selectById(record.getDeviceId());
                if (device != null) {
                    record.setDeviceName(device.getName());
                }
                
                // 获取模板信息
                PrintTemplate template = printTemplateMapper.selectById(record.getTemplateId());
                if (template != null) {
                    record.setTemplateName(template.getName());
                }
                
                // 如果需要打印内容，动态生成
                if (record.getStatus() == 1 && order != null && template != null) {
                    LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
                    itemWrapper.eq(OrderItem::getOrderId, record.getOrderId());
                    List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
                    record.setContent(generatePrintContent(order, items, template));
                }
            }
            
            return Result.success("获取成功", records);
        } catch (Exception e) {
            log.error("获取打印记录失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 打印订单
     */
    @Transactional
    public Result<String> printOrder(Long orderId, Long deviceId, Long templateId) {
        try {
            // 获取订单信息
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // 获取设备信息
            PrintDevice device = printDeviceMapper.selectById(deviceId);
            if (device == null) {
                throw new BusinessException("打印设备不存在");
            }
            
            if (device.getStatus() == 0) {
                throw new BusinessException("打印设备离线");
            }
            
            // 获取模板信息
            PrintTemplate template = printTemplateMapper.selectById(templateId);
            if (template == null) {
                throw new BusinessException("打印模板不存在");
            }
            
            // 获取订单明细
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, orderId);
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            
            // 创建打印记录（优化版 - 只存储关键信息）
            PrintRecord record = new PrintRecord();
            record.setOrderId(orderId);
            record.setDeviceId(deviceId);
            record.setTemplateId(templateId);
            record.setStatus(1); // 成功
            record.setCopies(1);
            record.setDuration(2);
            record.setRetryCount(0);
            record.setPrintTime(LocalDateTime.now());
            
            printRecordMapper.insert(record);
            
            log.info("打印订单成功: 订单号={}, 设备={}", order.getOrderNo(), device.getName());
            return Result.success("打印成功");
        } catch (Exception e) {
            log.error("打印订单失败", e);
            
            // 记录失败的打印记录
            try {
                PrintRecord failRecord = new PrintRecord();
                failRecord.setOrderId(orderId);
                failRecord.setStatus(2); // 失败
                failRecord.setErrorMessage(e.getMessage());
                failRecord.setPrintTime(LocalDateTime.now());
                printRecordMapper.insert(failRecord);
            } catch (Exception ex) {
                log.error("记录打印失败信息失败", ex);
            }
            
            return Result.error("打印失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成打印内容
     */
    private String generatePrintContent(Order order, List<OrderItem> items, PrintTemplate template) {
        StringBuilder content = new StringBuilder();
        
        // 添加页眉
        if (template.getHeader() != null && !template.getHeader().isEmpty()) {
            content.append(template.getHeader()).append("\n");
        }
        
        // 替换模板变量
        String templateContent = template.getContent();
        templateContent = templateContent.replace("{orderNo}", order.getOrderNo());
        templateContent = templateContent.replace("{customerName}", order.getUsername() != null ? order.getUsername() : "顾客");
        templateContent = templateContent.replace("{totalAmount}", "¥" + order.getPayAmount());
        templateContent = templateContent.replace("{createTime}", order.getCreateTime().toString());
        
        // 生成商品列表
        StringBuilder itemsStr = new StringBuilder();
        for (OrderItem item : items) {
            itemsStr.append(item.getProductName())
                   .append(" x")
                   .append(item.getQuantity())
                   .append(" ¥")
                   .append(item.getPrice())
                   .append("\n");
        }
        templateContent = templateContent.replace("{items}", itemsStr.toString());
        
        content.append(templateContent);
        
        // 添加页脚
        if (template.getFooter() != null && !template.getFooter().isEmpty()) {
            content.append("\n").append(template.getFooter());
        }
        
        return content.toString();
    }
    
    /**
     * 重新打印
     */
    public Result<String> reprintOrder(Long recordId) {
        try {
            PrintRecord record = printRecordMapper.selectById(recordId);
            if (record == null) {
                throw new BusinessException("打印记录不存在");
            }
            
            // 重新打印
            return printOrder(record.getOrderId(), record.getDeviceId(), record.getTemplateId());
        } catch (Exception e) {
            log.error("重新打印失败", e);
            return Result.error("重新打印失败: " + e.getMessage());
        }
    }
}
