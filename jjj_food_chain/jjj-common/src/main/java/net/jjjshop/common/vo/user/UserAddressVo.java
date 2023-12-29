package net.jjjshop.common.vo.user;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.jjjshop.common.entity.user.UserAddress;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel("用户地址VO")
public class UserAddressVo extends UserAddress {


}
