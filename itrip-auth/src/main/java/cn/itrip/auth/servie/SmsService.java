package cn.itrip.auth.servie;

public interface SmsService {
    void sendSms(String to,String templateId,String[] datas) throws Exception;
}
