package net.wecash.dingyabin.dao;

import net.wecash.annotations.DataSource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 */
@Repository()
@DataSource(DataSource.DataSourceType.MASTER)
public interface AddServiceDao {

       /**
        * 插入service
        * @param map map
        * @return int
        */
       int saveService(Map<String, Object> map);



       /**
        * 查找service
        * @return ServiceName
        */
       List<Map<String, Object>> getAllService();

       /**
        * 插入serviceDataformat
        * @param map map
        * @return int
        */
       int saveServiceDataFormat(Map<String, Object> map);

       /**
        * 插入request
        * @param map map
        * @return int
        */
       int saveRequest(Map<String, Object> map);


       /**
        * 插入CodeDict
        * @param list list
        * @return int
        */

       int saveCodeDict(@Param("list") List<Map<String, Object>> list);


       /**
        * 根据serviceType查找服务
        * @param serviceType serviceType
        * @return
        */
       Map<String, Object> selectByServiceType(@Param("serviceType") String serviceType);


       /**
        * 根据RequestId查找Request
        * @param requestId id
        * @return
        */
       Map<String, Object> selectByRequestId(@Param("requestId") Long requestId);


       /**
        * 根据RequestId查找Request
        * @param source source
        * @param serviceType serviceType
        * @return
        */
       List<Map<String, Object>> selectClientService(@Param("source") Long source, @Param("serviceType") String serviceType);

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
        * 查找用户
        * @param source source
        * @param username  username
        * @return   map
        */
       Map<String, Object> selectUser(@Param("source") Long source, @Param("username") String username);


       /**
        * 重置密码
        * @param source source
        * @param username  username
        */
       void resetPwd(@Param("source") Long source, @Param("username") String username);


       /**
        * 根据source查找订阅权限
        * @param source source
        * @return List<String>
        */
       String getSubPermissionBySource(Long source);



       /**
        * 根据source修改订阅权限
        * @param source source
        * @param allowedServiceReg allowedServiceReg
        * @return List<String>
        */
       int updateSubPermission(@Param("source")Long source,  @Param("allowedServiceReg")String allowedServiceReg);


       /**
        * 查找授权项
        * @return 集合
        */
       List<Map<String,String>> selectAuthServcice();

}
