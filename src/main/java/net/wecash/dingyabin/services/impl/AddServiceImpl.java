package net.wecash.dingyabin.services.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import net.wecash.dingyabin.dao.AddServiceDao;
import net.wecash.dingyabin.services.AddService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.wecash.utils.GsonUtils;
import net.wecash.web.exception.BaseBusinessException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * Created by dingyabin on 2017/11/8.
 */
@Service("addService")
public class AddServiceImpl implements AddService {

    @Resource
    private AddServiceDao addServiceDao;


    @Override
    public Map<String, Object> selectByServiceType(String serviceType) {
        if (StringUtils.isBlank(serviceType)){
            return null;
        }
        return addServiceDao.selectByServiceType(serviceType);
    }


    @Override
    public Map<String, Object> selectRequestById(String requestId) {
        if (StringUtils.isBlank(requestId)){
            return null;
        }
        return addServiceDao.selectByRequestId(Long.valueOf(requestId));
    }


    @Override
    public List<Map<String, Object>> selectClientService(String source, String serviceType) {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(serviceType)) {
            return null;
        }
        return addServiceDao.selectClientService(Long.valueOf(source), serviceType);
    }


    @Override
    public int updateRequestById(Map<String, String> map) {
        return addServiceDao.updateRequestById(map);
    }


    @Override
    public int updateClientServivce(Map<String, String> map) {
        return addServiceDao.updateClientServivce(map);
    }


    @Override
    public void saveService(Map<String, String> map) {
        HashMap<String, Object> serviceMap = Maps.newHashMap();
        serviceMap.put("serviceName", map.get("serviceName"));
        serviceMap.put("serviceType", map.get("serviceType"));
        serviceMap.put("serviceDescription", map.get("serviceDescription"));
        addServiceDao.saveService(serviceMap);
    }

    @Override
    public void saveServiceDataFormat(Map<String, String> map) {
        HashMap<String, Object> serviceDataFormatMap = Maps.newHashMap();
        serviceDataFormatMap.put("requestId", map.get("requestId"));
        serviceDataFormatMap.put("serviceName", map.get("serviceName"));
        serviceDataFormatMap.put("serviceType", map.get("serviceType"));
        serviceDataFormatMap.put("serviceFormatType", map.get("serviceFormatType"));
        addServiceDao.saveServiceDataFormat(serviceDataFormatMap);
    }

    @Override
    public Map<String, Object> saveRequest(Map<String, String> map) {
        HashMap<String, Object> requestMap = Maps.newHashMap();
        requestMap.put("requestId", map.get("requestId"));
        requestMap.put("url", map.get("url"));
        requestMap.put("method", map.get("method"));
        requestMap.put("header", map.get("header"));
        requestMap.put("charset", map.get("UTF-8"));
        //爬虫的request
        if (map.get("calllbackIndex") == null) {
            requestMap.put("params", buildRequestParams(map.get("params")));
        //回调的request
        }else {
            requestMap.put("params", map.get("params"));
        }
        if (StringUtils.isNotBlank(map.get("resultParseTemplate"))) {
            requestMap.put("resultParseTemplate", map.get("resultParseTemplate"));
        }
        addServiceDao.saveRequest(requestMap);
        return requestMap;
    }


    @Override
    public void saveCodeDict(Map<String, String> map) {
        if (StringUtils.isBlank(map.get("resultParseTemplate"))) {
            return;
        }
        List<Map<String, Object>> list = buildCodeDictParams(map);
        if (!CollectionUtils.isEmpty(list)) {
            addServiceDao.saveCodeDict(list);
        }
    }



    private String buildRequestParams(String originalParams){
        JSONArray arrays = JSONObject.parseArray(Optional.ofNullable(originalParams).orElse("[]"));
        Map<String, String> paramMap = Maps.newHashMap();
        for (int i = 0; i < arrays.size(); i++) {
            JSONObject jsonObject = arrays.getJSONObject(i);
            paramMap.put(jsonObject.getString("crawlerParam"), jsonObject.getString("platParam"));
        }
        return  GsonUtils.toJson(paramMap);
    }



    private List<Map<String, Object>> buildCodeDictParams(Map<String, String> map) {
        List<Map<String, Object>> list = Lists.newArrayList();
        JSONArray arrays = JSONObject.parseArray(Optional.ofNullable(map.get("codeInfo")).orElse("[]"));
        for (int i = 0; i < arrays.size(); i++) {
            JSONObject jsonObject = arrays.getJSONObject(i);
            HashMap<String, Object> codeMap = Maps.newHashMap();
            codeMap.put("key", map.get("serviceType") + "_origin");
            codeMap.put("codeType", jsonObject.getString("codeType"));
            codeMap.put("code", jsonObject.getString("code"));
            codeMap.put("message", jsonObject.getString("message"));
            codeMap.put("success", jsonObject.getBooleanValue("success"));
            list.add(codeMap);
        }
        return list;
    }




    @Override
    public String buildSQL(Map<String, String> maps) throws IOException {
        List<String> strings = IOUtils.readLines(AddServiceImpl.class.getResourceAsStream("/static/addService.txt"), "UTF-8");
        if (CollectionUtils.isEmpty(strings) || strings.size() < 4) {
            throw new IllegalArgumentException("请不要修改模板");
        }
        String firstThreeSQL = strings.get(0) + "\n\n" + strings.get(1) + "\n\n" + strings.get(2) + "\n\n";

        String finalSQL = firstThreeSQL.replaceAll("\\$\\{serviceName\\}",maps.get("serviceName"))
                                       .replaceAll("\\$\\{serviceType\\}", maps.get("serviceType"))
                                       .replaceAll("\\$\\{serviceDescription\\}", maps.get("serviceDescription"))
                                       .replaceAll("\\$\\{serviceFormatType\\}", maps.get("serviceFormatType"))
                                       .replaceAll("\\$\\{requestId\\}", maps.get("requestId") + "")
                                       .replaceAll("\\$\\{url\\}", maps.get("url"))
                                       .replaceAll("\\$\\{method\\}", maps.get("method"))
                                       .replaceAll("\\$\\{header\\}", maps.get("header"))
                                       .replaceAll("\\$\\{params\\}", buildRequestParams(maps.get("params")))
                                       .replaceAll("\\$\\{resultParseTemplate\\}", maps.get("resultParseTemplate") == null
                                               ? "null" : String.format("'%s'",maps.get("resultParseTemplate")));

        for (Map<String, Object> map : buildCodeDictParams(maps)) {
            finalSQL += strings.get(3).replaceAll("\\$\\{key\\}", map.get("key").toString())
                               .replaceAll("\\$\\{codeType\\}", map.get("codeType").toString())
                               .replaceAll("\\$\\{code\\}", map.get("code").toString())
                               .replaceAll("\\$\\{message\\}", map.get("message").toString())
                               .replaceAll("\\$\\{success\\}", map.get("success").toString()) + "\n\n";
        }
        return finalSQL;
    }


    @Override
    public void resetPwd(String source, String username) {
        if (Strings.isNullOrEmpty(source) || Strings.isNullOrEmpty(username)) {
            throw new BaseBusinessException("E000003", "参数缺失");
        }
        Map<String, Object> user = addServiceDao.selectUser(Long.parseLong(source), username);
        if (CollectionUtils.isEmpty(user)){
            throw new BaseBusinessException("E000004", "查无此人!");
        }
        addServiceDao.resetPwd(Long.parseLong(source), username);
    }
}
