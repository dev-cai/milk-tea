package com.milktea.controller.admin;

import com.milktea.common.Result;
import com.milktea.entity.PrintDevice;
import com.milktea.entity.PrintRecord;
import com.milktea.entity.PrintTemplate;
import com.milktea.service.PrintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端打印控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/print")
public class AdminPrintController {
    
    private final PrintService printService;
    
    public AdminPrintController(PrintService printService) {
        this.printService = printService;
    }
    
    /**
     * 获取打印设备列表
     */
    @GetMapping("/devices")
    public Result<List<PrintDevice>> getDeviceList() {
        return printService.getDeviceList();
    }
    
    /**
     * 添加打印设备
     */
    @PostMapping("/device")
    public Result<String> addDevice(@RequestBody PrintDevice device) {
        return printService.addDevice(device);
    }
    
    /**
     * 更新打印设备
     */
    @PutMapping("/device/{id}")
    public Result<String> updateDevice(@PathVariable Long id, @RequestBody PrintDevice device) {
        device.setId(id);
        return printService.updateDevice(device);
    }
    
    /**
     * 删除打印设备
     */
    @DeleteMapping("/device/{id}")
    public Result<String> deleteDevice(@PathVariable Long id) {
        return printService.deleteDevice(id);
    }
    
    /**
     * 测试打印设备
     */
    @PostMapping("/device/{id}/test")
    public Result<String> testDevice(@PathVariable Long id) {
        return printService.testDevice(id);
    }
    
    /**
     * 获取打印模板列表
     */
    @GetMapping("/templates")
    public Result<List<PrintTemplate>> getTemplateList() {
        return printService.getTemplateList();
    }
    
    /**
     * 添加打印模板
     */
    @PostMapping("/template")
    public Result<String> addTemplate(@RequestBody PrintTemplate template) {
        return printService.addTemplate(template);
    }
    
    /**
     * 更新打印模板
     */
    @PutMapping("/template/{id}")
    public Result<String> updateTemplate(@PathVariable Long id, @RequestBody PrintTemplate template) {
        template.setId(id);
        return printService.updateTemplate(template);
    }
    
    /**
     * 删除打印模板
     */
    @DeleteMapping("/template/{id}")
    public Result<String> deleteTemplate(@PathVariable Long id) {
        return printService.deleteTemplate(id);
    }
    
    /**
     * 设置默认模板
     */
    @PutMapping("/template/{id}/default")
    public Result<String> setDefaultTemplate(@PathVariable Long id) {
        return printService.setDefaultTemplate(id);
    }
    
    /**
     * 获取打印记录列表
     */
    @GetMapping("/records")
    public Result<List<PrintRecord>> getPrintRecords() {
        return printService.getPrintRecords();
    }
    
    /**
     * 打印订单
     */
    @PostMapping("/order")
    public Result<String> printOrder(@RequestBody Map<String, Long> request) {
        Long orderId = request.get("orderId");
        Long deviceId = request.get("deviceId");
        Long templateId = request.get("templateId");
        return printService.printOrder(orderId, deviceId, templateId);
    }
    
    /**
     * 重新打印
     */
    @PostMapping("/record/{id}/reprint")
    public Result<String> reprintOrder(@PathVariable Long id) {
        return printService.reprintOrder(id);
    }
}
