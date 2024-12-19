package top.mqxu.share.content.controller;


import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import top.mqxu.share.common.resp.CommonResp;
import top.mqxu.share.content.domain.dto.ExchangeDTO;
import top.mqxu.share.content.domain.dto.ShareRequestDTO;
import top.mqxu.share.content.domain.entity.Notice;

import top.mqxu.share.content.domain.entity.Share;
import top.mqxu.share.content.domain.resp.ShareResp;
import top.mqxu.share.content.service.NoticeService;
import top.mqxu.share.content.service.ShareService;
import top.mqxu.share.util.JwtUtil;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/share")
public class ShareController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private ShareService shareService;

    // 定义每页最多的数据条数，以防前端传递超大参数，造成页面数据量过大
    private final int MAX = 50;

    @GetMapping("/notice")
    public CommonResp<Notice> getLatestNotice() {
        CommonResp<Notice> commonResp = new CommonResp<>();
        commonResp.setData(noticeService.getLatest());
        return commonResp;
    }

    @GetMapping("/list")
    public CommonResp<List<Share>> getShareList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize,
            @RequestHeader(value = "token", required = false) String token) {

        // 控制 pageSize 最大值
        if (pageSize > MAX) {
            pageSize = MAX;
        }

        Long userId = getUserIdFromToken(token);
        CommonResp<List<Share>> commonResp = new CommonResp<>();
        commonResp.setData(shareService.getList(title, pageNo, pageSize, userId));
        return commonResp;
    }

    /**
     * 封装一个私有方法，从 token 中解析出 userId
     *
     * @param token token
     * @return userId
     */
    private Long getUserIdFromToken(String token) {
        long userId = 0;
        String noToken = "no-token";

        if (!noToken.equals(token)) {
            JSONObject jsonObject = JwtUtil.getJSONObject(token);
            log.info("解析到 token 中的数据：{}", jsonObject);
            userId = Long.parseLong(jsonObject.get("id").toString());
        } else {
            log.info("没有 token");
        }

        return userId;
    }

    /**
     * 根据 id 查询分享内容
     * @param id 分享内容 id
     * @return 分享内容
     */
    @GetMapping("/{id}")
    public CommonResp<ShareResp> getShareById(@PathVariable Long id) {
        ShareResp shareResp = shareService.findById(id);
        CommonResp<ShareResp> commonResp = new CommonResp<>();
        commonResp.setData(shareResp);
        return commonResp;
    }

    @PostMapping("/exchange")
    public CommonResp<Share> exchange(@RequestBody ExchangeDTO exchangeDTO) {
        CommonResp<Share> resp = new CommonResp<>();
        resp.setData(shareService.exchange(exchangeDTO)); // 调用服务层的 exchange 方法
        return resp; // 返回响应
    }

    @PostMapping("/contribute")
    public CommonResp<Integer> contribute(@RequestBody ShareRequestDTO shareRequestDTO, @RequestHeader(value="token",required=false)String token)
    {
        Long userId=getUserIdFromToken(token);
    shareRequestDTO.setUserId(userId);
    CommonResp<Integer> resp=new CommonResp<>();
    resp.setData(shareService.contribute(shareRequestDTO));
    return resp;
    }

    @GetMapping("/myContribute")
    public CommonResp<List<Share>> myContribute(
            @RequestParam(required=false,defaultValue="1") Integer pageNo,
            @RequestParam(required=false,defaultValue="3") Integer pageSize,
            @RequestHeader(value="token",required=false)String token) {
        if (pageSize > MAX) {
            pageSize = MAX;
        }
        Long userId = getUserIdFromToken(token);
        CommonResp<List<Share>> resp = new CommonResp<>();
        resp.setData(shareService.myContribute(pageNo, pageSize, userId));
        return resp;
    }


}
