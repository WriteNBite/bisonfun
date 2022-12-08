package com.bisonfun.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/err")
    public String handleErrors(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(status != null){
            int statusCode = Integer.parseInt(status.toString());
            switch (statusCode){
                case 400: return "error/400";
                case 404: return "error/404";
                case 403: return "error/403";
                case 500: return "error/500";
            }
        }
        return "error/500";
    }
}
