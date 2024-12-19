package top.mqxu.share.user.service;

import lombok.extern.slf4j.Slf4j;
import top.mqxu.share.user.domain.dto.UserAddBonusMsgDTO;
import top.mqxu.share.user.domain.entity.BonusEventLog;
import top.mqxu.share.user.mapper.BonusEventLogMapper;
import top.mqxu.share.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.mqxu.share.common.exception.BusinessException;
import top.mqxu.share.common.exception.BusinessExceptionEnum;
import top.mqxu.share.user.domain.dto.LoginDTO;
import top.mqxu.share.user.domain.entity.User;
import top.mqxu.share.user.domain.resp.UserLoginResp;
import top.mqxu.share.user.mapper.UserMapper;
import top.mqxu.share.util.SnowUtil;

import java.util.Date;

/**
 * UserService 处理用户业务逻辑
 * @author DingYihang
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private BonusEventLogMapper bonusEventLogMapper;

    /**
     * 统计用户数量
     * @return 用户数量
     */
    public Long count() {
        return userMapper.selectCount(null);
    }


    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public User findById(Long userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 用户登录方法
     * @param loginDTO 包含登录信息的DTO
     * @return 登录成功的用户信息
     */
    public UserLoginResp login(LoginDTO loginDTO) {
        // 根据手机号查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone, loginDTO.getPhone()));

        // 用户不存在，抛出异常
        if (user == null) {
            throw new BusinessException(BusinessExceptionEnum.PHONE_NOT_EXIST);
        }

        // 密码错误，抛出异常
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new BusinessException(BusinessExceptionEnum.PASSWORD_ERROR);
        }

        //都正确，返回
        UserLoginResp userLoginResp = UserLoginResp.builder()
                .user(user)
                .build();
//        String key = "hello world";
//        Map<String,Object> map = BeanUtil.beanToMap(userLoginResp);
        String token = JwtUtil.createToken(userLoginResp.getUser().getId(),userLoginResp.getUser().getPhone());
        userLoginResp.setToken(token);
        return userLoginResp;
    }

    /**
     * 用户注册方法
     * @param loginDTO 包含用户注册信息的DTO
     * @return 注册成功的用户ID
     * @throws BusinessException 如果手机号已存在
     */
    public Long register(LoginDTO loginDTO) {
        // 根据手机号查询用户，检查手机号是否已存在
        User userDb = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone, loginDTO.getPhone()));

        // 如果手机号已存在，抛出业务异常
        if (userDb != null) {
            throw new BusinessException(BusinessExceptionEnum.PHONE_EXIST);
        }

        // 创建并构建新用户对象
        User saveUser = User.builder()
                .id(SnowUtil.getSnowflakeNextId())
                .phone(loginDTO.getPhone())
                .password(loginDTO.getPassword())
                .nickname("新用户")
                .avatarUrl("https://niit-soft.oss-cn-hangzhou.aliyuncs.com/avatar/8.jpg")
                .bonus(100)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        // 插入新用户记录到数据库
        userMapper.insert(saveUser);

        // 返回新注册用户的ID
        return saveUser.getId();
    }

    public void updateBonus(UserAddBonusMsgDTO userAddBonusMsgDTO) {
        // 1. 为用户修改积分
        Long userId = userAddBonusMsgDTO.getUserId();
        Integer bonus = userAddBonusMsgDTO.getBonus();

        User user = userMapper.selectById(userId);
        user.setBonus(user.getBonus() + bonus);
        userMapper.update(user, new QueryWrapper<User>().lambda().eq(User::getId, userId));

        // 2. 记录日志到 bonus_event_log 表
        bonusEventLogMapper.insert(BonusEventLog.builder()
                .userId(userId)
                .value(bonus)
                .event(userAddBonusMsgDTO.getEvent())
                .description(userAddBonusMsgDTO.getDescription())
                .createTime(new Date())
                .build());

        log.info("积分添加完毕……");
    }
}

