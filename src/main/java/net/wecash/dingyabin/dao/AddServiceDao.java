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

}
