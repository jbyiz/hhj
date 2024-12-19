package top.mqxu.share.content.domain.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.mqxu.share.content.domain.entity.Share;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareResp {

    /**
     * 分享的内容
     */
    private Share share;

    /**
     * 发布者的昵称
     */
    private String nickname;

    /**
     * 发布者的头像
     */
    private String avatarUrl;
}
