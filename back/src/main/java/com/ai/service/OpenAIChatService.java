package com.ai.service;

import com.ai.dto.OpenAIChatRequestBody;
import com.ai.dto.OpenAIMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIChatService {

    @Value("${openai.base-url}")
    private String baseUrl;

    @Value("${openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public OpenAIChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String chat(String userQuestion) {
        OpenAIMessage systemMsg = new OpenAIMessage();
        systemMsg.setRole("system");
        // 说明：当前前端只发送文本问题，不包含图片；提示词要保持“纯文本问答”。
        systemMsg.setContent("你是一个课堂学生学习状态答疑助手。请直接根据用户输入进行回答，不要输出无意义的问号。");

        OpenAIMessage userMsg = new OpenAIMessage();
        userMsg.setRole("user");
        userMsg.setContent(userQuestion);

        OpenAIChatRequestBody body = new OpenAIChatRequestBody();
        body.setModel("qwen3-vl-plus");
        body.setMessages(Arrays.asList(systemMsg, userMsg));
        body.setStream(false);
        body.setExtra_body(new HashMap<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<OpenAIChatRequestBody> entity = new HttpEntity<>(body, headers);

        String url = baseUrl + "/chat/completions";
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> respBody = response.getBody();
            Object choicesObj = respBody.get("choices");
            if (choicesObj instanceof List) {
                List choices = (List) choicesObj;
                if (!choices.isEmpty()) {
                    Object firstObj = choices.get(0);
                    if (firstObj instanceof Map) {
                        Map first = (Map) firstObj;
                        Object messageObj = first.get("message");
                        if (messageObj instanceof Map) {
                            Map message = (Map) messageObj;
                            Object content = message.get("content");
                            if (content != null) {
                                return content.toString();
                            }
                        }
                    }
                }
            }
            // 兜底：尝试从其它常见字段取输出
            Object output = respBody.get("output_text");
            if (output != null) return output.toString();
            return respBody.toString();
        }
        throw new RuntimeException("调用 AI 接口失败: " + response.getStatusCode());
    }
}

