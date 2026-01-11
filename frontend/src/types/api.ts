// API Types - Matching backend DTOs

export interface ShoppingItem {
  id: string;
  name: string;
  quantity: number;
  createdAt: string;
  updatedAt: string;
}

export interface AddItemRequest {
  name: string;
  quantity?: number;
}

export interface UpdateItemRequest {
  quantity: number;
}

export interface ShoppingItemListResponse {
  items: ShoppingItem[];
  count: number;
}

export interface RemoveItemResponse {
  action: 'decremented' | 'deleted';
  item: ShoppingItem | null;
}

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  createdAt: string;
}

export interface SendMessageRequest {
  message: string;
  sessionId?: string;
}

export interface ChatResponse {
  sessionId: string;
  userMessage: ChatMessage;
  assistantMessage: ChatMessage;
}

export interface ChatHistoryResponse {
  sessionId: string;
  messages: ChatMessage[];
}

export interface HealthResponse {
  status: 'healthy' | 'unhealthy';
  timestamp: string;
  database?: 'connected' | 'disconnected';
}

export interface ErrorResponse {
  error: string;
  message: string;
  timestamp: string;
  path?: string;
}
