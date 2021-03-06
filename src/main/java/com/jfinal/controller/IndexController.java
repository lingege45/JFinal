package com.jfinal.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.FirstInterceptor;
import com.jfinal.aop.NeedLogin;
import com.jfinal.aop.RegisterValidator;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.model.JsonResult;
import com.jfinal.model.User;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import org.eclipse.jetty.server.Authentication;

import java.util.List;

public class IndexController extends Controller {

    public void index(){
        renderText("IndexController index");
    }

    public void hello(){
       // renderText("<h2>hello</h2>");                //文本
        renderText("hello");
    }

    public void bye(){
       // renderHtml("<h2>bye</h2>");                  //html
        String username=get("username");
        String gender=get("gender");
        if(gender.equals("1")){
            renderHtml("bye "+username+"先生");
        }else{
            renderHtml("bye "+username+"女士");
        }

    }

    @Before({FirstInterceptor.class,NeedLogin.class})
    public void login(){
        renderHtml("登陆成功");
    }

    public void test(){
        String username=get("username");
        set("username",username);
        renderFreeMarker("test.ftl");
    }

    public void loginCheck(){
        String username=get("username");
        String password=get("password");
        String sql="SELECT * FROM t_user WHERE username= ? AND password= ?";
        List<User> users = User.dao.find(sql, username, password);
        if(users.size()!=0){
            renderHtml("登陆成功");
            setSessionAttr("username",username);
        }else {
            renderHtml("登陆失败");
        }
    }

    public void manage(){
        String username=getSessionAttr("username");
        if(username!=null){
            renderHtml("已经登陆");
        }else{
            redirect("login.html");
        }
    }

    public void logout(){
        removeSessionAttr("username");
        redirect("login.html");
    }

    //@Before(RegisterValidator.class)
    public void register(){
        //renderHtml("注册成功");
        renderFreeMarker("register.ftl");
    }

    @Before(RegisterValidator.class)
    public void doregister(){
        String username = get("username");
        String password = get("password");
        String gender = get("gender");
        String nickname = get("nickname");
        String email=get("email");

        User u=new User();
        u.set("username",username);
        u.set("password",password);
        u.set("email",email);
        u.set("nickname",nickname);
        u.set("gender",gender);

        boolean success=false;
        String message="注册失败";
        try {
            u.save();
            success=true;
            renderHtml("注册成功");
        } catch (ActiveRecordException e) {
            /*renderHtml("注册失败");
            e.printStackTrace();*/
            LogKit.error("用户注册失败,原因："+e.getMessage());
        }

        Kv result=Kv.create();
        result.set("message",message);
        result.set("success",success);
        renderJson(result);
    }

    public void json(){
        JsonResult result=new JsonResult(false,"验证码不正确");
        renderJson(result);
    }
}
