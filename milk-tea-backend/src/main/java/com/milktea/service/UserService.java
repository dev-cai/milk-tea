package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.LoginHistory;
import com.milktea.entity.User;
import com.milktea.entity.UserAddress;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.LoginHistoryMapper;
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
    private final LoginHistoryMapper loginHistoryMapper;
    
    public UserService(UserMapper userMapper, UserAddressMapper userAddressMapper, LoginHistoryMapper loginHistoryMapper) {
        this.userMapper = userMapper;
        this.userAddressMapper = userAddressMapper;
        this.loginHistoryMapper = loginHistoryMapper;
    }
    
    /**
     * 获取用户信息
     */
    public Result<User> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 根据积分自动计算会员等级
        Integer calculatedLevel = calculateMemberLevel(user.getPoints());
        if (!calculatedLevel.equals(user.getMemberLevel())) {
            log.info("用户 {} 会员等级变更: {} -> {}, 当前积分: {}", 
                     userId, user.getMemberLevel(), calculatedLevel, user.getPoints());
            user.setMemberLevel(calculatedLevel);
            userMapper.updateById(user);
        }
        
        // 清空密码字段
        user.setPassword(null);
        return Result.success("获取成功", user);
    }
    
    /**
     * 根据积分计算会员等级
     * 规则：
     * - 积分 < 1000: 普通会员 (0)
     * - 1000 <= 积分 < 5000: 黄金会员 (1)
     * - 积分 >= 5000: 钻石会员 (2)
     */
    private Integer calculateMemberLevel(Integer points) {
        if (points == null) {
            points = 0;
        }
        
        if (points >= 5000) {
            return 2; // 钻石会员
        } else if (points >= 1000) {
            return 1; // 黄金会员
        } else {
            return 0; // 普通会员
        }
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
        if (user.getNickname() != null) {
            existUser.setNickname(user.getNickname());
        }
        if (user.getAvatar() != null) {
            existUser.setAvatar(user.getAvatar());
        }
        if (user.getPhone() != null) {
            existUser.setPhone(user.getPhone());
        }
        if (user.getGender() != null) {
            existUser.setGender(user.getGender());
        }
        if (user.getBirthday() != null) {
            existUser.setBirthday(user.getBirthday());
        }
        
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
        if (!existAddress.getUserId().equals(address.getUserId())) {
            throw new BusinessException("无权操作此地址");
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

    public Result<String> deleteUserAddress(Long addressId, Long userId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此地址");
        }
        userAddressMapper.deleteById(addressId);
        return Result.success("删除成功");
    }
    
    /**
     * 绑定微信
     */
    public Result<String> bindWechat(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查openid是否已被其他用户绑定
        if (user.getOpenid() != null) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getOpenid, user.getOpenid())
                       .ne(User::getId, user.getId());
            User bindUser = userMapper.selectOne(queryWrapper);
            if (bindUser != null) {
                throw new BusinessException("该微信已被其他账号绑定");
            }
        }
        
        existUser.setOpenid(user.getOpenid());
        userMapper.updateById(existUser);
        return Result.success("绑定成功");
    }
    
    /**
     * 解绑微信
     */
    public Result<String> unbindWechat(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        existUser.setOpenid(null);
        userMapper.updateById(existUser);
        return Result.success("解绑成功");
    }
    
    /**
     * 修改密码
     */
    public Result<String> changePassword(java.util.Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String oldPassword = params.get("oldPassword").toString();
        String newPassword = params.get("newPassword").toString();
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证旧密码（需要MD5加密后比较）
        String encryptedOldPassword = com.milktea.utils.MD5Utils.encode(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        
        // 设置新密码（MD5加密）
        String encryptedNewPassword = com.milktea.utils.MD5Utils.encode(newPassword);
        user.setPassword(encryptedNewPassword);
        userMapper.updateById(user);
        
        return Result.success("密码修改成功");
    }
    
    /**
     * 获取登录记录
     */
    public Result<List<LoginHistory>> getLoginHistory(Long userId) {
        LambdaQueryWrapper<LoginHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginHistory::getUserId, userId)
                   .orderByDesc(LoginHistory::getLoginTime)
                   .last("LIMIT 10"); // 只返回最近10条记录
        
        List<LoginHistory> historyList = loginHistoryMapper.selectList(queryWrapper);
        return Result.success("获取成功", historyList);
    }
}
