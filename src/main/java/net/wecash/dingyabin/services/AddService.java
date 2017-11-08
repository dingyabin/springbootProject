package net.wecash.dingyabin.services;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dingyabin on 2017/11/8.
 */
public interface AddService {


    void saveService(Map<String, String> map);

    void saveServiceDataFormat(Map<String, String> map);

    Map<String, Object> saveRequest(Map<String, String> map);

    void saveCodeDict(Map<String, String> map);

    String buildSQL(Map<String, String> map) throws IOException;


}
