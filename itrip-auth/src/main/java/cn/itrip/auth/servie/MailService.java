package cn.itrip.auth.servie;

public interface MailService {
    void sendMail(String mailAddr, String code) throws Exception;
}
