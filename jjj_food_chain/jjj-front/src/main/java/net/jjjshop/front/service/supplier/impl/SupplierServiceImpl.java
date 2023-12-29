package net.jjjshop.front.service.supplier.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.jjjshop.common.entity.supplier.Supplier;
import net.jjjshop.common.entity.user.User;
import net.jjjshop.common.mapper.supplier.SupplierMapper;
import net.jjjshop.common.service.settings.RegionService;
import net.jjjshop.common.util.UploadFileUtils;
import net.jjjshop.framework.common.service.impl.BaseServiceImpl;
import net.jjjshop.front.service.supplier.SupplierService;
import net.jjjshop.front.vo.supplier.SupplierVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 供应商表 服务实现类
 *
 * @author jjjshop
 * @since 2022-10-17
 */
@Slf4j
@Service
public class SupplierServiceImpl extends BaseServiceImpl<SupplierMapper, Supplier> implements SupplierService {
    @Autowired
    private UploadFileUtils uploadFileUtils;
    @Autowired
    private RegionService regionService;


    @Override
    public SupplierVo getDetail(User user) {
        Supplier supplier = this.getOne(new LambdaQueryWrapper<Supplier>()
                //是否总店，0否1是
                .eq(Supplier::getIsMain,1));
        SupplierVo vo = new SupplierVo();
        BeanUtils.copyProperties(supplier, vo);
        //店内营业时间
        vo.setStoreTimeList(Arrays.stream(vo.getStoreTime().split(",")).collect(Collectors.toList()));
        //自提营业时间
        vo.setPickTimeList(Arrays.stream(vo.getPickTime().split(",")).collect(Collectors.toList()));
        //外卖营业时间
        vo.setDeliveryTimeList(Arrays.stream(vo.getDeliveryTime().split(",")).collect(Collectors.toList()));
        //外卖配送方式
        vo.setDeliverySetList(Arrays.stream(vo.getDeliverySet().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        //店内用餐方式
        vo.setStoreSetList(Arrays.stream(vo.getStoreSet().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        vo.setBusinessFilePath(uploadFileUtils.getFilePath(vo.getBusinessId()));
        if (StringUtils.isNotEmpty(supplier.getLatitude())) {
            vo.setCoordinate(supplier.getLatitude() + "," + supplier.getLongitude());
        }
        vo.setProvince(regionService.getById(supplier.getProvinceId()).getName());
        vo.setCity(regionService.getById(supplier.getCityId()).getName());
        vo.setRegion(regionService.getById(supplier.getRegionId()).getName());
        return vo;
    }

    @Override
    public SupplierVo getDetailById(Integer shopSupplierId) {
        Supplier supplier = this.getById(shopSupplierId);
        SupplierVo vo = new SupplierVo();
        BeanUtils.copyProperties(supplier, vo);
        //店内营业时间
        vo.setStoreTimeList(Arrays.stream(vo.getStoreTime().split(",")).collect(Collectors.toList()));
        //自提营业时间
        vo.setPickTimeList(Arrays.stream(vo.getPickTime().split(",")).collect(Collectors.toList()));
        //外卖营业时间
        vo.setDeliveryTimeList(Arrays.stream(vo.getDeliveryTime().split(",")).collect(Collectors.toList()));
        //外卖配送方式
        vo.setDeliverySetList(Arrays.stream(vo.getDeliverySet().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        //店内用餐方式
        vo.setStoreSetList(Arrays.stream(vo.getStoreSet().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        vo.setBusinessFilePath(uploadFileUtils.getFilePath(vo.getBusinessId()));
        if (StringUtils.isNotEmpty(supplier.getLatitude())) {
            vo.setCoordinate(supplier.getLatitude() + "," + supplier.getLongitude());
        }
        vo.setProvince(regionService.getById(supplier.getProvinceId()).getName());
        vo.setCity(regionService.getById(supplier.getCityId()).getName());
        vo.setRegion(regionService.getById(supplier.getRegionId()).getName());
        return vo;
    }
}
