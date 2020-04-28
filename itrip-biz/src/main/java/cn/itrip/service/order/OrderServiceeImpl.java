package cn.itrip.service.order;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import cn.itrip.beans.vo.order.ItripPersonalOrderRoomVO;
import cn.itrip.beans.vo.store.StoreVO;
import cn.itrip.common.DateUtil;
import cn.itrip.common.Page;
import cn.itrip.dao.hotelorder.ItripHotelOrderMapper;
import cn.itrip.dao.hoteltempstore.ItripHotelTempStoreMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.*;

@Service
@Transactional
public class OrderServiceeImpl implements OrderService{
    @Resource
    private ItripHotelTempStoreMapper tempStoreMapper;
    @Resource
    private ItripHotelOrderMapper orderMapper;
    @Resource
    private ItripHotelTempStoreMapper storeMapper;
    @Override
    public boolean validationRoomService(Map map) throws Exception {
        List<Date> beteList =DateUtil.getBetweenDates((Data) map.get("startTime"),(Data)map.get("endTime"));
        for (Date data:beteList){
            map.put("time",data);
            ItripHotelTempStore tempStore=tempStoreMapper.queryByTime(map);
            if (tempStore==null){
                ItripHotelTempStore store=new ItripHotelTempStore();
                store.setHotelId((Integer) map.get("hotrlId"));
                store.setRoomId((Long) map.get("roomId"));
                store.getStore(20);
                store.setRecordDate(data);
                store.setCreationDate(new Date());
                tempStoreMapper.insertItripHotelTempStore(store);
            }
            List<StoreVO>list=tempStoreMapper.queryRoomStore(map);
        for (StoreVO vo:list){
            if (vo.getStore()<(Integer) map.get("coun")){
                return false;
            }
        }
        }
        return true;
    }

    @Override
    public ItripHotelOrder getItripHotelOrderById(Long Id) throws Exception {
        return orderMapper.getItripHotelOrderById(Id);
    }

    @Override
    public ItripPersonalOrderRoomVO getPersonalOrderRoomInfo(Long id) throws Exception {
        return orderMapper.getItripHotelOrderRoomInfoById(id);
    }

    @Override
    public Page getPersonalOrderList(Map map) throws Exception {
        /*查询订单*/
        Integer beginPos=((Integer) map.get("pageNo")-1*(Integer) map.get("pageSize"));
        map.put("beginPos",beginPos);
        List list=orderMapper.getItripHotelOrderListByMap(map);
        Integer total=orderMapper.getItripHotelOrderCountByMap(map);
        /*分页*/
        Page<ItripHotelOrder> page = new Page((Integer)map.get("pageNo"),(Integer)map.get("pageSize"),total);
        page.setRows(list);
        return page;
    }

    @Override
    public List<StoreVO> queryRoomStore(Map map) throws Exception {
        return storeMapper.queryRoomStore(map);
    }

    @Override
    public Map insertOrder(ItripHotelOrder order) throws Exception {
        Map map = new HashMap();
        orderMapper.insertItripHotelOrder(order);
        map.put("id",order.getId());
        map.put("orderNo",order.getOrderNo());
        return map;
    }

    @Override
    public ItripHotelOrder queryOrderById(Long id) throws Exception {
        return orderMapper.getItripHotelOrderById(id);
    }

    @Override
    public int modifyOrder(ItripHotelOrder order) throws Exception {
        return orderMapper.updateItripHotelOrder(order);
    }

    @Override
    public boolean flushOrderStatus(Integer type) throws Exception {
        Integer i = 0;
        if(type == 1){
            i = orderMapper.flushCancelOrderStatus();
        }else if(type == 2){
            i = orderMapper.flushSuccessOrderStatus();
        }
        return i == 1;
    }
}
