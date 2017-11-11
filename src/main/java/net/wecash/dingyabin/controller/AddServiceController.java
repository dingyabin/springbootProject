package net.wecash.dingyabin.controller;

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
        String requestId = null;
        for (Map<String, Object> clientservivce : mapList) {
            if (Objects.equals("1", map.get("calllbackIndex")) && isLegalId(clientservivce.get("login_request_id"))) {
                requestId = clientservivce.get("login_request_id").toString();
                break;
            } else if (Objects.equals("2", map.get("calllbackIndex")) && isLegalId(clientservivce.get("request_id"))) {
                requestId = clientservivce.get("request_id").toString();
                break;
            }
        }
        if (requestId != null) {
            Map<String, Object> request = addService.selectRequestById(requestId);
            return new Response<>().success().data(request).toString();
        }
        return new Response<>().fail().msg("还没有配置第" + map.get("calllbackIndex")+"次回调").toString();
    }

}
