package com.yeogi.scms.aspect;

import com.yeogi.scms.domain.AccessLog;
import com.yeogi.scms.repository.AccessLogRepository;
import com.yeogi.scms.service.CustomUserDetails;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Aspect
@Component
public class AccessLogAspect {

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private HttpServletRequest request;

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {
        // 모든 Controller 클래스 내 메서드를 위한 포인트컷
    }

    @AfterReturning("controllerMethods()")
    public void logAccess(JoinPoint joinPoint) {
        AccessLog accessLog = new AccessLog();
        accessLog.setTimestamp(LocalDateTime.now());

        // 현재 유저를 가져옴
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) principal;
            accessLog.setAccessId(user.getNickname());
        } else {
            accessLog.setAccessId("Anonymous");
        }

        // action 결정
        String method = request.getMethod();
        String action;
        switch (method) {
            case "GET":
                action = "READ";
                break;
            case "POST":
                action = "CREATE";
                break;
            case "PUT":
                action = "UPDATE";
                break;
            case "DELETE":
                action = "DELETE";
                break;
            default:
                action = "UNKNOWN";
        }
        accessLog.setAction(action);

        // access path 설정
        String path = request.getRequestURI();
        accessLog.setAccessPath(path);

        // access log 저장
        accessLogRepository.save(accessLog);
    }
}
