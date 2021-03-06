package com.hp.springcloud.entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CommonResult
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/10 22:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {

    /**
     * 状态码：200,404,500
     */
    private Integer code;

    /**
     * 成功或错误信息
     */
    private String message;

    /**
     * 返回结果集
     */
    private T data;

    public CommonResult(Integer code, String message) {
        this(code, message, null);
    }
}
