import { useState, useCallback } from 'react';
import type { ChatMessage } from '../types/api';
import { sendMessage as apiSendMessage } from '../services/api';

interface UseChatResult {
  messages: ChatMessage[];
  sessionId: string | null;
  isLoading: boolean;
  error: string | null;
  sendMessage: (message: string) => Promise<void>;
  clearError: () => void;
}

/**
 * Custom hook for managing chat state.
 *
 * FRONTEND STATE FLOW EXPLANATION:
 * ================================
 *
 * This hook encapsulates all chat-related state and logic, following React best practices
 * for separation of concerns. The App component only needs to render - all state management
 * lives here.
 *
 * STATE VARIABLES:
 * - messages: Array of chat messages (both user and assistant)
 * - sessionId: UUID identifying the current chat session (null until first message)
 * - isLoading: True while waiting for API response (used to disable input)
 * - error: Error message if API call fails (null when no error)
 *
 * MESSAGE FLOW:
 * 1. User calls sendMessage("Add milk")
 * 2. isLoading set to true (shows loading indicator)
 * 3. API call to POST /api/chat
 * 4. Response contains both userMessage and assistantMessage
 * 5. Both messages appended to messages array
 * 6. isLoading set to false
 * 7. React re-renders ChatWindow with new messages
 *
 * SESSION MANAGEMENT:
 * - First message: sessionId is null, backend creates new session
 * - Response includes sessionId, saved for subsequent messages
 * - All messages in conversation share the same sessionId
 * - Refreshing the page starts a new session (state is in-memory)
 *
 * EXTENDING THIS HOOK:
 *
 * To add message history loading:
 * ```typescript
 * const loadHistory = useCallback(async (sid: string) => {
 *   const history = await api.getChatHistory(sid);
 *   setMessages(history.messages);
 *   setSessionId(sid);
 * }, []);
 * ```
 *
 * To add optimistic updates (show user message immediately):
 * ```typescript
 * const sendMessage = useCallback(async (message: string) => {
 *   const tempUserMessage = { id: 'temp', role: 'user', content: message, ... };
 *   setMessages(prev => [...prev, tempUserMessage]);
 *   setIsLoading(true);
 *   // ... rest of implementation
 * }, []);
 * ```
 *
 * To add streaming support:
 * - Change sendMessage to use Server-Sent Events
 * - Append to assistant message as chunks arrive
 * - Set isLoading false when stream completes
 *
 * Usage:
 * ```tsx
 * const { messages, sendMessage, isLoading, error } = useChat();
 *
 * // Send a message
 * await sendMessage("Add milk to my list");
 * ```
 */
function useChat(): UseChatResult {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const sendMessage = useCallback(async (message: string) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await apiSendMessage({
        message,
        sessionId: sessionId ?? undefined,
      });

      // Update session ID if this is a new session
      if (!sessionId) {
        setSessionId(response.sessionId);
      }

      // Add both user and assistant messages to the list
      setMessages((prev) => [
        ...prev,
        response.userMessage,
        response.assistantMessage,
      ]);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to send message';
      setError(errorMessage);
      console.error('Chat error:', err);
    } finally {
      setIsLoading(false);
    }
  }, [sessionId]);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    messages,
    sessionId,
    isLoading,
    error,
    sendMessage,
    clearError,
  };
}

export default useChat;
