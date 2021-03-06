package cn.itrip.auth.servie;

import cn.itrip.beans.pojo.ItripUser;

public interface UserService {
    ItripUser findByUserCode(String userCode) throws Exception;
    ItripUser login(String name,String password) throws Exception;

    void createUserByPhone(ItripUser user) throws Exception;
    boolean validatePhone(String phoneNum,String code) throws Exception;
    /*邮箱*/
    void createUserByMail(ItripUser user) throws Exception;
    boolean validateMail (String mail, String code)throws Exception;
}
