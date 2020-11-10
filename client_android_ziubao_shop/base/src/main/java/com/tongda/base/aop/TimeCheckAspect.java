package com.tongda.base.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import com.tongda.base.log.LogUtils;


@Aspect
public class TimeCheckAspect {
    final String TAG = TimeCheckAspect.class.getSimpleName();

    /**
     * 定义切点，拦截 MainActivity 中所有以 on 为前缀的方法
     */
    @Pointcut("execution(@gtzn.utils.aop.PrintTimeElapseAnnotation * *(..))")
    void printLog(){}

    @Around("printLog()")
    public void handlePrintLog(ProceedingJoinPoint joinPoint) throws Throwable {
        /**
         * check annotation
         */
        // PrintTimeElapseAnnotation 注解只修饰在方法上
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 只处理有 PrintTimeElapseAnnotation 注解修饰的方法
        if (!method.isAnnotationPresent(PrintTimeElapseAnnotation.class)) {
            //
            LogUtils.d("TimeCheckAspect", "--------------------------------\nPrintTimeElapseAnnotation only support method\n--------------------------------");


            //
            joinPoint.proceed();

            //
            return;
        }

        //
        LogUtils.d("TimeCheckAspect handlePrintLog", method.getName());

        /**
         * fetch annotation value
         */
        // 获取到 PrintTimeElapseAnnotation 注解对象
        PrintTimeElapseAnnotation printTimeElapseAnnotation = method.getAnnotation(PrintTimeElapseAnnotation.class);

        //
        String value = printTimeElapseAnnotation.value();

        /**
         * print time elapse
         */
        long startTime = System.currentTimeMillis();

        //
        joinPoint.proceed();

        // 输出程序运行时间
        long endTime = System.currentTimeMillis();
        LogUtils.d(value ,"************** run time **************：" + (endTime - startTime) + "ms");
    }
}
