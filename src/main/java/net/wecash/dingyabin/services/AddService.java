package net.wecash.dingyabin.services;


import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyabin on 2017/11/8.
 */
public interface AddService {

    /**
     * 根据serviceType查找服务
     * @param serviceType serviceType
     * @return
     */
    Map<String, Object> selectByServiceType(String serviceType);

    /**
     * 根据RequestId查找Request
     * @param requestId id
     * @return  Map<String, Object>
     */
    Map<String, Object> selectRequestById(String requestId);


    /**
     * 根据serviceType,serviceType查找ClientService,
     * @param source source
     * @param serviceType serviceType
     * @return
     */
    List<Map<String, Object>> selectClientService(String source, String serviceType);

    void saveService(Map<String, String> map);

    void saveServiceDataFormat(Map<String, String> map);

    Map<String, Object> saveRequest(Map<String, String> map);

    void saveCodeDict(Map<String, String> map);

    String buildSQL(Map<String, String> map) throws IOException;


}
