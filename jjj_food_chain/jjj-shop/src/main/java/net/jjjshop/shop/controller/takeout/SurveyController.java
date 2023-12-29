

package net.jjjshop.shop.controller.takeout;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.jjjshop.framework.common.api.ApiResult;
import net.jjjshop.framework.log.annotation.OperationLog;
import net.jjjshop.framework.util.ShopLoginUtil;
import net.jjjshop.shop.param.order.OrderPageParam;
import net.jjjshop.shop.service.statistics.OrderRankingService;
import net.jjjshop.shop.service.statistics.ProductRankingService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(value = "外卖订单概况", tags = {"外卖订单概况"})
@RestController
@RequestMapping("/shop/takeout/survey")
public class SurveyController {

    @Autowired
    private ProductRankingService productRankingService;

    @Autowired
    private OrderRankingService orderRankingService;

    //订单概况
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    @RequiresPermissions("/takeout/survey/index")
    @OperationLog(name = "index")
    @ApiOperation(value = "index", response = String.class)
    public ApiResult<Map<String,Object>> index(@Validated @RequestBody OrderPageParam param) throws Exception{
        Map<String,Object> result = new HashMap<>();
        //0外卖1店内
        param.setProductType(0);
        //用餐方式0外卖1店内
        param.setOrderType(0);
        param.setShopSupplierId(ShopLoginUtil.getShopSupplierId());
        result.put("detail", orderRankingService.getDataDetail(param));
        //销量排行榜
        result.put("salesNumRank", productRankingService.getSaleNumRank(param));
        //销售额排行榜
        result.put("salesMoneyRank", productRankingService.getSaleMoneyRank(param));
        return ApiResult.ok(result);
    }
}
