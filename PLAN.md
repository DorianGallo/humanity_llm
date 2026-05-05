# OfflineLLM-ZIM — Project Plan

Tracks implementation progress. Mark phases `[x]` when complete.

**Package**: `com.jegly.offlineLLM`  
**Base repo**: `jegly/OfflineLLM` (Apache 2.0)  
**Fork target**: `DorianGallo/OfflineLLM-ZIM`

---

## Key Decisions

| Topic | Decision |
|-------|----------|
| Mirostat JNI | Store in data/UI layer only; native not extended (Phase 2 TODO) |
| ModelConfig/ModelInfo | Separate Room entities, FK `ModelConfigEntity.modelInfoId → ModelInfo.id` (nullable) |
| Chat export format | One JSON file per conversation |
| Voice input Phase 1 | Android `SpeechRecognizer` (offline-capable); whisper.cpp is Phase 2 |
| DB migration | `fallbackToDestructiveMigration()` — fresh fork, no existing user DB |

---

## Phase 0 — Repository Setup [x]

**Goal**: Fork the upstream repo and clone it with submodules into the Codespace.

Steps:
1. Fork `jegly/OfflineLLM` → `DorianGallo/OfflineLLM-ZIM` via `gh repo fork`
2. Clone with submodules: `git clone --recurse-submodules <fork-url>`
3. Verify `llama.cpp/` submodule is populated

**Verify**: `ls OfflineLLM-ZIM/llama.cpp/` contains source files.

---

## Phase 1 — Manifest + Network Security [x]

**Goal**: Add INTERNET + RECORD_AUDIO permissions; restrict cleartext traffic to localhost.

Files to change:
- `app/src/main/AndroidManifest.xml` — add 2 permissions + `networkSecurityConfig` attribute
- `app/src/main/res/xml/network_security_config.xml` *(NEW)* — cleartext only to `localhost` + `127.0.0.1`

**Verify**: Build compiles; `adb shell dumpsys package com.jegly.offlineLLM | grep permission` shows new permissions.

---

## Phase 2 — Dependencies [x]

**Goal**: Add OkHttp to the app module.

Files to change:
- `app/build.gradle.kts` — add `implementation("com.squareup.okhttp3:okhttp:4.12.0")`

**Verify**: `./gradlew assembleDebug` resolves OkHttp; no dependency conflicts.

---

## Phase 3 — Data Layer [x]

**Goal**: Add `ModelConfigEntity` to Room, seed 4 model presets from JSON on first launch.

New files:
- `ai/ModelConfig.kt` — domain data class (pure Kotlin, no Room annotations)
- `data/local/entities/ModelConfigEntity.kt` — `@Entity`, FK → `ModelInfo.id` (nullable), includes all inference params + `presetKey`, `isActive`
- `data/local/dao/ModelConfigDao.kt` — `getAllConfigs(): Flow`, `getActiveConfig()`, `getByPresetKey()`, `insert/update/delete/setActive`
- `data/repository/ModelConfigRepository.kt` — wraps DAO; `seedIfEmpty(context)`, `getActiveConfig()`, `setActive()`, `updateConfig()`
- `assets/model_presets.json` — 4 entries: `nemotron`, `phi`, `qwen-heretic`, `gemmasutra`

Modified files:
- `data/local/ChatDatabase.kt` — add `ModelConfigEntity`, bump version `1 → 2`, `fallbackToDestructiveMigration()`
- `data/local/DatabaseModule.kt` — add `@Provides fun provideModelConfigDao(...)`

**model_presets.json schema**:
```json
[
  {
    "presetKey": "nemotron",
    "name": "Nemotron3-Nano-4B-Uncensored",
    "temperature": 0.2,
    "topP": 0.85,
    "topK": 30,
    "repeatPenalty": 1.08,
    "contextSize": 3072,
    "mirostat": 2,
    "mirostatTau": 3.0,
    "mirostatEta": 0.1,
    "systemPrompt": "..."
  },
  ...
]
```

**Verify**: Room compiles; on first launch, 4 rows appear in `ModelConfigEntity` table.

---

## Phase 4 — AI Layer [x]

**Goal**: Add KiwixClient, RagPipeline, VoiceInputManager.

New files:
- `ai/KiwixClient.kt` — `@Inject`; `suspend fun search(query): List<ZimResult>`; returns empty list (never throws) when server unreachable
- `ai/RagPipeline.kt` — `suspend fun enrichPrompt(userQuery): String`; wraps results in spec format; short-circuits if ZIM disabled or no results
- `ai/VoiceInputManager.kt` — wraps Android `SpeechRecognizer`; `startListening(): Flow<String>`; `stopListening()`

**RAG prompt format**:
```
[ZIM Context]
{results}

[User Question]
{query}

[Instructions: cite sources, use <think> tags]
```

**Verify**: Unit test `KiwixClient.search()` against a stubbed OkHttp response; `RagPipeline.enrichPrompt()` returns raw query when ZIM disabled.

---

## Phase 5 — System Prompt Templates [ ]

**Goal**: Define 3 pre-configured system prompt templates; add template merging.

Modified files:
- `ai/SystemPrompts.kt` — add sealed class / enum `SystemPromptTemplate`: `ZimRagMode`, `CoderMode`, `FastChat`
- `ai/PromptFormatter.kt` — add `fun mergeSystemPrompt(template: SystemPromptTemplate, modelBasePrompt: String): String`

**Templates**:
- `ZimRagMode` (default): ZIM context awareness, citation format, `<think>` tags
- `CoderMode`: code-first, minimal explanation, Termux/Android aware
- `FastChat`: minimal system prompt for speed

**Verify**: `mergeSystemPrompt(ZimRagMode, "base prompt")` produces expected combined string.

---

## Phase 6 — Engine Integration [ ]

**Goal**: Wire RagPipeline into InferenceEngine; wire ModelConfig into ModelManager.

Modified files:
- `ai/InferenceEngine.kt` — inject `RagPipeline`; call `ragPipeline.enrichPrompt(query)` in `generateResponse()` before passing to `instance.getResponseAsFlow()`
- `ai/ModelManager.kt` — on init call `modelConfigRepository.seedIfEmpty()`; in `loadModel()` read active `ModelConfigEntity`, map relevant fields → `SmolLM.InferenceParams` (mirostat fields stored but **not** forwarded to native — Phase 2 TODO)

**Verify**: After loading a model, sending a chat message with ZIM enabled routes through `enrichPrompt()`.

---

## Phase 7 — Settings/Repository [ ]

**Goal**: Persist ZIM settings and active system prompt template preference.

Modified files:
- `data/repository/SettingsRepository.kt` — add:
  - `var zimEnabled: Boolean` (default `true`, backed by `EncryptedSharedPreferences`)
  - `var zimServerUrl: String` (default `"localhost:8080"`)
  - `var activeSystemPromptTemplate: String` (default `"ZimRagMode"`)

**Verify**: Toggle `zimEnabled` in settings → survives app restart.

---

## Phase 8 — Chat Export/Import [ ]

**Goal**: Export one conversation to JSON; import back.

Modified files:
- `data/repository/ChatRepository.kt` — add:
  - `suspend fun exportConversation(conversationId: String): String`
  - `suspend fun importConversation(json: String): Result<Unit>`
- `data/local/entities/Conversation.kt` + `Message.kt` — add `@Serializable` (if not already present)

**Export JSON schema**:
```json
{
  "conversation": { "id": "...", "title": "...", ... },
  "messages": [ { "role": "user", "content": "...", ... } ]
}
```

**Verify**: Export a conversation → JSON file written to `Downloads/`; import it → same messages appear in a new conversation.

---

## Phase 9 — DI Wiring [ ]

**Goal**: Register all new classes with Hilt.

Modified files:
- `di/AppModule.kt` — `@Provides @Singleton fun provideOkHttpClient(): OkHttpClient`
- `di/InferenceModule.kt` — `@Provides KiwixClient`, `@Provides RagPipeline`
- `di/RepositoryModule.kt` — `@Provides ModelConfigRepository`

**Verify**: `./gradlew assembleDebug` — Hilt component graph compiles with no missing bindings.

---

## Phase 10 — UI [ ]

**Goal**: Add mic button, ZIM status dot, Model Config screen, Settings sections.

New files:
- `ui/components/ZimStatusIndicator.kt` — `@Composable` green/red dot; pings KiwixClient every 10s via `LaunchedEffect`
- `ui/screens/ModelConfigScreen.kt` — list of 4 presets; tap → edit form with all inference param fields; save → Room update

Modified files:
- `ui/components/InputBar.kt` — add mic `IconButton` (`onMicClick: (() -> Unit)?` param)
- `ui/screens/ChatScreen.kt` — add `ZimStatusIndicator` in top bar; pass `onMicClick` to `InputBar`
- `ui/screens/ChatViewModel.kt` — `zimConnectionState: StateFlow<Boolean>`; mic press → `VoiceInputManager.startListening()` → insert transcribed text
- `ui/screens/SettingsScreen.kt` — add ZIM Server section (URL + Test button); System Prompt template selector (3 options); link to Model Manager; Export/Import buttons
- `ui/screens/SettingsViewModel.kt` — ZIM test action, model dropdown state, template state, export/import actions
- `ui/navigation/NavGraph.kt` + `Routes.kt` — add `ModelConfig` route

**Verify**: Full app flow — chat, mic, ZIM indicator, settings, export, model manager all functional.

---

## Final Verification Checklist

- [ ] `./gradlew assembleDebug` — clean compile, APK at `app/build/outputs/apk/debug/`
- [ ] First launch → Settings → Model Manager shows 4 preset entries
- [ ] Import `.gguf` → model loads and generates a response
- [ ] ZIM disabled → chat works with raw query (no server needed)
- [ ] Kiwix on `localhost:8080` → ZIM dot green; response includes `[Source:]` format
- [ ] Mic tap → Android recognizer → transcribed text in input
- [ ] Kill + reopen app → messages preserved
- [ ] Export conversation → valid JSON; re-import → conversation restored
- [ ] Switch between 3 system prompt templates → behavior changes
