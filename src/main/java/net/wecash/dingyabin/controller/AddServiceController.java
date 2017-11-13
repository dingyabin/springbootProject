package net.wecash.dingyabin.controller;

import com.google.common.base.Strings;
import net.wecash.dingyabin.services.AddService;
import lombok.extern.slf4j.Slf4j;
import net.wecash.web.Response;
import net.wecash.web.exception.BaseBusinessException;
import net.wecash.web.filter.ReadableHttpServletRequestWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.wecash.dingyabin.util.ManyUtil.assertNotNull;
import static net.wecash.dingyabin.util.ManyUtil.isLegalId;
import static net.wecash.utils.WebUtils.genParamMap;

/**
 * Created by dingyabin on 2017/11/8.
 */
@Slf4j
@RestController
@RequestMapping("/service")
public class AddServiceController {

    @Resource
    private AddService addService;

    @RequestMapping("/addService")
    public String addService(HttpServletRequest request) throws IOException {
        ReadableHttpServletRequestWrapper requestWrapper = new ReadableHttpServletRequestWrapper(request);
        Map<String, String> paramMap = genParamMap(requestWrapper.getWrappedParams(), requestWrapper.getWrappedJson());
        //校验参数
        assertNotNull(paramMap, "resultParseTemplate", "codeInfo");

        //校验服务是否已经存在
        Map<String, Object> service= addService.selectByServiceType(paramMap.get("serviceType"));
        if (!CollectionUtils.isEmpty(service)){
            throw new BaseBusinessException("E000003", paramMap.get("serviceType") + "服务已经存在");
        }

        //插入service
        addService.saveService(paramMap);

        //插入Request并返回id
        Map<String, Object> map = addService.saveRequest(paramMap);
        paramMap.put("requestId", map.get("requestId").toString());

        //插入ServiceDataFormat
        addService.saveServiceDataFormat(paramMap);

        //插入CodeDict
        addService.saveCodeDict(paramMap);

        //打印sql
        String sql = addService.buildSQL(paramMap);
        log.info("恭喜,{}服务测试环境添加完毕！\n\n\n给老胖子发邮件吧，内容为:\n{}",
                paramMap.get("serviceType"), sql);
        return new Response<>().success().data(sql).toString();
    }



    @RequestMapping("/selectServiceCallback")
    public String selectServiceCallback(HttpServletRequest req) throws IOException {
        ReadableHttpServletRequestWrapper requestWrapper = new ReadableHttpServletRequestWrapper(req);
        Map<String, String> map = genParamMap(requestWrapper.getWrappedParams(), requestWrapper.getWrappedJson());
        //校验参数
        assertNotNull(map);

        List<Map<String, Object>> mapList= addService.selectClientService(map.get("source"), map.get("serviceType"));
        if (CollectionUtils.isEmpty(mapList)){
            return new Response<>().fail().msg("还没有订阅:" + map.get("serviceType")+"，请先订阅").toString();
        }
        String requestId = getRequestId(map.get("calllbackIndex"), mapList);
        if (requestId != null) {
            Map<String, Object> request = addService.selectRequestById(requestId);
            return new Response<>().success().data(request).toString();
        }
        return new Response<>().fail().msg("还没有配置第" + map.get("calllbackIndex")+"次回调").toString();
    }




    /**
     *
     * @param calllbackIndex 第几次回调
     * @param mapList  clientservivce集合
     * @return RequestId
     */
    private String getRequestId(String calllbackIndex, List<Map<String, Object>> mapList) {
        String requestId = null;
        for (Map<String, Object> clientservivce : mapList) {
            if (Objects.equals("1", calllbackIndex) && isLegalId(clientservivce.get("login_request_id"))) {
                requestId = clientservivce.get("login_request_id").toString();
                break;
            } else if (Objects.equals("2", calllbackIndex) && isLegalId(clientservivce.get("request_id"))) {
                requestId = clientservivce.get("request_id").toString();
                break;
            }
        }
        return requestId;
    }


    @RequestMapping("/updateCallback")
    public String updateCallback(HttpServletRequest req) throws IOException {
        ReadableHttpServletRequestWrapper requestWrapper = new ReadableHttpServletRequestWrapper(req);
        Map<String, String> map = genParamMap(requestWrapper.getWrappedParams(), requestWrapper.getWrappedJson());
        //校验参数
        assertNotNull(map,"resultParseTemplate", "codeInfo","requestId");

        //有requestid，修改request
        if (!Strings.isNullOrEmpty(map.get("requestId"))){
            addService.updateRequestById(map);
            return new Response<>().success().data(map).toString();
        }

        //无requestid，查找ClientService
        List<Map<String, Object>> clientServiceList= addService.selectClientService(map.get("source"), map.get("serviceType"));
        if (CollectionUtils.isEmpty(clientServiceList)){
            return new Response<>().fail().msg("还没有订阅:" + map.get("serviceType")+"，请先订阅").toString();
        }

        //根据查找到的ClientService,查找回调Id
        String requestId = getRequestId(map.get("calllbackIndex"), clientServiceList);

        //requestId=null，插入新的request，并拿到新插入的request的id，将其设置到ClientServivce里
        if (requestId == null) {
            map.put("requestId", addService.saveRequest(map).get("requestId").toString());
            addService.updateClientServivce(map);
            return new Response<>().success().data(map).toString();
            //requestId！=null,直接根据requestId修改request
        }else {
            map.put("requestId", requestId);
            addService.updateRequestById(map);
            return new Response<>().success().data(map).toString();
        }
    }




    @RequestMapping("/resetpwd")
    public String resetpwd(HttpServletRequest req) throws IOException {
        ReadableHttpServletRequestWrapper requestWrapper = new ReadableHttpServletRequestWrapper(req);
        Map<String, String> map = genParamMap(requestWrapper.getWrappedParams(), requestWrapper.getWrappedJson());
        //校验参数
        assertNotNull(map);

        //重置密码
        addService.resetPwd(map.get("source"), map.get("username"));
        return  new Response<>().success().data(map).msg("修改成功,新密码: 123456").toString();
    }

}
