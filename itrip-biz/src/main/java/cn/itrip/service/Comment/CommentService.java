package cn.itrip.service.Comment;

import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.beans.vo.comment.ItripScoreCommentVO;
import cn.itrip.common.Page;

import java.util.List;
import java.util.Map;

public interface CommentService {
    void addComment(ItripComment comment, List<ItripImage> imgList) throws Exception;
    Page<ItripListCommentVO> getCommentlist(Map map)throws Exception;
    List<ItripLabelDicVO> getTravelType()throws Exception;
    ItripScoreCommentVO getHotelScore(long id)throws Exception;
    Integer getCount(Map map)throws Exception;
}
