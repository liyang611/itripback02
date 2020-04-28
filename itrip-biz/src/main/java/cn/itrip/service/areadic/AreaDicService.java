package cn.itrip.service.areadic;

import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripComment;

import java.util.List;
import java.util.Map;

public interface AreaDicService {
    List<ItripAreaDic>getAreaDicList(Map map)throws Exception;
    List<ItripComment>getimg(Map map)throws Exception;
}
