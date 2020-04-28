package cn.itrip.service.Room;

import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.dao.hotelroom.ItripHotelRoomMapper;
import cn.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class RoomServiceImpl implements RoomService {
    @Resource
    private ItripLabelDicMapper labelDicMapper;
    @Resource
    private ItripHotelRoomMapper roomMapper;
    @Override
    public List<ItripLabelDicVO> queryRoomBed() throws Exception {
        /*该思路为使用map方法给定k与y值进行查询*/
        Map map = new HashMap();
        map.put("parentId",1);
        return labelDicMapper.getItripLabelDicListByMap(map);
    }

    @Override
    public List<ItripHotelRoom> queryHotelRoomByHotel(Map map) throws Exception {
        return roomMapper.getItripHotelRoomListByMap(map);
    }

    @Override
    public ItripHotelRoom queryRoomById(Long id) throws Exception {
        return roomMapper.getItripHotelRoomById(id);
    }
}
