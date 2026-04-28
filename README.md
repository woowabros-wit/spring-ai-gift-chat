# spring-ai-gift-chat

- [x] 사용자는 자연어로 선물 추천을 요청할 수 있다.
  - 채팅 API 엔드포인트 : `POST /api/chat`
    - 요청 본문 :
      - message : String (필수)
      - sessionId : String (선택)
        ```json
        {
          "message": "친구 생일 선물 추천해줘",
          "sessionId" : "unique-session-id-123"
        }
        ```
    - 응답 본문 :
      - requestId : String
      - message : String
      - sessionId : String
      - durationMs : Number
        ```json
        {
          "requestId": "unique-request-id-456",
          "message": "친구 생일 선물로는 책, 향수, 액세서리 등이 좋아요. 친구의 취향을 알려주시면 더 맞춤 추천도 가능해요!",
          "sessionId": "unique-session-id-123",
          "durationMs": 1500
        }
        ```
- [x] 서비스는 선물 추천 도우미 역할로 사용자의 메시지에 응답한다.
  - 역할은 별도 텍스트 파일로 관리하며, LLM 프롬프트에 포함하여 응답을 생성한다.
- [ ] LLM 호출 중 오류가 발생하면 사용자에게 오류를 노출하지 않고, 안내 메시지를 반환한다.
- [ ] 각 요청의 주요 정보(요청 식별자, 사용자 입력, 응답 생성 시간)를 로그로 남긴다.