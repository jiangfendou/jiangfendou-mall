package com.jiangfendou.mall.order.web;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.jiangfendou.mall.order.config.AlipayTemplate;
import com.jiangfendou.mall.order.entity.OrderEntity;
import com.jiangfendou.mall.order.service.OrderService;
import com.jiangfendou.mall.order.vo.PayAsyncVo;
import com.jiangfendou.mall.order.vo.PayVo;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Slf4j
@Controller
public class PayWebController {

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

//    @Autowired
//    private BestPayService bestPayService;
//
//    @Resource
//    private WxPayConfig wxPayConfig;

    /**
     * 用户下单:支付宝支付
     * 1、让支付页让浏览器展示
     * 2、支付成功以后，跳转到用户的订单列表页
     * @param orderSn
     * @return pay pay
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/aliPayOrder", produces = "text/html")
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }


    @PostMapping(value = "/payed/notify")
    public String handleAlipayed(PayAsyncVo asyncVo, HttpServletRequest request) throws AlipayApiException,
        UnsupportedEncodingException {
        // 只要收到支付宝的异步通知，返回 success 支付宝便不再通知
        // 获取支付宝POST过来反馈信息
        //TODO 需要验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                    : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
            alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名

        if (signVerified) {
            log.info("签名验证成功...");
            //去修改订单状态
            String result = orderService.handlePayResult(asyncVo);
            return result;
        } else {
            log.info("签名验证失败..");
            return "error";
        }
    }

    @PostMapping(value = "/pay/notify")
    public String asyncNotify(@RequestBody String notifyData) {
        //异步通知结果
        return orderService.asyncNotify(notifyData);
    }


    /**
     * 微信支付
//     * @param orderSn
     * @return
     */
//    @GetMapping(value = "/weixinPayOrder")
//    public String weixinPayOrder(@RequestParam("orderSn") String orderSn, Model model) {
//
//        OrderEntity orderInfo = orderService.getOrderByOrderSn(orderSn);
//
//        if (orderInfo == null) {
//            throw new RuntimeException("订单不存在");
//        }
//
//        PayRequest request = new PayRequest();
//        request.setOrderName("4559066-最好的支付sdk");
//        request.setOrderId(orderInfo.getOrderSn());
//        request.setOrderAmount(0.01);
//        request.setPayTypeEnum(WXPAY_NATIVE);
//
//        PayResponse payResponse = bestPayService.pay(request);
//        payResponse.setOrderId(orderInfo.getOrderSn());
//        log.info("发起支付 response={}", payResponse);
//
//        //传入前台的二维码路径生成支付二维码
//        model.addAttribute("codeUrl",payResponse.getCodeUrl());
//        model.addAttribute("orderId",payResponse.getOrderId());
//        model.addAttribute("returnUrl",wxPayConfig.getReturnUrl());
//
//        return "createForWxNative";
//    }


    //根据订单号查询订单状态的API
    @GetMapping(value = "/queryByOrderId")
    @ResponseBody
    public OrderEntity queryByOrderId(@RequestParam("orderId") String orderId) {
        log.info("查询支付记录...");
        return orderService.getOrderByOrderSn(orderId);
    }



}
