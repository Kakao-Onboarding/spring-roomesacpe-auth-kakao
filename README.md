# 🚀 1단계 - 로그인
## 상황 설명
* 전화 예약의 한계를 느껴 온라인 예약을 지원하기로 한다.
* 온라인 예약을 위해 로그인 기능을 제공한다.
## 요구사항
### 기능 요구사항
- [ ] 토큰 발급하는 API 생성
- [ ] 내 정보 조회하기
  - [ ] 토큰을 이용하여 본인 정보 응답하기

### 프로그래밍 요구사항
* 인증 로직은 Controller에서 구현하기 보다는 재사용이 용이하도록 분리하여 구현하다.
  * 가능하면 Controller와 인증 로직을 분리한다.
* 토큰을 이용한 인증 프로세스에 대해 이해가 어려운 경우 페어와 함께 추가학습을 진행한다.

### API 설계
#### 토큰 발급
```Java
POST /login/token HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "username": "username",
    "password": "password"
}
```
```Java
HTTP/1.1 200 
Content-Type: application/json

{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjYzMjk4NTEwLCJleHAiOjE2NjMzMDIxMTAsInJvbGUiOiJBRE1JTiJ9.7pxE1cjS51snIrfk21m2Nw0v08HCjgkRD2WSxTK318M"
}
```

#### 내 정보 조회
```Java
GET /members/me HTTP/1.1
authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjYzMjk4NTkwLCJleHAiOjE2NjMzMDIxOTAsInJvbGUiOiJBRE1JTiJ9.-OO1QxEpcKhmC34HpmuBhlnwhKdZ39U8q91QkTdH9i0
```
```Java
HTTP/1.1 200 
Content-Type: application/json

{
    "id": 1,
    "username": "username",
    "password": "password",
    "name": "name",
    "phone": "010-1234-5678"
}
```

## 힌트
### JwtTokenProvider
* jwt 토큰 생성, 토큰 검증, 토큰에서 인증 정보 추출하는 유틸 클래스
* JwtTokenProviderTest 학습 테스트를 통해 기능을 이해하기
<hr>

# 🚀 2단계 - 로그인 리팩터링
## 요구사항
### 기능 요구사항
- [ ] 예약하기, 예약취소 개선
  - [ ] 아래의 API 설계에 맞춰 API 스펙을 변경한다.
  - [ ] 비로그인 사용자는 예약이 불가능하다.
  - [ ] 자신의 예약이 아닌 경우 예약 취소가 불가능하다.

### 프로그래밍 요구사항
- [ ] HandlerMethodArgumentResolver를 활용한다.

### API 설계
#### 예약 생성
```Java
POST /reservations HTTP/1.1
authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjYzMjk4NTkwLCJleHAiOjE2NjMzMDIxOTAsInJvbGUiOiJBRE1JTiJ9.-OO1QxEpcKhmC34HpmuBhlnwhKdZ39U8q91QkTdH9i0
content-type: application/json; charset=UTF-8
host: localhost:8080

{
    // 필요한 값
    // ex) "scheduleId": 1
}
```
```Java
HTTP/1.1 201 Created
Location: /reservations/1
```
#### 예약 삭제
```Java
DELETE /reservations/1 HTTP/1.1
authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjYzMjk5MDcwLCJleHAiOjE2NjMzMDI2NzAsInJvbGUiOiJBRE1JTiJ9.zgz7h7lrKLNw4wP9I0W8apQnMUn3WHnmqQ1N2jNqwlQ
```
```Java
HTTP/1.1 204 
```

## 힌트
### HandlerMethodArgumentResolver
* HandlerMethodArgumentResolver은 컨트롤러 메서드에서 특정 조건에 맞는 파라미터가 있을 때 원하는 값을 바인딩해주는 인터페이스
```Java
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    // resolveArgument 메서드가 동작하는 조건을 정의하는 메서드
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 파라미터 중 @AuthenticationPrincipal이 붙은 경우 동작하게 설정
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    // supportsParameter가 true인 경우 동작하는 메서드
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        // TODO: 유효한 로그인인 경우 LoginMember 만들어서 응답하기
        return null;
    }
}
```
### WebMvcConfigurer
* Spring MVC Config 학습 테스트를 참고하여 HandlerMethodArgumentResolver를 등록한다.
