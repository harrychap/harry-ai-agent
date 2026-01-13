package com.example.mcp.config

import com.example.mcp.tools.ShoppingTools
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class McpServerConfig {
    @Bean
    fun shoppingToolsConfig(shoppingTools: ShoppingTools): ToolCallbackProvider {
        return MethodToolCallbackProvider
            .builder()
            .toolObjects(shoppingTools)
            .build()
    }
}