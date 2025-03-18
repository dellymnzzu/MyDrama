# 🦋 MyDrama
- 다양한 기능을 직접 공부하고 적용해보기 위해 진행한 1인 웹 개발 프로젝트
- 사용자가 원하는 상품을 검색하고 결제할 수 있으며, 관리자는 회원 및 상품을 효율적으로 관리할 수 있도록 설계
- 실시간 채팅 및 챗봇 상담 기능을 통해 사용자 편의성을 높이고, 결제 및 환불 시스템을 구현하여 원활한 거래 환경 제공

## 🏆 주요 기능
### 🛒 상품 거래 및 결제 시스템
- **상품 CRUD :** 사용자가 상품을 등록, 수정, 삭제, 조회 가능
- **결제 기능 :** 실제 금액이 차감되는 결제 및 환불 기능 지원
- **결제 내역 조회 :** 사용자는 본인의 결제 내역을 한눈에 확인 가능
- **환불 처리 :** 결제 취소 시 주문 취소 팝업 및 환불 처리 기능 제공


### 🔐 사용자 계정 관리
- **회원가입 및 로그인 :** Spring Security를 활용한 사용자 인증
- **비밀번호 재설정 :** 이메일을 통한 비밀번호 변경 기능 제공
- **소셜 로그인 :** Google, KaKao, Naver 등을 통한 간편 로그인


### 💬 실시간 채팅 및 상담 기능
- **WebSocket 기반 실시간 채팅 :** 사용자 간 메시지 교환 가능
- **챗봇 상담 기능 :** 기본적인 문의에 대한 자동 응답 제공
- **알림 기능 :** 새로운 메시지 도착 시 실시간 알림 전송


### 🏢 관리자 기능
- **회원 관리 :** 사용자 계정 조회, 수정, 삭제 기능
- **상품 관리 :** 판매 상품 승인 및 카테고리별 정리 기능 제공
- **배너 및 공지사항 :** 프로모션 및 공지사항 등록 및 관리

<br>

## 🛠  사용기술
### ⚙️ 백엔드
- **언어 및 프레임워크**:
  - Java 21
  - Spring Boot 3.4.1
  - Spring Security (인증 및 권한 관리)
  - Spring Data JPA (데이터 접근 계층)
  - QueryDSL (타입 안전 쿼리)

- **데이터베이스**:
  - MySQL (주 데이터베이스)
  - H2 Database (테스트용)

- **실시간 통신**:
  - WebSocket (실시간 채팅)
  - STOMP (메시징 프로토콜)

- **인증 및 권한**:
  - OAuth2 (소셜 로그인)
  - JWT (토큰 기반 인증)




### 💻 프론트엔드
- **템플릿 엔진**:
  - Thymeleaf (서버 사이드 템플릿 엔진)

- **웹 기술**:
  - HTML5
  - CSS3/SASS
  - JavaScript (ES6+)
  - jQuery

- **UI 프레임워크**:
  - Bootstrap 5

<br>

## 🏗  기타기능
- **다국어 지원**:
  - Google Cloud Translation API

- **알림 서비스**:
  - Spring Mail (이메일 알림)
  - WebSocket (실시간 알림)

- **결제 시스템**:
  - IamPort (결제 게이트웨이 연동)

- **데이터 처리**:
  - Apache POI (엑셀 파일 처리)
  - Schedule Tasks (자동화된 작업 스케줄링)

<br>

## 📦구조

<details><summary>📊 데이터베이스
</summary>

![Image](https://github.com/user-attachments/assets/bee8f2a5-c5d7-4c2e-9697-ce8b7db52b9a)
## 



</details>

<details><summary>📦 디렉토리 구조
</summary>


```
📂 src
│   📄 README.md
│
├── 📂 main
│   ├── 📂 java
│   │   └── 📂 com
│   │       └── 📂 MyDrama
│   │           ├── 📄 MyDramaApplication.java
│   │           │
│   │           ├── 📂 config
│   │           │   ├── 📄 AuditConfig.java
│   │           │   ├── 📄 AuditorAwareImpl.java
│   │           │   ├── 📄 CustomAuthenticationEntryPoint.java
│   │           │   ├── 📄 CustomOAuth2UserService.java
│   │           │   ├── 📄 MailConfig.java
│   │           │   ├── 📄 OAuthAttributes.java
│   │           │   ├── 📄 SecurityConfig.java
│   │           │   ├── 📄 SecurityUtil.java
│   │           │   ├── 📄 VisitorInterceptor.java
│   │           │   ├── 📄 WebMvcConfig.java
│   │           │   ├── 📄 WebSocketConfig.java
│   │           │
│   │           ├── 📂 constant
│   │           │   ├── 📄 Category.java
│   │           │   ├── 📄 Gender.java
│   │           │   ├── 📄 ItemSellStatus.java
│   │           │   ├── 📄 LINE.java
│   │           │   ├── 📄 MainCategory.java
│   │           │   ├── 📄 Membership.java
│   │           │   ├── 📄 OrderStatus.java
│   │           │   ├── 📄 QuestionStatus.java
│   │           │   ├── 📄 Role.java
│   │           │   ├── 📄 SkinConcern.java
│   │           │
│   │           ├── 📂 controller
│   │           │   ├── 📄 AdminController.java
│   │           │   ├── 📄 CartController.java
│   │           │   ├── 📄 ChatController.java
│   │           │   ├── 📄 CommentController.java
│   │           │   ├── 📄 ItemController.java
│   │           │   ├── 📄 MainController.java
│   │           │   ├── 📄 MemberController.java
│   │           │   ├── 📄 OrderController.java
│   │           │   ├── 📄 PaymentController.java
│   │           │   ├── 📄 QnAController.java
│   │           │
│   │           ├── 📂 dto
│   │           │   ├── 📄 AnswerDto.java
│   │           │   ├── 📄 BannerDto.java
│   │           │   ├── 📄 CartDetailDto.java
│   │           │   ├── 📄 CartItemDto.java
│   │           │   ├── 📄 CartOrderDto.java
│   │           │   ├── 📄 CartPaymentDto.java
│   │           │   ├── 📄 ChatRoomDto.java
│   │           │   ├── 📄 CommentDto.java
│   │           │   ├── 📄 CommentFormDto.java
│   │           │   ├── 📄 ItemCrawlerDto.java
│   │           │   ├── 📄 ItemDto.java
│   │           │   ├── 📄 ItemFormDto.java
│   │           │   ├── 📄 ItemImgDto.java
│   │           │   ├── 📄 ItemSearchDto.java
│   │           │   ├── 📄 MainItemDto.java
│   │           │   ├── 📄 MemberDto.java
│   │           │   ├── 📄 MemberFormDto.java
│   │           │   ├── 📄 MemberupdateDto.java
│   │           │   ├── 📄 NoticeDto.java
│   │           │   ├── 📄 OrderDto.java
│   │           │   ├── 📄 OrderHistDto.java
│   │           │   ├── 📄 OrderItemDto.java
│   │           │   ├── 📄 PasswordChangeFormDto.java
│   │           │   ├── 📄 PaymentDto.java
│   │           │   ├── 📄 QuestionDto.java
│   │           │   ├── 📄 QuestionForm.java
│   │           │   ├── 📄 SessionUser.java
│   │           │   ├── 📄 VisitorCountDto.java
│   │           │
│   │           ├── 📂 entity
│   │           │   ├── 📄 Answer.java
│   │           │   ├── 📄 Banner.java
│   │           │   ├── 📄 BaseEntity.java
│   │           │   ├── 📄 BaseTimeEntity.java
│   │           │   ├── 📄 Cart.java
│   │           │   ├── 📄 CartItem.java
│   │           │   ├── 📄 ChatMessage.java
│   │           │   ├── 📄 ChatRoom.java
│   │           │   ├── 📄 Comment.java
│   │           │   ├── 📄 Item.java
│   │           │   ├── 📄 ItemCrawl.java
│   │           │   ├── 📄 ItemImg.java
│   │           │   ├── 📄 ItemLike.java
│   │           │   ├── 📄 Member.java
│   │           │   ├── 📄 Notice.java
│   │           │   ├── 📄 Order.java
│   │           │   ├── 📄 OrderItem.java
│   │           │   ├── 📄 Payment.java
│   │           │   ├── 📄 Question.java
│   │           │   ├── 📄 SnsMember.java
│   │           │   ├── 📄 VisitorCount.java
│   │           │
│   │           ├── 📂 repository
│   │           │   ├── 📄 AnswerRepository.java
│   │           │   ├── 📄 BannerRepository.java
│   │           │   ├── 📄 CartItemRepository.java
│   │           │   ├── 📄 CartRepository.java
│   │           │   ├── 📄 ChatMessageRepository.java
│   │           │   ├── 📄 ChatRoomRepository.java
│   │           │   ├── 📄 CommentRepository.java
│   │           │   ├── 📄 ItemRepository.java
│   │           │   ├── 📄 MemberRepository.java
│   │           │   ├── 📄 OrderRepository.java
│   │           │   ├── 📄 PaymentRepository.java
│   │           │
│   │           ├── 📂 service
│   │           │   ├── 📄 AnswerService.java
│   │           │   ├── 📄 ChatbotService.java
│   │           │   ├── 📄 ChatService.java
│   │           │   ├── 📄 ItemService.java
│   │           │   ├── 📄 MemberService.java
│   │           │   ├── 📄 OrderService.java
│   │           │
│   ├── 📂 resources
│   │   ├── 📂 static
│   │   │   ├── 📂 css
│   │   │   │   ├── 📄 layout.css
│   │   │   │   ├── 📄 layout1.css
│   │   │   ├── 📂 img
│   │   │       ├── 🖼 correction_btn.png
│   │   │       ├── 🖼 delete_btn.png
│   │   ├── 📂 templates
│   │       ├── 📄 main.html
│   │       ├── 📂 admin
│   │       │   ├── 📄 admin.html
│   │       ├── 📂 cart
│   │       │   ├── 📄 cartList.html

```


</details>


</details>
