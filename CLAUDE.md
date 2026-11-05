# Elite Coding Principles & Agent Protocols
Inspired by Andrej Karpathy's engineering philosophy and strict autonomous agent workflows. 
Core bias: Caution over speed, clarity over cleverness, minimalism over over-engineering.

## 1. THE 4 CORE PILLARS (MANDATORY)
1. **Think Before Coding**: Don't assume. Don't hide confusion. Surface tradeoffs. State assumptions explicitly. If multiple interpretations exist, present them. If a simpler approach exists, push back.
2. **Simplicity First (Minimalism)**: Minimum code that solves the problem. No speculative features, no unnecessary abstractions, no "flexibility" that wasn't requested. If 10 lines solve the problem, do not write 100. 
3. **Surgical Changes**: Touch only what you must. Don't "improve" adjacent code, comments, or formatting. Match existing style. Remove imports/variables/functions that YOUR changes made unused, but do not delete pre-existing dead code unless asked.
4. **Goal-Driven Execution**: Transform tasks into verifiable goals. Define success criteria and loop until verified. (e.g., "Fix the bug" → "Write a test that reproduces it, then make it pass").

## 2. AGENT OPERATIONAL PROTOCOLS (MANDATORY)
### Session Startup & Context Efficiency
* **FIRST ACTION**: Always read `AGENT_MEMORY.md` before any other tool call. It is your ONLY context source.
* **CODEGRAPH FIRST**: Use `codegraph_explore` for ALL codebase understanding. Do NOT run `find`, `grep`, `ls -R`, or scan directories. 
* **MINIMAL READS**: Read a file ONLY if the task file explicitly names it. Maximum 2 file reads per turn. Never read files just to "understand the project".

### Concurrency Lock (ABSOLUTE PRIORITY)
* **ONE REQUEST AT A TIME**: Issue exactly ONE tool call, API request, or command at a time. Wait for the COMPLETE response before issuing the next one.
* **NO PARALLEL CALLS**: Never batch reads, writes, or API requests. Never spawn background processes.
* **ON RATE LIMIT (429)**: STOP. Do not retry immediately. Report the error and wait.

### Memory & Meta-Learning Protocol
* **State Anchor (`AGENT_MEMORY.md`)**: Overwrite the CURRENT STATE section after each verified step. Keep it under 500 tokens.
* **Execution Log (`docs/execution_log.md`)**: Append ONE micro-entry per verified step. Never read this file in full.
* **Meta-Learning Loop**: At the END of every session, review micro-entries, identify patterns, and propose ONE concrete improvement as a diff. WAIT FOR HUMAN APPROVAL.

## 3. ENGINEERING STANDARDS (The "Would Andrej Approve?" Test)
* **Explicit Error Handling**: Never silently swallow exceptions. Handle expected failure modes. Return user-friendly errors at the API boundary; log detailed errors server-side.
* **Documentation & Type Safety**: Every public function gets a concise docstring (what, params, returns, raises). Use strict type hints on all signatures. No `any` unless justified.
* **Immutability & Side Effects**: Prefer immutable data structures. Mutations must be explicit and localized.
* **Resilience by Default**: All external calls must have timeouts and retry logic. Validate external input at every boundary.
* **Testing**: Write a test for every bug fix *before* fixing it. Write a test for every new feature *before* implementing it. Tests must be fast, isolated, and deterministic.
* **File Organization**: One class per file. One module = one responsibility. Imports sorted: stdlib → third-party → local. No wildcard imports.

## 4. EXECUTION WORKFLOW & VERIFICATION
### Task Execution Loop
1. Save the human's prompt as `docs/tasks/task_X.Y.md`.
2. Execute the single action.
3. **DEBUG**: Check output for errors immediately.
4. **VERIFY**: Confirm the action produced the expected result.
5. **REPORT**: State "Requirement X complete and verified."
*(HALT ON FAILURE: If any requirement fails, STOP. Report the exact error.)*

### The Verification Protocol (CRITICAL CONSTRAINT)
* **File Upload Mandate**: NEVER assume OpenCode (or any local IDE agent) wrote code correctly from memory. Because local agents operate in a black box, **you must always request that the human upload the physical file(s) from their local drive** to audit, verify, and confirm the output before moving to the next step. 
* **Trust the Local File**: If the build/tests pass locally, trust it over stale context-window files.
