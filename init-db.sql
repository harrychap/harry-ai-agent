-- Create RAG database for vector storage
CREATE DATABASE rag_store;

-- Enable pgvector extension on shopping_list database
\c shopping_list
CREATE EXTENSION IF NOT EXISTS vector;

-- Enable pgvector extension on rag_store database
\c rag_store
CREATE EXTENSION IF NOT EXISTS vector;
