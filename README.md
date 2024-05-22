# CAMUS Backend

# ❗상단 이미지 및 설명

CAMUS는 4/8 ~ 5/20에 진행된 프로젝트입니다.

---

## 📄 목차

- [INTRODUCTION](#introduction)
- [BACKEND TEAMS](#backend-teams)
- [ROLE](#role)
- [SKILLS](#skills)
- [SYSTEM ARCHITECTURE](#system-architecture)
- [ERD](#erd)
- [SERVICE LAYOUT](#service-layout)
- [FUNCTION](#funtion)
- [DIRECTORY](#directory)

---

<a name="introduction"></a>
## 📖 Introduction

# ❗B2B와 B2C로 나뉜 메시징 서비스 설명 필요

<a name="backend-teams"></a>
## 🖇 Backend Teams

| <a href="https://github.com/damdam6"><img src="https://github.com/damdam6.png" width="120"/></a> | <a href="https://github.com/gabalja"><img src="https://github.com/gabalja.png" width="120"/></a> | <a href="https://github.com/SiyeonYoo"><img src="https://github.com/SiyeonYoo.png" width="120"/></a> | <a href="https://github.com/dtdtdz"><img src="https://github.com/dtdtdz.png" width="120"/></a> | <a href="https://github.com/nijesmik"><img src="https://github.com/nijesmik.png" width="120"/></a> |
|:-:|:-:|:-:|:-:|:-:|
|[이담비](https://github.com/damdam6)|[손의성](https://github.com/gabalja)|[유시연](https://github.com/SiyeonYoo)|[차우열](https://github.com/dtdtdz)|[김세진](https://github.com/nijesmik)|

---

<a name="role"></a>
## 🧰 Role

### 이담비

- 팀장
- ERD/시스템 아키텍쳐 설계
- 채팅 내역/채널-채팅방 관리 전체 API 구축
- Redis 저장-조회 전체 로직 구축
- Kafka 송수신 전반 구축
    - Redis/Stomp/AI 데이터 분산 처리 구축
- Stomp 로직 구축
- AI 학습 구조 설계/기획/데이터 전처리

- 기획/최종 발표
- 기획/최종 PPT 제작
- 서비스 아이디어 제안 및 구체화
- 아키텍처 기획 및 비용 산정

### 손의성

- SpringSecurity, JWT 기반 회원 관리 기능 구축
- 아키텍처 기획 및 비용 산정
- AWS 기반 아키텍처 설계

### 유시연

- Stomp 로직 전반 구축
- Kafka 설정 및 sub/pub 구조 구축
- Kafka-Stomp 연결 로직 구축

### 차우열

- ERD 설계
- 단순 일치 기반 필터링 구현
- 다중 클래스 분류 모델 기반 단문 분석 AI 생성
- HyperClovaX  기반 맥락 분석 필터링 AI 생성
- 필터링 api  및 Kafka 메시징 구현
- 필터링 AI - Lambda 이미지 구축

### 김세진

- 서버 CI/CD 구축 및 인프라 총괄

---

<a name ="skill"></a>
## ⭐ Skill

### Language
- Java 17
### Framework
- SpringBoot 3.2.5
- SpringSecurity
- SpringBatch
- MongoRepository

### Sub
- Stomp
- JWT
- kafka 3.6.2

### Database
- MongoDB : 7.0.9
- Redis : 6.2.14

### CI/CD
- AWS ECR
- AWS ECS
- AWS Lambda
- Docker
- Jenkins 2.440.3
- AWS CodeDeploy
- AWS CodePipeline

### Sub
- AWS S3
- AWS ALB
- VPC
- AWS Route53
- AWS CloudFront

---

<a name="system-architecture"></a>
## 🏢 System Architecture

![infra](https://github.com/Camus-chat/backend/blob/main/readme-asset/infra.png)

---

<a name="erd"></a>
## 💾 ERD

![ERD](https://github.com/Camus-chat/backend/blob/main/readme-asset/ERD.png)

---

<a name="service-layout"></a>
## 🌈 Service Layout
- # ❗추가필요

---

<a name="function"></a>
## 📩 Function

### 회원 : Spring Security & JWT 사용하여 로그인 구현
- 개인회원, 기업회원, 비회원
- 회원가입
- 로그인 로그아웃
- 마이페이지
 - 회원 정보 수정
- 회원 탈퇴
    
### 채널/룸 : 채팅은 링크를 통해 채널로 입장.
- 채널 : 공유 가능한 형식의 테마 구성
- 채팅방 : 개인/그룹 채팅 분리 구현
    - 메시지 : 서비스 상태에 따른 Notice 메시지 송출
    
- 메시지 :
    - Stomp
        - 클라이언트와 Kafka 간 메시지 송수신
        - 채팅방 sub/pub을 통해 메시지 동시송수신
        
    - Kafka : 대용량 메시징/데이터 처리를 위한 MessageQue사용
        - Stomp 메시지 송수신
        - Redis 메시지 저장 요청 및 서버 데이터 전송 송수신
        - AI 필터링 요청 송수신
        
    - Redis : 메시지 저장을 위한 데이터 저장소
        - 메시지 Stream기반 메시지 기록
        - 필터링 내역 기록
        - 사용자/채팅룸 별 읽은 위치 기록
        - 채팅방 별 메시지 수 기록
        
### 필터링
- 기본 필터링
    - 단순 일치를 통한 메시지 필터링
- 단문 필터링 AI 모델
    - 문장 별 AI 필터링
- HyperCloverX 분류 모델
    - 다수 메시징 동시 전송 및 필터링
    - 대화 부분 맥락에 기반한 필터링 추가

### 인프라
- # ❗적어주세요

---

<a name="directory"></a>
## Directory

```
              '
└─backend
    ├─chat
    │  ├─controller
    │  ├─domain
    │  │  ├─document
    │  │  ├─dto
    │  │  │  └─chatmessagedto
    │  │  ├─message
    │  │  └─repository
    │  ├─service
    │  │  ├─KafkaConsumer
    │  │  └─KafkaProducer
    │  └─util
    ├─filter
    │  ├─controller
    │  ├─domain
    │  │  ├─Request
    │  │  └─Response
    │  ├─service
    │  │  └─kafka
    │  └─util
    │      ├─component
    │      └─type
    ├─global
    │  ├─config
    │  ├─Exception
    │  ├─jwt
    │  │  ├─controller
    │  │  ├─service
    │  │  └─util
    │  └─util
    ├─manage
    │  ├─controller
    │  ├─domain
    │  │  ├─document
    │  │  ├─dto
    │  │  └─repository
    │  ├─service
    │  └─util
    ├─member
    │  ├─controller
    │  ├─domain
    │  │  ├─document
    │  │  │  └─MemberProfile
    │  │  ├─dto
    │  │  └─repository
    │  └─service
    ├─statistic
    │  ├─controller
    │  ├─domain
    │  │  ├─document
    │  │  └─repository
    │  └─service
    └─util

```