package com.hu.study.chapter23;

import com.spring4all.swagger.EnableSwagger2Doc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableSwagger2Doc
@SpringBootApplication
public class Chapter23Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter23Application.class, args);
    }

    @Api(tags = {"1-教师管理","3-教学管理"})
    @RestController
    @RequestMapping(value = "/teacher")
    static class TeacherController {

        @ApiOperation(value = "xxx")
        @GetMapping("/xxx")
        public String xxx() {
            return "xxx";
        }

    }

    @Api(tags = {"2-学生管理"})
    @RestController
    @RequestMapping(value = "/student")
    static class StudentController {

        @ApiOperation(value = "获取学生清单", tags = "3-教学管理")
        @GetMapping("/list")
        public String bbb() {
            return "bbb";
        }

        @ApiOperation("获取教某个学生的老师清单")
        @GetMapping("/his-teachers")
        public String ccc() {
            return "ccc";
        }

        @ApiOperation("创建一个学生")
        @PostMapping("/aaa")
        public String aaa() {
            return "aaa";
        }

    }

}
