/**
 * API Client for Backend Communication
 *
 * BACKEND COMMUNICATION EXPLANATION:
 * ==================================
 *
 * This module provides typed functions for all backend API calls.
 * It handles request formatting, error handling, and response parsing.
 *
 * ARCHITECTURE:
 * - All API calls go through the Vite dev server proxy (see vite.config.ts)
 * - In development: localhost:5173/api/* -> localhost:8080/api/*
 * - In production: nginx proxies /api/* to the backend container
 *
 * ERROR HANDLING:
 * - All functions throw ApiError on non-2xx responses
 * - ApiError contains status code and structured error response
 * - Callers should try/catch and handle errors appropriately
 *
 * ADDING NEW ENDPOINTS:
 * 1. Add types to types/api.ts
 * 2. Create async function here following the pattern
 * 3. Use handleResponse<T> for consistent error handling
 *
 * EXTENDING FOR STREAMING:
 * For SSE (Server-Sent Events) streaming:
 * ```typescript
 * export async function* streamMessage(request: SendMessageRequest) {
 *   const response = await fetch(`${API_BASE}/chat/stream`, {
 *     method: 'POST',
 *     headers: { 'Content-Type': 'application/json' },
 *     body: JSON.stringify(request),
 *   });
 *
 *   const reader = response.body!.getReader();
 *   const decoder = new TextDecoder();
 *
 *   while (true) {
 *     const { done, value } = await reader.read();
 *     if (done) break;
 *     yield decoder.decode(value);
 *   }
 * }
 * ```
 *
 * TESTING:
 * You can test endpoints directly with curl:
 * ```bash
 * # Health check
 * curl http://localhost:8080/api/health
 *
 * # Send chat message
 * curl -X POST http://localhost:8080/api/chat \
 *   -H "Content-Type: application/json" \
 *   -d '{"message": "Hello!"}'
 *
 * # List shopping items
 * curl http://localhost:8080/api/items
 * ```
 */

import type {
  ChatResponse,
  ChatHistoryResponse,
  SendMessageRequest,
  ShoppingItemListResponse,
  ShoppingItem,
  AddItemRequest,
  UpdateItemRequest,
  RemoveItemResponse,
  ErrorResponse,
} from '../types/api';

/** Base URL for all API calls - proxied through Vite in development */
const API_BASE = '/api';

class ApiError extends Error {
  constructor(
    public status: number,
    public errorResponse: ErrorResponse
  ) {
    super(errorResponse.message);
    this.name = 'ApiError';
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorData: ErrorResponse = await response.json().catch(() => ({
      error: 'UNKNOWN_ERROR',
      message: 'An unknown error occurred',
      timestamp: new Date().toISOString(),
    }));
    throw new ApiError(response.status, errorData);
  }
  return response.json();
}

// Chat API
export async function sendMessage(request: SendMessageRequest): Promise<ChatResponse> {
  const response = await fetch(`${API_BASE}/chat`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  return handleResponse<ChatResponse>(response);
}

export async function getChatHistory(sessionId: string): Promise<ChatHistoryResponse> {
  const response = await fetch(`${API_BASE}/chat/${sessionId}/history`);
  return handleResponse<ChatHistoryResponse>(response);
}

// Shopping Items API
export async function listItems(): Promise<ShoppingItemListResponse> {
  const response = await fetch(`${API_BASE}/items`);
  return handleResponse<ShoppingItemListResponse>(response);
}

export async function addItem(request: AddItemRequest): Promise<ShoppingItem> {
  const response = await fetch(`${API_BASE}/items`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  return handleResponse<ShoppingItem>(response);
}

export async function updateItem(itemId: string, request: UpdateItemRequest): Promise<ShoppingItem> {
  const response = await fetch(`${API_BASE}/items/${itemId}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  return handleResponse<ShoppingItem>(response);
}

export async function removeItem(itemId: string): Promise<RemoveItemResponse> {
  const response = await fetch(`${API_BASE}/items/${itemId}`, {
    method: 'DELETE',
  });
  return handleResponse<RemoveItemResponse>(response);
}

export { ApiError };
