package cn.itrip.auth.controller;


import cn.itrip.auth.servie.UserService;
import cn.itrip.auth.servie.TokenService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.common.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.regex.Pattern;
@Api(value = "用户Controller",tags = {"用户操作接口"})
@Controller
@RequestMapping("/api")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private TokenService tokenService;
    @RequestMapping(value="/dologin",method = RequestMethod.POST)
    @ResponseBody
    /*登陆*/
    public Dto dologin(String name, String password, HttpServletRequest request){
        try {
            ItripUser user = userService.login(name, MD5.getMd5(password,32));
            //登录失败
            if(user == null){
                return DtoUtil.returnFail("用户名密码错误", ErrorCode.AUTH_UNKNOWN);
            }
            //登录成功  生成token
            String userAgent = request.getHeader("user-agent");
            String token = tokenService.generateToken(userAgent, user);
            //保存token到redis
            tokenService.saveToken(token,user);
            //返回一个vo对象
            ItripTokenVO vo = new ItripTokenVO(token,
                    Calendar.getInstance().getTimeInMillis()+2*60*60*1000,
                    Calendar.getInstance().getTimeInMillis());
            return DtoUtil.returnSuccess("登录成功",vo);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_UNKNOWN);
        }
    }
    @RequestMapping(value="/logout",method = RequestMethod.GET,headers = "token")
    /*退出*/
    @ResponseBody
    public Dto logout(HttpServletRequest request){
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if(tokenService.validateToken(userAgent,token)){
                tokenService.deleteToken(token);
                return DtoUtil.returnSuccess("退出成功");
            }else{
                return DtoUtil.returnFail("token无效",ErrorCode.AUTH_TOKEN_INVALID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_UNKNOWN);
        }
    }
    /*注册用户*/
    @ApiOperation(value = "通过手机",httpMethod = "POST",response = Dto.class,notes = "通过手机")
    @RequestMapping(value="/registerbyphone",method = RequestMethod.POST)
    @ResponseBody
    public Dto registerByPhone(@ApiParam(name = "userVO",value = "实体用户",required = true)
            @RequestBody ItripUserVO vo){
        if(!validatePhone(vo.getUserCode())){
            return DtoUtil.returnFail("请输入正确的手机号",ErrorCode.AUTH_ILLEGAL_USERCODE);
        }
        ItripUser user = new ItripUser();
        user.setUserCode(vo.getUserCode());
        user.setUserName(vo.getUserName());

        try {
            if(userService.findByUserCode(user.getUserCode()) != null){
                return DtoUtil.returnFail("用户已存在",ErrorCode.AUTH_USER_ALREADY_EXISTS);
            }
            user.setUserPassword(MD5.getMd5(vo.getUserPassword(),32));
            userService.createUserByPhone(user);
            return DtoUtil.returnSuccess("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_UNKNOWN);
        }
    }
    /*手机验证*/
    @RequestMapping(value="/validatephone",method = RequestMethod.PUT)
    @ResponseBody
    public Dto validatePhone(String user,String code){
        try {
            if(userService.validatePhone(user,code)){
                return DtoUtil.returnSuccess("验证成功");
            }else{
                return DtoUtil.returnSuccess("验证失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_UNKNOWN);
        }
    }
    private boolean validatePhone(String phoneNum){
        String reg = "^1[356789]\\d{9}$";
        return Pattern.compile(reg).matcher(phoneNum).find();
    }
/*邮箱登陆*/
@RequestMapping(value="/doregister",method = RequestMethod.POST)
@ResponseBody
public Dto registerByMail(@RequestBody ItripUserVO userVO){
    if (!validEmail(userVO.getUserCode())){
        return DtoUtil.returnFail("邮箱地址不正确",ErrorCode.AUTH_ILLEGAL_USERCODE);
    }
    ItripUser user = new ItripUser();
    user.setUserCode(userVO.getUserCode());
    user.setUserName(userVO.getUserName());
    try {
        if(userService.findByUserCode(user.getUserCode()) != null){
            return DtoUtil.returnFail("邮箱已被注册",ErrorCode.AUTH_USER_ALREADY_EXISTS);
        }
        user.setUserPassword(MD5.getMd5(userVO.getUserPassword(),32));
        userService.createUserByMail(user);
        return DtoUtil.returnSuccess("注册成功");
    } catch (Exception e) {
        e.printStackTrace();
        return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_UNKNOWN);
    }

}
    @RequestMapping(value="/activate",method = RequestMethod.PUT)
    @ResponseBody
    public Dto validateMail(String user,String code){
        try {
            if(userService.validateMail(user,code)){
                return DtoUtil.returnSuccess("激活成功");
            }else{
                return DtoUtil.returnFail("激活失败",ErrorCode.AUTH_UNKNOWN);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_UNKNOWN);
        }
    }

    private boolean validEmail(String email){

        String regex="^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"  ;
        return Pattern.compile(regex).matcher(email).find();
    }
}
