package framework.core.common.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务器端公用缓存，保存已被注册的bean，以便客户端调用
 */
public class CommonServerCache {
    /* 键：bean对应的类的类名，值：bean */
    public static final Map<String, Object> PROVIDER_CLASS_MAP = new HashMap<String, Object>();
}
