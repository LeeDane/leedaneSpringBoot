package com.cn.leedane.controller;

import com.cn.leedane.service.HDFSService;
import com.cn.leedane.utils.ResponseMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author LeeDane
 * 2020年03月11日 14:48
 * Version 1.0
 */
@RestController
@RequestMapping("/api/hadoop/hdfs")
public class HDFSController {
    // 日志记录
    private static final Logger logger = LoggerFactory.getLogger(HDFSController.class);
    @Autowired
    private HDFSService service;
    @RequestMapping(value = "/add", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> add(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        boolean target = service.mkdirFolder("/user1/root/");
        return message.getMap();
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> uploadLocalData(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        service.createLocalFile("/hadoop/appdata", "data.sql");
        return message.getMap();
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> read(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        service.readFileContent("/hadoop/appdata/data.sql");
        return message.getMap();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> delete(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        service.deleteFile("/hadoop/appdata/ddd.sql");
        return message.getMap();
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> listFile(Model model, HttpServletRequest request){
        ResponseMap message = new ResponseMap();
        service.listFile("/hadoop");
        return message.getMap();
    }
    @RequestMapping(value = "/job", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> doJob(Model model, HttpServletRequest request) throws IOException, InterruptedException, ClassNotFoundException {
        ResponseMap message = new ResponseMap();
        service.doJob("hadoopJob", "/user81/root/test.txt", "/user81/root/output");
        return message.getMap();
    }
}
