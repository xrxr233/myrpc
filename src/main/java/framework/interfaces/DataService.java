package framework.interfaces;

import java.util.List;

/**
 * 测试用的接口
 */
public interface DataService {
    /**
     * 发送数据
     * @param body
     * @return
     */
    String sendData(String body);

    /**
     * 获取数据
     * @return
     */
    List<String> getList();
}
