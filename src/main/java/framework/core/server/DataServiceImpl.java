package framework.core.server;

import framework.interfaces.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * 可注册并被远程访问的bean
 */
public class DataServiceImpl implements DataService {
    public String sendData(String body) {
        System.out.println("已收到的参数长度：" + body.length());
        return "success";
    }

    public List<String> getList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("idea1");
        arrayList.add("idea2");
        arrayList.add("idea3");
        return arrayList;
    }
}
