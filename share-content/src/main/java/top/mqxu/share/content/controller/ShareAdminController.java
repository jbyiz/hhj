package top.mqxu.share.content.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mqxu.share.common.resp.CommonResp;
import top.mqxu.share.content.domain.entity.Share;
import top.mqxu.share.content.service.ShareService;

import java.util.List;

@RestController
@RequestMapping("/share/admin")
@Slf4j
@AllArgsConstructor
public class ShareAdminController {
    private final ShareService shareService;
    @GetMapping("/list")
    public CommonResp<List<Share>> getSharesNotYet() {
        CommonResp<List<Share>> resp=new CommonResp<>();
        resp.setData(shareService.queryShareNotYet());
        return resp;
    }
}
