

package net.jjjshop.shop.controller.store;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.jjjshop.common.enums.DeliveryTypeEnum;
import net.jjjshop.common.param.order.OrderParam;
import net.jjjshop.framework.common.api.ApiResult;
import net.jjjshop.framework.log.annotation.OperationLog;
import net.jjjshop.framework.util.ShopLoginUtil;
import net.jjjshop.shop.param.order.*;
import net.jjjshop.shop.service.order.OrderAddressService;
import net.jjjshop.shop.service.order.OrderService;
import net.jjjshop.shop.vo.order.OrderVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(value = "店内订单管理", tags = {"店内订单管理"})
@RestController
@RequestMapping("/shop/store/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    @RequiresPermissions("/store/order/index")
    @OperationLog(name = "index")
    @ApiOperation(value = "index", response = String.class)
    public ApiResult<Map<String,Object>> index(@Validated @RequestBody OrderPageParam orderPageParam) throws Exception{
        Map<String,Object> result = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        //用餐方式0外卖1店内
        orderPageParam.setOrderType(1);
        orderPageParam.setShopSupplierId(ShopLoginUtil.getShopSupplierId());
        //所有订单数
        jsonObject.put("all", orderService.getCount("all",orderPageParam));
        //未付款订单数
        jsonObject.put("payment", orderService.getCount("payment",orderPageParam));
        //进行中订单数
        jsonObject.put("process", orderService.getCount("process",orderPageParam));
        //已完成订单数
        jsonObject.put("complete", orderService.getCount("complete",orderPageParam));
        //已取消订单数
        jsonObject.put("cancel", orderService.getCount("cancel",orderPageParam));
        result.put("list", orderService.getList(orderPageParam));
        result.put("orderCount", jsonObject);
        //店内配送方式
        result.put("exStyle", DeliveryTypeEnum.getStoreList());
        return ApiResult.ok(result);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @RequiresPermissions("/store/order/detail")
    @OperationLog(name = "detail")
    @ApiOperation(value = "detail", response = String.class)
    public ApiResult<OrderVo> detail(Integer orderId) throws Exception{
        return ApiResult.ok(orderService.detail(orderId));
    }
}