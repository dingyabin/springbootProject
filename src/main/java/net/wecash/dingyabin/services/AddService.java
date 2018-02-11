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


    /**
     * 修改request
     * @param map map
     * @return int
     */
    int updateRequestById(Map<String, String> map);


    /**
     * 修改ClientServivce的回调id
     * @param map map
     * @return int
     */
    int updateClientServivce(Map<String, String> map);

    /**
     * 保存service
     * @param map map
     */

    void saveService(Map<String, String> map);


    void saveServiceDataFormat(Map<String, String> map);


    Map<String, Object> saveRequest(Map<String, String> map);


    /**
     *
     * @param map
     */
    void saveCodeDict(Map<String, String> map);


    /**
     * 生成sql语句
     * @param map
     * @return sql语句
     * @throws IOException
     */
    String buildSQL(Map<String, String> map) throws IOException;


    /**
     * 重置密码，如果找不到用户会抛异常
     * @param source 渠道号
     * @param username 用户名
     */
    void resetPwd(String source,String username);




    /**
     * 根据source查找订阅权限
     * @param source source
     * @return List<String>
     */
    List<Map<String, Object>> getSubPermissionBySource(String source);


    /**
     * 根据source,serviceType修改订阅权限
     * @param source
     * @param serviceType
     */
    void updatePermisson(String source,String serviceType,String wantToChecked);


    /**
     * 查找授权项
     * @return 集合
     */
    List<Map<String,String>> selectAuthServcice();

}
