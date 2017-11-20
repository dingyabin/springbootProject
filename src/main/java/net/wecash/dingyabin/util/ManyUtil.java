package net.wecash.dingyabin.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.wecash.web.exception.BaseBusinessException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by MrDing
 * Date: 2017/11/11.
 * Time:17:04
 */
@Slf4j
public class ManyUtil {



    public static void assertNotNull(Map<String, String> map , String ... nullAbleFilds){
        map.forEach((k,v)->{
            if (!ArrayUtils.contains(nullAbleFilds, k) && StringUtils.isBlank(v)) {
                throw new BaseBusinessException("E000001",k+"不能为空！");
            }
        });
    }


    public static void assertNotNull(Map<String, String> map){
        map.forEach((k,v)->{
            if (StringUtils.isBlank(v)) {
                throw new BaseBusinessException("E000001",k+"不能为空！");
            }
        });
    }


    /**
     * id是否合法
     * @param id id
     * @return true/fasle
     */
    public static boolean isLegalId(Object id) {
        if (id == null) {
            return false;
        }
        String idStr = id.toString();
        return !Strings.isNullOrEmpty(idStr) && Long.parseLong(idStr) > 0;
    }


}
