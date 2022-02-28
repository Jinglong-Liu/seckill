package cn.edu.nju.seckill.utils;

import com.sun.jmx.snmp.internal.SnmpSubSystem;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.stereotype.Component;

import java.util.logging.SocketHandler;

@Component
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }
    private static final String salt="1a2b3c4d";
    public static String inputPassToFromPass(String inputPass){
        String str = "" +salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String fromPassToDBPass(String formPass,String salt){
        String str = "" +salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass,String salt){
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFromPass("123456"));//a86ceca3a64d8cea4b031f08ff91c110
        System.out.println(fromPassToDBPass(inputPassToFromPass("123456"),"1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }
}
