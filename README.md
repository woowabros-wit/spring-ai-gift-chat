# spring-ai-gift-chat

## 채팅 API
```
POST /api/gifts/recommend
```
- 자연어로 선물 추천을 요청할 수 있어야 한다
- 선물 추천 도우미의 역할을 가진다
- llm 호출 중 요류가 발생하는 경우 안내 메시지를 응답할 수 있어야 한다
- 빈 요청 메시지인 경우 에러를 반환한다
- ai chat 외부요청이 실패한 경우 에러를 반환한다
- 요청의 주요 정보를 로그로 남긴다
  - 요청별 고유 requestId를 MDC에 세팅한다
  - 사용자 메시지, 컨텍스트, 응답, 소요 시간을 로깅한다

### spec
**Request Body**
- `message` (String, 필수): 사용자 메시지
- `sessionId` (String, 선택): 대화 단위 식별자

**Response Body**
- `message` (String): AI 응답 메시지
- `sessionId` (String): 대화 단위 식별자

## LLM 추상화
- `GiftRecommendChatClient` 인터페이스로 LLM 호출을 추상화한다
  - 요청 메시지와 sessionId를 받아 `Result<String>`을 반환한다
- 구현체
  - `SpringAiGiftRecommendChatClient`: Spring AI ChatClient 기반
  - `ClaudeCodeGiftRecommendChatClient`: Claude Code SDK 기반
    - 세션이 유지되도록 `CLIOptions`를 조정한다
- `GiftRecommendChatClientConfig`에서 `@ConditionalOnProperty`로 빈 등록을 분기한다
  - `llm.provider=spring-ai` → SpringAiGiftRecommendChatClient
  - `llm.provider=claude-code` → ClaudeCodeGiftRecommendChatClient
- `GiftRecommenderService`는 `GiftRecommendChatClient`를 주입받아 구현체에 의존하지 않는다
