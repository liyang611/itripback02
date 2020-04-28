package cn.itrip.service.order;

import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.vo.order.ItripPersonalOrderRoomVO;
import cn.itrip.beans.vo.store.StoreVO;
import cn.itrip.common.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {
    boolean validationRoomService (Map map)throws Exception;
    ItripHotelOrder getItripHotelOrderById(Long orderId) throws  Exception;
    ItripPersonalOrderRoomVO getPersonalOrderRoomInfo(Long id) throws Exception;
    Page getPersonalOrderList(Map map) throws Exception;

    List<StoreVO> queryRoomStore(Map map) throws Exception;

    Map insertOrder(ItripHotelOrder order) throws Exception;
    ItripHotelOrder queryOrderById(Long id) throws Exception;
    int modifyOrder(ItripHotelOrder order) throws Exception;

    boolean flushOrderStatus(Integer type) throws Exception;
}

