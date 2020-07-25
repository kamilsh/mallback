package scu.suncaper.mallback.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scu.suncaper.mallback.pojo.Cart;
import scu.suncaper.mallback.pojo.Order;
import scu.suncaper.mallback.result.Result;
import scu.suncaper.mallback.result.ResultFactory;
import scu.suncaper.mallback.service.OrderService;
import java.util.List;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @CrossOrigin
    @PostMapping("/api/search")
    @ResponseBody
    public List<Object[]> showAllOrders() {
        return orderService.findAllOrders();
    }


    //从购物车里增加未支付订单
    @CrossOrigin
    @PostMapping("/api/order/addPayOrder1")
    @ResponseBody
    public Result addOrderPay1(@RequestBody Cart cartToPay) {
        Integer cid = cartToPay.getCid();
        int proNum=orderService.orderProNum(cid);
        int cartNum=orderService.orderCartNum(cid);
        if(cartNum<=proNum){
            orderService.addOrderPay1(cid,1);
            orderService.dropCartOrder(cid);
            return ResultFactory.buildSuccessResult(cartToPay.getCid());
        }else{
            orderService.dropCartOrder(cid);
            return ResultFactory.buildFailResult("商品库存不足！");
        }
    }

    //从购物车里增加已支付订单
    @CrossOrigin
    @PostMapping("/api/order/addPayOrder2")
    @ResponseBody
    public Result addOrderPay2(@RequestBody Cart cartToPay) {
        Integer cid = cartToPay.getCid();
        int proNum=orderService.orderProNum(cid);
        int cartNum=orderService.orderCartNum(cid);
        int pid =orderService.cartPid(cid);
        int num = orderService.orderCartNum(cid);
        if(cartNum<=proNum){
            orderService.addOrderPay2(cid,1);
            orderService.orderProDrop(pid,num);
            orderService.dropCartOrder(cid);
            return ResultFactory.buildSuccessResult(cartToPay.getCid());
        }else{
            orderService.dropCartOrder(cid);
            return ResultFactory.buildFailResult("商品库存不足！");
        }
    }

    //用户支付“未支付”
    @CrossOrigin
    @PostMapping("/api/order/orderPay")
    @ResponseBody
    public Result orderPay(@RequestBody Order orderToPay) {
        Integer oid = orderToPay.getOid();
        int proNum=orderService.orderProNum1(oid);
        int orderNum=orderService.orderNumber(oid);
        int pid=orderService.orderPid(oid);
        int num=orderService.orderNumber(oid);
        if(orderNum<=proNum){
            orderService.orderPay(oid);
            orderService.orderProDrop(pid,num);
            return ResultFactory.buildSuccessResult(orderToPay.getOid());
        }else{
            return ResultFactory.buildFailResult("商品库存不足！");
        }
    }



    @CrossOrigin
    @PostMapping("/api/searchBy/sname")
    @ResponseBody
    public List<Object[]> showOrdersBySname(@RequestBody String snameToShow) {
        JSON sname = com.alibaba.fastjson.JSONObject.parseObject(snameToShow);
        String name = ((JSONObject) sname).getString("input");
        List<Object[]> AllOrdersForSaler = orderService.getOrdersBySname(name);
        return AllOrdersForSaler;
    }
    @CrossOrigin
    @PostMapping("/api/searchBy/uname")
    @ResponseBody
    public List<Object[]> showOrdersByUname(@RequestBody String unameToShow) {
        JSON sname = com.alibaba.fastjson.JSONObject.parseObject(unameToShow);
        String name = ((JSONObject) sname).getString("input");
        return orderService.getOrdersByUname(name);
    }
    @CrossOrigin
    @PostMapping("/api/searchBy/pname/saler")
    @ResponseBody
    public List<Object[]> showOrdersByPnameForSaler(@RequestBody String pnameToShow) {
        JSON pname = com.alibaba.fastjson.JSONObject.parseObject(pnameToShow);
        String targetPname = ((JSONObject) pname).getString("input");
        String salerName = ((JSONObject) pname).getString("myName");
        return orderService.getOrdersByPnameAndSname(targetPname, salerName);
    }
    @CrossOrigin
    @PostMapping("/api/searchBy/uname/saler")
    @ResponseBody
    public List<Object[]> ShowOrdersByUnameForSaler(@RequestBody String unameToShow) {
        JSON pname = com.alibaba.fastjson.JSONObject.parseObject(unameToShow);
        String targetUname = ((JSONObject) pname).getString("input");
        String salerName = ((JSONObject) pname).getString("myName");
        return orderService.getOrdersByUnameAndSname(targetUname, salerName);
    }
    @CrossOrigin
    @PostMapping("/api/searchBy/pname")
    @ResponseBody
    public List<Object[]> showOrdersByPname(@RequestBody String pnameToShow) {
        JSON pname = com.alibaba.fastjson.JSONObject.parseObject(pnameToShow);
        String name = ((JSONObject) pname).getString("input");
        return orderService.getOrdersByPname(name);
    }
    @CrossOrigin
    @PostMapping("/api/cart/deleteUserOrder")
    @ResponseBody
    public void dropById(@RequestBody Order orderToDelete) {
        Integer oid = orderToDelete.getOid();
        orderService.deleteCertain(oid);
    }

    //用户删除“未支付”
    @CrossOrigin
    @PostMapping("/api/order/dropUnpaid")
    @ResponseBody
    public Result dropOrderUnpaid(@RequestBody Order orderToDelete) {
        Integer oid = orderToDelete.getOid();
        orderService.dropOrder_unpaid(oid);
        return ResultFactory.buildSuccessResult(orderToDelete.getOid());
    }

    //用户删除“已支付未发货”
    @CrossOrigin
    @PostMapping("/api/order/dropSend")
    @ResponseBody
    public Result dropOrderSend(@RequestBody Order orderToDelete) {
        Integer oid = orderToDelete.getOid();
        int pid=orderService.orderPid(oid);
        int num = orderService.orderNumber(oid);
        orderService.orderProPlus(pid,num);
        orderService.dropOrder_unpaid(oid);
        return ResultFactory.buildSuccessResult(orderToDelete.getOid());
    }


    //用户查看全部订单
    @CrossOrigin
    @PostMapping("/api/userorder/view")
    @ResponseBody
    public List<List<String>> view(@RequestBody String unameToShow) {
        JSON uname = com.alibaba.fastjson.JSONObject.parseObject(unameToShow);
        String name = ((JSONObject) uname).getString("myName");
        List<List<String>> orders= orderService.getUserOrder(name);
        for(int i=0;i<orders.size();i++){
            if(orders.get(i).get(7).equals("1")){
                orders.get(i).add(8,"已发货");
            }else{
                if(orders.get(i).get(6).equals("0")){
                    orders.get(i).add(8,"未支付");
                }else if(orders.get(i).get(6).equals("1")){
                    orders.get(i).add(8,"待发货");
                }
            }
        }
        return orders;
    }

    //用户查看未支付订单
    @CrossOrigin
    @PostMapping("/api/userorder/view1")
    @ResponseBody
    public List<List<String>> view1(@RequestBody String unameToShow) {
        JSON uname = com.alibaba.fastjson.JSONObject.parseObject(unameToShow);
        String name = ((JSONObject) uname).getString("myName");
        List<List<String>> orders= orderService.getUserOrder1(name);
        for(int i=0;i<orders.size();i++){
            orders.get(i).add(8,"未支付");
        }
        return orders;
    }

    //用户查看未支付订单-list
    @CrossOrigin
    @PostMapping("/api/userorder/viewlist")
    @ResponseBody
    public List<List> viewList(@RequestBody String unameToShow) {
        JSON uname = com.alibaba.fastjson.JSONObject.parseObject(unameToShow);
        String name = ((JSONObject) uname).getString("myName");
        return orderService.getUserOrder_list(name);
    }

    //用户查看待发货订单-list
    @CrossOrigin
    @PostMapping("/api/userorder/viewlistSend")
    @ResponseBody
    public List<List> viewList2(@RequestBody String unameToShow) {
        JSON uname = com.alibaba.fastjson.JSONObject.parseObject(unameToShow);
        String name = ((JSONObject) uname).getString("myName");
        return orderService.getUserOrder2_list(name);
    }

    //用户查看待发货订单
    @CrossOrigin
    @PostMapping("/api/userorder/view2")
    @ResponseBody
    public List<List<String>> view2(@RequestBody String unameToShow) {
        JSON uname = com.alibaba.fastjson.JSONObject.parseObject(unameToShow);
        String name = ((JSONObject) uname).getString("myName");
        List<List<String>> orders= orderService.getUserOrder2(name);
        for(int i=0;i<orders.size();i++){
            orders.get(i).add(8,"未发货");
        }
        return orders;
    }

    //用户查看待收货订单
    @CrossOrigin
    @PostMapping("/api/userorder/view3")
    @ResponseBody
    public List<List<String>> view3(@RequestBody String unameToShow) {
        JSON uname = com.alibaba.fastjson.JSONObject.parseObject(unameToShow);
        String name = ((JSONObject) uname).getString("myName");
        List<List<String>> orders= orderService.getUserOrder3(name);
        for(int i=0;i<orders.size();i++){
            orders.get(i).add(8,"已发货");
        }
        return orders;
    }
}
