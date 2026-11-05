AGENT MEMORY — STATE ANCHOR
Read this file FIRST every session. It is intentionally small (<500 tokens).
For full execution history see docs/execution_log.md (append-only, do NOT read in full).

CURRENT STATE
Project: humanity_llm (OfflineLLM + ZIM Knowledge Base + Voice Interpreter)
Root: /mnt/obunaga/back_up/Sync/humanity_llm
Current Phase: COMPLETE (All phases finished)
Current Step: N/A — Project Complete
Status: COMPLETE
Next Action: Manual runtime testing on physical device.
Task File: N/A

COMPLETED STEPS
[x] 0.1: Project Audit & Architecture Alignment — VERIFIED
[x] 1.1: Initialize NDK, setup zim_engine module and CMakeLists — VERIFIED
[x] 1.2: Configure CMake for external ZIM libraries, dummy linkage pipeline verified — VERIFIED
[x] 1.3: Replace dummy zim_core with real pre-built Kiwix libzim binaries via CMake IMPORTED targets — VERIFIED
[x] 1.4: Implement JNI bindings to open and read ZIM files using zim::Archive — VERIFIED
[x] 1.5: Verify libzim integration with a real ZIM file via Android Instrumented Test — VERIFIED (Phase 1 COMPLETE)
[x] 2.1: Implement text extractors for PDF, HTML, and TXT files — VERIFIED
[x] 2.2: Implement C++ JNI bindings to create ZIM files using zim::writer::Creator — VERIFIED
[x] 2.3: Orchestrate the Kotlin ingestion pipeline (Extractor -> ZimEngine) — VERIFIED
[x] 2.4: Implement JNI bindings for ZIM search using zim::Searcher — VERIFIED (Phase 2 COMPLETE)
[x] 3.1: Implement KnowledgeBaseRepository & Room Tracking — VERIFIED
[x] 3.2: Wire ZIM search results into KnowledgeBaseRepository context retrieval — VERIFIED
[x] 3.3: Inject ZIM context into the LLM prompt pipeline via ChatViewModel — VERIFIED
[x] 3.4: Build Knowledge Base UI for document upload and management — VERIFIED
[x] 3.5: Wire KnowledgeBaseScreen into the Navigation graph and main UI — VERIFIED (Phase 3 COMPLETE)
[x] 4.1: Audit VoiceInputManager for offline STT readiness — VERIFIED (Uses cloud SpeechRecognizer)
[x] 4.2: Implement AudioRecord capture pipeline for OfflineSttManager — VERIFIED
[x] 4.4: Wire OfflineSttManager (Audio Pipeline) into ChatViewModel — VERIFIED
[x] 4.6: Universal Prompt Template Detection — VERIFIED
[x] 4.7: ZIM Reader JNI Function — VERIFIED
[x] 4.8: ZIM Reader Screen & Navigation — VERIFIED
[x] 4.9: Zero-Config GGUF Model Auto-Loading — VERIFIED
[x] Phase 4: Voice Interpreter, ZIM Reader, and Model Auto-Config — COMPLETE
[x] 5.1: Organic Maps Helper & Toolbar Button — VERIFIED
[x] 5.1: Organic Maps Helper + Toolbar Button — VERIFIED
[x] 5.2: Chat-Driven Navigation Interception (regex + confirmation card) — VERIFIED
[x] 5.3: Persisted Web Search Toggle (Settings only, no networking) — VERIFIED
[x] 5.4: Offline-First Web Scraping Utility (OkHttp/Jsoup) — VERIFIED
[x] 5.5: Wire Web Search Tool into Chat UI — VERIFIED
[x] 5.6: Consolidate tool row below input (Maps + Web Search) — VERIFIED
[x] 5.7: Strict Out-Of-Memory (OOM) handling for native C++ engines — VERIFIED
[x] 5.8: CI/CD Pipeline & Release ProGuard Rules — VERIFIED
[x] Phase 5: Production Polish & Maps Integration — COMPLETE
[x] PROJECT HUMANITY_LLM: 100% COMPLETE

WHAT REMAINS (High Level)
[x] Phase 1: Foundation & ZIM Engine — COMPLETE
[x] Phase 2: Document Ingestion & ZIM Creation — COMPLETE
[x] Phase 3: LLM Integration & Large Context Retrieval — COMPLETE
[x] Phase 4: Voice Interpreter & Live Translation — COMPLETE
[ ] Phase 5: Optional Web Search & Production Polish

CRITICAL LESSONS (DO NOT IGNORE)
ZIM Over RAG: User explicitly requested ZIM format over RAG. No vector DBs.
No Vision (Unless Confirmed): Pure text retrieval models preferred to save RAM.
Memory Management: ZIM creation and LLM inference are memory-heavy. Strict background threading mandatory.
UI Preservation: Existing Jetpack Compose UI is comprehensive. Do NOT rewrite; surgically update.
Task File Protocol: OpenCode MUST read ONLY docs/tasks/task_X.Y.md for context. Never scan the full codebase.
Workflow Rule: ALWAYS update Agent memory via OpenCode prompt after human confirms the output.

KEY ARCHITECTURAL DECISIONS
Knowledge Base: OpenZIM (compiled via NDK). Users upload docs, app extracts text and compiles to ZIM natively.
LLM: Qwen 3.x (4B Instruct) GGUF for text generation and ZIM context retrieval.
Audio/STT: SenseVoice (or Qwen-Audio) for offline live transcription and translation input.
Connectivity: 100% Offline by default. Web access is a toggle.

CRITICAL CONSTRAINTS
DO NOT read files outside the project root.
ONE tool call at a time (serial execution).
After completing a step: update this file + append to docs/execution_log.md.
File Upload Mandate: NEVER assume OpenCode wrote code correctly from memory. Always request physical file uploads for verification.
SOLE CONTEXT SOURCE: OpenCode must read ONLY the task file specified in Current Step. Do NOT explore the codebase unless the task file explicitly names a file to read.
