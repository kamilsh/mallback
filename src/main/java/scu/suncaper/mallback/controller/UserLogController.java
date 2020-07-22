package scu.suncaper.mallback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import scu.suncaper.mallback.dao.UserDAO;
import scu.suncaper.mallback.pojo.User;
import scu.suncaper.mallback.result.Result;
import scu.suncaper.mallback.result.ResultFactory;
import scu.suncaper.mallback.service.UserService;

import java.util.Objects;

@RestController
public class UserLogController {
    @Autowired
    UserService userService;

    @CrossOrigin
    @PostMapping("/api/login/user")
    @ResponseBody
    public Result UserLogin(@RequestBody User requestUser) {
        // 对 html 标签进行转义，防止 XSS 攻击
        System.out.println(requestUser);
        String uname = requestUser.getUname();
        uname = HtmlUtils.htmlEscape(uname);
        String password = requestUser.getPassword();

        User user = userService.findByUname(uname);
        if(user == null)
            return ResultFactory.buildFailResult("用户不存在");
        user = userService.get(uname,password);
        if(user == null)
            return ResultFactory.buildFailResult("密码不匹配");
        return ResultFactory.buildSuccessResult(uname);
    }

    @CrossOrigin
    @PostMapping("/api/register/user")
    public Result UserRegister(@RequestBody User user) {
        int status = userService.userRegister(user);
        switch (status) {
            case 0:
                return ResultFactory.buildFailResult("用户名/密码/电话号不能为空");
            case 1:
                return ResultFactory.buildSuccessResult("注册成功");
            case 2:
                return ResultFactory.buildFailResult("用户名重复");
        }
        return ResultFactory.buildFailResult("未知错误");
    }

    @CrossOrigin
    @PostMapping("/api/pwdreset/user")
    @ResponseBody
    public Result UserPwdReset(@RequestBody User requestUser) {
        String uname = HtmlUtils.htmlEscape(requestUser.getUname());
        String phone = HtmlUtils.htmlEscape(requestUser.getPhone());
        String password = requestUser.getPassword();
        User user = userService.findByUname(uname);
        if(user == null)
            return ResultFactory.buildFailResult("用户不存在");
        user = userService.findByUnameAndPhone(uname,phone);
        if(user == null)
            return ResultFactory.buildFailResult("号码不匹配");
        user.setPassword(password);
        userService.save(user);
        return ResultFactory.buildSuccessResult(uname);
    }
}










