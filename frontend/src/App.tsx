import './App.css';
import ChatWindow from './components/ChatWindow';
import MessageInput from './components/MessageInput';
import useChat from './hooks/useChat';

function App() {
  const { messages, isLoading, error, sendMessage, clearError } = useChat();

  return (
    <div className="app">
      <header className="app-header">
        <h1>AI Shopping Assistant</h1>
      </header>
      <main className="chat-container">
        {error && (
          <div className="error-banner" onClick={clearError}>
            {error} (click to dismiss)
          </div>
        )}
        <ChatWindow messages={messages} isLoading={isLoading} />
        <MessageInput onSend={sendMessage} disabled={isLoading} />
      </main>
    </div>
  );
}

export default App;
