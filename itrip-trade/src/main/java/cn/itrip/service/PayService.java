package cn.itrip.service;

import cn.itrip.bean.AlipayBean;


public interface PayService {
    String alipay(AlipayBean alipayBean) throws Exception;
}
