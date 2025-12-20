package fun.fengwk.convention4j.comfyui;

/**
 * ComfyUI 全局常量定义
 *
 * @author fengwk
 */
public final class ComfyUIConstants {

    private ComfyUIConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * ComfyUI API 路径常量
     */
    public static final class ApiPaths {
        private ApiPaths() {}
        
        public static final String PROMPT = "/prompt";
        public static final String HISTORY = "/history/";
        public static final String UPLOAD_IMAGE = "/upload/image";
        public static final String VIEW = "/view";
        public static final String WS = "/ws";
    }

    /**
     * JSON 字段常量
     */
    public static final class JsonFields {
        private JsonFields() {}
        
        // API 请求/响应字段
        public static final String CLIENT_ID = "client_id";
        public static final String PROMPT = "prompt";
        public static final String PROMPT_ID = "prompt_id";
        public static final String ERROR = "error";
        
        // 文件相关字段
        public static final String FILENAME = "filename";
        public static final String SUBFOLDER = "subfolder";
        public static final String TYPE = "type";
        public static final String NAME = "name";
        
        // WebSocket 消息字段
        public static final String DATA = "data";
        public static final String NODE = "node";
        public static final String VALUE = "value";
        public static final String MAX = "max";
        public static final String NODES = "nodes";
        public static final String EXCEPTION_MESSAGE = "exception_message";
        
        // Workflow 字段
        public static final String CLASS_TYPE = "class_type";
        public static final String INPUTS = "inputs";
        public static final String META = "_meta";
        public static final String LINKS = "links";
        
        // 输出字段
        public static final String IMAGES = "images";
        public static final String VIDEO = "video";
        public static final String AUDIO = "audio";
        public static final String TEXT = "text";
        public static final String VALUE_OUTPUT = "value";
    }

    /**
     * HTTP 头常量
     */
    public static final class HttpHeaders {
        private HttpHeaders() {}
        
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String APPLICATION_JSON = "application/json";
        public static final String MULTIPART_FORM_DATA_PREFIX = "multipart/form-data; boundary=";
        public static final String AUTHORIZATION = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
    }

    /**
     * URL 参数名常量（与 JsonFields 共用）
     */
    public static final class UrlParams {
        private UrlParams() {}
        
        public static final String FILENAME = JsonFields.FILENAME;
        public static final String SUBFOLDER = JsonFields.SUBFOLDER;
        public static final String TYPE = JsonFields.TYPE;
        public static final String CLIENT_ID = "clientId";
    }

    /**
     * ComfyUI 节点类型常量
     */
    public static final class NodeTypes {
        private NodeTypes() {}
        
        public static final String LOAD_IMAGE = "LoadImage";
        public static final String LOAD_AUDIO = "LoadAudio";
        public static final String LOAD_VIDEO = "LoadVideo";
    }

    /**
     * 输入字段名常量
     */
    public static final class InputFields {
        private InputFields() {}
        
        public static final String IMAGE = "image";
        public static final String AUDIO = "audio";
        public static final String VIDEO = "video";
        public static final String CKPT_NAME = "ckpt_name";
        public static final String SEED = "seed";
        public static final String NOISE_SEED = "noise_seed";
        public static final String CTRL_SEED = "ctrl_seed";
    }
}