package com.ai.controller;

import com.ai.dto.ChatRequest;
import com.ai.service.OpenAIChatService;
import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.ShiyanshixinxiEntity;
import com.service.ShiyanshixinxiService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class AiChatController {

    private final OpenAIChatService openAIChatService;
    private final ShiyanshixinxiService shiyanshixinxiService;

    public AiChatController(OpenAIChatService openAIChatService, ShiyanshixinxiService shiyanshixinxiService) {
        this.openAIChatService = openAIChatService;
        this.shiyanshixinxiService = shiyanshixinxiService;
    }

    @IgnoreAuth
    @PostMapping(
        value = "",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = "text/plain;charset=UTF-8"
    )
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        try {
            if (request == null || request.getQuestion() == null) {
                return ResponseEntity.ok("");
            }
            String q = request.getQuestion().trim();
            if (q.isEmpty()) {
                return ResponseEntity.ok("");
            }

            // 业务兜底：如果用户问“实验室数量”，直接查库返回，避免大模型猜测
            boolean isLabCountQuestion = false;
            if (q.contains("实验室")) {
                isLabCountQuestion =
                        q.contains("几") ||
                        q.contains("多少") ||
                        q.contains("数量") ||
                        q.contains("一共有") ||
                        q.contains("总数") ||
                        q.contains("间");
            }

            if (isLabCountQuestion) {
                long count = shiyanshixinxiService.selectCount(new EntityWrapper<ShiyanshixinxiEntity>());
                return ResponseEntity.ok("当前共有 " + count + " 间实验室。");
            }

            return ResponseEntity.ok(openAIChatService.chat(q));
        } catch (Exception e) {
            // 让前端始终拿到可读文本，避免乱码问号
            return ResponseEntity.ok("AI请求失败：" + (e.getMessage() == null ? e.toString() : e.getMessage()));
        }
    }
}

