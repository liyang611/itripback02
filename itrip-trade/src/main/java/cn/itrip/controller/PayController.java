package cn.itrip.controller;

import cn.itrip.bean.AlipayConfig;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.service.OrderService;
import cn.itrip.service.PayService;
import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class PayController {
    @Resource
    private OrderService orderService;
    @Resource
    private AlipayConfig alipayConfig;
    @Resource
    private PayService payService;
    /*客户端提交订单支付请求，对该API的返回结果不用处理，
    浏览器将自动跳转至支付宝。<br><b>请使用普通表单提交，
    不能使用ajax异步提交。*/
/*准备支付接口*/
@RequestMapping(value = "/prepay/{orderNo}",method = RequestMethod.GET)
    public String prepay(@PathVariable String orderNo, Model model){
    try {
        ItripHotelOrder order=orderService.loadItripHotelOrder(orderNo);
        model.addAttribute("orderNo",orderNo);
        model.addAttribute("hotelName",order.getHotelName());
        model.addAttribute("payAmount",order.getPayAmount());
        model.addAttribute("roomId",order.getRoomId());
        model.addAttribute("count",order.getCount());
        return "pay";
    } catch (Exception e) {
        e.printStackTrace();
    }return "notfound";
}
    /*支付结果*/
    @RequestMapping(value="/notify",method = RequestMethod.POST)
    public String trackPaymentStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType()); //调用SDK验证签名
        if(signVerified){
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            if(trade_status.equals("TRADE_FINISHED")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                orderService.paySuccess(out_trade_no,1,trade_no);
                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            }else if (trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                orderService.paySuccess(out_trade_no,1,trade_no);
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
            }
            return "success";
        }else{
            return "failure";
        }
    }

    @RequestMapping(value="/return",method = RequestMethod.GET)
    public void success(HttpServletRequest request,HttpServletResponse response) throws Exception {
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType()); //调用SDK验证签名
        if(signVerified){
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            Long id = orderService.loadItripHotelOrder(out_trade_no).getId();
            response.sendRedirect(String.format(alipayConfig.getPaymentSuccessUrl(),out_trade_no,id));
        }else{
            response.sendRedirect(String.format(alipayConfig.getPaymentFailureUrl()));
        }
    }
}
