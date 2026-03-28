package com.ai.dto;

import java.util.List;
import java.util.Map;

public class OpenAIChatRequestBody {
    private String model;
    private List<OpenAIMessage> messages;
    private Boolean stream;
    private Map<String, Object> extra_body;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<OpenAIMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<OpenAIMessage> messages) {
        this.messages = messages;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public Map<String, Object> getExtra_body() {
        return extra_body;
    }

    public void setExtra_body(Map<String, Object> extra_body) {
        this.extra_body = extra_body;
    }
}

