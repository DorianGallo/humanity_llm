# humanity_llm Implementation Plan

## Phase 1: Foundation & ZIM Engine
[ ] 1.1 Initialize Android NDK, create `zim_engine` module, and configure `CMakeLists.txt`.
[ ] 1.2 Compile `libzim` and `zimwriterfs` C++ dependencies via NDK.
[ ] 1.3 Create JNI bridge (`zim_bridge.cpp`) and Kotlin wrapper (`ZimEngine.kt`).
[ ] 1.4 Write basic instrumentation tests to verify native library loads successfully.

## Phase 2: Document Ingestion & ZIM Creation
[ ] 2.1 Implement native text extractors for PDF, HTML, and TXT files.
[ ] 2.2 Create background worker to pipe extracted text into `zimwriterfs` to generate `.zim`.
[ ] 2.3 Implement ZIM mounting logic to make newly created ZIM files searchable via `libzim`.
[ ] 2.4 Build the Compose UI for document upload and ingestion progress tracking.

## Phase 3: LLM Integration & Large Context Retrieval
[ ] 3.1 Integrate `Qwen3-4B-Instruct` (or chosen text model) GGUF via `llama.cpp`.
[ ] 3.2 Configure `llama.cpp` context window (e.g., 32k) for ZIM article retrieval.
[ ] 3.3 Implement the core chat loop: Query ZIM -> Inject Context -> Generate Response.
[ ] 3.4 Implement streaming response integration with the existing Compose UI.

## Phase 4: Voice Interpreter & Live Translation
[x] 4.1 Integrate SenseVoice (or Qwen-Audio) ONNX/C++ wrapper for offline STT.
[x] 4.2 Build live transcription UI and robust audio recording state machine.
[x] 4.3 Implement the Translation Pipeline: STT -> LLM Translation Prompt -> TTS output.
[x] 4.4 Add language selection toggles for Source and Target languages.
[x] 4.4 Wire OfflineSttManager (Audio Pipeline) into ChatViewModel
[x] 4.5 Integrate Sherpa-ONNX Streaming API into OfflineSttManager
[x] 4.6 Implement universal chat template detection (ChatML, Llama, Gemma, Mistral, Phi)
[x] 4.7 Create ZIM Reader JNI bridge and Kotlin wrapper for reading entries
[x] 4.8 Build ZIM Reader UI screen and wire into Jetpack Navigation
[x] 4.9 Implement zero-config GGUF model auto-loading on file pick

## Phase 5: Optional Web Search & Production Polish
[x] 5.1 Implement Organic Maps offline navigation integration
[x] 5.2 Implement chat-driven navigation interception
[x] 5.3 Implement persisted Web Search toggle in Settings
[x] 5.4 Implement offline-first web scraping utility (OkHttp/Jsoup)
[x] 5.5 Wire Web Search tool into Chat UI
[x] 5.6 Consolidate tool row below input (Maps + Web Search)
[x] 5.7 Add strict Out-Of-Memory (OOM) handling for native C++ engines
[x] 5.8 Configure CI/CD (GitHub Actions) and finalize release ProGuard rules
