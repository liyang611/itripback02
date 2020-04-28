package cn.itrip.common;

/**
 * 系统错误编码，根据业务定义如下
 * <br/>
 * 酒店主业务biz：1开头（10000）<br/>
 * 评论：10001 ——10100<br/>
 * 酒店详情：10101 ——10200<br/>
 * 订单：10201 ——10400<br/>
 * 搜索search：2开头（20000）<br/>
 * 认证auth：3开头（30000）<br/>
 * 支付trade：4开头（40000）<br/>
 * @author hduser
 *
 */
public class ErrorCode {

	/*认证模块错误码-start*/
	public final static String AUTH_UNKNOWN="30000";
	public final static String AUTH_USER_ALREADY_EXISTS="30001";//用户已存在
	public final static String AUTH_AUTHENTICATION_FAILED="30002";//认证失败
	public final static String AUTH_PARAMETER_ERROR="30003";//用户名密码参数错误，为空
	public final static String AUTH_ACTIVATE_FAILED="30004";//邮件注册，激活失败
	public final static String AUTH_REPLACEMENT_FAILED="30005";//置换token失败
	public final static String AUTH_TOKEN_INVALID="30006";//token无效
	public static final String AUTH_ILLEGAL_USERCODE = "30007";//非法的用户名

	public static final String SEARCH_CITYID_NOTFOUND="20004";//城市不为空
	public static final String SEARCH_UNKNOWN="20003";//系统异常
	public static final String SEARCH_DESTINATION_NOTNULL="20002";//目的地不能为空

	public static final String BIZ_CITYID_NOTFOUND="10203";//cityId不能为空
	public static final String BIZ_UNKNOWN="10204";//系统异常，获取失败

	public static final String BIZ_TARGETID_UNKNOWN="100212";//获取酒店图片失败
	public static final String BIZ_TARGETID_NOTFOUND="100213";//targetid不能为空

	public static final String BIZ_HOTELID_NOTNULL="10206";
	public static final String BIZ_IDNOTNOLL="100215";//id不为空
	/*认证模块错误码-end*/
	public static final String SEARCH_ROOM_NOTNULL="100302";//房间id不能为空
	public static final String BIZ_ROOIMG_FAILURE="100301";//获取酒店房型图片失败
	public static final String BIZ_ROOBED_FAILURE="100305";//获取房间床型失败
	public static final String BIZ_HOTEL_NOTNULL="100303";//酒店id不能为空,酒店入住及退房时间不能为空,入住时间不能大于退房时间
	public static final String BIZ_ROOM_UNKNOWN="100304";//系统异常
}
