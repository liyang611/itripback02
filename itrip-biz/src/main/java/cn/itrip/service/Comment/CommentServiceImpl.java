package cn.itrip.service.Comment;

import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.beans.vo.comment.ItripScoreCommentVO;
import cn.itrip.common.Page;
import cn.itrip.dao.comment.ItripCommentMapper;
import cn.itrip.dao.hotelorder.ItripHotelOrderMapper;
import cn.itrip.dao.image.ItripImageMapper;
import cn.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private ItripLabelDicMapper dicMapper;
   @Resource
   private ItripHotelOrderMapper orderMapper;
    @Resource
    private ItripCommentMapper commentMapper;
    @Resource
    private ItripImageMapper imageMapper;
    @Transactional
    @Override
    public void addComment(ItripComment comment, List<ItripImage> imgList) throws Exception {
        int sum = comment.getFacilitiesScore()+comment.getServiceScore()+comment.getPositionScore()+comment.getHygieneScore();
        int score = (int)Math.round(sum*1.0 / 4);
        comment.setScore(score);
        if(commentMapper.insertItripComment(comment) > 0){
            if(imgList != null && imgList.size() > 0){
                for(ItripImage img : imgList){
                    imageMapper.insertItripImage(img);
                }
            }
        }
        orderMapper.updateHotelOrderStatus(comment.getOrderId(),comment.getCreatedBy());
    }
    /*page分页*/
    @Override
    public Page<ItripListCommentVO> getCommentlist(Map map)throws Exception{
        List list=commentMapper.getItripCommentListByMap(map);
        Integer total=commentMapper.getItripCommentCountByMap(map);
        Integer pageNo=(Integer)map.get("pageNO");
        Integer pageSize=(Integer)map.get("pageSize");
        Page page=new Page(pageNo,pageSize,total);
        page.getRows(list);
        return page;
    }

    @Override
    public List<ItripLabelDicVO> getTravelType()throws Exception {
        return dicMapper.getItripLabelDicByParentId(107L);
    }

    @Override
    public ItripScoreCommentVO getHotelScore(long id) throws Exception {
        return commentMapper.getCommentAvgScore(id);
    }

    @Override
    public Integer getCount(Map map) throws Exception {
        return commentMapper.getItripCommentCountByMap(map);
    }
}
