package net.wecash.dingyabin.controller;

import net.wecash.dingyabin.services.AddService;
import lombok.extern.slf4j.Slf4j;
import net.wecash.web.Response;
import net.wecash.web.exception.BaseBusinessException;
import net.wecash.web.filter.ReadableHttpServletRequestWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

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



    private void assertNotNull( Map<String, String> map ,String ... nullAbleFilds){
        map.forEach((k,v)->{
            if (!ArrayUtils.contains(nullAbleFilds, k) && StringUtils.isBlank(v)) {
                throw new BaseBusinessException("E000001",k+"不能为空！");
            }
        });
    }

}
