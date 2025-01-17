package net.jjjshop.common.settings.vo.agent;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel("分销商基础设置VO")
public class BasicVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer isOpen;
    private Integer level;
    private Integer selfBuy;

    public BasicVo() {
        this.isOpen = 0;
        this.level = 3;
        this.selfBuy = 0;
    }
}
