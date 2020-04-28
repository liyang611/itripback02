package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.order.*;
import cn.itrip.beans.vo.store.StoreVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.common.ValidationToken;
import cn.itrip.service.Room.RoomService;
import cn.itrip.service.hotel.HotelService;
import cn.itrip.service.order.OrderService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/aqi/hotelorder")
public class OrderController {
    @Resource
    private ValidationToken validationToken;
    @Resource
    private OrderService orderService;
    @Resource
    private HotelService hotelService;
    @Resource
    private RoomService roomService;
    /*修改预约日期验证是否有房*/
    @RequestMapping(value = "/validateroomstore",method = RequestMethod.POST,headers = "token.jsn")
    @ResponseBody
    public Dto validateRoomStore(@RequestBody ValidateRoomStoreVO vo, HttpServletRequest request){
    String token=request.getHeader("token");
    ItripUser user =validationToken.getCurrentUser(token);
    if (user==null){
        return DtoUtil.returnFail("token,失效，请重新登录","100000");
    }
    if (EmptyUtils.isEmpty(vo.getHotelId())){
        return DtoUtil.returnFail("hotelId不能为空","100515");
    }
    if (EmptyUtils.isEmpty(vo.getRoomId())){
        return DtoUtil.returnFail("RoomId不能为空","100516");
    }
        Map map=new HashMap();
    map.put("hotelId",vo.getHotelId());
    map.put("roomId",vo.getRoomId());
    map.put("starTime",vo.getCheckInDate());
    map.put("endTie",vo.getHotelId());
    map.put("count",vo.getCount());
        try {
            boolean flag=orderService.validationRoomService(map);
            map.clear();
            map.put("flag",flag);
            return DtoUtil.returnSuccess("成功",map);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常","100517");
        }


    }
    /*根据订单ID查看个人订单详情*/
    @RequestMapping(value = "getpersonalorderinfo / {orderId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto getpersonalorderinfo (@PathVariable String orderId,HttpServletRequest request) {
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if (EmptyUtils.isEmpty(orderId)) {
            return DtoUtil.returnFail("请传递参数：orderId", "100525");
        }
        try {
            ItripHotelOrder itripHotelOrder = orderService.getItripHotelOrderById(Long.parseLong(orderId));
        if (itripHotelOrder==null){return DtoUtil.returnFail("没有相关订单","100526");}
            ItripPersonalHotelOrderVO vo=new ItripPersonalHotelOrderVO();
            BeanUtils.copyProperties(itripHotelOrder,vo);
            Object ok= JSON.parse("{\"1\":\"订单提交\",\"2\":\"订单支付\",\"3\":\"支付成功\",\"4\":\"入住\",\"5\":\"订单点评\",\"6\":\"订单完成\"}");
            Object cancle = JSON.parse("{\"1\":\"订单提交\",\"2\":\"订单支付\",\"3\":\"订单取消\"}");
        if(itripHotelOrder.getOrderStatus()==0){
            vo.setOrderProcess(ok);
            vo.setProcessNode("2");
        }else if (itripHotelOrder.getOrderStatus()==1){
            vo.setOrderProcess(cancle);
            vo.setProcessNode("3");
        } else if (itripHotelOrder.getOrderStatus() == 2) {
            vo.setOrderProcess(ok);
            vo.setProcessNode("3");
        }else if(itripHotelOrder.getOrderStatus() == 3){
            vo.setOrderProcess(ok);
            vo.setProcessNode("5");
        }else if(itripHotelOrder.getOrderStatus() == 4){
            vo.setOrderProcess(ok);
            vo.setProcessNode("6");
        }else{
            vo.setOrderProcess(null);
            vo.setProcessNode(null);
        }
            vo.setRoomPayType(itripHotelOrder.getPayType());
            return DtoUtil.returnDataSuccess(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单信息错误","100527");
        }
    }
    /*根据订单ID查看个人订单详情-房型相关信息*/
    @RequestMapping(value = "getpersonalorderroominfo / {orderId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto getpersonalorderroominfo ( String orderId,HttpServletRequest request) {
    String toenk=request.getHeader("toenk");
    ItripUser user=validationToken.getCurrentUser(toenk);
    if (user==null){
        return DtoUtil.returnFail("toenk失效,请重新登陆","100000");
    }if (EmptyUtils.isEmpty(orderId)){
        return DtoUtil.returnFail("请传递参数：ordrId","100530");
        }
        try {
            ItripPersonalOrderRoomVO roomInfo=orderService.getPersonalOrderRoomInfo(Long.parseLong(orderId));
            if (roomInfo==null){
                return DtoUtil.returnFail("没有相关订单房型","100530");
            }return DtoUtil.returnSuccess("获取成功",roomInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单房型信息错误","100531");
        }
    }
    /*根据个人订单列表，并分页显示*/
    @RequestMapping(value = "getpersonalorderlist",method = RequestMethod.POST)
    @ResponseBody
    public Dto getpersonalorderlist(@RequestBody ItripSearchOrderVO vo,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(vo.getOrderType())){
            return DtoUtil.returnFail("请传递参数：orderType","100501");
        }
        if(EmptyUtils.isEmpty(vo.getOrderStatus())){
            return DtoUtil.returnFail("请传递参数：orderStatus","100502");
        }
        Map map = new HashMap();
        map.put("orderNo",vo.getOrderNo());
        map.put("orderType",vo.getOrderType() == -1 ? null : vo.getOrderType());
        map.put("orderStatus",vo.getOrderStatus() == -1 ? null : vo.getOrderStatus());
        map.put("userId",user.getId());
        map.put("linkUserName",vo.getLinkUserName());
        map.put("startDate",vo.getStartDate());
        map.put("endDate",vo.getEndDate());
        Integer pageNo = vo.getPageNo() == null ? 1 : vo.getPageNo();
        Integer pageSize = vo.getPageSize() == null ? 5 : vo.getPageSize();
        map.put("pageNo",pageNo);
        map.put("pageSize",pageSize);
        try {
            Page<ItripHotelOrder> page = orderService.getPersonalOrderList(map);
            return DtoUtil.returnSuccess("获取成功",page);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单列表错误","100503");
        }
    }
    /*生成订单前，获取预订信息*/
    @RequestMapping(value="/getpreorderinfo",method = RequestMethod.POST)
    @ResponseBody
    public Dto getPreOrderinfo(@RequestBody ValidateRoomStoreVO vo,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(vo.getHotelId())){
            return DtoUtil.returnFail("hotelId不能为空","100510");
        }
        if(EmptyUtils.isEmpty(vo.getRoomId())){
            return DtoUtil.returnFail("roomId不能为空","100511");
        }
        try {
            ItripHotel hotel = hotelService.queryHotelById(vo.getHotelId());
            ItripHotelRoom room = roomService.queryRoomById(vo.getRoomId());

            Map map = new HashMap();
            map.put("hotelId", vo.getHotelId());
            map.put("roomId", vo.getRoomId());
            map.put("startTime",vo.getCheckInDate());
            map.put("endTime",vo.getCheckOutDate());

            boolean flag = orderService.validationRoomService(map);
            if(!flag){
                return DtoUtil.returnFail(" 暂时无房","100512");
            }
            List<StoreVO> list = orderService.queryRoomStore(map);
            PreAddOrderVO preAddOrderVO = new PreAddOrderVO();
            preAddOrderVO.setHotelId(vo.getHotelId());
            preAddOrderVO.setRoomId(vo.getRoomId());
            preAddOrderVO.setCheckInDate(vo.getCheckInDate());
            preAddOrderVO.setCheckOutDate(vo.getCheckOutDate());
            preAddOrderVO.setHotelName(hotel.getHotelName());
            preAddOrderVO.setPrice(room.getRoomPrice());
            preAddOrderVO.setCount(vo.getCount());
            preAddOrderVO.setStore(list.get(0).getStore());
            return DtoUtil.returnSuccess("获取成功",preAddOrderVO);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常","100513");
        }
    }
    /*支付成功后查询订单信息*/
    @RequestMapping(value = " querysuccessorderinfo / {id}",method = RequestMethod.GET)
    @ResponseBody
    public Dto querysuccessorderinfo ( @PathVariable Long id ,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("id不能为空","100519");
        }
        try {
            ItripHotelOrder order = orderService.getItripHotelOrderById(id);
            if(EmptyUtils.isEmpty(order)){
                return DtoUtil.returnFail("没有查询到相应订单", "100519");
            }
            ItripHotelRoom room = roomService.queryRoomById(order.getRoomId());
            Map map = new HashMap();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("payType", order.getPayType());
            map.put("payAmount", order.getPayAmount());
            map.put("hotelName", order.getHotelName());
            map.put("roomTitle", room.getRoomTitle());
            return DtoUtil.returnSuccess("获取数据成功",map);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取数据失败","100520");
        }
    }

}
