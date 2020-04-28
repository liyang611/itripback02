package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripAreaDicVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import cn.itrip.service.Img.ItripImgService;
import cn.itrip.service.areadic.AreaDicService;
import cn.itrip.service.feature.FeatureService;
import cn.itrip.service.hotel.HotelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/hotel")
public class HotelController {
    @Resource
    private FeatureService featureService;
    @Resource
    private ItripImgService imgService;
    @Resource
    private HotelService hotelService;
   @Resource
    private AreaDicService areaDicService;
   /*查询商圈*/
    @RequestMapping(value = "/querytradearea/{cityId}",method = RequestMethod.POST)
    @ResponseBody
    public Dto queryTradeArea(@PathVariable Integer cityId){
       if(EmptyUtils.isEmpty(cityId)){
           return DtoUtil.returnFail("cityId不能为空", ErrorCode.BIZ_CITYID_NOTFOUND);
       }
        Map map=new HashMap();
       map.put("isTradingArea",1);
       map.put("parent",cityId);

        try {
            List<ItripAreaDic> areaDicslist= areaDicService.getAreaDicList(map);
            List<ItripTokenVO> voList=new ArrayList<>();
            for (ItripAreaDic diz:areaDicslist){
                ItripTokenVO vo=new ItripTokenVO();
                BeanUtils.copyProperties(diz,vo);
                voList.add(vo);
            }
            return DtoUtil.returnDataSuccess(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }
    }
    /*. 根据targetId查询酒店图片(type=0)*/
    @RequestMapping(value = "/getimg /{targetId}",method = RequestMethod.POST)
    @ResponseBody
    public Dto getimg(@PathVariable Integer targetId){

        if(EmptyUtils.isEmpty(targetId)){
            return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_TARGETID_UNKNOWN);
        }
        Map map=new HashMap();
        map.put("isTradingArea",0);
        map.put("parent",targetId);
        try {
            List<ItripComment> areaDicslist= areaDicService.getimg(map);
            if (areaDicslist.size()==0){
                return DtoUtil.returnFail("获取失败",ErrorCode.BIZ_TARGETID_UNKNOWN);
            }
    return DtoUtil.returnDataSuccess(areaDicslist);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_TARGETID_NOTFOUND);
        }
    }
/*根据酒店id查询酒店设施*/
@RequestMapping(value = "/queryhotelfacilities/{id}",method = RequestMethod.POST)
    public Dto queryhotelfacilities(@PathVariable long id) throws Exception {
        if(EmptyUtils.isEmpty(id)){return DtoUtil.returnFail("id",ErrorCode.BIZ_HOTELID_NOTNULL);}
    try {
        ItripSearchFacilitiesHotelVO vo= hotelService.queryHotelFacilities(id);
        return DtoUtil.returnDataSuccess(vo.getFacilities());
    }catch (Exception e){
        e.printStackTrace();
        return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
    }
    }
    /*根据酒店id查询酒店特色、商圈、酒店名称*/
    @RequestMapping(value = "getvideodesc/{hotelId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto getvideodesc(@PathVariable String hotelId){
        if (EmptyUtils.isEmpty(hotelId)) {
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_IDNOTNOLL);
        }
        try {
            HotelVideoDescVO vo=hotelService.queryVideoDesc(Long.parseLong(hotelId));
            return DtoUtil.returnDataSuccess(vo);

            }catch (Exception e){
                e.printStackTrace();
                return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
            }
        }


    /*查询酒店特色列表*/
    @RequestMapping(value = "queryhotelfeature",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryhotelfeature(){
        try {
        Map map=new HashMap();
        map.put("parentId",16);
            List<ItripLabelDic> dicList=hotelService.queryHotelFeature(map);
            List<ItripLabelDicVO> voList=new ArrayList<>();
            for (ItripLabelDic dic:dicList){
                ItripLabelDicVO vo = new ItripLabelDicVO();
                BeanUtils.copyProperties(dic,vo);
                voList.add(vo);
            }
            return DtoUtil.returnDataSuccess(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }
        }
    /*查询热门城市*/
    @RequestMapping(value = "queryhotcity/{type}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryHotcity(@PathVariable Integer type){
        Map map=new HashMap();
        map.put("isHot",1);
        map.put("isChina",type);
        try {
            List<ItripAreaDic> dicList=areaDicService.getAreaDicList(map);
            List<ItripAreaDicVO> voList=new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;}
    /*根据酒店id查询酒店特色和介绍*/
    @RequestMapping(value = "queryhoteldetails/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryhoteldetails(@PathVariable long id){
        if(EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            List<ItripLabelDic> dicList = featureService.queryDetailsHotel(id);
            List<ItripSearchDetailsHotelVO> list = new ArrayList<>();
            for(ItripLabelDic dic : dicList){
                ItripSearchDetailsHotelVO vo = new ItripSearchDetailsHotelVO();
                BeanUtils.copyProperties(dic,vo);
                list.add(vo);
            }
            return DtoUtil.returnDataSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }

    }
    /*根据酒店id查询酒店政策*/
    @RequestMapping(value = "queryhotelpolicy/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryhotelpolicy(@PathVariable long id){if(EmptyUtils.isEmpty(id)){
        return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_HOTELID_NOTNULL);
    }
        try {
            ItripSearchPolicyHotelVO policy = hotelService.queryHotelPolicy(id);
            return DtoUtil.returnDataSuccess(policy);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }}

}
