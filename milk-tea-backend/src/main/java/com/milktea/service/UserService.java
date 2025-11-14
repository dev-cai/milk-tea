package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.User;
import com.milktea.entity.UserAddress;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.UserAddressMapper;
import com.milktea.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class UserService {
    
    private final UserMapper userMapper;
    private final UserAddressMapper userAddressMapper;
    
    public UserService(UserMapper userMapper, UserAddressMapper userAddressMapper) {
        this.userMapper = userMapper;
        this.userAddressMapper = userAddressMapper;
    }
    
    /**
     * 获取用户信息
     */
    public Result<User> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 清空密码字段
        user.setPassword(null);
        return Result.success("获取成功", user);
    }
    
    /**
     * 更新用户信息
     */
    public Result<String> updateUserInfo(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 只更新允许修改的字段
        existUser.setNickname(user.getNickname());
        existUser.setAvatar(user.getAvatar());
        existUser.setPhone(user.getPhone());
        
        userMapper.updateById(existUser);
        return Result.success("更新成功");
    }
    
    /**
     * 获取用户地址列表
     */
    public Result<List<UserAddress>> getUserAddresses(Long userId) {
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId)
                   .orderByDesc(UserAddress::getIsDefault)
                   .orderByDesc(UserAddress::getCreateTime);
        
        List<UserAddress> addresses = userAddressMapper.selectList(queryWrapper);
        return Result.success("获取成功", addresses);
    }
    
    /**
     * 添加用户地址
     */
    public Result<String> addUserAddress(UserAddress address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            LambdaQueryWrapper<UserAddress> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(UserAddress::getUserId, address.getUserId())
                        .eq(UserAddress::getIsDefault, 1);
            
            UserAddress updateAddress = new UserAddress();
            updateAddress.setIsDefault(0);
            userAddressMapper.update(updateAddress, updateWrapper);
        }
        
        userAddressMapper.insert(address);
        return Result.success("添加成功");
    }
    
    /**
     * 更新用户地址
     */
    public Result<String> updateUserAddress(UserAddress address) {
        UserAddress existAddress = userAddressMapper.selectById(address.getId());
        if (existAddress == null) {
            throw new BusinessException("地址不存在");
        }
        
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            LambdaQueryWrapper<UserAddress> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(UserAddress::getUserId, address.getUserId())
                        .eq(UserAddress::getIsDefault, 1)
                        .ne(UserAddress::getId, address.getId());
            
            UserAddress updateAddress = new UserAddress();
            updateAddress.setIsDefault(0);
            userAddressMapper.update(updateAddress, updateWrapper);
        }
        
        userAddressMapper.updateById(address);
        return Result.success("更新成功");
    }
    
    /**
     * 删除用户地址
     */
    public Result<String> deleteUserAddress(Long addressId) {
        userAddressMapper.deleteById(addressId);
        return Result.success("删除成功");
    }
}
