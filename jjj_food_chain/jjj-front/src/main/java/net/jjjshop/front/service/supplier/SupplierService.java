package net.jjjshop.front.service.supplier;

import net.jjjshop.common.entity.supplier.Supplier;
import net.jjjshop.common.entity.user.User;
import net.jjjshop.framework.common.service.BaseService;
import net.jjjshop.framework.core.pagination.Paging;
import net.jjjshop.front.vo.supplier.SupplierVo;

/**
 * 供应商表 服务类
 *
 * @author jjjshop
 * @since 2022-10-17
 */
public interface SupplierService extends BaseService<Supplier> {

    SupplierVo getDetail(User user);

    SupplierVo getDetailById(Integer shopSupplierId);
}
