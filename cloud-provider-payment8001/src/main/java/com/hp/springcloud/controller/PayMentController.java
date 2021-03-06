package com.hp.springcloud.controller;

import com.hp.springcloud.entites.CommonResult;
import com.hp.springcloud.entites.PayMent;
import com.hp.springcloud.service.PayMentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName PayMentController
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/7 23:53
 */

@RestController
@Slf4j
public class PayMentController {

    @Resource
    private PayMentService payMentService;

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private DiscoveryClient discoveryClient;

    /**
     * 创建该类的日志对象但使用注解@Slf4j效果一样
     * 推荐使用注解@Slf4j
     */
    //Logger log=LoggerFactory.getLogger(PayMentController.class);

    @PostMapping(value = "/payment/create")
    public CommonResult create(
            @RequestBody PayMent payMent
    ) {
        int result = payMentService.create(payMent);
        log.info("*****插入结果：" + result);

        if (result > 0) {
            return new CommonResult(200, "插入数据库成功,serverPort："+serverPort, result);
        } else {
            return new CommonResult(444, "插入数据库失败,serverPort："+serverPort, null);
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult<PayMent> getPayMentById(
            @PathVariable("id") Long id) {
        PayMent payment = payMentService.getPaymentById(id);
        log.info("*****返回结果：" + payment);

        if (null != payment) {
            return new CommonResult(200, "查询成功,serverPort："+serverPort, payment);
        } else {
            return new CommonResult(444, "查询失败，查询ID："+id+"serverPort："+serverPort, null);
        }
    }

    @GetMapping(value = "/payment/discovery")
    public Object get(){

        List<String> services = discoveryClient.getServices();
        for (String service:services
             ) {
            System.out.println("*****服务实例有:"+service);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for(ServiceInstance instance :instances
             ) {
            System.out.println("服务id："+instance.getServiceId());
            System.out.println("服务主机："+instance.getHost());
            System.out.println("服务的url："+instance.getUri());
        }

        return discoveryClient;
    }
}
