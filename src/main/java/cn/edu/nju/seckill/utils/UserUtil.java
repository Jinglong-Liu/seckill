package cn.edu.nju.seckill.utils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.nju.seckill.pojo.User;
import cn.edu.nju.seckill.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 生成用户工具类
 */
public class UserUtil {
    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>();
        for(int i = 0;i<=count;i++){
            User user = new User();
            user.setLoginCount(1);
            user.setId(13000000000L + i);
            user.setNickname("user" + i);
            user.setSlat("1a2b3c4d");
            user.setRegisterDate(new Date());
            user.setPasword(MD5Util.inputPassToDBPass("123456",user.getSlat()));
            users.add(user);
        }
        Connection conn = getConn();
        String sql = "insert into t_user(login_count, nickname, register_date, slat, pasword, id)values(?,?,?,?,?,?)";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         for (int i = 0; i < users.size(); i++) {
         	User user = users.get(i);
         	pstmt.setInt(1, user.getLoginCount());
         	pstmt.setString(2, user.getNickname());
         	pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
         	pstmt.setString(4, user.getSlat());
         	pstmt.setString(5, user.getPasword());
         	pstmt.setLong(6, user.getId());
         	pstmt.addBatch();
         }
         pstmt.executeBatch();
         pstmt.close();
         conn.close();
         System.out.println("insert to db");
         //生成ticket
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("E:\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            ((OutputStream) out).flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket : " + user.getId() +" " + userTicket);

            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();

        System.out.println("over");
    }
    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "1234";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
}
