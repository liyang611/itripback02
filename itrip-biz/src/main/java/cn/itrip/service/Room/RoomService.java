package cn.itrip.service.Room;

import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.vo.ItripLabelDicVO;

import java.util.List;
import java.util.Map;

public interface RoomService {
    List<ItripLabelDicVO> queryRoomBed() throws Exception;

    List<ItripHotelRoom> queryHotelRoomByHotel(Map map) throws Exception;
    ItripHotelRoom queryRoomById(Long id) throws Exception;
}
