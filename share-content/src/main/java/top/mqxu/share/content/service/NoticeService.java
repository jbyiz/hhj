package top.mqxu.share.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.mqxu.share.content.domain.entity.Notice;
import top.mqxu.share.content.mapper.NoticeMapper;

import java.util.List;

@Service
public class NoticeService {
    @Resource

    private NoticeMapper noticeMapper;
    public Notice getLatest() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getShowFlag, 1);
        wrapper.orderByDesc(Notice::getId);
        List<Notice> noticeList = noticeMapper.selectList(wrapper);
        return noticeList.get(0);
    }
}